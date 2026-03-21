package exception;

/**
 * Exception levée lors d'un accès à un registre invalide.
 */
public class RegisterOutOfBoundsException extends RuntimeException {

    /**
     * Construit une exception pour un index de registre invalide.
     * @param index l'index du registre hors limites
     */
    public RegisterOutOfBoundsException(int index) {
        super("Registre hors limites : " + index + ". Les registres valides vont de 0 à 15.");
    }
}
