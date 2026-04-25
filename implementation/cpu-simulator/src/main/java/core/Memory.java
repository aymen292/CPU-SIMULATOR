package core;

import exception.MemoryOutOfBoundsException;

/**
 * Mémoire principale du simulateur.
 * Représente un espace de 65 536 octets (64 Ko), adressable de 0 à 65 535.
 * Toutes les cases sont initialisées à zéro à la construction.
 */
public class Memory {

    /** Taille totale de la mémoire en octets (64 Ko). */
    public static final int MEMORY_SIZE = 65536;

    private byte[] data;

    /**
     * Crée une mémoire de 65 536 octets, tous initialisés à zéro.
     */
    public Memory() {
        data = new byte[MEMORY_SIZE];
    }

    /**
     * Lit l'octet situé à l'adresse donnée.
     *
     * @param address adresse à lire, comprise entre 0 et 65 535
     * @return l'octet stocké à cette adresse
     * @throws MemoryOutOfBoundsException si l'adresse est hors de la plage valide
     */
    public byte read(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        return data[address];
    }

    /**
     * Écrit un octet à l'adresse donnée.
     *
     * @param address adresse où écrire, comprise entre 0 et 65 535
     * @param value   valeur à stocker
     * @throws MemoryOutOfBoundsException si l'adresse est hors de la plage valide
     */
    public void write(int address, byte value) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        data[address] = value;
    }

    /**
     * Lit un mot de 16 bits encodé en big-endian à partir de l'adresse donnée.
     * L'octet de poids fort se trouve à l'adresse, l'octet de poids faible à adresse + 1.
     *
     * @param address adresse du premier octet (poids fort)
     * @return valeur 16 bits non signée, dans la plage [0, 65 535]
     * @throws MemoryOutOfBoundsException si l'adresse ou l'adresse + 1 est hors limites
     */
    public int readWord(int address) {
        int high = read(address) & 0xFF;
        int low = read(address + 1) & 0xFF;
        return (high << 8) | low;
    }

    /**
     * Écrit un mot de 16 bits en big-endian à partir de l'adresse donnée.
     * L'octet de poids fort est écrit à l'adresse, l'octet de poids faible à adresse + 1.
     *
     * @param address adresse du premier octet (poids fort)
     * @param value   valeur 16 bits à stocker ; seuls les 16 bits de poids faible sont utilisés
     * @throws MemoryOutOfBoundsException si l'adresse ou l'adresse + 1 est hors limites
     */
    public void writeWord(int address, int value) {
        int high = value / 256;
        int low = value % 256;
        write(address, (byte) high);
        write(address + 1, (byte) low);
    }

    /**
     * Remet toutes les cases mémoire à zéro.
     * L'état précédent est perdu.
     */
    public void reset() {
        data = new byte[MEMORY_SIZE];
    }
}
