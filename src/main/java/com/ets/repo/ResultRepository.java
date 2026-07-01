package com.ets.repo;

import com.ets.model.Result;

import java.util.List;

public interface ResultRepository {

    void saveResult(Result result);

    List<Result> getAllResults();

    List<Result> getResultsByQuiz(String quizId);
}