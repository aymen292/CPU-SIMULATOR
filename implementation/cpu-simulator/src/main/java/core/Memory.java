package core;

import exception.MemoryOutOfBoundsException;

/**
 * Représente la mémoire du simulateur (64 Ko = 65536 octets).
 */
public class Memory {

    /** Taille totale de la mémoire en octets. */
    public static final int MEMORY_SIZE = 65536;

    /** Tableau d'octets représentant la mémoire. */
    private byte[] data;

    /**
     * Construit une nouvelle mémoire initialisée à zéro.
     */
    public Memory() {
        // TODO : à implémenter
    }

    /**
     * Lit un octet à l'adresse donnée.
     * @param address l'adresse mémoire à lire
     * @return la valeur lue
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public byte read(int address) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Écrit un octet à l'adresse donnée.
     * @param address l'adresse mémoire où écrire
     * @param value la valeur à écrire
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public void write(int address, byte value) {
        // TODO : à implémenter
    }

    /**
     * Lit un mot de 16 bits (2 octets) en big-endian à l'adresse donnée.
     * @param address l'adresse mémoire de départ
     * @return la valeur 16 bits non signée
     */
    public int readWord(int address) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Écrit un mot de 16 bits (2 octets) en big-endian à l'adresse donnée.
     * @param address l'adresse mémoire de départ
     * @param value la valeur 16 bits à écrire
     */
    public void writeWord(int address, int value) {
        // TODO : à implémenter
    }

    /**
     * Remet toute la mémoire à zéro.
     */
    public void reset() {
        // TODO : à implémenter
    }
}
