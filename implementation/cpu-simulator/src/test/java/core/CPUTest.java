package core;

import exception.InvalidOpcodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la classe CPU.
 * Les opcodes sont écrits directement en mémoire (sans passer par l'assembleur)
 * et l'état des registres ou de la mémoire est vérifié après l'appel à run().
 *
 * Codes utilisés :
 *  0 = BREAK, 1 = LOAD_CONST, 2 = LOAD_MEM, 3 = STORE, 4 = ADD, 5 = SUB,
 *  6 = MUL, 7 = DIV, 8 = AND, 9 = OR, 10 = XOR, 11 = JUMP, 12 = BEQ,
 *  13 = BNE, 14 = LOAD_INDEXED, 15 = STORE_INDEXED.
 */
public class CPUTest {

    private Memory memory;
    private RegisterFile registers;
    private CPU cpu;

    /**
     * Crée une mémoire, un banc de registres et un CPU vierges avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        memory = new Memory();
        registers = new RegisterFile();
        cpu = new CPU(memory, registers);
    }

    /**
     * Vérifie que BREAK seul arrête le CPU (isRunning retourne false).
     */
    @Test
    public void testBreak() {
        memory.write(0, (byte) 0);
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    /**
     * Vérifie que LOAD_CONST r0, 42 place bien 42 dans r0.
     */
    @Test
    public void testLoadConst() {
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 42);
        memory.write(3, (byte) 0);
        cpu.run();
        assertEquals((byte) 42, registers.get(0));
    }

    /**
     * Vérifie que LOAD_MEM r1, @1000 charge en r1 la valeur stockée à l'adresse 1000.
     * 1000 en big-endian = 0x03, 0xE8.
     */
    @Test
    public void testLoadMem() {
        memory.write(1000, (byte) 77);
        memory.write(0, (byte) 2);
        memory.write(1, (byte) 1);
        memory.write(2, (byte) 0x03);
        memory.write(3, (byte) 0xE8);
        memory.write(4, (byte) 0);
        cpu.run();
        assertEquals((byte) 77, registers.get(1));
    }

    /**
     * Vérifie que STORE r2, @2000 écrit la valeur de r2 à l'adresse 2000.
     * 2000 en big-endian = 0x07, 0xD0.
     */
    @Test
    public void testStore() {
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 2);
        memory.write(2, (byte) 55);
        memory.write(3, (byte) 3);
        memory.write(4, (byte) 2);
        memory.write(5, (byte) 0x07);
        memory.write(6, (byte) 0xD0);
        memory.write(7, (byte) 0);
        cpu.run();
        assertEquals((byte) 55, memory.read(2000));
    }

    /**
     * Vérifie que ADD r2, r0, r1 calcule bien r2 = 10 + 20 = 30.
     */
    @Test
    public void testAdd() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 10);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 20);
        memory.write(6,  (byte) 4);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);
        cpu.run();
        assertEquals((byte) 30, registers.get(2));
    }

    /**
     * Vérifie que SUB r2, r0, r1 calcule bien r2 = 15 - 5 = 10.
     */
    @Test
    public void testSub() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 15);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 5);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);
        cpu.run();
        assertEquals((byte) 10, registers.get(2));
    }

    /**
     * Vérifie que MUL r2, r3, r0, r1 calcule 6 * 7 = 42 :
     * octet haut dans r2 = 0, octet bas dans r3 = 42.
     */
    @Test
    public void testMul() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 6);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 7);
        memory.write(6,  (byte) 6);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 3);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);
        cpu.run();
        assertEquals((byte) 0,  registers.get(2));
        assertEquals((byte) 42, registers.get(3));
    }

    /**
     * Vérifie que DIV r2, r3, r0, r1 calcule 17 / 5 :
     * quotient dans r2 = 3, reste dans r3 = 2.
     */
    @Test
    public void testDiv() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 17);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 7);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 3);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);
        cpu.run();
        assertEquals((byte) 3, registers.get(2));
        assertEquals((byte) 2, registers.get(3));
    }

    /**
     * Vérifie que AND r2, r0, r1 calcule 0b1100 & 0b1010 = 0b1000 = 8.
     */
    @Test
    public void testAnd() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b1010);
        memory.write(6,  (byte) 8);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);
        cpu.run();
        assertEquals((byte) 0b1000, registers.get(2));
    }

    /**
     * Vérifie que OR r2, r0, r1 calcule 0b1100 | 0b0011 = 0b1111 = 15.
     */
    @Test
    public void testOr() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b0011);
        memory.write(6,  (byte) 9);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);
        cpu.run();
        assertEquals((byte) 0b1111, registers.get(2));
    }

    /**
     * Vérifie que XOR r2, r0, r1 calcule 0b1100 ^ 0b1010 = 0b0110 = 6.
     */
    @Test
    public void testXor() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b1010);
        memory.write(6,  (byte) 10);
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);
        cpu.run();
        assertEquals((byte) 0b0110, registers.get(2));
    }

    /**
     * Vérifie que JUMP @5 redirige le PC vers l'adresse 5, sautant les octets 3 et 4.
     */
    @Test
    public void testJump() {
        memory.write(0, (byte) 11);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 99);
        memory.write(4, (byte) 99);
        memory.write(5, (byte) 0);
        cpu.run();
        assertFalse(cpu.isRunning());
        assertEquals(6, cpu.getPC());
    }

    /**
     * Vérifie que BEQ prend le saut quand r0 == r1 : r2 doit rester à 0.
     */
    @Test
    public void testBeqPris() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 7);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 7);
        memory.write(6,  (byte) 12);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);
        memory.write(15, (byte) 0);
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    /**
     * Vérifie que BEQ ne prend pas le saut quand r0 != r1 : r2 doit valoir 1.
     */
    @Test
    public void testBeqNonPris() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 3);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 12);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);
        memory.write(15, (byte) 0);
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    /**
     * Vérifie que BNE prend le saut quand r0 != r1 : r2 doit rester à 0.
     */
    @Test
    public void testBnePris() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 3);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 8);
        memory.write(6,  (byte) 13);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);
        memory.write(15, (byte) 0);
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    /**
     * Vérifie que BNE ne prend pas le saut quand r0 == r1 : r2 doit valoir 1.
     */
    @Test
    public void testBneNonPris() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 4);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 4);
        memory.write(6,  (byte) 13);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);
        memory.write(15, (byte) 0);
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    /**
     * Vérifie que LOAD_INDEXED charge mémoire[1000 + r1] dans r0.
     * r1 = 5, mémoire[1005] = 88, donc r0 doit valoir 88.
     */
    @Test
    public void testLoadIndexed() {
        memory.write(1005, (byte) 88);
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 1);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 14);
        memory.write(4, (byte) 0);
        memory.write(5, (byte) 0x03);
        memory.write(6, (byte) 0xE8);
        memory.write(7, (byte) 1);
        memory.write(8, (byte) 0);
        cpu.run();
        assertEquals((byte) 88, registers.get(0));
    }

    /**
     * Vérifie que STORE_INDEXED écrit r0 à mémoire[1000 + r1].
     * r0 = 77, r1 = 3, donc mémoire[1003] doit valoir 77.
     */
    @Test
    public void testStoreIndexed() {
        memory.write(0,  (byte) 1);
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 77);
        memory.write(3,  (byte) 1);
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 3);
        memory.write(6,  (byte) 15);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 0x03);
        memory.write(9,  (byte) 0xE8);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);
        cpu.run();
        assertEquals((byte) 77, memory.read(1003));
    }

    /**
     * Vérifie qu'un opcode inconnu (ex. 99) lève une InvalidOpcodeException.
     */
    @Test
    public void testOpcodeInvalide() {
        memory.write(0, (byte) 99);
        assertThrows(InvalidOpcodeException.class, () -> cpu.run());
    }

    /**
     * Vérifie que reset repositionne le PC à 0 et passe running à false.
     */
    @Test
    public void testReset() {
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 0);
        cpu.run();
        assertTrue(cpu.getPC() > 0);
        cpu.reset();
        assertEquals(0, cpu.getPC());
        assertFalse(cpu.isRunning());
    }
}
