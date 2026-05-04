package core;

import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemoryTest {

    private Memory memory;

    @BeforeEach
    public void setUp() throws Exception {
        memory = new Memory();
    }

    // écriture puis lecture → même valeur
    @Test
    public void testReadWriteByte() throws Exception {
        memory.write(100, (byte) 42);
        assertEquals((byte) 42, memory.read(100));
    }

    // écriture puis lecture d'un mot 16 bits → même valeur
    @Test
    public void testReadWriteWord() throws Exception {
        memory.writeWord(200, 1000);
        assertEquals(1000, memory.readWord(200));
    }

    // case non écrite → valeur par défaut = 0
    @Test
    public void testReadDefaultValue() throws Exception {
        assertEquals((byte) 0, memory.read(5000));
    }

    // adresses limites 0 et 65535 accessibles sans erreur
    @Test
    public void testBoundaryAddresses() throws Exception {
        memory.write(0,     (byte) 1);
        memory.write(65535, (byte) 2);
        assertEquals((byte) 1, memory.read(0));
        assertEquals((byte) 2, memory.read(65535));
    }

    // lecture hors [0, 65535] → MemoryOutOfBoundsException
    @Test
    public void testOutOfBoundsRead() throws Exception {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
    }

    // écriture hors [0, 65535] → MemoryOutOfBoundsException
    @Test
    public void testOutOfBoundsWrite() throws Exception {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
    }

    // reset → toutes les cases reviennent à 0
    @Test
    public void testReset() throws Exception {
        memory.write(100, (byte) 99);
        memory.reset();
        assertEquals((byte) 0, memory.read(100));
    }
}
