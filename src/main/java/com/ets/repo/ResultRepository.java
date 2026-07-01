package com.ets.repo;

import com.ets.model.Result;

import java.util.List;

public interface ResultRepository {

    void saveResult(Result result);

    List<Result> getAllResults();

    List<Result> getResultsByQuiz(String quizId);

    /**
     * Returns all results for one student, intended to be sorted most-recent-first
     * by implementations.
     *
     * @param username the student's username, matched case-insensitively
     * @return matching results, newest first
     */
    List<Result> getResultsByUsername(String username);
}