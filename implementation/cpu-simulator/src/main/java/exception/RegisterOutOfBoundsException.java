package exception;

/**
 * Exception levée lorsqu'un accès au banc de registres utilise un numéro
 * de registre hors de la plage valide [0, 15].
 * C'est une exception non vérifiée (RuntimeException).
 */
public class RegisterOutOfBoundsException extends RuntimeException {

    /**
     * Crée l'exception avec un message indiquant le numéro de registre invalide
     * et la plage autorisée (0 à 15).
     *
     * @param index numéro de registre hors limites fourni lors de l'accès
     */
    public RegisterOutOfBoundsException(int index) {
        super("Registre hors limites : " + index + " (il faut un numero entre 0 et 15)");
    }
}
