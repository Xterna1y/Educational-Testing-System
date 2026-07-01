package com.ets.services;

import com.ets.builder.QuizBuilder;
import com.ets.model.Answer;
import com.ets.model.Question;
import com.ets.model.Quiz;
import com.ets.model.Result;
import com.ets.strategy.QuizStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Core business logic for quiz operations.
 * Creates quizzes, evaluates answers, and calculates results.
 */
public final class QuizService {

    private static final int POINTS_PER_QUESTION = 10;
    private static final int DEFAULT_TIME_LIMIT_MINUTES = 30;
    private static final boolean DEFAULT_SHUFFLE_QUESTIONS = false;
    private static final boolean DEFAULT_SHOW_FEEDBACK_IMMEDIATELY = true;
    private static final int UUID_LENGTH = 8;

    public Quiz createQuiz(String title, String description,
                           List<Question> questions,
                           QuizStrategy strategy) {
        String quizId = UUID.randomUUID().toString().substring(0, UUID_LENGTH);
        List<Question> selected = strategy.selectQuestions(questions, questions.size());

        return new QuizBuilder()
                .withQuizId(quizId)
                .withTitle(title)
                .withDescription(description)
                .addQuestions(selected)
                .withTimeLimitMinutes(DEFAULT_TIME_LIMIT_MINUTES)
                .withShuffleQuestions(DEFAULT_SHUFFLE_QUESTIONS)
                .withShowFeedbackImmediately(DEFAULT_SHOW_FEEDBACK_IMMEDIATELY)
                .build();
    }

    /**
     * Evaluates a completed quiz attempt and builds the resulting {@link Result}.
     *
     * @param quiz     the quiz that was taken
     * @param answers  the student's submitted answers
     * @param username the student who took the quiz, stored on the Result
     *                 so results can later be looked up per-student
     */
    public Result evaluateQuiz(Quiz quiz, List<Answer> answers, String username) {
        Objects.requireNonNull(quiz);
        Objects.requireNonNull(answers);
        Objects.requireNonNull(username);
        List<Question> questions = quiz.getQuestions();
        int score = 0;
        for (Answer answer : answers) {
            Question question = findQuestion(questions, answer.getQuestionId());
            if (question != null && answer.isCorrectFor(question)) {
                score += POINTS_PER_QUESTION;
            }
        }
        int totalPoints = quiz.getTotalPoints();
        return new Result(username, quiz.getQuizId(), answers, score, totalPoints);
    }

    public List<Answer> gradeAnswers(Quiz quiz, List<Integer> selectedIndices) {
        Objects.requireNonNull(quiz);
        Objects.requireNonNull(selectedIndices);
        List<Question> questions = quiz.getQuestions();
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < Math.min(questions.size(), selectedIndices.size()); i++) {
            Question q = questions.get(i);
            answers.add(new Answer(q.getId(), selectedIndices.get(i)));
        }
        return Collections.unmodifiableList(new ArrayList<>(answers));
    }

    private Question findQuestion(List<Question> questions, String questionId) {
        for (Question q : questions) {
            if (q.getId().equals(questionId)) {
                return q;
            }
        }
        return null;
    }
}