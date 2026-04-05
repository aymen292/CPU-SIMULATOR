package core;

import exception.RegisterOutOfBoundsException;

/**
 * Représente le banc de 16 registres 8 bits du processeur.C'est une zone de stockage , directement accessible par
 * le CPU , contrairement à la mémoire principale. Implémentée comme un tableau de byte[16]
 */
public class RegisterFile {

    public static final int NUM_REGISTERS = 16; // Nombre total de registres
    private byte[] registers; // Tableau des valeurs de registres/

    /**
     * Construit un nouveau banc de registres initialisés à zéro.
     */
    public RegisterFile() {
        registers = new byte[NUM_REGISTERS];
    }

    /**
     * Retourne la valeur d'un registre.
     * @param index l'index du registre (0 à 15)
     * @return la valeur du registre
     * @throws RegisterOutOfBoundsException si l'index est hors limites
     */
    public byte get(int index) {
        // On vérifie que le numéro de registre demandé existe bien
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        return registers[index];
    }

    /**
     * Définit la valeur d'un registre.
     * @param index l'index du registre (0 à 15)
     * @param value la valeur à affecter
     * @throws RegisterOutOfBoundsException si l'index est hors limites
     */
    public void set(int index, byte value) {
        // On vérifie que le numéro de registre demandé existe bien
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        registers[index] = value;
    }

    /**
     * Remet tous les registres à zéro.
     */
    public void reset() {
        // On recrée simplement un tableau vierge
        registers = new byte[NUM_REGISTERS];
    }
}
