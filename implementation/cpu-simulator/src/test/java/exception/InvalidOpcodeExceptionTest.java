package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests pour InvalidOpcodeException.
 */
public class InvalidOpcodeExceptionTest {

    // le message doit contenir le code
    @Test
    public void testMessage() {
        InvalidOpcodeException e = new InvalidOpcodeException(99);
        assertEquals("Opcode inconnu : 99", e.getMessage());
    }

    // le message varie selon le code
    @Test
    public void testMessageVarie() {
        assertEquals("Opcode inconnu : 0",   new InvalidOpcodeException(0).getMessage());
        assertEquals("Opcode inconnu : 255", new InvalidOpcodeException(255).getMessage());
    }

    // c'est bien une RuntimeException (non verifiee)
    @Test
    public void testEstRuntimeException() {
        assertTrue(new InvalidOpcodeException(0) instanceof RuntimeException);
    }
}
