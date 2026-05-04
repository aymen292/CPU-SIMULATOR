package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidOpcodeExceptionTest {

    // le message contient la valeur de l'opcode inconnu
    @Test
    public void testMessage() {
        InvalidOpcodeException e = new InvalidOpcodeException(99);
        assertEquals("Opcode inconnu : 99", e.getMessage());
    }

    // le message varie selon la valeur passée au constructeur
    @Test
    public void testMessageVarie() {
        assertEquals("Opcode inconnu : 0",   new InvalidOpcodeException(0).getMessage());
        assertEquals("Opcode inconnu : 255", new InvalidOpcodeException(255).getMessage());
    }

    // InvalidOpcodeException est bien une Exception vérifiée
    @Test
    public void testEstException() {
        assertTrue(new InvalidOpcodeException(0) instanceof Exception);
    }
}
