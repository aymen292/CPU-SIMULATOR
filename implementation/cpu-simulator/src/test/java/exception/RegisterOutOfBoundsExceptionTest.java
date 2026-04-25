package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la classe RegisterOutOfBoundsException.
 * Vérifie le contenu du message d'erreur et le type de l'exception.
 */
public class RegisterOutOfBoundsExceptionTest {

    /**
     * Vérifie que le message contient l'index invalide et la plage autorisée (0 à 15).
     */
    @Test
    public void testMessageContientIndexEtPlage() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(16);
        assertTrue(e.getMessage().contains("16"));
        assertTrue(e.getMessage().contains("0"));
        assertTrue(e.getMessage().contains("15"));
    }

    /**
     * Vérifie que le message fonctionne aussi pour un index négatif.
     */
    @Test
    public void testMessageIndexNegatif() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    /**
     * Vérifie que RegisterOutOfBoundsException est bien une RuntimeException (non vérifiée).
     */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new RegisterOutOfBoundsException(16) instanceof RuntimeException);
    }
}
