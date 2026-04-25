package exception;

/**
 * Exception levée lorsque le CPU rencontre un octet qui ne correspond
 * à aucun opcode valide du jeu d'instructions.
 * C'est une exception non vérifiée (RuntimeException) qui interrompt
 * immédiatement l'exécution du programme simulé.
 */
public class InvalidOpcodeException extends RuntimeException {

    /**
     * Crée l'exception avec un message indiquant la valeur de l'opcode inconnu.
     *
     * @param code valeur de l'octet qui n'a pas pu être décodé
     */
    public InvalidOpcodeException(int code) {
        super("Opcode inconnu : " + code);
    }
}
