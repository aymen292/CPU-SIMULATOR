package exception;

/**
 * Exception quand on donne un numero de registre qui n'existe pas.
 */
public class RegisterOutOfBoundsException extends RuntimeException {

    public RegisterOutOfBoundsException(int index) {
        super("Registre hors limites : " + index + " (il faut un numero entre 0 et 15)");
    }
}
