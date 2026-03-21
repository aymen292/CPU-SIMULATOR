package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Memory.
 */
public class MemoryTest {

    private Memory memory;

    /**
     * Initialise une nouvelle mémoire avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'on peut écrire et relire un octet à une adresse donnée.
     */
    @Test
    public void testReadWriteByte() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'on peut écrire et relire un mot de 16 bits.
     */
    @Test
    public void testReadWriteWord() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que la valeur par défaut d'une adresse non écrite est 0.
     */
    @Test
    public void testReadDefaultValue() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que les adresses aux limites de la mémoire sont accessibles.
     */
    @Test
    public void testBoundaryAddresses() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'une lecture hors limites lève une MemoryOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsRead() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'une écriture hors limites lève une MemoryOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsWrite() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que reset() remet toute la mémoire à zéro.
     */
    @Test
    public void testReset() {
        // TODO : à implémenter
    }
}
