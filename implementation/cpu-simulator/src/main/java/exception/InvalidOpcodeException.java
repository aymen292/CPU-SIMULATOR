package exception;

public class InvalidOpcodeException extends RuntimeException {

    public InvalidOpcodeException(int code) {
        super("Opcode inconnu : " + code);
    }
}
