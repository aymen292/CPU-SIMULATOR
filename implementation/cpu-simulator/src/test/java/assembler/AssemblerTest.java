package assembler;

import core.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Assembler.
 */
public class AssemblerTest {

    private Memory memory;
    private Assembler assembler;

    /**
     * Initialise la mémoire et l'assembleur avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que LOAD_CONST est correctement assemblé en code machine.
     */
    @Test
    public void testAssembleLoadConst() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que LOAD_MEM est correctement assemblé avec une adresse.
     */
    @Test
    public void testAssembleLoadMem() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que STORE est correctement assemblé.
     */
    @Test
    public void testAssembleStore() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que les adresses en hexadécimal sont correctement parsées.
     */
    @Test
    public void testAssembleHexAddress() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que ADD est correctement assemblé avec ses opérandes.
     */
    @Test
    public void testAssembleAdd() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que JUMP est correctement assemblé avec son adresse de destination.
     */
    @Test
    public void testAssembleJump() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que les données brutes (.data) sont correctement écrites en mémoire.
     */
    @Test
    public void testAssembleData() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que les chaînes de caractères (.string) sont correctement écrites en mémoire.
     */
    @Test
    public void testAssembleString() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que les commentaires sont ignorés lors de l'assemblage.
     */
    @Test
    public void testIgnoreComments() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'un programme complet est correctement assemblé de bout en bout.
     */
    @Test
    public void testFullProgram() {
        // TODO : à implémenter
    }
}
