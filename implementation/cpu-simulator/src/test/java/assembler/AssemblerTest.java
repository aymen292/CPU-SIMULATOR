package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires pour la classe Assembler.
 * Vérifie que chaque mnémonique produit les bons octets en mémoire.
 *
 * Convention des adresses (big-endian 16 bits) :
 *   1000 = 0x03E8 → octet haut = 0x03, octet bas = 0xE8
 *    500 = 0x01F4 → octet haut = 0x01, octet bas = 0xF4
 */
public class AssemblerTest {

    private Memory    memory;
    private Assembler assembler;

    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        assembler = new Assembler(memory);
    }

    // ------------------------------------------------------------ LOAD_CONST

    /**
     * LOAD_CONST r3 42 → [opcode=1, dest=3, valeur=42]
     */
    @Test
    public void testAssembleLoadConst() {
        assembler.assemble("LOAD_CONST r3 42");
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 3,  memory.read(1));
        assertEquals((byte) 42, memory.read(2));
    }

    // -------------------------------------------------------------- LOAD_MEM

    /**
     * LOAD_MEM r0 @1000 → [opcode=2, dest=0, 0x03, 0xE8]
     */
    @Test
    public void testAssembleLoadMem() {
        assembler.assemble("LOAD_MEM r0 @1000");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x03, memory.read(2));
        assertEquals((byte) 0xE8, memory.read(3));
    }

    // ----------------------------------------------------------------- STORE

    /**
     * STORE r1 @500 → [opcode=3, src=1, 0x01, 0xF4]
     */
    @Test
    public void testAssembleStore() {
        assembler.assemble("STORE r1 @500");
        assertEquals((byte) 3,    memory.read(0));
        assertEquals((byte) 1,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0xF4, memory.read(3));
    }

    // -------------------------------------------------- Adresse hexadécimale

    /**
     * L'adresse @0x100 (= 256) est parsée en big-endian : 0x01, 0x00
     */
    @Test
    public void testAdresseHexadecimale() {
        assembler.assemble("LOAD_MEM r0 @0x100");
        assertEquals((byte) 2,    memory.read(0));
        assertEquals((byte) 0,    memory.read(1));
        assertEquals((byte) 0x01, memory.read(2));
        assertEquals((byte) 0x00, memory.read(3));
    }

    // --------------------------------------------------------------------- ADD

    /**
     * ADD r2 r0 r1 → [opcode=4, dest=2, regA=0, regB=1]
     */
    @Test
    public void testAssembleAdd() {
        assembler.assemble("ADD r2 r0 r1");
        assertEquals((byte) 4, memory.read(0));
        assertEquals((byte) 2, memory.read(1));
        assertEquals((byte) 0, memory.read(2));
        assertEquals((byte) 1, memory.read(3));
    }

    // -------------------------------------------------------------------- JUMP

    /**
     * JUMP @50 → [opcode=11, 0x00, 0x32]
     */
    @Test
    public void testAssembleJump() {
        assembler.assemble("JUMP @50");
        assertEquals((byte) 11, memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 50, memory.read(2));
    }

    // ------------------------------------------------------------------ .data

    /**
     * .data 0xFF 0x01 42 écrit trois octets bruts en mémoire.
     */
    @Test
    public void testAssembleData() {
        assembler.assemble(".data 0xFF 0x01 42");
        assertEquals((byte) 0xFF, memory.read(0));
        assertEquals((byte) 0x01, memory.read(1));
        assertEquals((byte) 42,   memory.read(2));
    }

    // ---------------------------------------------------------------- .string

    /**
     * .string "hello" écrit les codes ASCII de h, e, l, l, o.
     */
    @Test
    public void testAssembleString() {
        assembler.assemble(".string \"hello\"");
        assertEquals((byte) 'h', memory.read(0));
        assertEquals((byte) 'e', memory.read(1));
        assertEquals((byte) 'l', memory.read(2));
        assertEquals((byte) 'l', memory.read(3));
        assertEquals((byte) 'o', memory.read(4));
    }

    // --------------------------------------------------------------- Commentaires

    /**
     * Les lignes commençant par ';' sont ignorées.
     * BREAK doit donc se trouver à l'adresse 0.
     */
    @Test
    public void testCommentairesIgnores() {
        assembler.assemble("; ceci est un commentaire\nBREAK");
        assertEquals((byte) 0, memory.read(0)); // BREAK = opcode 0
    }

    // ---------------------------------------------------------- Programme complet

    /**
     * Programme : LOAD_CONST r0 10 | LOAD_CONST r1 20 | ADD r2 r0 r1 | STORE r2 @1000 | BREAK
     *
     * Layout en mémoire :
     *   addr 0-2  : [1, 0, 10]          ← LOAD_CONST r0 10
     *   addr 3-5  : [1, 1, 20]          ← LOAD_CONST r1 20
     *   addr 6-9  : [4, 2, 0, 1]        ← ADD r2 r0 r1
     *   addr 10-13: [3, 2, 0x03, 0xE8]  ← STORE r2 @1000
     *   addr 14   : [0]                 ← BREAK
     */
    @Test
    public void testProgrammeComplet() {
        assembler.assemble(
            "LOAD_CONST r0 10\n" +
            "LOAD_CONST r1 20\n" +
            "ADD r2 r0 r1\n"    +
            "STORE r2 @1000\n"  +
            "BREAK"
        );
        // LOAD_CONST r0 10
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 10, memory.read(2));
        // LOAD_CONST r1 20
        assertEquals((byte) 1,  memory.read(3));
        assertEquals((byte) 1,  memory.read(4));
        assertEquals((byte) 20, memory.read(5));
        // ADD r2 r0 r1
        assertEquals((byte) 4, memory.read(6));
        assertEquals((byte) 2, memory.read(7));
        assertEquals((byte) 0, memory.read(8));
        assertEquals((byte) 1, memory.read(9));
        // STORE r2 @1000
        assertEquals((byte) 3,    memory.read(10));
        assertEquals((byte) 2,    memory.read(11));
        assertEquals((byte) 0x03, memory.read(12));
        assertEquals((byte) 0xE8, memory.read(13));
        // BREAK
        assertEquals((byte) 0, memory.read(14));
    }
}
