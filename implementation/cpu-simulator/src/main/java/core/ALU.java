package core;

/**
 * Unité Arithmétique et Logique (ALU). Effectue les opérations de calcul sur les registres.
 */
public class ALU {

    /**
     * Additionne deux valeurs sur 8 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a + b
     */
    public byte add(byte a, byte b) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Soustrait deux valeurs sur 8 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a - b
     */
    public byte sub(byte a, byte b) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Multiplie deux valeurs sur 8 bits et retourne un résultat 16 bits.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return tableau [highByte, lowByte] du résultat 16 bits
     */
    public byte[] mul(byte a, byte b) {
        // TODO : à implémenter
        return null;
    }

    /**
     * Divise deux valeurs sur 8 bits et retourne le quotient et le reste.
     * @param a le dividende
     * @param b le diviseur
     * @return tableau [quotient, reste]
     * @throws ArithmeticException si b == 0
     */
    public byte[] div(byte a, byte b) {
        // TODO : à implémenter
        return null;
    }

    /**
     * Effectue un ET logique bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a & b
     */
    public byte and(byte a, byte b) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Effectue un OU logique bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a | b
     */
    public byte or(byte a, byte b) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Effectue un OU exclusif bit à bit entre deux valeurs.
     * @param a premier opérande
     * @param b deuxième opérande
     * @return a ^ b
     */
    public byte xor(byte a, byte b) {
        // TODO : à implémenter
        return 0;
    }
}
