package ch.epfl.test.professorTest.part1;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {
    @Test
    void checkArgumentSucceedsForTrue() {
        assertDoesNotThrow(() -> {
            ch.epfl.javelo.Preconditions.checkArgument(true);
        });
    }

    @Test
    void checkArgumentThrowsForFalse() {
        assertThrows(IllegalArgumentException.class, () -> {
            ch.epfl.javelo.Preconditions.checkArgument(false);
        });
    }
}
