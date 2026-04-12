package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires pour MemoryOutOfBoundsException.
 */
public class MemoryOutOfBoundsExceptionTest {

    /** Le message doit contenir l'adresse hors limites */
    @Test
    public void testMessageContientAdresse() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(70000);
        assertTrue(e.getMessage().contains("70000"));
    }

    /** Le message fonctionne aussi pour les adresses négatives */
    @Test
    public void testMessageAdresseNegative() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    /** MemoryOutOfBoundsException est une RuntimeException (non vérifiée) */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new MemoryOutOfBoundsException(0) instanceof RuntimeException);
    }
}
