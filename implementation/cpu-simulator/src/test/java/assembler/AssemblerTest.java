package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Adresses 16 bits encodées en big-endian : 1000 = 0x03,0xE8 ; 500 = 0x01,0xF4
public class AssemblerTest {

    private Memory memory;
    private Assembler assembler;

    @BeforeEach
    public void setUp() throws Exception {
        memory    = new Memory();
        assembler = new Assembler(memory);
    }

    // "load r3, 42" → [1, 3, 42]
    @Test
    public void testAssembleLoadConstante() throws Exception {
        assembler.assemble("load r3, 42");
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 3,  memory.read(1));
        assertEquals((byte) 42, memory.read(2));
    }

    // "load r0, @1000" → [2, 0, 0x03, 0xE8]
    @Test
    public void testAssembleLoadMem() throws Exception {
        assembler.assemble("load r0, @1000");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x03, memory.read(2));
        assertEquals((byte) 0xE8, memory.read(3));
    }

    // "load r2, @100, r1" → [14, 2, 0x00, 0x64, 1]
    @Test
    public void testAssembleLoadIndexed() throws Exception {
        assembler.assemble("load r2, @100, r1");
        assertEquals((byte) 14,   memory.read(0));
        assertEquals((byte) 2,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    // "store r1, @500" → [3, 1, 0x01, 0xF4]
    @Test
    public void testAssembleStore() throws Exception {
        assembler.assemble("store r1, @500");
        assertEquals((byte) 3,    memory.read(0));
        assertEquals((byte) 1,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0xF4, memory.read(3));
    }

    // "store r0, @100, r1" → [15, 0, 0x00, 0x64, 1]
    @Test
    public void testAssembleStoreIndexed() throws Exception {
        assembler.assemble("store r0, @100, r1");
        assertEquals((byte) 15,   memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x00, memory.read(2));
        assertEquals((byte) 0x64, memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
    }

    // adresse hexadécimale @0x100 = 256 → [0x01, 0x00]
    @Test
    public void testAdresseHexadecimale() throws Exception {
        assembler.assemble("load r0, @0x100");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0x00, memory.read(3));
    }

    // "add r2, r0, r1" → [4, 2, 0, 1]
    @Test
    public void testAssembleAdd() throws Exception {
        assembler.assemble("add r2, r0, r1");
        assertEquals((byte) 4, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // "sub r2, r0, r1" → [5, 2, 0, 1]
    @Test
    public void testAssembleSub() throws Exception {
        assembler.assemble("sub r2, r0, r1");
        assertEquals((byte) 5, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // "mul r2, r3, r0, r1" → [6, 2, 3, 0, 1]
    @Test
    public void testAssembleMul() throws Exception {
        assembler.assemble("mul r2, r3, r0, r1");
        assertEquals((byte) 6, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    // "div r2, r3, r0, r1" → [7, 2, 3, 0, 1]
    @Test
    public void testAssembleDiv() throws Exception {
        assembler.assemble("div r2, r3, r0, r1");
        assertEquals((byte) 7, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 3, memory.read(2));
        assertEquals((byte) 0, memory.read(3));
        assertEquals((byte) 1, memory.read(4));
    }

    // "and r2, r0, r1" → [8, 2, 0, 1]
    @Test
    public void testAssembleAnd() throws Exception {
        assembler.assemble("and r2, r0, r1");
        assertEquals((byte) 8, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // "or r2, r0, r1" → [9, 2, 0, 1]
    @Test
    public void testAssembleOr() throws Exception {
        assembler.assemble("or r2, r0, r1");
        assertEquals((byte) 9, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // "xor r2, r0, r1" → [10, 2, 0, 1]
    @Test
    public void testAssembleXor() throws Exception {
        assembler.assemble("xor r2, r0, r1");
        assertEquals((byte) 10, memory.read(0));
        assertEquals((byte) 2,  memory.read(1));
        assertEquals((byte) 0,  memory.read(2));
        assertEquals((byte) 1,  memory.read(3));
    }

    // "jump @50" → [11, 0, 50]
    @Test
    public void testAssembleJump() throws Exception {
        assembler.assemble("jump @50");
        assertEquals((byte) 11, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 50, memory.read(2));
    }

    // "beq r0, r1, @30" → [12, 0, 1, 0, 30]
    @Test
    public void testAssembleBeq() throws Exception {
        assembler.assemble("beq r0, r1, @30");
        assertEquals((byte) 12, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 1,  memory.read(2));
        assertEquals((byte) 0,  memory.read(3));
        assertEquals((byte) 30, memory.read(4));
    }

    // "bne r2, r3, @100" → [13, 2, 3, 0, 100]
    @Test
    public void testAssembleBne() throws Exception {
        assembler.assemble("bne r2, r3, @100");
        assertEquals((byte) 13,  memory.read(0));
        assertEquals((byte) 2,   memory.read(1));
        assertEquals((byte) 3,   memory.read(2));
        assertEquals((byte) 0,   memory.read(3));
        assertEquals((byte) 100, memory.read(4));
    }

    // "break" → [0]
    @Test
    public void testAssembleBreak() throws Exception {
        assembler.assemble("break");
        assertEquals((byte) 0, memory.read(0));
    }

    // instruction inconnue → IllegalArgumentException
    @Test
    public void testInstructionInconnue() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> assembler.assemble("bidon r0, r1"));
    }

    // directive "data 0xFF, 0x01, 42" → trois octets bruts en mémoire
    @Test
    public void testAssembleData() throws Exception {
        assembler.assemble("data 0xFF, 0x01, 42");
        assertEquals((byte) 0xFF, memory.read(0));
        assertEquals((byte) 0x01, memory.read(1));
        assertEquals((byte) 42,   memory.read(2));
    }

    // directive 'string "hello"' → codes ASCII correspondants
    @Test
    public void testAssembleString() throws Exception {
        assembler.assemble("string \"hello\"");
        assertEquals((byte) 'h', memory.read(0));
        assertEquals((byte) 'e', memory.read(1));
        assertEquals((byte) 'l', memory.read(2));
        assertEquals((byte) 'l', memory.read(3));
        assertEquals((byte) 'o', memory.read(4));
    }

    // lignes commençant par ";" sont ignorées
    @Test
    public void testCommentairesIgnores() throws Exception {
        assembler.assemble("; ceci est un commentaire\nbreak");
        assertEquals((byte) 0, memory.read(0));
    }

    // programme complet : load / add / store / break → encodage vérifié octet par octet
    @Test
    public void testProgrammeComplet() throws Exception {
        assembler.assemble(
            "load r0, 10\n"    +
            "load r1, 20\n"    +
            "add r2, r0, r1\n" +
            "store r2, @1000\n"+
            "break"
        );
        assertEquals((byte) 1,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 10,   memory.read(2));
        assertEquals((byte) 1,    memory.read(3));
        assertEquals((byte) 1,    memory.read(4));
        assertEquals((byte) 20,   memory.read(5));
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
