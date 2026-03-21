package instruction;

/**
 * Enumération des codes d'instructions du processeur simulé.
 * Chaque constante représente une instruction avec son code numérique associé.
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

    /** Code numérique de l'instruction. */
    private final int code;

    /**
     * Constructeur privé de l'enum.
     * @param code le code numérique associé à l'instruction
     */
    private Opcode(int code) {
        this.code = code;
    }

    /**
     * Retourne le code numérique de l'instruction.
     * @return le code numérique
     */
    public int getCode() {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Retourne l'Opcode correspondant à un code numérique donné.
     * @param code le code numérique à rechercher
     * @return l'Opcode correspondant, ou null si inconnu
     */
    public static Opcode fromCode(int code) {
        // TODO : à implémenter
        return null;
    }
}
