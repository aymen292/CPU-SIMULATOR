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

public class MainScenariosTest {

    private Memory memory;
    private RegisterFile registers;
    private CPU cpu;
    private Assembler assembler;

    @BeforeEach
    public void setUp() throws Exception {
        memory    = new Memory();
        registers = new RegisterFile();
        cpu       = new CPU(memory, registers);
        assembler = new Assembler(memory);
    }

    // réinitialise tout, assemble le programme puis l'exécute
    private void assembleAndRun(String programme) throws Exception {
        memory.reset();
        registers.reset();
        cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble(programme);
        cpu.run();
    }

    // LOAD_CONST + ADD + STORE + BREAK : r0=10, r1=20, r2=30, mémoire[1000]=30
    @Test
    public void testScenarioAddition() throws Exception {
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

    // SUB : 50 - 17 = 33
    @Test
    public void testScenarioSoustraction() throws Exception {
        assembleAndRun(
            "load r0, 50\n" +
            "load r1, 17\n" +
            "sub r2, r0, r1\n" +
            "break"
        );
        assertEquals(33, registers.get(2) & 0xFF);
    }

    // MUL : 12 * 11 = 132 → haut(r2)=0, bas(r3)=132
    @Test
    public void testScenarioMultiplication() throws Exception {
        assembleAndRun(
            "load r0, 12\n" +
            "load r1, 11\n" +
            "mul r2, r3, r0, r1\n" +
            "break"
        );
        assertEquals(0,   registers.get(2) & 0xFF);
        assertEquals(132, registers.get(3) & 0xFF);
    }

    // DIV : 17 / 5 → quotient(r2)=3, reste(r3)=2
    @Test
    public void testScenarioDivision() throws Exception {
        assembleAndRun(
            "load r0, 17\n" +
            "load r1, 5\n" +
            "div r2, r3, r0, r1\n" +
            "break"
        );
        assertEquals(3, registers.get(2) & 0xFF);
        assertEquals(2, registers.get(3) & 0xFF);
    }

    // DIV par zéro → ArithmeticException
    @Test
    public void testScenarioDivisionParZero() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r0, 10\nload r1, 0\ndiv r2, r3, r0, r1\nbreak");
        assertThrows(ArithmeticException.class, () -> cpu.run());
    }

    // opcode inconnu en mémoire → InvalidOpcodeException
    @Test
    public void testScenarioOpcodeInvalide() throws Exception {
        memory.write(0, (byte) 99);
        assertThrows(InvalidOpcodeException.class, () -> cpu.run());
    }

    // AND / OR / XOR : 12(0b1100) op 10(0b1010)
    @Test
    public void testScenarioOperationsLogiques() throws Exception {
        assembleAndRun(
            "load r0, 12\n" +
            "load r1, 10\n" +
            "and r2, r0, r1\n" +
            "or  r3, r0, r1\n" +
            "xor r4, r0, r1\n" +
            "break"
        );
        assertEquals(8,  registers.get(2) & 0xFF);  // 0b1000
        assertEquals(14, registers.get(3) & 0xFF);  // 0b1110
        assertEquals(6,  registers.get(4) & 0xFF);  // 0b0110
    }

    // JUMP inconditionnel : saute par-dessus "load r1, 55" → r1 reste 0
    @Test
    public void testScenarioJump() throws Exception {
        assembleAndRun(
            "load r0, 99\n" +
            "jump @9\n" +
            "load r1, 55\n" +
            "break"
        );
        assertEquals(99, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);
    }

    // BEQ pris : r0==r1 → saute par-dessus "load r2, 99" → r2 reste 0
    @Test
    public void testScenarioBeqPris() throws Exception {
        assembleAndRun(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "beq r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(0, registers.get(2) & 0xFF);
    }

    // BEQ non pris : r0!=r1 → continue → r2 = 99
    @Test
    public void testScenarioBeqNonPris() throws Exception {
        assembleAndRun(
            "load r0, 3\n" +
            "load r1, 7\n" +
            "beq r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(99, registers.get(2) & 0xFF);
    }

    // BNE pris : r0!=r1 → saute par-dessus "load r2, 99" → r2 reste 0
    @Test
    public void testScenarioBnePris() throws Exception {
        assembleAndRun(
            "load r0, 3\n" +
            "load r1, 8\n" +
            "bne r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(0, registers.get(2) & 0xFF);
    }

    // BNE non pris : r0==r1 → continue → r2 = 99
    @Test
    public void testScenarioBneNonPris() throws Exception {
        assembleAndRun(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "bne r0, r1, @14\n" +
            "load r2, 99\n" +
            "break"
        );
        assertEquals(99, registers.get(2) & 0xFF);
    }

    // LOAD_MEM : charge la valeur 77 depuis l'adresse 3000
    @Test
    public void testScenarioLoadMem() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r5, @3000\nbreak");
        memory.write(3000, (byte) 77);
        cpu.run();
        assertEquals(77, registers.get(5) & 0xFF);
    }

    // LOAD_INDEXED : charge mémoire[1000 + r1] avec r1=5, mémoire[1005]=88
    @Test
    public void testScenarioLoadIndexed() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r1, 5\nload r0, @1000, r1\nbreak");
        memory.write(1005, (byte) 88);
        cpu.run();
        assertEquals(88, registers.get(0) & 0xFF);
    }

    // STORE_INDEXED : écrit r0=42 à mémoire[2000 + r1] avec r1=5
    @Test
    public void testScenarioStoreIndexed() throws Exception {
        assembleAndRun(
            "load r0, 42\n" +
            "load r1, 5\n" +
            "store r0, @2000, r1\n" +
            "break"
        );
        assertEquals(42, memory.read(2005) & 0xFF);
    }

    // exécution pas à pas : vérifie l'état des registres après chaque step
    @Test
    public void testScenarioStepParStep() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r0, 10\nload r1, 20\nadd r2, r0, r1\nbreak");

        assertTrue(cpu.step());                       // load r0, 10
        assertEquals(10, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);

        assertTrue(cpu.step());                       // load r1, 20
        assertEquals(20, registers.get(1) & 0xFF);

        assertTrue(cpu.step());                       // add r2, r0, r1
        assertEquals(30, registers.get(2) & 0xFF);

        assertFalse(cpu.step());                      // break → retourne false
        assertFalse(cpu.isRunning());
    }

