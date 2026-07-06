package com.ets.gui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuizFrame#isAnswerSelected(int)}.
 *
 * <p>This is the only piece of {@link QuizFrame}'s logic that can be tested
 * without instantiating Swing components, since the class as a whole is
 * excluded from JaCoCo coverage (event-driven, rendering-bound GUI code).
 * The check itself is a pure, stateless function of the selected-option
 * index returned by {@link QuestionPanel#getSelectedOption()}, so it is
 * pulled out and tested directly here.</p>
 */
class QuizFrameTest {

    @Test
    void isAnswerSelected_negativeOne_returnsFalse() {
        assertFalse(QuizFrame.isAnswerSelected(-1));
    }

    @ParameterizedTest(name = "selectedOption={0} is a valid selection")
    @ValueSource(ints = {0, 1, 2, 3, 10})
    void isAnswerSelected_nonNegativeIndex_returnsTrue(int selectedOption) {
        assertTrue(QuizFrame.isAnswerSelected(selectedOption));
    }
}
