package core;

/**
 * Unité Arithmétique et Logique (ALU). C'est le composant qui réalise tous les calculs du processeur. Elle ne possède
 * aucun attribut : elle reçoit des opérandes, effectue une opération, et retourne un resultat.
 */
public class ALU {

    /**
     * Additionne deux valeurs sur 8 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a + b
     */
    public byte add(byte a, byte b) {
        return (byte) (a + b) ;
    }

    /**
     * Soustrait deux valeurs sur 8 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a - b
     */
    public byte sub(byte a, byte b) {
        return (byte) (a - b);
    }

    /**
     * Multiplie deux valeurs sur 8 bits et retourne un résultat 16 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return tableau [highByte, lowByte] du résultat 16 bits
     */
    public byte[] mul(byte a, byte b) {
        // On multiplie les deux valeurs en int pour éviter les pertes
        int resultat = a * b;

        // On décompose le résultat en deux octets
        int octetHaut = resultat / 256;  // partie gauche
        int octetBas  = resultat % 256;  // partie droite

        // On crée un tableau de 2 octets et on le retourne
        byte[] tableau = new byte[2];
        tableau[0] = (byte) octetHaut;
        tableau[1] = (byte) octetBas;

        return tableau;

    }

    /**
     * Divise deux valeurs sur 8 bits et retourne le quotient et le reste.
     * @param a le dividende
     * @param b le diviseur
     * @return tableau [quotient, reste]
     * @throws ArithmeticException si b == 0
     */
    public byte[] div(byte a, byte b) {
        // La division par zéro est impossible en mathématiques
        if (b == 0) {
            throw new ArithmeticException("Division par zéro interdite");
        }

        byte quotient = (byte) (a / b);  // résultat entier de la division
        byte reste    = (byte) (a % b);  // ce qui reste après la division

        return new byte[]{quotient, reste};
    }

    /**
     * Effectue un ET logique bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a & b
     */
    public byte and(byte a, byte b) {
        return (byte) (a & b);
    }

    /**
     * Effectue un OU logique bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a | b
     */
    public byte or(byte a, byte b) {
        return (byte) (a | b);
    }

    /**
     * Effectue un OU exclusif bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a ^ b
     */
    public byte xor(byte a, byte b) {
        return (byte) (a ^ b);
    }
}
