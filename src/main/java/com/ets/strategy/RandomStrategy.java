package com.ets.strategy;

import com.ets.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Randomly shuffles and selects questions from the bank.
 */
public final class RandomStrategy implements QuizStrategy {

    @Override
    public List<Question> selectQuestions(List<Question> questions, int count) {
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}