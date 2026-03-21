package exception;

/**
 * Exception levée lorsqu'un code d'instruction inconnu est rencontré.
 */
public class InvalidOpcodeException extends RuntimeException {

    /**
     * Construit une exception pour un code opcode invalide.
     * @param code le code numérique inconnu
     */
    public InvalidOpcodeException(int code) {
        super("Opcode inconnu : " + code);
    }
}
