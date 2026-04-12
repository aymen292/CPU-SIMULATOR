package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires pour RegisterOutOfBoundsException.
 */
public class RegisterOutOfBoundsExceptionTest {

    /** Le message doit contenir l'index invalide et la plage autorisée (0 à 15) */
    @Test
    public void testMessageContientIndexEtPlage() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(16);
        assertTrue(e.getMessage().contains("16"));
        assertTrue(e.getMessage().contains("0"));
        assertTrue(e.getMessage().contains("15"));
    }

    /** Le message fonctionne aussi pour les index négatifs */
    @Test
    public void testMessageIndexNegatif() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    /** RegisterOutOfBoundsException est une RuntimeException (non vérifiée) */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new RegisterOutOfBoundsException(16) instanceof RuntimeException);
    }
}