    // PC avance correctement après chaque instruction
    @Test
    public void testScenarioConsulterPC() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r0, 10\nbreak");
        assertEquals(0, cpu.getPC());
        cpu.step();   // load r0, 10 → 3 octets → PC = 3
        assertEquals(3, cpu.getPC());
        cpu.step();   // break → 1 octet → PC = 4
        assertEquals(4, cpu.getPC());
    }

    // les 16 registres ont les bonnes valeurs après exécution
    @Test
    public void testScenarioConsulterRegistres() throws Exception {
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

    // lecture directe en mémoire après écriture
    @Test
    public void testScenarioConsulterMemoire() throws Exception {
        memory.write(0,  (byte) 42);
        memory.write(1,  (byte) 100);
        memory.write(10, (byte) 7);
        assertEquals(42,  memory.read(0)  & 0xFF);
        assertEquals(100, memory.read(1)  & 0xFF);
        assertEquals(7,   memory.read(10) & 0xFF);
        assertEquals(0,   memory.read(2)  & 0xFF);
    }

    // adresses hors [0, 65535] → MemoryOutOfBoundsException
    @Test
    public void testScenarioAdresseHorsLimites() throws Exception {
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(-1));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.read(65536));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(-1,    (byte) 0));
        assertThrows(MemoryOutOfBoundsException.class, () -> memory.write(65536, (byte) 0));
    }

    // adressage indexé hors limites (base=65500, offset=200 → 65700) → MemoryOutOfBoundsException
    @Test
    public void testScenarioLoadIndexedHorsLimites() throws Exception {
        memory.write(0, (byte) 1);     // LOAD_CONST
        memory.write(1, (byte) 0);     // r0
        memory.write(2, (byte) 200);   // valeur 200
        memory.write(3, (byte) 14);    // LOAD_INDEXED
        memory.write(4, (byte) 1);     // r1
        memory.write(5, (byte) 0xFF);  // base haut → 65500
        memory.write(6, (byte) 0xDC);  // base bas
        memory.write(7, (byte) 0);     // offset = r0 (200)
        memory.write(8, (byte) 0);     // BREAK
        assertThrows(MemoryOutOfBoundsException.class, () -> cpu.run());
    }

    // instruction assembleur inconnue → IllegalArgumentException
    @Test
    public void testScenarioInstructionInconnue() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> assembler.assemble("foobar r0, r1"));
    }

    // programme vide (commentaires uniquement) → BREAK immédiat
    @Test
    public void testScenarioProgrammeVide() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("; commentaire\n# autre commentaire\n\n");
        assertEquals(0, memory.read(0));
        cpu.run();
        assertFalse(cpu.isRunning());
    }

    // reset complet puis ré-exécution d'un nouveau programme
    @Test
    public void testScenarioReinitialisationReExecution() throws Exception {
        assembleAndRun("load r0, 42\nbreak");
        assertEquals(42, registers.get(0) & 0xFF);

        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r0, 77\nbreak");
        cpu.run();
        assertEquals(77, registers.get(0) & 0xFF);
        assertEquals(0,  registers.get(1) & 0xFF);
    }

    // directive data : jump passe par-dessus les données, puis load les lit
    @Test
    public void testScenarioDirectiveData() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
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

    // directive string : jump passe par-dessus "Hi", puis load lit 'H'
    @Test
    public void testScenarioDirectiveString() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
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

    // boucle avec BNE : r0 compte de 0 à 5
    @Test
    public void testScenarioBoucleComptage() throws Exception {
        assembleAndRun(
            "load r0, 0\n" +
            "load r1, 1\n" +
            "load r2, 5\n" +
            "add r0, r0, r1\n" +    // adresse 9
            "bne r0, r2, @9\n" +    // reboucle si r0 != 5
            "break"
        );
        assertEquals(5, registers.get(0) & 0xFF);
    }

    // adresse hexadécimale @0x0064 = 100
    @Test
    public void testScenarioAdresseHexadecimale() throws Exception {
        memory.reset(); registers.reset(); cpu.reset();
        assembler = new Assembler(memory);
        assembler.assemble("load r0, @0x0064\nbreak");
        memory.write(100, (byte) 55);
        cpu.run();
        assertEquals(55, registers.get(0) & 0xFF);
    }

    // commentaires en fin de ligne ignorés
    @Test
    public void testScenarioCommentairesFinDeLigne() throws Exception {
        assembleAndRun(
            "load r0, 7  ; charge 7 dans r0\n" +
            "break       ; fin du programme"
        );
        assertEquals(7, registers.get(0) & 0xFF);
    }

    // adresse limite 65535 accessible en lecture et écriture
    @Test
    public void testScenarioAdresseLimiteValide() throws Exception {
        memory.write(65535, (byte) 123);
        assertEquals(123, memory.read(65535) & 0xFF);
    }
}
