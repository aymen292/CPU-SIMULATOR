package exception;

/**
 * Exception quand on essaye de lire ou ecrire a une adresse qui n'existe pas.
 */
public class MemoryOutOfBoundsException extends RuntimeException {

    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address);
    }
}
