package core;

import exception.RegisterOutOfBoundsException;

/**
 * Banc de 16 registres généraux du processeur (r0 à r15).
 * Chaque registre stocke un octet signé (8 bits).
 * Tous les registres sont initialisés à zéro à la construction.
 */
public class RegisterFile {

    /** Nombre de registres disponibles dans le processeur. */
    public static final int NUM_REGISTERS = 16;

    private byte[] registers;

    /**
     * Crée un banc de 16 registres, tous initialisés à zéro.
     */
    public RegisterFile() {
        registers = new byte[NUM_REGISTERS];
    }

    /**
     * Retourne la valeur du registre numéro index.
     * Lève une RegisterOutOfBoundsException si index est hors de la plage [0, 15].
     *
     * @param index numéro du registre, compris entre 0 et 15
     * @return valeur 8 bits stockée dans le registre
     * @throws RegisterOutOfBoundsException si index est hors de la plage [0, 15]
     */
    public byte get(int index) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        return registers[index];
    }

    /**
     * Écrit value dans le registre numéro index.
     * Lève une RegisterOutOfBoundsException si index est hors de la plage [0, 15].
     *
     * @param index numéro du registre, compris entre 0 et 15
     * @param value valeur 8 bits à stocker
     * @throws RegisterOutOfBoundsException si index est hors de la plage [0, 15]
     */
    public void set(int index, byte value) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        registers[index] = value;
    }

    /**
     * Remet tous les registres à zéro.
     * L'état précédent de tous les registres est perdu.
     */
    public void reset() {
        registers = new byte[NUM_REGISTERS];
    }
}
