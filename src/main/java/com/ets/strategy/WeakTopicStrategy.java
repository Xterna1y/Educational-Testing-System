package com.ets.strategy;

import com.ets.model.Answer;
import com.ets.model.Question;
import com.ets.model.Result;
import com.ets.repo.ResultRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Adaptive strategy that prioritises questions from the categories where
 * the given user's historical accuracy is lowest.
 *
 * <p>Accuracy per category is computed from this user's past {@link Result}s,
 * read via {@link ResultRepository#getResultsByUsername(String)}. Only
 * {@link Answer}s that carry category metadata are counted — answers
 * persisted before category tracking existed (or answers submitted through
 * a path that didn't populate it) are silently skipped rather than treated
 * as evidence of anything.</p>
 *
 * <p>Falls back to plain random selection (identical to {@link RandomStrategy})
 * when the user has no usable history at all, so a brand-new student still
 * gets a quiz instead of an empty one.</p>
 */
public final class WeakTopicStrategy implements QuizStrategy {

    /** Accuracy assumed for a category with no historical data, when at least one other category does have data. */
    private static final double NEUTRAL_ACCURACY = 0.5;

    private final String username;
    private final ResultRepository resultRepository;

    public WeakTopicStrategy(String username, ResultRepository resultRepository) {
        this.username = Objects.requireNonNull(username);
        this.resultRepository = Objects.requireNonNull(resultRepository);
    }

    @Override
    public List<Question> selectQuestions(List<Question> questions, int count) {
        Map<String, Double> accuracyByCategory = computeAccuracyByCategory();

        List<Question> ordered = new ArrayList<>(questions);
        // Shuffle first so ties (equal/unknown accuracy) aren't always
        // returned in the same order, then stable-sort weakest-first.
        Collections.shuffle(ordered);

        if (!accuracyByCategory.isEmpty()) {
            ordered.sort(Comparator.comparingDouble(
                    q -> accuracyByCategory.getOrDefault(q.getCategory(), NEUTRAL_ACCURACY)));
        }

        return ordered.subList(0, Math.min(count, ordered.size()));
    }

    /**
     * Returns this user's accuracy (0.0-1.0) for each category they have
     * previously answered questions in. Categories with no recorded answers
     * are absent from the map; an empty map means "no usable history",
     * which the caller treats as a signal to fall back to random order.
     */
    private Map<String, Double> computeAccuracyByCategory() {
        Map<String, int[]> tally = new HashMap<>(); // category -> [correctCount, totalCount]

        for (Result result : resultRepository.getResultsByUsername(username)) {
            for (Answer answer : result.getAnswers()) {
                String category = answer.getCategory();
                if (category == null) {
                    continue;
                }
                int[] counts = tally.computeIfAbsent(category, key -> new int[2]);
                counts[1]++;
                if (answer.isCorrect()) {
                    counts[0]++;
                }
            }
        }

        Map<String, Double> accuracy = new HashMap<>();
        for (Map.Entry<String, int[]> entry : tally.entrySet()) {
            int[] counts = entry.getValue();
            accuracy.put(entry.getKey(), (double) counts[0] / counts[1]);
        }
        return accuracy;
    }
}
