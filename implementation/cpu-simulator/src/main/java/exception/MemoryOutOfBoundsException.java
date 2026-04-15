package exception;

/**
 * Exception levee quand on essaye d'acceder a une adresse qui n'existe pas.
 * La memoire va de 0 a 65535.
 */
public class MemoryOutOfBoundsException extends RuntimeException {

    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address);
    }
}
