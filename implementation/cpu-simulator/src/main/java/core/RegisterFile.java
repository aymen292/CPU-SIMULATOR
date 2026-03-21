package core;

import exception.RegisterOutOfBoundsException;

/**
 * Représente le banc de 16 registres 8 bits du processeur.
 */
public class RegisterFile {

    /** Nombre total de registres. */
    public static final int NUM_REGISTERS = 16;

    /** Tableau des valeurs de registres. */
    private byte[] registers;

    /**
     * Construit un nouveau banc de registres initialisés à zéro.
     */
    public RegisterFile() {
        // TODO : à implémenter
    }

    /**
     * Retourne la valeur d'un registre.
     * @param index l'index du registre (0 à 15)
     * @return la valeur du registre
     * @throws RegisterOutOfBoundsException si l'index est hors limites
     */
    public byte get(int index) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Définit la valeur d'un registre.
     * @param index l'index du registre (0 à 15)
     * @param value la valeur à affecter
     * @throws RegisterOutOfBoundsException si l'index est hors limites
     */
    public void set(int index, byte value) {
        // TODO : à implémenter
    }

    /**
     * Remet tous les registres à zéro.
     */
    public void reset() {
        // TODO : à implémenter
    }
}
