package exception;

/**
 * Exception quand on donne un numero de registre qui n'existe pas (pas entre 0 et 15).
 */
public class RegisterOutOfBoundsException extends RuntimeException {

    public RegisterOutOfBoundsException(int index) {
        super("Registre hors limites : " + index + ". Les registres valides vont de 0 à 15.");
    }
}
