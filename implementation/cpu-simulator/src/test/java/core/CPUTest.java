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
     */
    @BeforeEach
    public void setUp() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que l'instruction BREAK arrête correctement l'exécution.
     */
    @Test
    public void testBreak() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que LOAD_CONST charge bien une constante dans un registre.
     */
    @Test
    public void testLoadConst() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que LOAD_MEM charge bien une valeur depuis la mémoire.
     */
    @Test
    public void testLoadMem() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que STORE écrit bien la valeur d'un registre en mémoire.
     */
    @Test
    public void testStore() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que ADD additionne correctement deux registres.
     */
    @Test
    public void testAdd() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que JUMP effectue bien un saut inconditionnel.
     */
    @Test
    public void testJump() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que BEQ effectue un saut si les deux registres sont égaux.
     */
    @Test
    public void testBeq() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que BNE effectue un saut si les deux registres sont différents.
     */
    @Test
    public void testBne() {
        // TODO : à implémenter
    }
}
