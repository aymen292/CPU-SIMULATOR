package core;

/**
 * Processeur simulé. Exécute le cycle fetch/decode/execute sur les instructions en mémoire.
 */
public class CPU {

    /** Mémoire du système. */
    private Memory memory;

    /** Banc de registres du processeur. */
    private RegisterFile registers;

    /** Unité arithmétique et logique. */
    private ALU alu;

    /** Compteur de programme (program counter). */
    private int pc;

    /** Indicateur d'exécution en cours. */
    private boolean running;

    /**
     * Construit un nouveau CPU avec la mémoire et les registres donnés.
     * @param memory la mémoire du système
     * @param registers le banc de registres
     */
    public CPU(Memory memory, RegisterFile registers) {
        // TODO : à implémenter
    }

    /**
     * Lance la boucle d'exécution jusqu'à l'instruction BREAK.
     */
    public void run() {
        // TODO : à implémenter
    }

    /**
     * Remet le compteur de programme à 0 et arrête l'exécution.
     */
    public void reset() {
        // TODO : à implémenter
    }

    /**
     * Retourne la valeur du compteur de programme.
     * @return la valeur du compteur de programme
     */
    public int getPC() {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Indique si le CPU est en cours d'exécution.
     * @return true si le CPU est en cours d'exécution
     */
    public boolean isRunning() {
        // TODO : à implémenter
        return false;
    }

    /**
     * Lit l'octet à l'adresse PC et incrémente le PC.
     * @return l'octet lu
     */
    private byte fetch() {
        // TODO : à implémenter
        return 0;
    }

    /**
     * Décode l'opcode et exécute l'instruction correspondante.
     * @param opcodeByte l'octet représentant l'opcode à décoder
     */
    private void decode(byte opcodeByte) {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction BREAK : arrête l'exécution du CPU.
     */
    private void executeBreak() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction LOAD_CONST : charge une constante dans un registre.
     * Lit en mémoire : [registre destination, valeur constante].
     */
    private void executeLoadConst() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction LOAD_MEM : charge une valeur depuis une adresse mémoire dans un registre.
     * Lit en mémoire : [registre destination, adresse 16 bits].
     */
    private void executeLoadMem() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction STORE : écrit la valeur d'un registre en mémoire.
     * Lit en mémoire : [registre source, adresse 16 bits].
     */
    private void executeStore() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction ADD : additionne deux registres et stocke le résultat.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeAdd() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction SUB : soustrait deux registres et stocke le résultat.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeSub() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction MUL : multiplie deux registres et stocke le résultat sur 16 bits.
     * Lit en mémoire : [registre high destination, registre low destination, registre A, registre B].
     */
    private void executeMul() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction DIV : divise deux registres et stocke le quotient et le reste.
     * Lit en mémoire : [registre quotient, registre reste, registre A, registre B].
     */
    private void executeDiv() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction AND : effectue un ET logique entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeAnd() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction OR : effectue un OU logique entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeOr() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction XOR : effectue un OU exclusif entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeXor() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction JUMP : effectue un saut inconditionnel à une adresse.
     * Lit en mémoire : [adresse 16 bits de destination].
     */
    private void executeJump() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction BEQ : saut conditionnel si deux registres sont égaux.
     * Lit en mémoire : [registre A, registre B, adresse 16 bits de destination].
     */
    private void executeBeq() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction BNE : saut conditionnel si deux registres sont différents.
     * Lit en mémoire : [registre A, registre B, adresse 16 bits de destination].
     */
    private void executeBne() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction LOAD_INDEXED : charge depuis l'adresse base + offset registre.
     * Lit en mémoire : [registre destination, adresse 16 bits de base, registre offset].
     */
    private void executeLoadIndexed() {
        // TODO : à implémenter
    }

    /**
     * Exécute l'instruction STORE_INDEXED : écrit à l'adresse base + offset registre.
     * Lit en mémoire : [registre source, adresse 16 bits de base, registre offset].
     */
    private void executeStoreIndexed() {
        // TODO : à implémenter
    }
}
