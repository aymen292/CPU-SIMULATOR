package exception;

/**
 * Exception levée lors d'un accès mémoire hors limites.
 */
public class MemoryOutOfBoundsException extends RuntimeException {

    /**
     * Construit une exception pour une adresse mémoire invalide.
     * @param address l'adresse hors limites
     */
    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address);
    }
}
