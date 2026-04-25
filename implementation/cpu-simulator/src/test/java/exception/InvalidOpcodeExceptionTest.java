package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la classe InvalidOpcodeException.
 * Vérifie le message d'erreur et le type de l'exception.
 */
public class InvalidOpcodeExceptionTest {

    /**
     * Vérifie que le message contient bien la valeur de l'opcode inconnu.
     */
    @Test
    public void testMessage() {
        InvalidOpcodeException e = new InvalidOpcodeException(99);
        assertEquals("Opcode inconnu : 99", e.getMessage());
    }

    /**
     * Vérifie que le message varie selon la valeur du code passé au constructeur.
     */
    @Test
    public void testMessageVarie() {
        assertEquals("Opcode inconnu : 0",   new InvalidOpcodeException(0).getMessage());
        assertEquals("Opcode inconnu : 255", new InvalidOpcodeException(255).getMessage());
    }

    /**
     * Vérifie que InvalidOpcodeException est bien une RuntimeException (non vérifiée).
     */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new InvalidOpcodeException(0) instanceof RuntimeException);
    }
}
