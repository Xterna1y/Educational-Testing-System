package com.ets.strategy;

import com.ets.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Adaptive strategy for prioritizing weaker topic areas.
 * Currently, implements random selection as a placeholder.
 */
public final class WeakTopicStrategy implements QuizStrategy {

    @Override
    public List<Question> selectQuestions(List<Question> questions, int count) {
        List<Question> selected = new ArrayList<>(questions);
        Collections.shuffle(selected);
        return selected.subList(0, Math.min(count, selected.size()));
    }
}