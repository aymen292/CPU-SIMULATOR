package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires pour InvalidOpcodeException.
 */
public class InvalidOpcodeExceptionTest {

    /** Le message doit contenir le code invalide */
    @Test
    public void testMessage() {
        InvalidOpcodeException e = new InvalidOpcodeException(99);
        assertEquals("Opcode inconnu : 99", e.getMessage());
    }

    /** Le message varie selon le code passé */
    @Test
    public void testMessageVarie() {
        assertEquals("Opcode inconnu : 0",   new InvalidOpcodeException(0).getMessage());
        assertEquals("Opcode inconnu : 255", new InvalidOpcodeException(255).getMessage());
    }

    /** InvalidOpcodeException est une RuntimeException (non vérifiée) */
    @Test
    public void testEstRuntimeException() {
        assertTrue(new InvalidOpcodeException(0) instanceof RuntimeException);
    }
}
