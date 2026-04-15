package instruction;

/**
 * Les 16 instructions du processeur. Chacune a un code entre 0 et 15.
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

    private Opcode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // cherche l'opcode qui correspond au code donne, renvoie null si on trouve pas
    public static Opcode fromCode(int code) {
        for (Opcode op : Opcode.values()) {
            if (op.code == code) return op;
        }
        return null;
    }
}
