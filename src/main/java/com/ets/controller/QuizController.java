package com.ets.controller;

import com.ets.model.Answer;
import com.ets.model.Question;
import com.ets.model.Quiz;
import com.ets.model.Result;
import com.ets.services.QuizService;
import com.ets.strategy.QuizStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls quiz flow between GUI and business logic.
 * Tracks current question index and submitted answers.
 */
public final class QuizController {

    private final QuizService quizService;
    private Quiz currentQuiz;
    private List<Answer> currentAnswers;
    private int currentQuestionIndex;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
        this.currentAnswers = new ArrayList<>();
    }

    public void startQuiz(List<Question> questions, QuizStrategy strategy) {
        currentQuiz = quizService.createQuiz("Quiz", "Description", questions, strategy);
        currentQuestionIndex = 0;
        currentAnswers.clear();
    }

    public Question getCurrentQuestion() {
        return currentQuiz.getQuestions().get(currentQuestionIndex);
    }

    public boolean hasNextQuestion() {
        return currentQuestionIndex < currentQuiz.getQuestions().size() - 1;
    }

    public void nextQuestion() {
        currentQuestionIndex++;
    }

    public void submitAnswer(int selectedOption) {
        Question q = getCurrentQuestion();
        currentAnswers.add(new Answer(q.getId(), selectedOption));
    }

    public Result finishQuiz() {
        return quizService.evaluateQuiz(currentQuiz, currentAnswers);
    }
}