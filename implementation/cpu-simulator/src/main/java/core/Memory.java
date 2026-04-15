package core;

import exception.MemoryOutOfBoundsException;

/**
 * Memoire du simulateur. C'est un gros tableau de 65536 octets (64 Ko).
 * Les adresses valides vont de 0 a 65535.
 */
public class Memory {

    public static final int MEMORY_SIZE = 65536;
    private byte[] data;

    public Memory() {
        data = new byte[MEMORY_SIZE];
    }

    // lit un octet a l'adresse donnee
    public byte read(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        return data[address];
    }

    // ecrit un octet a l'adresse donnee
    public void write(int address, byte value) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new MemoryOutOfBoundsException(address);
        }
        data[address] = value;
    }

    // lit une valeur sur 16 bits (2 octets, big-endian)
    public int readWord(int address) {
        int high = read(address) & 0xFF;
        int low = read(address + 1) & 0xFF;
        // le & 0xFF c'est pour eviter que le byte devienne negatif quand on le met dans un int
        return (high << 8) | low;
    }

    // ecrit une valeur sur 16 bits en big endian
    public void writeWord(int address, int value) {
        int high = value / 256;
        int low = value % 256;
        write(address, (byte) high);
        write(address + 1, (byte) low);
    }

    // remet toute la memoire a zero
    public void reset() {
        data = new byte[MEMORY_SIZE];
    }
}
