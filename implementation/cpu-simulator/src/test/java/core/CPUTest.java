package core;

import exception.InvalidOpcodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Opcodes : 0=BREAK 1=LOAD_CONST 2=LOAD_MEM 3=STORE 4=ADD 5=SUB
//           6=MUL 7=DIV 8=AND 9=OR 10=XOR 11=JUMP 12=BEQ 13=BNE
//           14=LOAD_INDEXED 15=STORE_INDEXED
public class CPUTest {

    private Memory memory;
    private RegisterFile registers;
    private CPU cpu;

    @BeforeEach
    public void setUp() throws Exception {
        memory    = new Memory();
        registers = new RegisterFile();
        cpu       = new CPU(memory, registers);
    }

    // écrit les octets donnés en mémoire à partir de l'adresse 0
    private void write(int... bytes) throws Exception {
        for (int i = 0; i < bytes.length; i++)
            memory.write(i, (byte) bytes[i]);
    }

    // BREAK seul → CPU s'arrête
    @Test
    public void testBreak() throws Exception {
        write(0);
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    // LOAD_CONST r0, 42 → r0 = 42
    @Test
    public void testLoadConst() throws Exception {
        write(1, 0, 42,  0);
        cpu.run();
        assertEquals((byte) 42, registers.get(0));
    }

    // LOAD_MEM r1, @1000 → r1 = valeur stockée à l'adresse 1000
    @Test
    public void testLoadMem() throws Exception {
        memory.write(1000, (byte) 77);
        write(2, 1, 0x03, 0xE8,  0);
        cpu.run();
        assertEquals((byte) 77, registers.get(1));
    }

    // STORE r2, @2000 → mémoire[2000] = valeur de r2
    @Test
    public void testStore() throws Exception {
        write(1,2,55,  3,2,0x07,0xD0,  0);
        cpu.run();
        assertEquals((byte) 55, memory.read(2000));
    }

    // ADD r2, r0, r1 → r2 = 10 + 20 = 30
    @Test
    public void testAdd() throws Exception {
        write(1,0,10,  1,1,20,  4,2,0,1,  0);
        cpu.run();
        assertEquals((byte) 30, registers.get(2));
    }

    // SUB r2, r0, r1 → r2 = 15 - 5 = 10
    @Test
    public void testSub() throws Exception {
        write(1,0,15,  1,1,5,  5,2,0,1,  0);
        cpu.run();
        assertEquals((byte) 10, registers.get(2));
    }

    // MUL r2, r3, r0, r1 → 6*7=42 → haut(r2)=0, bas(r3)=42
    @Test
    public void testMul() throws Exception {
        write(1,0,6,  1,1,7,  6,2,3,0,1,  0);
        cpu.run();
        assertEquals((byte) 0,  registers.get(2));
        assertEquals((byte) 42, registers.get(3));
    }

    // DIV r2, r3, r0, r1 → 17/5 → quotient(r2)=3, reste(r3)=2
    @Test
    public void testDiv() throws Exception {
        write(1,0,17,  1,1,5,  7,2,3,0,1,  0);
        cpu.run();
        assertEquals((byte) 3, registers.get(2));
        assertEquals((byte) 2, registers.get(3));
    }

    // AND r2, r0, r1 → 0b1100 & 0b1010 = 0b1000 = 8
    @Test
    public void testAnd() throws Exception {
        write(1,0,0b1100,  1,1,0b1010,  8,2,0,1,  0);
        cpu.run();
        assertEquals((byte) 0b1000, registers.get(2));
    }

    // OR r2, r0, r1 → 0b1100 | 0b0011 = 0b1111 = 15
    @Test
    public void testOr() throws Exception {
        write(1,0,0b1100,  1,1,0b0011,  9,2,0,1,  0);
        cpu.run();
        assertEquals((byte) 0b1111, registers.get(2));
    }

    // XOR r2, r0, r1 → 0b1100 ^ 0b1010 = 0b0110 = 6
    @Test
    public void testXor() throws Exception {
        write(1,0,0b1100,  1,1,0b1010,  10,2,0,1,  0);
        cpu.run();
        assertEquals((byte) 0b0110, registers.get(2));
    }

    // JUMP @5 → PC saute à l'adresse 5 (octets 3 et 4 sont ignorés)
    @Test
    public void testJump() throws Exception {
        write(11, 0, 5,  99, 99,  0);
        cpu.run();
        assertFalse(cpu.isRunning());
        assertEquals(6, cpu.getPC());
    }

    // BEQ pris : r0==r1 (7==7) → saut vers @15 → r2 reste 0
    @Test
    public void testBeqPris() throws Exception {
        write(1,0,7,  1,1,7,  12,0,1,0,15,  1,2,1,  0,  0);
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    // BEQ non pris : r0!=r1 (3!=5) → pas de saut → r2 = 1
    @Test
    public void testBeqNonPris() throws Exception {
        write(1,0,3,  1,1,5,  12,0,1,0,15,  1,2,1,  0,  0);
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    // BNE pris : r0!=r1 (3!=8) → saut vers @15 → r2 reste 0
    @Test
    public void testBnePris() throws Exception {
        write(1,0,3,  1,1,8,  13,0,1,0,15,  1,2,1,  0,  0);
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    // BNE non pris : r0==r1 (4==4) → pas de saut → r2 = 1
    @Test
    public void testBneNonPris() throws Exception {
        write(1,0,4,  1,1,4,  13,0,1,0,15,  1,2,1,  0,  0);
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    // LOAD_INDEXED r0, @1000, r1 → r0 = mémoire[1000 + 5] = 88
    @Test
    public void testLoadIndexed() throws Exception {
        memory.write(1005, (byte) 88);
        write(1,1,5,  14,0,0x03,0xE8,1,  0);
        cpu.run();
        assertEquals((byte) 88, registers.get(0));
    }

    // STORE_INDEXED r0, @1000, r1 → mémoire[1000 + 3] = 77
    @Test
    public void testStoreIndexed() throws Exception {
        write(1,0,77,  1,1,3,  15,0,0x03,0xE8,1,  0);
        cpu.run();
        assertEquals((byte) 77, memory.read(1003));
    }

    // opcode inconnu → InvalidOpcodeException
    @Test
    public void testOpcodeInvalide() throws Exception {
        write(99);
        assertThrows(InvalidOpcodeException.class, () -> cpu.run());
    }

    // reset → PC = 0 et running = false
    @Test
    public void testReset() throws Exception {
        write(1, 0, 5,  0);
        cpu.run();
        assertTrue(cpu.getPC() > 0);
        cpu.reset();
        assertEquals(0, cpu.getPC());
        assertFalse(cpu.isRunning());
    }
}
