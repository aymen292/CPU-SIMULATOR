package core;

/**
 * Unité Arithmétique et Logique du simulateur.
 * Effectue les calculs sur des opérandes de 8 bits (byte).
 */
public class ALU {

    /**
     * Additionne deux octets signés.
     * Le résultat est tronqué sur 8 bits ;
     *
     * @param a premier opérande
     * @param b second opérande
     * @return (byte)(a + b) sur 8 bits
     */
    public byte add(byte a, byte b) {
        int somme = a + b;
        return (byte) somme;
    }

    /**
     * Soustrait b de a.
     *
     * @param a opérande dont on soustrait
     * @param b opérande à soustraire
     * @return (byte)(a - b) sur 8 bits
     */
    public byte sub(byte a, byte b) {
        int difference = a - b;
        return (byte) difference;
    }

    /**
     * Multiplie deux octets signés et renvoie le résultat sur 16 bits.
     * Le produit étant encodé dans un tableau de 2 octets :
     *
     * @param a premier facteur
     * @param b second facteur
     * @return tableau de 2 octets [octet_haut, octet_bas] représentant le produit 16 bits
     */
    public byte[] mul(byte a, byte b) {
        int produit = a * b;

        // limite du byte : un byte peut stocker des valeurs entre -127 et 128
        // sauf que le produit de deux bytes peut largement depasser cette valeur
        // dOnc on utilise deux bytes pour representer 16 bits
        return new byte[] {
            (byte) (produit >> 8),
            (byte) produit
        };
    }

    /**
     * Effectue la division entière de a par b.
     * Renvoie un tableau de 2 octets : result[0] = quotient, result[1] = reste.
     *
     * @param a dividende
     * @param b diviseur, doit être différent de zéro
     * @return tableau [quotient, reste]
     * @throws ArithmeticException si b vaut zéro
     */
    public byte[] div(byte a, byte b) {
    if (b == 0) {
        throw new ArithmeticException("Division par zéro interdite");
    }

    // On crée et on remplit le tableau directement avec le quotient et le reste
    return new byte[] {
        (byte) (a / b), // Position 0 : le quotient
        (byte) (a % b)  // Position 1 : le reste
    };
}

    /**
     * Calcule le ET logique bit à bit de a et b.
     *
     * @param a premier opérande
     * @param b second opérande
     * @return (byte)(a & b)
     */
    public byte and(byte a, byte b) {
        int resultat = a & b;
        return (byte) resultat;
    }

    /**
     * Calcule le OU logique bit à bit de a et b.
     *
     * @param a premier opérande
     * @param b second opérande
     * @return (byte)(a | b)
     */
    public byte or(byte a, byte b) {
        int resultat = a | b;
        return (byte) resultat;
    }

    /**
     * Calcule le OU exclusif bit à bit (XOR) de a et b.
     *
     * @param a premier opérande
     * @param b second opérande
     * @return (byte)(a ^ b)
     */
    public byte xor(byte a, byte b) {
        int resultat = a ^ b;
        return (byte) resultat;
    }
}
