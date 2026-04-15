package core;

import exception.RegisterOutOfBoundsException;

/**
 * Les 16 registres du processeur (r0 a r15). Chaque registre fait 8 bits (un byte).
 * C'est une petite memoire rapide directement dans le CPU.
 */
public class RegisterFile {

    public static final int NUM_REGISTERS = 16;
    private byte[] registers;

    public RegisterFile() {
        registers = new byte[NUM_REGISTERS];
    }

    // renvoie la valeur du registre
    public byte get(int index) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        return registers[index];
    }

    // met une valeur dans le registre
    public void set(int index, byte value) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new RegisterOutOfBoundsException(index);
        }
        registers[index] = value;
    }

    // remet tous les registres a zero
    public void reset() {
        registers = new byte[NUM_REGISTERS];
    }
}
