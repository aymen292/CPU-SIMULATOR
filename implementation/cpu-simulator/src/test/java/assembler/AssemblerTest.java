package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de la classe Assembler.
 * Vérifie que chaque instruction assembleur produit les bons octets en mémoire.
 * Les adresses 16 bits sont encodées en big-endian : 1000 = 0x03, 0xE8 ; 500 = 0x01, 0xF4.
 */
public class AssemblerTest {

    private Memory memory;
    private Assembler assembler;

    /**
     * Crée une nouvelle mémoire et un nouvel assembleur avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        memory = new Memory();
        assembler = new Assembler(memory);
    }

    /**
     * Vérifie que "load r3, 42" produit les octets [1, 3, 42].
     */
    @Test
    public void testAssembleLoadConstante() {
        assembler.assemble("load r3, 42");
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 3,  memory.read(1));
        assertEquals((byte) 42, memory.read(2));
    }

    /**
     * Vérifie que "load r0, @1000" produit les octets [2, 0, 0x03, 0xE8].
     */
    @Test
    public void testAssembleLoadMem() {
        assembler.assemble("load r0, @1000");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x03, memory.read(2));
        assertEquals((byte) 0xE8, memory.read(3));
    }

    /**
     * Vérifie que "load r2, @100, r1" produit les octets [14, 2, 0x00, 0x64, 1].
     */
    @Test
    public void testAssembleLoadIndexed() {
        assembler.assemble("load r2, @100, r1");
        assertEquals((byte) 14,   memory.read(0));
        assertEquals((byte) 2,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    /**
     * Vérifie que "store r1, @500" produit les octets [3, 1, 0x01, 0xF4].
     */
    @Test
    public void testAssembleStore() {
        assembler.assemble("store r1, @500");
        assertEquals((byte) 3,    memory.read(0));
        assertEquals((byte) 1,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0xF4, memory.read(3));
    }

    /**
     * Vérifie que "store r0, @100, r1" produit les octets [15, 0, 0x00, 0x64, 1].
     */
    @Test
    public void testAssembleStoreIndexed() {
        assembler.assemble("store r0, @100, r1");
        assertEquals((byte) 15,   memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    /**
     * Vérifie que les adresses hexadécimales sont bien interprétées :
     * @0x100 = 256, encodé [0x01, 0x00].
     */
    @Test
    public void testAdresseHexadecimale() {
        assembler.assemble("load r0, @0x100");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0x00, memory.read(3));
    }

    /**
     * Vérifie que "add r2, r0, r1" produit les octets [4, 2, 0, 1].
     */
    @Test
    public void testAssembleAdd() {
        assembler.assemble("add r2, r0, r1");
        assertEquals((byte) 4, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    /**
     * Vérifie que "sub r2, r0, r1" produit les octets [5, 2, 0, 1].
     */
    @Test
    public void testAssembleSub() {
        assembler.assemble("sub r2, r0, r1");
        assertEquals((byte) 5, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    /**
     * Vérifie que "mul r2, r3, r0, r1" produit les octets [6, 2, 3, 0, 1].
     */
    @Test
    public void testAssembleMul() {
        assembler.assemble("mul r2, r3, r0, r1");
        assertEquals((byte) 6, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    /**
     * Vérifie que "div r2, r3, r0, r1" produit les octets [7, 2, 3, 0, 1].
     */
    @Test
    public void testAssembleDiv() {
        assembler.assemble("div r2, r3, r0, r1");
        assertEquals((byte) 7, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    /**
     * Vérifie que "and r2, r0, r1" produit les octets [8, 2, 0, 1].
     */
    @Test
    public void testAssembleAnd() {
        assembler.assemble("and r2, r0, r1");
        assertEquals((byte) 8, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    /**
     * Vérifie que "or r2, r0, r1" produit les octets [9, 2, 0, 1].
     */
    @Test
    public void testAssembleOr() {
        assembler.assemble("or r2, r0, r1");
        assertEquals((byte) 9, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    /**
     * Vérifie que "xor r2, r0, r1" produit les octets [10, 2, 0, 1].
     */
    @Test
    public void testAssembleXor() {
        assembler.assemble("xor r2, r0, r1");
        assertEquals((byte) 10, memory.read(0));
        assertEquals((byte) 2,  memory.read(1));
        assertEquals((byte) 0,  memory.read(2));
        assertEquals((byte) 1,  memory.read(3));
    }

    /**
     * Vérifie que "jump @50" produit les octets [11, 0, 50].
     */
    @Test
    public void testAssembleJump() {
        assembler.assemble("jump @50");
        assertEquals((byte) 11, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 50, memory.read(2));
    }

    /**
     * Vérifie que "beq r0, r1, @30" produit les octets [12, 0, 1, 0, 30].
     */
    @Test
    public void testAssembleBeq() {
        assembler.assemble("beq r0, r1, @30");
        assertEquals((byte) 12, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 1,  memory.read(2));
        assertEquals((byte) 0,  memory.read(3));
        assertEquals((byte) 30, memory.read(4));
    }

    /**
     * Vérifie que "bne r2, r3, @100" produit les octets [13, 2, 3, 0, 100].
     */
    @Test
    public void testAssembleBne() {
        assembler.assemble("bne r2, r3, @100");
        assertEquals((byte) 13,  memory.read(0));
        assertEquals((byte) 2,   memory.read(1));
        assertEquals((byte) 3,   memory.read(2));
        assertEquals((byte) 0,   memory.read(3));
        assertEquals((byte) 100, memory.read(4));
    }

    /**
     * Vérifie que "break" produit l'octet [0].
     */
    @Test
    public void testAssembleBreak() {
        assembler.assemble("break");
        assertEquals((byte) 0, memory.read(0));
    }

    /**
     * Vérifie qu'un mnémonique inconnu lève une IllegalArgumentException.
     */
    @Test
    public void testMnemoniqueInconnu() {
        assertThrows(IllegalArgumentException.class,
                     () -> assembler.assemble("bidon r0, r1"));
    }

    /**
     * Vérifie que la directive "data 0xFF, 0x01, 42" écrit trois octets bruts en mémoire.
     */
    @Test
    public void testAssembleData() {
        assembler.assemble("data 0xFF, 0x01, 42");
        assertEquals((byte) 0xFF, memory.read(0));
        assertEquals((byte) 0x01, memory.read(1));
        assertEquals((byte) 42,   memory.read(2));
    }

    /**
     * Vérifie que la directive 'string "hello"' écrit les codes ASCII correspondants.
     */
    @Test
    public void testAssembleString() {
        assembler.assemble("string \"hello\"");
        assertEquals((byte) 'h', memory.read(0));
        assertEquals((byte) 'e', memory.read(1));
        assertEquals((byte) 'l', memory.read(2));
        assertEquals((byte) 'l', memory.read(3));
        assertEquals((byte) 'o', memory.read(4));
    }

    /**
     * Vérifie que les lignes commençant par ";" sont ignorées lors de l'assemblage.
     */
    @Test
    public void testCommentairesIgnores() {
        assembler.assemble("; ceci est un commentaire\nbreak");
        assertEquals((byte) 0, memory.read(0));
    }

    /**
     * Vérifie l'encodage d'un programme complet :
     * load r0, 10 | load r1, 20 | add r2, r0, r1 | store r2, @1000 | break.
     */
    @Test
    public void testProgrammeComplet() {
        assembler.assemble(
            "load r0, 10\n"    +
            "load r1, 20\n"    +
            "add r2, r0, r1\n" +
            "store r2, @1000\n"+
            "break"
        );
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 10, memory.read(2));
        assertEquals((byte) 1,  memory.read(3));
        assertEquals((byte) 1,  memory.read(4));
        assertEquals((byte) 20, memory.read(5));
        assertEquals((byte) 4,    memory.read(6));
        assertEquals((byte) 2,    memory.read(7));
        assertEquals((byte) 0,    memory.read(8));
        assertEquals((byte) 1,    memory.read(9));
        assertEquals((byte) 3,    memory.read(10));
        assertEquals((byte) 2,    memory.read(11));
        assertEquals((byte) 0x03, memory.read(12));
        assertEquals((byte) 0xE8, memory.read(13));
        assertEquals((byte) 0,    memory.read(14));
    }
}
