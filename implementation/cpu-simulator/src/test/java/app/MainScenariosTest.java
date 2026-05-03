package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;
import exception.InvalidOpcodeException;
import exception.MemoryOutOfBoundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration simulant tous les scénarios d'utilisation de Main.java.
 * Chaque test reproduit une séquence d'actions (options 1-7) via assembler + CPU.
 */
public class MainScenariosTest {

    private Memory memory;
    private RegisterFile registers;
    private CPU cpu;
    private Assembler assembler;

    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        registers = new RegisterFile();
        cpu       = new CPU(memory, registers);
        assembler = new Assembler(memory);
    }

    // Simule option 2 (assembler) + option 3 (run) comme dans Main.java
    private void assembleAndRun(String programme) {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(programme);
        cpu.run();
    }

    // -------------------------------------------------------------------------
    // SCENARIO 1 : LOAD_CONST + ADD + STORE + BREAK (programme de base)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioAddition() {
        assembleAndRun(
            "load r0, 10\n" +
            "load r1, 20\n" +
            "add r2, r0, r1\n" +
            "store r2, @1000\n" +
            "break"
        );
        assertEquals(10, registers.get(0) & 0xFF);
        assertEquals(20, registers.get(1) & 0xFF);
        assertEquals(30, registers.get(2) & 0xFF);
        assertEquals(30, memory.read(1000) & 0xFF);
        assertFalse(cpu.isRunning());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 2 : SUB
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioSoustraction() {
        assembleAndRun(
            "load r0, 50\n" +
            "load r1, 17\n" +
            "sub r2, r0, r1\n" +
            "break"
        );
        assertEquals(33, registers.get(2) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 3 : MUL (résultat 16 bits dans deux registres)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioMultiplication() {
        assembleAndRun(
            "load r0, 12\n" +
            "load r1, 11\n" +
            "mul r2, r3, r0, r1\n" +
            "break"
        );
        // 12 × 11 = 132  →  octet haut = 0, octet bas = 132
        assertEquals(0,   registers.get(2) & 0xFF);
        assertEquals(132, registers.get(3) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 4 : DIV (quotient + reste)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioDivision() {
        assembleAndRun(
            "load r0, 17\n" +
            "load r1, 5\n" +
            "div r2, r3, r0, r1\n" +
            "break"
        );
        assertEquals(3, registers.get(2) & 0xFF);   // quotient
        assertEquals(2, registers.get(3) & 0xFF);   // reste
    }

    // -------------------------------------------------------------------------
    // SCENARIO 5 : DIV par zéro → ArithmeticException (option 3 la capture)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioDivisionParZero() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r0, 10\n" +
            "load r1, 0\n" +
            "div r2, r3, r0, r1\n" +
            "break"
        );
        assertThrows(ArithmeticException.class, () -> cpu.run());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 6 : Opcode invalide → InvalidOpcodeException
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioOpcodeInvalide() {
        memory.write(0, (byte) 99);
        assertThrows(InvalidOpcodeException.class, () -> cpu.run());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 7 : AND / OR / XOR
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioOperationsLogiques() {
        assembleAndRun(
            "load r0, 12\n" +   // 0b00001100
            "load r1, 10\n" +   // 0b00001010
            "and r2, r0, r1\n" +
            "or  r3, r0, r1\n" +
            "xor r4, r0, r1\n" +
            "break"
        );
        assertEquals(8,  registers.get(2) & 0xFF);  // 0b00001000
        assertEquals(14, registers.get(3) & 0xFF);  // 0b00001110
        assertEquals(6,  registers.get(4) & 0xFF);  // 0b00000110
    }

    // -------------------------------------------------------------------------
    // SCENARIO 8 : JUMP inconditionnel — saute par-dessus une instruction
    // Layout mémoire :
    //   0-2  : load r0, 99  (3 octets)
    //   3-5  : jump @9      (3 octets)
    //   6-8  : load r1, 55  (3 octets)  ← NE doit PAS être exécuté
    //   9    : break         (1 octet)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioJump() {
        assembleAndRun(
            "load r0, 99\n" +
            "jump @9\n" +
            "load r1, 55\n" +
            "break"
        );
        assertEquals(99, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);  // non modifié
    }

    // -------------------------------------------------------------------------
    // SCENARIO 9 : BEQ pris (r0 == r1 → saute par-dessus load r2)
    // Layout :
    //   0-2  : load r0, 5
    //   3-5  : load r1, 5
    //   6-10 : beq r0, r1, @14
    //   11-13: load r2, 99   ← NE doit PAS être exécuté
    //   14   : break
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioBeqPris() {
        assembleAndRun(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "beq r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(0, registers.get(2) & 0xFF);  // r2 non modifié
    }

    // -------------------------------------------------------------------------
    // SCENARIO 10 : BEQ non pris (r0 != r1 → continue sans sauter)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioBeqNonPris() {
        assembleAndRun(
            "load r0, 3\n" +
            "load r1, 7\n" +
            "beq r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(99, registers.get(2) & 0xFF);  // r2 modifié car saut non pris
    }

    // -------------------------------------------------------------------------
    // SCENARIO 11 : BNE pris (r0 != r1 → saute par-dessus load r2)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioBnePris() {
        assembleAndRun(
            "load r0, 3\n" +
            "load r1, 8\n" +
            "bne r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(0, registers.get(2) & 0xFF);  // r2 non modifié
    }

    // -------------------------------------------------------------------------
    // SCENARIO 12 : BNE non pris (r0 == r1 → continue)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioBneNonPris() {
        assembleAndRun(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "bne r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(99, registers.get(2) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 13 : LOAD_MEM — charge une valeur depuis la mémoire
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioLoadMem() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r5, @3000\n" +
            "break"
        );
        memory.write(3000, (byte) 77);  // valeur placée après assemblage
        cpu.run();
        assertEquals(77, registers.get(5) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 14 : LOAD_INDEXED — charge mem[base + r_offset]
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioLoadIndexed() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r1, 5\n" +
            "load r0, @1000, r1\n" +
            "break"
        );
        memory.write(1005, (byte) 88);
        cpu.run();
        assertEquals(88, registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 15 : STORE_INDEXED — écrit r_src à mem[base + r_offset]
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioStoreIndexed() {
        assembleAndRun(
            "load r0, 42\n" +
            "load r1, 5\n" +
            "store r0, @2000, r1\n" +
            "break"
        );
        assertEquals(42, memory.read(2005) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 16 : Exécution pas à pas (option 4 répétée)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioStepParStep() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r0, 10\n" +
            "load r1, 20\n" +
            "add r2, r0, r1\n" +
            "break"
        );

        assertTrue(cpu.step());                        // load r0, 10
        assertEquals(10, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);

        assertTrue(cpu.step());                        // load r1, 20
        assertEquals(20, registers.get(1) & 0xFF);

        assertTrue(cpu.step());                        // add r2, r0, r1
        assertEquals(30, registers.get(2) & 0xFF);

        assertFalse(cpu.step());                       // break → retourne false
        assertFalse(cpu.isRunning());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 17 : Consultation PC (option 5c) après chaque step
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioConsulterPC() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r0, 10\n" +   // 3 octets : adresses 0-2
            "break"             // 1 octet  : adresse 3
        );
        assertEquals(0, cpu.getPC());
        cpu.step();              // exécute load r0, 10 (PC avance de 3)
        assertEquals(3, cpu.getPC());
        cpu.step();              // exécute break (PC avance de 1)
        assertEquals(4, cpu.getPC());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 18 : Consultation registres (option 5b) — tous les 16 registres
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioConsulterRegistres() {
        assembleAndRun(
            "load r0, 1\n" +
            "load r5, 100\n" +
            "load r15, 127\n" +
            "break"
        );
        assertEquals(1,   registers.get(0)  & 0xFF);
        assertEquals(100, registers.get(5)  & 0xFF);
        assertEquals(127, registers.get(15) & 0xFF);
        for (int i = 1; i <= 14; i++) {
            if (i != 5) assertEquals(0, registers.get(i) & 0xFF, "r" + i + " doit valoir 0");
        }
    }

    // -------------------------------------------------------------------------
    // SCENARIO 19 : Consultation mémoire (option 5a) — lecture directe
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioConsulterMemoire() {
        memory.write(0,  (byte) 42);
        memory.write(1,  (byte) 100);
        memory.write(10, (byte) 7);
        assertEquals(42,  memory.read(0)  & 0xFF);
        assertEquals(100, memory.read(1)  & 0xFF);
        assertEquals(7,   memory.read(10) & 0xFF);
        assertEquals(0,   memory.read(2)  & 0xFF);  // case non écrite = 0
    }

    // -------------------------------------------------------------------------
    // SCENARIO 20 : Adresse mémoire hors limites → MemoryOutOfBoundsException
    //               (adresse invalide en option 5a aurait planté avant fix)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioAdresseHorsLimites() {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
    }

    // -------------------------------------------------------------------------
    // SCENARIO 21 : Adressage indexé hors limites → MemoryOutOfBoundsException
    //               base=65500, offset=200 → 65700 > 65535
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioLoadIndexedHorsLimites() {
        // Encodage manuel : LOAD_CONST r0, 200 puis LOAD_INDEXED r1, @65500, r0
        memory.write(0, (byte) 1);     // LOAD_CONST
        memory.write(1, (byte) 0);     // dest r0
        memory.write(2, (byte) 200);   // valeur 200
        memory.write(3, (byte) 14);    // LOAD_INDEXED
        memory.write(4, (byte) 1);     // dest r1
        memory.write(5, (byte) 0xFF);  // base haut : 255
        memory.write(6, (byte) 0xDC);  // base bas  : 220  → base = 65500
        memory.write(7, (byte) 0);     // offset_reg = r0 (= 200)
        memory.write(8, (byte) 0);     // BREAK
        // 65500 + 200 = 65700 → MemoryOutOfBoundsException
        assertThrows(MemoryOutOfBoundsException.class, () -> cpu.run());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 22 : Instruction assembleur inconnue → IllegalArgumentException
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioInstructionInconnue() {
        assertThrows(IllegalArgumentException.class,
            () -> assembler.assemble("foobar r0, r1")
        );
    }

    // -------------------------------------------------------------------------
    // SCENARIO 23 : Programme vide (commentaires uniquement) → BREAK immédiat
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioProgrammeVide() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("; commentaire\n# autre commentaire\n\n");
        // Aucun octet écrit → mémoire[0] = 0 (BREAK par défaut)
        assertEquals(0, memory.read(0));
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    // -------------------------------------------------------------------------
    // SCENARIO 24 : Réinitialisation (option 6) puis ré-exécution (option 2+3)
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioReinitialisationReExecution() {
        assembleAndRun("load r0, 42\nbreak");
        assertEquals(42, registers.get(0) & 0xFF);

        // Simulation option 6 (reset)
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);

        // Nouveau programme (option 1 + 2 + 3)
        assembler.assemble("load r0, 77\nbreak");
        cpu.run();
        assertEquals(77, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 25 : Directive data — octets bruts en mémoire
    // Layout :
    //   0-2 : jump @6    (saut par-dessus les données)
    //   3-5 : data 10, 20, 30
    //   6-9 : load r0, @3   (charge mem[3] = 10)
    //   10  : break
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioDirectiveData() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "jump @6\n" +
            "data 10, 20, 30\n" +
            "load r0, @3\n" +
            "break"
        );
        cpu.run();
        assertEquals(10, registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 26 : Directive string — octets ASCII en mémoire
    // Layout :
    //   0-2 : jump @5
    //   3-4 : string "Hi"  ('H'=72, 'i'=105)
    //   5-8 : load r0, @3
    //   9   : break
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioDirectiveString() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "jump @5\n" +
            "string \"Hi\"\n" +
            "load r0, @3\n" +
            "break"
        );
        cpu.run();
        assertEquals('H', registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 27 : Boucle de comptage avec BNE (programme non trivial)
    // r0 compte de 0 à 5 ; adresses : add=9, bne=13, break=18
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioBoucleComptage() {
        assembleAndRun(
            "load r0, 0\n" +
            "load r1, 1\n" +
            "load r2, 5\n" +
            "add r0, r0, r1\n" +     // adresse 9
            "bne r0, r2, @9\n" +     // adresse 13 — reboucle si r0 != 5
            "break"
        );
        assertEquals(5, registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 28 : Adresses hexadécimales dans l'assembleur
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioAdresseHexadecimale() {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(
            "load r0, @0x0064\n" +   // 0x0064 = 100
            "break"
        );
        memory.write(100, (byte) 55);
        cpu.run();
        assertEquals(55, registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 29 : Commentaires en fin de ligne ignorés
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioCommentairesFinDeLigne() {
        assembleAndRun(
            "load r0, 7  ; charge 7 dans r0\n" +
            "break       ; fin du programme"
        );
        assertEquals(7, registers.get(0) & 0xFF);
    }

    // -------------------------------------------------------------------------
    // SCENARIO 30 : Adresse limite valide (65535) — lecture et écriture
    // -------------------------------------------------------------------------
    @Test
    public void testScenarioAdresseLimiteValide() {
        memory.write(65535, (byte) 123);
        assertEquals(123, memory.read(65535) & 0xFF);
    }
}
