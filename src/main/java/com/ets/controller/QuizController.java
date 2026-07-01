package com.ets.controller;

import com.ets.model.Answer;
import com.ets.model.Question;
import com.ets.model.Quiz;
import com.ets.model.Result;
import com.ets.repo.ResultRepository;
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
    private final ResultRepository resultRepository;
    private final String username;
    private Quiz currentQuiz;
    private List<Answer> currentAnswers;
    private int currentQuestionIndex;

    /**
     * @param quizService      handles quiz creation and scoring logic
     * @param resultRepository where completed results get persisted;
     *                          may be {@code null} if persistence isn't needed
     * @param username          the student taking the quiz, attached to the
     *                          resulting {@link Result} so history can be
     *                          looked up per-student
     */
    public QuizController(QuizService quizService, ResultRepository resultRepository, String username) {
        this.quizService = quizService;
        this.resultRepository = resultRepository;
        this.username = username;
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

    /**
     * Evaluates the completed quiz and persists the resulting {@link Result}
     * (if a repository was provided) before returning it for display.
     */
    public Result finishQuiz() {
        Result result = quizService.evaluateQuiz(currentQuiz, currentAnswers, username);
        if (resultRepository != null) {
            resultRepository.saveResult(result);
        }
        return result;
    }
}