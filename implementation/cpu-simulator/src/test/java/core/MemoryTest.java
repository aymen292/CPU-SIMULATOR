package core;

import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de la classe Memory.
 * Vérifie les lectures, les écritures, les valeurs par défaut,
 * les adresses limites et la détection des accès hors bornes.
 */
public class MemoryTest {

    private Memory memory;

    /**
     * Crée une nouvelle instance de Memory avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        memory = new Memory();
    }

    /**
     * Vérifie qu'un octet écrit à une adresse est bien relu à la même adresse.
     */
    @Test
    public void testReadWriteByte() {
        memory.write(100, (byte) 42);
        assertEquals((byte) 42, memory.read(100));
    }

    /**
     * Vérifie qu'un mot de 16 bits écrit est bien relu à la même adresse.
     */
    @Test
    public void testReadWriteWord() {
        memory.writeWord(200, 1000);
        assertEquals(1000, memory.readWord(200));
    }

    /**
     * Vérifie qu'une case non écrite vaut 0 par défaut.
     */
    @Test
    public void testReadDefaultValue() {
        assertEquals((byte) 0, memory.read(5000));
    }

    /**
     * Vérifie que les adresses limites 0 et 65 535 sont accessibles sans erreur.
     */
    @Test
    public void testBoundaryAddresses() {
        memory.write(0,     (byte) 1);
        memory.write(65535, (byte) 2);
        assertEquals((byte) 1, memory.read(0));
        assertEquals((byte) 2, memory.read(65535));
    }

    /**
     * Vérifie qu'une lecture hors des bornes [0, 65535] lève une MemoryOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsRead() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
    }

    /**
     * Vérifie qu'une écriture hors des bornes [0, 65535] lève une MemoryOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsWrite() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
    }

    /**
     * Vérifie que reset remet toutes les cases à zéro, y compris les cases précédemment écrites.
     */
    @Test
    public void testReset() {
        memory.write(100, (byte) 99);
        memory.reset();
        assertEquals((byte) 0, memory.read(100));
    }
}
