package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Assembler.
 * Vérifie que chaque instruction est correctement traduite en code machine
 * et écrite en mémoire au bon format (big-endian pour les adresses 16 bits).
 */
public class AssemblerTest {

    private Memory memory;
    private Assembler assembler;

    /**
     * Initialise la mémoire et l'assembleur avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        memory    = new Memory();
        assembler = new Assembler(memory);
    }

    /**
     * Vérifie que LOAD_CONST est correctement assemblé en code machine.
     * Format attendu en mémoire : [opcode=1, registre, constante]
     */
    @Test
    public void testAssembleLoadConst() {
        assembler.assemble("LOAD_CONST r3 42");
        assertEquals((byte) 1,  memory.read(0)); // opcode LOAD_CONST
        assertEquals((byte) 3,  memory.read(1)); // registre destination
        assertEquals((byte) 42, memory.read(2)); // valeur constante
    }

    /**
     * Vérifie que LOAD_MEM est correctement assemblé avec une adresse.
     * Format attendu en mémoire : [opcode=2, registre, adresse_high, adresse_low]
     */
    @Test
    public void testAssembleLoadMem() {
        // 1000 = 0x03E8 => octet haut = 0x03, octet bas = 0xE8
        assembler.assemble("LOAD_MEM r0 @1000");
        assertEquals((byte) 2,    memory.read(0)); // opcode LOAD_MEM
        assertEquals((byte) 0,    memory.read(1)); // registre destination
        assertEquals((byte) 0x03, memory.read(2)); // octet haut de l'adresse 1000
        assertEquals((byte) 0xE8, memory.read(3)); // octet bas de l'adresse 1000
    }

    /**
     * Vérifie que STORE est correctement assemblé.
     * Format attendu en mémoire : [opcode=3, registre, adresse_high, adresse_low]
     */
    @Test
    public void testAssembleStore() {
        // 500 = 0x01F4 => octet haut = 0x01, octet bas = 0xF4
        assembler.assemble("STORE r1 @500");
        assertEquals((byte) 3,    memory.read(0)); // opcode STORE
        assertEquals((byte) 1,    memory.read(1)); // registre source
        assertEquals((byte) 0x01, memory.read(2)); // octet haut de l'adresse 500
        assertEquals((byte) 0xF4, memory.read(3)); // octet bas de l'adresse 500
    }

    /**
     * Vérifie que les adresses en hexadécimal sont correctement parsées.
     * Format attendu en mémoire : [opcode=2, registre, adresse_high, adresse_low]
     */
    @Test
    public void testAssembleHexAddress() {
        // 0x100 = 256 => octet haut = 0x01, octet bas = 0x00
        assembler.assemble("LOAD_MEM r0 @0x100");
        assertEquals((byte) 2,    memory.read(0)); // opcode LOAD_MEM
        assertEquals((byte) 0,    memory.read(1)); // registre destination
        assertEquals((byte) 0x01, memory.read(2)); // octet haut de l'adresse 0x100
        assertEquals((byte) 0x00, memory.read(3)); // octet bas de l'adresse 0x100
    }

    /**
     * Vérifie que ADD est correctement assemblé avec ses opérandes.
     * Format attendu en mémoire : [opcode=4, registre_dest, registre_A, registre_B]
     */
    @Test
    public void testAssembleAdd() {
        assembler.assemble("ADD r2 r0 r1");
        assertEquals((byte) 4, memory.read(0)); // opcode ADD
        assertEquals((byte) 2, memory.read(1)); // registre destination
        assertEquals((byte) 0, memory.read(2)); // registre opérande A
        assertEquals((byte) 1, memory.read(3)); // registre opérande B
    }

    /**
     * Vérifie que JUMP est correctement assemblé avec son adresse de destination.
     * Format attendu en mémoire : [opcode=11, adresse_high, adresse_low]
     */
    @Test
    public void testAssembleJump() {
        // 50 = 0x0032 => octet haut = 0x00, octet bas = 0x32
        assembler.assemble("JUMP @50");
        assertEquals((byte) 11, memory.read(0)); // opcode JUMP
        assertEquals((byte) 0,  memory.read(1)); // octet haut de l'adresse 50
        assertEquals((byte) 50, memory.read(2)); // octet bas de l'adresse 50
    }

    /**
     * Vérifie que les données brutes (.data) sont correctement écrites en mémoire.
     * Chaque valeur est écrite telle quelle sur un octet.
     */
    @Test
    public void testAssembleData() {
        assembler.assemble(".data 0xFF 0x01 42");
        assertEquals((byte) 0xFF, memory.read(0)); // premier octet
        assertEquals((byte) 0x01, memory.read(1)); // deuxième octet
        assertEquals((byte) 42,   memory.read(2)); // troisième octet
    }

    /**
     * Vérifie que les chaînes de caractères (.string) sont correctement écrites en mémoire.
     * Chaque caractère est écrit en tant qu'octet selon son code ASCII.
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

    /**
     * Vérifie que les commentaires sont ignorés lors de l'assemblage.
     * Une ligne de commentaire ne doit produire aucun octet en mémoire.
     */
    @Test
    public void testIgnoreComments() {
        assembler.assemble("; ceci est un commentaire\nBREAK");
        // Le commentaire est ignoré, BREAK doit se trouver à l'adresse 0
        assertEquals((byte) 0, memory.read(0)); // opcode BREAK
    }

    /**
     * Vérifie qu'un programme complet est correctement assemblé de bout en bout.
     * Le programme charge deux constantes, les additionne, stocke le résultat et s'arrête.
     */
    @Test
    public void testFullProgram() {
        String program =
            "LOAD_CONST r0 10\n" +
            "LOAD_CONST r1 20\n" +
            "ADD r2 r0 r1\n" +
            "STORE r2 @1000\n" +
            "BREAK";
        assembler.assemble(program);

        // LOAD_CONST r0 10 : [1, 0, 10] -> adresses 0, 1, 2
        assertEquals((byte) 1,  memory.read(0));
        assertEquals((byte) 0,  memory.read(1));
        assertEquals((byte) 10, memory.read(2));

        // LOAD_CONST r1 20 : [1, 1, 20] -> adresses 3, 4, 5
        assertEquals((byte) 1,  memory.read(3));
        assertEquals((byte) 1,  memory.read(4));
        assertEquals((byte) 20, memory.read(5));

        // ADD r2 r0 r1 : [4, 2, 0, 1] -> adresses 6, 7, 8, 9
        assertEquals((byte) 4, memory.read(6));
        assertEquals((byte) 2, memory.read(7));
        assertEquals((byte) 0, memory.read(8));
        assertEquals((byte) 1, memory.read(9));

        // STORE r2 @1000 : [3, 2, 0x03, 0xE8] -> adresses 10, 11, 12, 13
        assertEquals((byte) 3,    memory.read(10));
        assertEquals((byte) 2,    memory.read(11));
        assertEquals((byte) 0x03, memory.read(12));
        assertEquals((byte) 0xE8, memory.read(13));

        // BREAK : [0] -> adresse 14
        assertEquals((byte) 0, memory.read(14));
    }
}
