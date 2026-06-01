package com.ets.strategy;

import com.ets.model.Question;

import java.util.List;

/**
 * Strategy interface for question selection algorithms.
 * Demonstrates the Strategy design pattern.
 */
public interface QuizStrategy {

    /**
     * Selects questions from the provided bank.
     *
     * @param questions the full question bank
     * @param count the number of questions to select
     * @return the selected questions
     */
    List<Question> selectQuestions(List<Question> questions, int count);
}