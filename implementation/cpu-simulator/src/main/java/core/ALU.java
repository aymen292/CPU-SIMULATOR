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
     * @return (byte)(a + b), tronqué sur 8 bits
     */
    public byte add(byte a, byte b) {
        int resultat = a + b;
        return (byte) resultat;
    }

    /**
     * Soustrait b de a.
     * Le résultat est tronqué sur 8 bits sans signalement d'un éventuel dépassement.
     *
     * @param a opérande dont on soustrait
     * @param b opérande à soustraire
     * @return (byte)(a - b), tronqué sur 8 bits
     */
    public byte sub(byte a, byte b) {
        int resultat = a - b;
        return (byte) resultat;
    }

    /**
     * Multiplie deux octets signés et renvoie le résultat sur 16 bits.
     * Le produit étant encodé dans un tableau de 2 octets :
     * - result[0] : octet de poids fort (bits 15 à 8)
     * - result[1] : octet de poids faible (bits 7 à 0)
     * Exemple : 50 * 10 = 500 → result[0] = 1, result[1] = -12.
     *
     * @param a premier facteur
     * @param b second facteur
     * @return tableau de 2 octets [octet_haut, octet_bas] représentant le produit 16 bits
     */
    public byte[] mul(byte a, byte b) {
        int resultat = a * b;

        // On coupe le résultat en deux (octet haut et bas) car un seul byte est trop petit pour 127x127.
        int octetHaut = resultat / 256;
        int octetBas  = resultat % 256;

        byte[] tab = new byte[2];
        tab[0] = (byte) octetHaut;
        tab[1] = (byte) octetBas;

        return tab;
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

        byte quotient = (byte) (a / b);
        byte reste    = (byte) (a % b);

        byte[] resultat = new byte[2];
        resultat[0] = quotient;
        resultat[1] = reste;

        return resultat;
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
