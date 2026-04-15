package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests pour RegisterOutOfBoundsException.
 */
public class RegisterOutOfBoundsExceptionTest {

    // le message doit contenir l'index et la plage 0 a 15
    @Test
    public void testMessageContientIndexEtPlage() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(16);
        assertTrue(e.getMessage().contains("16"));
        assertTrue(e.getMessage().contains("0"));
        assertTrue(e.getMessage().contains("15"));
    }

    // marche aussi pour les index negatifs
    @Test
    public void testMessageIndexNegatif() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    // c'est bien une RuntimeException
    @Test
    public void testEstRuntimeException() {
        assertTrue(new RegisterOutOfBoundsException(16) instanceof RuntimeException);
    }
}
