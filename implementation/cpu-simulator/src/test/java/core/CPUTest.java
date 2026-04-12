package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires pour la classe CPU.
 * Chaque test écrit directement les opcodes en mémoire et vérifie l'état après exécution.
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

    // ------------------------------------------------------------------ BREAK

    /**
     * BREAK (opcode 0) doit arrêter l'exécution immédiatement.
     * Programme : [0]
     */
    @Test
    public void testBreak() {
        memory.write(0, (byte) 0); // BREAK
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    // -------------------------------------------------------------- LOAD_CONST

    /**
     * LOAD_CONST r0, 42 charge la valeur 42 dans r0.
     * Programme : [1, 0, 42, 0]
     */
    @Test
    public void testLoadConst() {
        memory.write(0, (byte) 1);  // LOAD_CONST
        memory.write(1, (byte) 0);  // dest = r0
        memory.write(2, (byte) 42); // valeur = 42
        memory.write(3, (byte) 0);  // BREAK
        cpu.run();
        assertEquals((byte) 42, registers.get(0));
    }

    // ---------------------------------------------------------------- LOAD_MEM

    /**
     * LOAD_MEM r1, @1000 charge en r1 la valeur stockée à l'adresse 1000.
     * 1000 en big-endian = 0x03, 0xE8
     * Programme : [2, 1, 0x03, 0xE8, 0]
     */
    @Test
    public void testLoadMem() {
        memory.write(1000, (byte) 77); // valeur cible
        memory.write(0, (byte) 2);    // LOAD_MEM
        memory.write(1, (byte) 1);    // dest = r1
        memory.write(2, (byte) 0x03); // adresse haute (1000 >> 8)
        memory.write(3, (byte) 0xE8); // adresse basse (1000 & 0xFF)
        memory.write(4, (byte) 0);    // BREAK
        cpu.run();
        assertEquals((byte) 77, registers.get(1));
    }

    // ------------------------------------------------------------------- STORE

    /**
     * STORE r2, @2000 écrit la valeur de r2 à l'adresse 2000.
     * 2000 en big-endian = 0x07, 0xD0
     */
    @Test
    public void testStore() {
        memory.write(0, (byte) 1);    // LOAD_CONST
        memory.write(1, (byte) 2);    // dest = r2
        memory.write(2, (byte) 55);   // valeur = 55
        memory.write(3, (byte) 3);    // STORE
        memory.write(4, (byte) 2);    // src = r2
        memory.write(5, (byte) 0x07); // adresse haute (2000 >> 8)
        memory.write(6, (byte) 0xD0); // adresse basse (2000 & 0xFF)
        memory.write(7, (byte) 0);    // BREAK
        cpu.run();
        assertEquals((byte) 55, memory.read(2000));
    }

    // --------------------------------------------------------------------- ADD

    /**
     * ADD r2, r0, r1 : r2 = r0 + r1 = 10 + 20 = 30
     */
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

    // --------------------------------------------------------------------- SUB

    /**
     * SUB r2, r0, r1 : r2 = r0 - r1 = 15 - 5 = 10
     */
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

    // ---------------------------------------------------------------------- JUMP

    /**
     * JUMP @5 saute à l'adresse 5, en ignorant les octets 3 et 4.
     * Programme : [11, 0, 5, 99, 99, 0]
     *              addr0  1  2   3   4  5
     */
    @Test
    public void testJump() {
        memory.write(0, (byte) 11); // JUMP
        memory.write(1, (byte) 0);  // adresse haute = 0
        memory.write(2, (byte) 5);  // adresse basse = 5
        memory.write(3, (byte) 99); // ne doit PAS être exécuté
        memory.write(4, (byte) 99); // ne doit PAS être exécuté
        memory.write(5, (byte) 0);  // BREAK (cible du saut)
        cpu.run();
        assertFalse(cpu.isRunning());
        assertEquals(6, cpu.getPC()); // le PC avance d'un octet après BREAK
    }

    // --------------------------------------------------------------------- BEQ

    /**
     * BEQ r0, r1, @15 : saute si r0 == r1.
     * Ici r0=r1=7 → saut pris → r2 reste 0.
     */
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
        memory.write(9,  (byte) 0);    // adresse haute
        memory.write(10, (byte) 15);   // adresse basse
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 — ne doit PAS s'exécuter
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK (séquence)
        memory.write(15, (byte) 0);    // BREAK (cible du saut)
        cpu.run();
        assertEquals((byte) 0, registers.get(2)); // r2 non modifié = saut pris
    }

    /**
     * BEQ r0, r1, @15 : r0=3, r1=5 → saut non pris → r2 = 1.
     */
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
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 — DOIT s'exécuter
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (cible non atteinte)
        cpu.run();
        assertEquals((byte) 1, registers.get(2));
    }

    // --------------------------------------------------------------------- BNE

    /**
     * BNE r0, r1, @15 : r0=3, r1=8 → saut pris → r2 reste 0.
     */
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
        memory.write(11, (byte) 1);    // LOAD_CONST r2, 1 — ne doit PAS s'exécuter
        memory.write(12, (byte) 2);
        memory.write(13, (byte) 1);
        memory.write(14, (byte) 0);    // BREAK
        memory.write(15, (byte) 0);    // BREAK (cible du saut)
        cpu.run();
        assertEquals((byte) 0, registers.get(2));
    }

    // ------------------------------------------------------------------- RESET

    /**
     * reset() remet le PC à 0 et arrête l'exécution.
     */
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
