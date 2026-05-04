package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterOutOfBoundsExceptionTest {

    // le message contient l'index invalide et la plage autorisée (0 à 15)
    @Test
    public void testMessageContientIndexEtPlage() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(16);
        assertTrue(e.getMessage().contains("16"));
        assertTrue(e.getMessage().contains("0"));
        assertTrue(e.getMessage().contains("15"));
    }

    // le message fonctionne aussi pour un index négatif
    @Test
    public void testMessageIndexNegatif() {
        RegisterOutOfBoundsException e = new RegisterOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    // RegisterOutOfBoundsException est bien une Exception vérifiée
    @Test
    public void testEstException() {
        assertTrue(new RegisterOutOfBoundsException(16) instanceof Exception);
    }
}
