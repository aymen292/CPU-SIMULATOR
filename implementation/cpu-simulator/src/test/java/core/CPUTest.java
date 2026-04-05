package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe CPU.
 */
public class CPUTest {

    private Memory memory;
    private RegisterFile registers;
    private CPU cpu;

    /**
     * Initialise les composants du CPU avant chaque test.
     * Une mémoire vierge, un banc de registres vierge et un CPU sont créés.
     */
    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        registers = new RegisterFile();
        cpu       = new CPU(memory, registers);
    }

    /**
     * Vérifie que l'instruction BREAK arrête correctement l'exécution.
     * Programme : BREAK
     * Attendu   : le CPU n'est plus en cours d'exécution après run()
     */
    @Test
    public void testBreak() {
        // BREAK = opcode 0
        memory.write(0, (byte) 0);

        cpu.run();

        assertFalse(cpu.isRunning());
    }

    /**
     * Vérifie que LOAD_CONST charge bien une constante dans un registre.
     * Programme : LOAD_CONST r0, 42 | BREAK
     * Attendu   : registre 0 contient 42
     */
    @Test
    public void testLoadConst() {
        // LOAD_CONST = opcode 1 | dest=r0 | valeur=42
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 42);
        // BREAK
        memory.write(3, (byte) 0);

        cpu.run();

        assertEquals(42, registers.get(0));
    }

    /**
     * Vérifie que LOAD_MEM charge bien une valeur depuis une adresse mémoire dans un registre.
     * Programme : LOAD_MEM r1, @1000 | BREAK
     * Attendu   : registre 1 contient la valeur préalablement écrite à l'adresse 1000
     */
    @Test
    public void testLoadMem() {
        // Valeur à charger : 77 à l'adresse 1000
        memory.write(1000, (byte) 77);

        // LOAD_MEM = opcode 2 | dest=r1 | adresse 1000 en big-endian (0x03, 0xE8)
        memory.write(0, (byte) 2);
        memory.write(1, (byte) 1);
        memory.write(2, (byte) 0x03);
        memory.write(3, (byte) 0xE8);
        // BREAK
        memory.write(4, (byte) 0);

        cpu.run();

        assertEquals(77, registers.get(1));
    }

    /**
     * Vérifie que STORE écrit bien la valeur d'un registre en mémoire.
     * Programme : LOAD_CONST r2, 55 | STORE r2, @2000 | BREAK
     * Attendu   : l'adresse mémoire 2000 contient 55
     */
    @Test
    public void testStore() {
        // LOAD_CONST r2, 55
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 2);
        memory.write(2, (byte) 55);
        // STORE = opcode 3 | src=r2 | adresse 2000 en big-endian (0x07, 0xD0)
        memory.write(3, (byte) 3);
        memory.write(4, (byte) 2);
        memory.write(5, (byte) 0x07);
        memory.write(6, (byte) 0xD0);
        // BREAK
        memory.write(7, (byte) 0);

        cpu.run();

        assertEquals(55, memory.read(2000));
    }

    /**
     * Vérifie que ADD additionne correctement deux registres.
     * Programme : LOAD_CONST r0, 10 | LOAD_CONST r1, 20 | ADD r2, r0, r1 | BREAK
     * Attendu   : registre 2 contient 30
     */
    @Test
    public void testAdd() {
        // LOAD_CONST r0, 10
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 10);
        // LOAD_CONST r1, 20
        memory.write(3, (byte) 1);
        memory.write(4, (byte) 1);
        memory.write(5, (byte) 20);
        // ADD = opcode 4 | dest=r2 | regA=r0 | regB=r1
        memory.write(6, (byte) 4);
        memory.write(7, (byte) 2);
        memory.write(8, (byte) 0);
        memory.write(9, (byte) 1);
        // BREAK
        memory.write(10, (byte) 0);

        cpu.run();

        assertEquals(30, registers.get(2));
    }

    /**
     * Vérifie que SUB soustrait correctement deux registres.
     * Programme : LOAD_CONST r0, 15 | LOAD_CONST r1, 5 | SUB r2, r0, r1 | BREAK
     * Attendu   : registre 2 contient 10
     */
    @Test
    public void testSub() {
        // LOAD_CONST r0, 15
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 15);
        // LOAD_CONST r1, 5
        memory.write(3, (byte) 1);
        memory.write(4, (byte) 1);
        memory.write(5, (byte) 5);
        // SUB = opcode 5 | dest=r2 | regA=r0 | regB=r1
        memory.write(6, (byte) 5);
        memory.write(7, (byte) 2);
        memory.write(8, (byte) 0);
        memory.write(9, (byte) 1);
        // BREAK
        memory.write(10, (byte) 0);

        cpu.run();

        assertEquals(10, registers.get(2));
    }

    /**
     * Vérifie que JUMP effectue bien un saut inconditionnel à une adresse.
     * Programme : JUMP @5 | (octet ignoré) | BREAK à l'adresse 5
     * Attendu   : le CPU saute directement au BREAK sans passer par l'octet intermédiaire
     */
    @Test
    public void testJump() {
        // JUMP = opcode 11 | adresse cible = 5 en big-endian (0x00, 0x05)
        memory.write(0, (byte) 11);
        memory.write(1, (byte) 0x00);
        memory.write(2, (byte) 0x05);
        // Octet à ne PAS exécuter (si JUMP fonctionne, il est ignoré)
        memory.write(3, (byte) 99);
        memory.write(4, (byte) 99);
        // BREAK à l'adresse 5
        memory.write(5, (byte) 0);

        cpu.run();

        assertFalse(cpu.isRunning());
        assertEquals(6, cpu.getPC()); // pc avance après le BREAK
    }

    /**
     * Vérifie que BEQ effectue un saut si les deux registres sont égaux.
     * Programme : LOAD_CONST r0, 7 | LOAD_CONST r1, 7 | BEQ r0, r1, @adresse | BREAK cible
     * Attendu   : le CPU saute à l'adresse cible car r0 == r1
     */
    @Test
    public void testBeq() {
        // LOAD_CONST r0, 7
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 7);
        // LOAD_CONST r1, 7
        memory.write(3, (byte) 1);
        memory.write(4, (byte) 1);
        memory.write(5, (byte) 7);
        // BEQ = opcode 12 | regA=r0 | regB=r1 | adresse cible=15 (0x00, 0x0F)
        memory.write(6,  (byte) 12);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0x00);
        memory.write(10, (byte) 0x0F);
        // Octet à ne PAS exécuter si le saut a bien eu lieu
        memory.write(11, (byte) 99);
        memory.write(12, (byte) 99);
        memory.write(13, (byte) 99);
        memory.write(14, (byte) 99);
        // BREAK à l'adresse 15
        memory.write(15, (byte) 0);

        cpu.run();

        assertFalse(cpu.isRunning());
    }

    /**
     * Vérifie que BEQ ne saute PAS si les deux registres sont différents.
     * Programme : LOAD_CONST r0, 3 | LOAD_CONST r1, 5 | BEQ r0, r1, @loin | BREAK immédiat
     * Attendu   : le CPU ne saute pas et exécute le BREAK immédiat
     */
    @Test
    public void testBeqNoJump() {
        // LOAD_CONST r0, 3
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 3);
        // LOAD_CONST r1, 5
        memory.write(3, (byte) 1);
        memory.write(4, (byte) 1);
        memory.write(5, (byte) 5);
        // BEQ = opcode 12 | regA=r0 | regB=r1 | adresse cible=100 (loin)
        memory.write(6,  (byte) 12);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0x00);
        memory.write(10, (byte) 0x64);
        // BREAK immédiat (les registres sont différents donc on ne saute pas)
        memory.write(11, (byte) 0);

        cpu.run();

        assertFalse(cpu.isRunning());
    }

    /**
     * Vérifie que BNE effectue un saut si les deux registres sont différents.
     * Programme : LOAD_CONST r0, 3 | LOAD_CONST r1, 8 | BNE r0, r1, @adresse | BREAK cible
     * Attendu   : le CPU saute à l'adresse cible car r0 != r1
     */
    @Test
    public void testBne() {
        // LOAD_CONST r0, 3
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 3);
        // LOAD_CONST r1, 8
        memory.write(3, (byte) 1);
        memory.write(4, (byte) 1);
        memory.write(5, (byte) 8);
        // BNE = opcode 13 | regA=r0 | regB=r1 | adresse cible=15 (0x00, 0x0F)
        memory.write(6,  (byte) 13);
        memory.write(7,  (byte) 0);
        memory.write(8,  (byte) 1);
        memory.write(9,  (byte) 0x00);
        memory.write(10, (byte) 0x0F);
        // Octets à ne PAS exécuter si le saut a bien eu lieu
        memory.write(11, (byte) 99);
        memory.write(12, (byte) 99);
        memory.write(13, (byte) 99);
        memory.write(14, (byte) 99);
        // BREAK à l'adresse 15
        memory.write(15, (byte) 0);

        cpu.run();

        assertFalse(cpu.isRunning());
    }

    /**
     * Vérifie que reset() remet bien le PC à 0 et arrête l'exécution.
     */
    @Test
    public void testReset() {
        memory.write(0, (byte) 1);
        memory.write(1, (byte) 0);
        memory.write(2, (byte) 5);
        memory.write(3, (byte) 0);

        cpu.run();

        // Après run(), le PC a avancé
        assertTrue(cpu.getPC() > 0);

        cpu.reset();

        assertEquals(0, cpu.getPC());
        assertFalse(cpu.isRunning());
    }
}