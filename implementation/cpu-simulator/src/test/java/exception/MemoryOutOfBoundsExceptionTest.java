package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests pour MemoryOutOfBoundsException.
 */
public class MemoryOutOfBoundsExceptionTest {

    // le message doit contenir l'adresse hors limites
    @Test
    public void testMessageContientAdresse() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(70000);
        assertTrue(e.getMessage().contains("70000"));
    }

    // marche aussi pour les adresses negatives
    @Test
    public void testMessageAdresseNegative() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    // c'est bien une RuntimeException
    @Test
    public void testEstRuntimeException() {
        assertTrue(new MemoryOutOfBoundsException(0) instanceof RuntimeException);
    }
}
