package instruction;

/**
 * Ensemble des 16 opcodes du jeu d'instructions du processeur simulé.
 * Chaque constante représente une instruction et possède un code numérique
 * compris entre 0 et 15. Ce code est l'octet écrit en mémoire par l'assembleur
 * et lu par le CPU lors du décodage.
 *
 * Tableau récapitulatif :
 *   0  BREAK          Arrête le CPU
 *   1  LOAD_CONST     Charge une constante dans un registre
 *   2  LOAD_MEM       Charge un octet depuis la mémoire
 *   3  STORE          Écrit un registre en mémoire
 *   4  ADD            Addition
 *   5  SUB            Soustraction
 *   6  MUL            Multiplication 16 bits
 *   7  DIV            Division entière
 *   8  AND            ET logique bit à bit
 *   9  OR             OU logique bit à bit
 *  10  XOR            OU exclusif bit à bit
 *  11  JUMP           Saut inconditionnel
 *  12  BEQ            Saut si égal
 *  13  BNE            Saut si différent
 *  14  LOAD_INDEXED   Chargement indexé (base + offset)
 *  15  STORE_INDEXED  Stockage indexé  (base + offset)
 */
public enum Opcode {

    BREAK(0),
    LOAD_CONST(1),
    LOAD_MEM(2),
    STORE(3),
    ADD(4),
    SUB(5),
    MUL(6),
    DIV(7),
    AND(8),
    OR(9),
    XOR(10),
    JUMP(11),
    BEQ(12),
    BNE(13),
    LOAD_INDEXED(14),
    STORE_INDEXED(15);

    private final int code;

    /**
     * Associe un code numérique à la constante d'opcode.
     *
     * @param code valeur entière de l'opcode, comprise entre 0 et 15
     */
    private Opcode(int code) {
        this.code = code;
    }

    /**
     * Retourne le code numérique de cet opcode.
     * C'est la valeur écrite en mémoire par l'assembleur et lue par le CPU.
     *
     * @return code entier compris entre 0 et 15
     */
    public int getCode() {
        return code;
    }

    /**
     * Recherche l'opcode correspondant au code numérique donné.
     * Retourne null si aucun opcode ne correspond.
     *
     * @param code valeur numérique à rechercher
     * @return l'opcode correspondant, ou null si le code est inconnu
     */
    public static Opcode fromCode(int code) {
        Opcode[] tousLesOpcodes = Opcode.values();

        for (int i = 0; i < tousLesOpcodes.length; i++) {
            if (tousLesOpcodes[i].code == code) {
                return tousLesOpcodes[i];
            }
        }

        return null;
    }
}
