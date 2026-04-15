package core;

/**
 * L'ALU fait les calculs du processeur (add, sub, mul, div, and, or, xor).
 * Elle n'a pas d'attribut : on lui passe les deux operandes et elle renvoie le resultat.
 */
public class ALU {

    public byte add(byte a, byte b) {
        return (byte) (a + b);
    }

    public byte sub(byte a, byte b) {
        return (byte) (a - b);
    }

    // multiplication : le resultat peut aller jusqu'a 16 bits donc on renvoie 2 octets
    public byte[] mul(byte a, byte b) {
        int resultat = a * b;

        // on decoupe le resultat en 2 octets
        int octetHaut = resultat / 256;
        int octetBas  = resultat % 256;

        byte[] tab = new byte[2];
        tab[0] = (byte) octetHaut;
        tab[1] = (byte) octetBas;
        return tab;
    }

    // division : renvoie le quotient et le reste
    public byte[] div(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro interdite");
        }
        byte quotient = (byte) (a / b);
        byte reste    = (byte) (a % b);
        return new byte[]{quotient, reste};
    }

    public byte and(byte a, byte b) {
        return (byte) (a & b);
    }

    public byte or(byte a, byte b) {
        return (byte) (a | b);
    }

    public byte xor(byte a, byte b) {
        return (byte) (a ^ b);
    }
}
