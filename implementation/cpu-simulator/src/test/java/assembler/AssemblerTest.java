package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests pour la classe Assembler.
 * On verifie que chaque instruction assembleur produit les bons octets en memoire.
 *
 * Rappel pour les adresses 16 bits en big-endian :
 *   1000 = 0x03E8 -> octet haut = 0x03, octet bas = 0xE8
 *    500 = 0x01F4 -> octet haut = 0x01, octet bas = 0xF4
 */
public class AssemblerTest {

    private Memory    memory;
    private Assembler assembler;

    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        assembler = new Assembler(memory);
    }

    // load r3, 42 -> [1, 3, 42]
    @Test
    public void testAssembleLoadConstante() {
        assembler.assemble("load r3, 42");
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 3,  memory.read(1));
        assertEquals((byte) 42, memory.read(2));
    }

    // load r0, @1000 -> [2, 0, 0x03, 0xE8]
    @Test
    public void testAssembleLoadMem() {
        assembler.assemble("load r0, @1000");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x03, memory.read(2));
        assertEquals((byte) 0xE8, memory.read(3));
    }

    // load r2, @100, r1 -> [14, 2, 0x00, 0x64, 1]
    @Test
    public void testAssembleLoadIndexed() {
        assembler.assemble("load r2, @100, r1");
        assertEquals((byte) 14,   memory.read(0));
        assertEquals((byte) 2,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    // store r1, @500 -> [3, 1, 0x01, 0xF4]
    @Test
    public void testAssembleStore() {
        assembler.assemble("store r1, @500");
        assertEquals((byte) 3,    memory.read(0));
        assertEquals((byte) 1,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0xF4, memory.read(3));
    }

    // store r0, @100, r1 -> [15, 0, 0x00, 0x64, 1]
    @Test
    public void testAssembleStoreIndexed() {
        assembler.assemble("store r0, @100, r1");
        assertEquals((byte) 15,   memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    // adresse en hexa : @0x100 = 256 = 0x01, 0x00
    @Test
    public void testAdresseHexadecimale() {
        assembler.assemble("load r0, @0x100");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0x00, memory.read(3));
    }

    // add r2, r0, r1 -> [4, 2, 0, 1]
    @Test
    public void testAssembleAdd() {
        assembler.assemble("add r2, r0, r1");
        assertEquals((byte) 4, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // sub r2, r0, r1 -> [5, 2, 0, 1]
    @Test
    public void testAssembleSub() {
        assembler.assemble("sub r2, r0, r1");
        assertEquals((byte) 5, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // mul r2, r3, r0, r1 -> [6, 2, 3, 0, 1]
    @Test
    public void testAssembleMul() {
        assembler.assemble("mul r2, r3, r0, r1");
        assertEquals((byte) 6, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    // div r2, r3, r0, r1 -> [7, 2, 3, 0, 1]
    @Test
    public void testAssembleDiv() {
        assembler.assemble("div r2, r3, r0, r1");
        assertEquals((byte) 7, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    // and r2, r0, r1 -> [8, 2, 0, 1]
    @Test
    public void testAssembleAnd() {
        assembler.assemble("and r2, r0, r1");
        assertEquals((byte) 8, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // or r2, r0, r1 -> [9, 2, 0, 1]
    @Test
    public void testAssembleOr() {
        assembler.assemble("or r2, r0, r1");
        assertEquals((byte) 9, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // xor r2, r0, r1 -> [10, 2, 0, 1]
    @Test
    public void testAssembleXor() {
        assembler.assemble("xor r2, r0, r1");
        assertEquals((byte) 10, memory.read(0));
        assertEquals((byte) 2,  memory.read(1));
        assertEquals((byte) 0,  memory.read(2));
        assertEquals((byte) 1,  memory.read(3));
    }

    // jump @50 -> [11, 0, 50]
    @Test
    public void testAssembleJump() {
        assembler.assemble("jump @50");
        assertEquals((byte) 11, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 50, memory.read(2));
    }

    // beq r0, r1, @30 -> [12, 0, 1, 0, 30]
    @Test
    public void testAssembleBeq() {
        assembler.assemble("beq r0, r1, @30");
        assertEquals((byte) 12, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 1,  memory.read(2));
        assertEquals((byte) 0,  memory.read(3));
        assertEquals((byte) 30, memory.read(4));
    }

    // bne r2, r3, @100 -> [13, 2, 3, 0, 100]
    @Test
    public void testAssembleBne() {
        assembler.assemble("bne r2, r3, @100");
        assertEquals((byte) 13,  memory.read(0));
        assertEquals((byte) 2,   memory.read(1));
        assertEquals((byte) 3,   memory.read(2));
        assertEquals((byte) 0,   memory.read(3));
        assertEquals((byte) 100, memory.read(4));
    }

    // break tout seul -> [0]
    @Test
    public void testAssembleBreak() {
        assembler.assemble("break");
        assertEquals((byte) 0, memory.read(0));
    }

    // un mnemonique qui n'existe pas doit lever une exception
    @Test
    public void testMnemoniqueInconnu() {
        assertThrows(IllegalArgumentException.class,
                     () -> assembler.assemble("bidon r0, r1"));
    }

    // data 0xFF, 0x01, 42 : ecrit 3 octets bruts
    @Test
    public void testAssembleData() {
        assembler.assemble("data 0xFF, 0x01, 42");
        assertEquals((byte) 0xFF, memory.read(0));
        assertEquals((byte) 0x01, memory.read(1));
        assertEquals((byte) 42,   memory.read(2));
    }

    // string "hello" : ecrit les codes ASCII
    @Test
    public void testAssembleString() {
        assembler.assemble("string \"hello\"");
        assertEquals((byte) 'h', memory.read(0));
        assertEquals((byte) 'e', memory.read(1));
        assertEquals((byte) 'l', memory.read(2));
        assertEquals((byte) 'l', memory.read(3));
        assertEquals((byte) 'o', memory.read(4));
    }

    // les lignes commencant par ; sont ignorees
    @Test
    public void testCommentairesIgnores() {
        assembler.assemble("; ceci est un commentaire\nbreak");
        assertEquals((byte) 0, memory.read(0)); // break = opcode 0
    }

    // un petit programme complet
    // load r0, 10 | load r1, 20 | add r2, r0, r1 | store r2, @1000 | break
    @Test
    public void testProgrammeComplet() {
        assembler.assemble(
            "load r0, 10\n"    +
            "load r1, 20\n"    +
            "add r2, r0, r1\n" +
            "store r2, @1000\n"+
            "break"
        );
        // load r0, 10
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 10, memory.read(2));
        // load r1, 20
        assertEquals((byte) 1,  memory.read(3));
        assertEquals((byte) 1,  memory.read(4));
        assertEquals((byte) 20, memory.read(5));
        // add r2, r0, r1
        assertEquals((byte) 4, memory.read(6));
        assertEquals((byte) 2, memory.read(7));
        assertEquals((byte) 0, memory.read(8));
        assertEquals((byte) 1, memory.read(9));
        // store r2, @1000
        assertEquals((byte) 3,    memory.read(10));
        assertEquals((byte) 2,    memory.read(11));
        assertEquals((byte) 0x03, memory.read(12));
        assertEquals((byte) 0xE8, memory.read(13));
        // break
        assertEquals((byte) 0, memory.read(14));
    }
}
