package core;

import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests pour la classe Memory.
 */
public class MemoryTest {

    private Memory memory;

    @BeforeEach
    public void setUp() {
        memory = new Memory();
    }

    @Test
    public void testReadWriteByte() {
        memory.write(100, (byte) 42);
        assertEquals((byte) 42, memory.read(100));
    }

    @Test
    public void testReadWriteWord() {
        memory.writeWord(200, 1000);
        assertEquals(1000, memory.readWord(200));
    }

    // une case non ecrite doit valoir 0 par defaut
    @Test
    public void testReadDefaultValue() {
        assertEquals((byte) 0, memory.read(5000));
    }

    // aux bords : 0 et 65535 doivent marcher
    @Test
    public void testBoundaryAddresses() {
        memory.write(0,     (byte) 1);
        memory.write(65535, (byte) 2);
        assertEquals((byte) 1, memory.read(0));
        assertEquals((byte) 2, memory.read(65535));
    }

    // au dela des bords : exception
    @Test
    public void testOutOfBoundsRead() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
    }

    @Test
    public void testOutOfBoundsWrite() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
    }

    @Test
    public void testReset() {
        memory.write(100, (byte) 99);
        memory.reset();
        assertEquals((byte) 0, memory.read(100));
    }
}
