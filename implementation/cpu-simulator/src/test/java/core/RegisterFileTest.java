package core;

import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterFileTest {

    private RegisterFile registers;

    @BeforeEach
    public void setUp() throws Exception {
        registers = new RegisterFile();
    }

    // écriture puis lecture → même valeur
    @Test
    public void testGetSet() throws Exception {
        registers.set(5, (byte) 42);
        assertEquals((byte) 42, registers.get(5));
    }

    // les 16 registres fonctionnent indépendamment
    @Test
    public void testAllRegisters() throws Exception {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++)
            registers.set(i, (byte) (i + 1));
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++)
            assertEquals((byte) (i + 1), registers.get(i));
    }

    // à la construction tous les registres valent 0
    @Test
    public void testDefaultValue() throws Exception {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++)
            assertEquals((byte) 0, registers.get(i));
    }

    // indice hors [0, 15] en lecture → RegisterOutOfBoundsException
    @Test
    public void testOutOfBoundsGet() throws Exception {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(16));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(-1));
    }

    // indice hors [0, 15] en écriture → RegisterOutOfBoundsException
    @Test
    public void testOutOfBoundsSet() throws Exception {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(16, (byte) 0));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(-1, (byte) 0));
    }

    // reset → tous les registres reviennent à 0
    @Test
    public void testReset() throws Exception {
        registers.set(3, (byte) 99);
        registers.reset();
        assertEquals((byte) 0, registers.get(3));
    }
}
