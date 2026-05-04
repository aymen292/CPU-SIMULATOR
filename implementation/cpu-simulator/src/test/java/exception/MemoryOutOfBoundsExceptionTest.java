package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryOutOfBoundsExceptionTest {

    // le message contient l'adresse hors limites
    @Test
    public void testMessageContientAdresse() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(70000);
        assertTrue(e.getMessage().contains("70000"));
    }

    // le message fonctionne aussi pour une adresse négative
    @Test
    public void testMessageAdresseNegative() {
        MemoryOutOfBoundsException e = new MemoryOutOfBoundsException(-1);
        assertTrue(e.getMessage().contains("-1"));
    }

    // MemoryOutOfBoundsException est bien une Exception vérifiée
    @Test
    public void testEstException() {
        assertTrue(new MemoryOutOfBoundsException(0) instanceof Exception);
    }
}
