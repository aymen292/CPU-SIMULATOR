package exception;

/**
 * Exception quand on tombe sur un opcode qu'on connait pas.
 */
public class InvalidOpcodeException extends RuntimeException {

    public InvalidOpcodeException(int code) {
        super("Opcode inconnu : " + code);
    }
}
