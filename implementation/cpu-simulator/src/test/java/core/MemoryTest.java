package core;

import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires pour la classe Memory.
 */
public class MemoryTest {

    private Memory memory;

    @BeforeEach
    public void setUp() {
        memory = new Memory();
    }

    /** Écriture puis lecture d'un octet à l'adresse 100 */
    @Test
    public void testReadWriteByte() {
        memory.write(100, (byte) 42);
        assertEquals((byte) 42, memory.read(100));
    }

    /** Écriture puis lecture d'un mot 16 bits (big-endian) */
    @Test
    public void testReadWriteWord() {
        memory.writeWord(200, 1000);
        assertEquals(1000, memory.readWord(200));
    }

    /** Une adresse non écrite doit valoir 0 par défaut */
    @Test
    public void testReadDefaultValue() {
        assertEquals((byte) 0, memory.read(5000));
    }

    /** Lecture et écriture aux adresses limites : 0 et 65535 */
    @Test
    public void testBoundaryAddresses() {
        memory.write(0,     (byte) 1);
        memory.write(65535, (byte) 2);
        assertEquals((byte) 1, memory.read(0));
        assertEquals((byte) 2, memory.read(65535));
    }

    /** Lecture à l'adresse 65536 et -1 → MemoryOutOfBoundsException */
    @Test
    public void testOutOfBoundsRead() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
    }

    /** Écriture à l'adresse 65536 et -1 → MemoryOutOfBoundsException */
    @Test
    public void testOutOfBoundsWrite() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
    }

    /** reset() remet toute la mémoire à zéro */
    @Test
    public void testReset() {
        memory.write(100, (byte) 99);
        memory.reset();
        assertEquals((byte) 0, memory.read(100));
    }
}
