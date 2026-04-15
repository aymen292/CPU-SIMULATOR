package core;

import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests pour la classe RegisterFile.
 */
public class RegisterFileTest {

    private RegisterFile registers;

    @BeforeEach
    public void setUp() {
        registers = new RegisterFile();
    }

    @Test
    public void testGetSet() {
        registers.set(5, (byte) 42);
        assertEquals((byte) 42, registers.get(5));
    }

    // on teste les 16 registres
    @Test
    public void testAllRegisters() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            registers.set(i, (byte) (i + 1));
        }
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) (i + 1), registers.get(i));
        }
    }

    // au debut, tous les registres valent 0
    @Test
    public void testDefaultValue() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) 0, registers.get(i));
        }
    }

    @Test
    public void testOutOfBoundsGet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(16));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(-1));
    }

    @Test
    public void testOutOfBoundsSet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(16, (byte) 0));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(-1, (byte) 0));
    }

    @Test
    public void testReset() {
        registers.set(3, (byte) 99);
        registers.reset();
        assertEquals((byte) 0, registers.get(3));
    }
}
