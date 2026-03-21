package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe RegisterFile.
 */
public class RegisterFileTest {

    private RegisterFile registers;

    /**
     * Initialise un nouveau banc de registres avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'on peut affecter et lire la valeur d'un registre.
     */
    @Test
    public void testGetSetRegister() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que tous les registres (0 à 15) sont accessibles en lecture/écriture.
     */
    @Test
    public void testAllRegisters() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que la valeur par défaut de chaque registre est 0.
     */
    @Test
    public void testDefaultValue() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'un get hors limites lève une RegisterOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsGet() {
        // TODO : à implémenter
    }

    /**
     * Vérifie qu'un set hors limites lève une RegisterOutOfBoundsException.
     */
    @Test
    public void testOutOfBoundsSet() {
        // TODO : à implémenter
    }

    /**
     * Vérifie que reset() remet tous les registres à zéro.
     */
    @Test
    public void testReset() {
        // TODO : à implémenter
    }
}
