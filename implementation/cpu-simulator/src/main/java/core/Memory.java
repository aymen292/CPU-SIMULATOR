package core;

import exception.MemoryOutOfBoundsException;

/**
 * Modélise la mémoire vive (RAM) du simulateur. Elle est implémentée comme un tableau de 65536 octets , ce qui
 * représente 64 Ko adressables de 0x0000 à 0xFFFF . Elle gère les accès en lecture et en écritur.
 */
public class Memory {

    public static final int MEMORY_SIZE = 65536; // Taille totale de la mémoire en octets
    private byte[] data; // Tableau d'octets représentant la mémoire

    /**
     * Construit une nouvelle mémoire initialisée à zéro.
     */
    public Memory() {
        this.data = new byte[MEMORY_SIZE];
    }

    /**
     * Lit un octet à l'adresse donnée.
     * @param address l'adresse mémoire à lire
     * @return la valeur lue
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public byte read(int address) {
        if (address < 0) {
            throw new MemoryOutOfBoundsException(address);
        }
        if (address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        return data[address];
    }

    /**
     * Écrit un octet à l'adresse donnée.
     * @param address l'adresse mémoire où écrire
     * @param value la valeur à écrire
     * @throws MemoryOutOfBoundsException si l'adresse est hors limites
     */
    public void write(int address, byte value) {
        if (address < 0) {
            throw new MemoryOutOfBoundsException(address);
        }
        if (address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        data[address] = value;
    }

    /**
     * Lit un mot de 16 bits (2 octets) en big-endian à l'adresse donnée.
     * @param address l'adresse mémoire de départ
     * @return la valeur 16 bits non signée
     */
    public int readWord(int address) {
        int high = read(address) & 0xFF; // octet de poids fort, masqué pour éviter l'extension de signe
        int low  = read(address + 1) & 0xFF; // octet de poids faible, masqué pour éviter l'extension de signe
        return (high << 8) | low;
    }

    /**
     * Écrit un mot de 16 bits (2 octets) en big-endian à l'adresse donnée.
     * @param address l'adresse mémoire de départ
     * @param value la valeur 16 bits à écrire
     */
    public void writeWord(int address, int value) {
        // On décompose le nombre en deux octets
        int high = value / 256;   // octet de gauche (poids fort)
        int low  = value % 256;   // octet de droite (poids faible)

        write(address,     (byte) high);
        write(address + 1, (byte) low);
    }

    /**
     * Remet toute la mémoire à zéro.
     */
    public void reset() {
        // On recrée simplement un tableau vierge
        data = new byte[MEMORY_SIZE];
    }
}
