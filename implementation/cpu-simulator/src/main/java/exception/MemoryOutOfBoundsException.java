package exception;

/**
 * Exception levée lorsqu'un accès mémoire cible une adresse hors de la
 * plage valide [0, 65535].
 * C'est une exception non vérifiée (RuntimeException) qui signale soit
 * une erreur dans le programme simulé, soit un bug d'implémentation du CPU.
 */
public class MemoryOutOfBoundsException extends RuntimeException {

    /**
     * Crée l'exception avec un message indiquant l'adresse invalide.
     *
     * @param address adresse mémoire hors limites ayant provoqué l'erreur
     */
    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address);
    }
}
