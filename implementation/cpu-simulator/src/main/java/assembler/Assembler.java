package assembler;

import core.Memory;

/**
 * Assembleur qui traduit un programme textuel en codes machine écrits en mémoire.
 */
public class Assembler {

    /** Mémoire cible dans laquelle écrire les instructions. */
    private Memory memory;

    /** Adresse courante d'écriture en mémoire. */
    private int currentAddress;

    /**
     * Construit un nouvel assembleur associé à une mémoire.
     * @param memory la mémoire cible
     */
    public Assembler(Memory memory) {
        // TODO : à implémenter
    }

    /**
     * Assemble un programme assembleur multiligne et écrit le code machine en mémoire.
     * @param program le programme assembleur sous forme de chaîne multiligne
     */
    public void assemble(String program) {
        // TODO : à implémenter
    }

    /**
     * Parse et traduit une ligne d'assembleur en code machine.
     * @param line la ligne à parser
     */
    private void parseLine(String line) {
        // TODO : à implémenter
    }

    /**
     * Écrit un octet en mémoire à currentAddress et incrémente l'adresse courante.
     * @param value l'octet à écrire
     */
    private void writeByte(byte value) {
        // TODO : à implémenter
    }

    /**
     * Écrit une adresse 16 bits en big-endian (2 octets) en mémoire.
     * @param address l'adresse 16 bits à écrire
     */
    private void writeAddress(int address) {
        // TODO : à implémenter
    }

    /**
     * Parse un token de la forme "rX" et retourne le numéro du registre.
     * @param token le token à parser (ex : "r3")
     * @return le numéro du registre
     */
    private int parseRegister(String token) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Parse une valeur décimale, hexadécimale ou une adresse préfixée par @.
     * @param token le token à parser (ex : "42", "0xFF", "@100")
     * @return la valeur entière parsée
     */
    private int parseValue(String token) {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Indique si un token représente une adresse mémoire (commence par @).
     * @param token le token à tester
     * @return true si le token commence par @
     */
    private boolean isAddress(String token) {
        // TODO : à implémenter
        return false;
    }
}
