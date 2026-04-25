package core;

import exception.RegisterOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de la classe RegisterFile.
 * Vérifie les lectures, les écritures, les valeurs par défaut,
 * le parcours des 16 registres et la détection des indices hors bornes.
 */
public class RegisterFileTest {

    private RegisterFile registers;

    /**
     * Crée une nouvelle instance de RegisterFile avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        registers = new RegisterFile();
    }

    /**
     * Vérifie qu'une valeur écrite dans un registre est bien relue.
     */
    @Test
    public void testGetSet() {
        registers.set(5, (byte) 42);
        assertEquals((byte) 42, registers.get(5));
    }

    /**
     * Vérifie que les 16 registres (r0 à r15) fonctionnent indépendamment.
     */
    @Test
    public void testAllRegisters() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            registers.set(i, (byte) (i + 1));
        }
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) (i + 1), registers.get(i));
        }
    }

    /**
     * Vérifie que tous les registres valent 0 à la construction.
     */
    @Test
    public void testDefaultValue() {
        for (int i = 0; i < RegisterFile.NUM_REGISTERS; i++) {
            assertEquals((byte) 0, registers.get(i));
        }
    }

    /**
     * Vérifie qu'une lecture avec un indice hors de [0, 15] lève une RegisterOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsGet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(16));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.get(-1));
    }

    /**
     * Vérifie qu'une écriture avec un indice hors de [0, 15] lève une RegisterOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsSet() {
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(16, (byte) 0));
        assertThrows(RegisterOutOfBoundsException.class, () -> registers.set(-1, (byte) 0));
    }

    /**
     * Vérifie que reset remet tous les registres à zéro, y compris les registres précédemment écrits.
     */
    @Test
    public void testReset() {
        registers.set(3, (byte) 99);
        registers.reset();
        assertEquals((byte) 0, registers.get(3));
    }
}
