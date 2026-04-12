package core;

import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires pour la classe RegisterFile.
 */
public class RegisterFileTest {

    private RegisterFile registers;

    @BeforeEach
    public void setUp() {
        registers = new RegisterFile();
    }

    /** set(5, 42) puis get(5) doit retourner 42 */
    @Test
    public void testGetSet() {
        registers.set(5, (byte) 42);
        assertEquals((byte) 42, registers.get(5));
    }

    /** Les 16 registres (r0 à r15) sont tous accessibles */
    @Test
    public void testAllRegisters() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            registers.set(i, (byte) (i + 1));
        }
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) (i + 1), registers.get(i));
        }
    }

    /** Tous les registres valent 0 à la création */
    @Test
    public void testDefaultValue() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) 0, registers.get(i));
        }
    }

    /** get(16) et get(-1) → RegisterOutOfBoundsException */
    @Test
    public void testOutOfBoundsGet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(16));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(-1));
    }

    /** set(16, ...) et set(-1, ...) → RegisterOutOfBoundsException */
    @Test
    public void testOutOfBoundsSet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(16, (byte) 0));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(-1, (byte) 0));
    }

    /** reset() remet tous les registres à zéro */
    @Test
    public void testReset() {
        registers.set(3, (byte) 99);
        registers.reset();
        assertEquals((byte) 0, registers.get(3));
    }
}
