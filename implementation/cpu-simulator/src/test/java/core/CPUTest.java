package core;

import exception.InvalidOpcodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests pour la classe CPU. Pour chaque test on ecrit les opcodes direct
 * en memoire (sans passer par l'assembleur) et on verifie l'etat apres run.
 *
 * Rappel des opcodes :
 *   0=BREAK  1=LOAD_CONST  2=LOAD_MEM  3=STORE
 *   4=ADD    5=SUB         6=MUL       7=DIV
 *   8=AND    9=OR         10=XOR      11=JUMP
 *  12=BEQ   13=BNE        14=LOAD_INDEXED  15=STORE_INDEXED
 */
public class CPUTest {

    private Memory       memory;
    private RegisterFile registers;
    private CPU          cpu;

    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        registers = new RegisterFile();
        cpu       = new CPU(memory, registers);
    }

    // BREAK tout seul doit arreter le CPU
    @Test
    public void testBreak() {
        memory.write(0, (byte) 0); // BREAK
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    // LOAD_CONST r0, 42 -> r0 = 42
    @Test
    public void testLoadConst() {
        memory.write(0, (byte) 1);  // LOAD_CONST
        memory.write(1, (byte) 0);  // dest = r0
        memory.write(2, (byte) 42); // valeur
        memory.write(3, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 42, registers.get(0));
    }

    // LOAD_MEM r1, @1000 -> r1 = memoire[1000]
    // 1000 en big-endian = 0x03, 0xE8
    @Test
    public void testLoadMem() {
        memory.write(1000, (byte) 77); // valeur en memoire
        memory.write(0, (byte) 2);     // LOAD_MEM
        memory.write(1, (byte) 1);     // dest = r1
        memory.write(2, (byte) 0x03);
        memory.write(3, (byte) 0xE8);
        memory.write(4, (byte) 0);     // BREAK
        cpu.run();
        assertEquals((byte) 77, registers.get(1));
    }

    // STORE r2, @2000 -> memoire[2000] = r2
    // 2000 en big-endian = 0x07, 0xD0
    @Test
    public void testStore() {
        memory.write(0, (byte) 1);    // LOAD_CONST r2, 55
        memory.write(1, (byte) 2);
        memory.write(2, (byte) 55);
        memory.write(3, (byte) 3);    // STORE r2, @2000
        memory.write(4, (byte) 2);
        memory.write(5, (byte) 0x07);
        memory.write(6, (byte) 0xD0);
        memory.write(7, (byte) 0);    // BREAK
        cpu.run();
        assertEquals((byte) 55, memory.read(2000));
    }

    // ADD r2, r0, r1 : r2 = 10 + 20 = 30
    @Test
    public void testAdd() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 10
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 10);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 20
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 20);
        memory.write(6,  (byte) 4);  // ADD r2, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 30, registers.get(2));
    }

    // SUB r2, r0, r1 : r2 = 15 - 5 = 10
    @Test
    public void testSub() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 15
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 15);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 5
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 5);  // SUB r2, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 10, registers.get(2));
    }

    // MUL r2, r3, r0, r1 : 6 * 7 = 42 -> high=0, low=42
    @Test
    public void testMul() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 6
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 6);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 7
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 7);
        memory.write(6,  (byte) 6);  // MUL r2, r3, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 3);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 0,  registers.get(2)); // octet haut
        assertEquals((byte) 42, registers.get(3)); // octet bas
    }

    // DIV r2, r3, r0, r1 : 17 / 5 -> quotient 3, reste 2
    @Test
    public void testDiv() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 17
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 17);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 5
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 7);  // DIV r2, r3, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 3);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 3, registers.get(2)); // quotient
        assertEquals((byte) 2, registers.get(3)); // reste
    }

    // AND r2, r0, r1 : 0b1100 & 0b1010 = 0b1000 = 8
    @Test
    public void testAnd() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 12
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 10
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b1010);
        memory.write(6,  (byte) 8);  // AND r2, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 0b1000, registers.get(2));
    }

    // OR r2, r0, r1 : 0b1100 | 0b0011 = 0b1111 = 15
    @Test
    public void testOr() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 12
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 3
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b0011);
        memory.write(6,  (byte) 9);  // OR r2, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 0b1111, registers.get(2));
    }

    // XOR r2, r0, r1 : 0b1100 ^ 0b1010 = 0b0110 = 6
    @Test
    public void testXor() {
        memory.write(0,  (byte) 1);  // LOAD_CONST r0, 12
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 0b1100);
        memory.write(3,  (byte) 1);  // LOAD_CONST r1, 10
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 0b1010);
        memory.write(6,  (byte) 10); // XOR r2, r0, r1
        memory.write(7,  (byte) 2);
        memory.write(8,  (byte) 0);
        memory.write(9,  (byte) 1);
        memory.write(10, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 0b0110, registers.get(2));
    }

    // JUMP @5 : on saute par dessus les adresses 3 et 4
    @Test
    public void testJump() {
        memory.write(0, (byte) 11); // JUMP
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 99); // doit pas etre execute
        memory.write(4, (byte) 99); // doit pas etre execute
        memory.write(5, (byte) 0);  // BREAK
        cpu.run();
        assertFalse(cpu.isRunning());
        assertEquals(6, cpu.getPC()); // le PC avance d'un cran apres BREAK
    }

    // BEQ r0, r1, @15 avec r0 == r1 : saut pris
    @Test
    public void testBeqPris() {
        memory.write(0,  (byte) 1);    // LOAD_CONST r0, 7
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 7);
        memory.write(3,  (byte) 1);    // LOAD_CONST r1, 7
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 7);
        memory.write(6,  (byte) 12);   // BEQ r0, r1, @15
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 (saute)
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (cible)
        cpu.run();
        assertEquals((byte) 0, registers.get(2)); // r2 = 0 => saut pris
    }

    // BEQ r0, r1, @15 avec r0 != r1 : saut non pris
    @Test
    public void testBeqNonPris() {
        memory.write(0,  (byte) 1);    // LOAD_CONST r0, 3
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 3);
        memory.write(3,  (byte) 1);    // LOAD_CONST r1, 5
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 5);
        memory.write(6,  (byte) 12);   // BEQ r0, r1, @15
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 (execute)
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (pas atteinte)
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    // BNE r0, r1, @15 avec r0 != r1 : saut pris
    @Test
    public void testBnePris() {
        memory.write(0,  (byte) 1);    // LOAD_CONST r0, 3
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 3);
        memory.write(3,  (byte) 1);    // LOAD_CONST r1, 8
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 8);
        memory.write(6,  (byte) 13);   // BNE r0, r1, @15
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 (saute)
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (cible)
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    // BNE r0, r1, @15 avec r0 == r1 : saut non pris
    @Test
    public void testBneNonPris() {
        memory.write(0,  (byte) 1);    // LOAD_CONST r0, 4
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 4);
        memory.write(3,  (byte) 1);    // LOAD_CONST r1, 4
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 4);
        memory.write(6,  (byte) 13);   // BNE r0, r1, @15
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0);
        memory.write(10, (byte) 15);
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 (execute)
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (pas atteinte)
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    // LOAD_INDEXED r0, @1000, r1 : charge memoire[1000 + r1] dans r0
    // r1 = 5, memoire[1005] = 88 -> r0 doit valoir 88
    @Test
    public void testLoadIndexed() {
        memory.write(1005, (byte) 88);  // valeur en memoire
        memory.write(0, (byte) 1);      // LOAD_CONST r1, 5
        memory.write(1, (byte) 1);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 14);     // LOAD_INDEXED r0, @1000, r1
        memory.write(4, (byte) 0);
        memory.write(5, (byte) 0x03);   // base high (1000 = 0x03E8)
        memory.write(6, (byte) 0xE8);   // base low
        memory.write(7, (byte) 1);      // offset = r1
        memory.write(8, (byte) 0);      // BREAK
        cpu.run();
        assertEquals((byte) 88, registers.get(0));
    }

    // STORE_INDEXED r0, @1000, r1 : ecrit r0 a memoire[1000 + r1]
    // r0 = 77, r1 = 3 -> memoire[1003] = 77
    @Test
    public void testStoreIndexed() {
        memory.write(0,  (byte) 1);     // LOAD_CONST r0, 77
        memory.write(1,  (byte) 0);
        memory.write(2,  (byte) 77);
        memory.write(3,  (byte) 1);     // LOAD_CONST r1, 3
        memory.write(4,  (byte) 1);
        memory.write(5,  (byte) 3);
        memory.write(6,  (byte) 15);    // STORE_INDEXED r0, @1000, r1
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 0x03);
        memory.write(9,  (byte) 0xE8);
        memory.write(10, (byte) 1);
        memory.write(11, (byte) 0);     // BREAK
        cpu.run();
        assertEquals((byte) 77, memory.read(1003));
    }

    // un opcode inconnu (ex : 99) doit lever InvalidOpcodeException
    @Test
    public void testOpcodeInvalide() {
        memory.write(0, (byte) 99); // opcode qui n'existe pas
        assertThrows(InvalidOpcodeException.class, () -> cpu.run());
    }

    // reset() remet le PC a 0 et arrete le CPU
    @Test
    public void testReset() {
        memory.write(0, (byte) 1);  // LOAD_CONST r0, 5
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 0);  // BREAK
        cpu.run();
        assertTrue(cpu.getPC() > 0);
        cpu.reset();
        assertEquals(0, cpu.getPC());
        assertFalse(cpu.isRunning());
    }
}
