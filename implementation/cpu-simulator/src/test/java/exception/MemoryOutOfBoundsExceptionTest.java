package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la classe MemoryOutOfBoundsException.
 * Vérifie le contenu du message d'erreur et le type de l'exception.
 */
public class MemoryOutOfBoundsExceptionTest {

    /**
     * Vérifie que le message contient l'adresse hors limites.
     */
    @Test
    public void testMessageContientAdresse() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(70000);
        assertTrue(e.getMessage().contains("70000"));
    }

    /**
     * Vérifie que le message fonctionne aussi pour une adresse négative.
     */
    @Test
    public void testMessageAdresseNegative() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    /**
     * Vérifie que MemoryOutOfBoundsException est bien une RuntimeException (non vérifiée).
     */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new MemoryOutOfBoundsException(0) instanceof RuntimeException);
    }
}
