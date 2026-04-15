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

    // multiplication : resultat sur 16 bits donc on le renvoie en 2 octets
    public byte[] mul(byte a, byte b) {
        int resultat = a * b;
        // on coupe en 2 : la partie haute et la partie basse
        int haut = resultat / 256;
        int bas = resultat % 256;
        byte[] tab = new byte[2];
        tab[0] = (byte) haut;
        tab[1] = (byte) bas;
        return tab;
    }

    // division entiere : on renvoie [quotient, reste]
    public byte[] div(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro interdite");
        }
        byte q = (byte) (a / b);
        byte r = (byte) (a % b);
        return new byte[]{q, r};
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
