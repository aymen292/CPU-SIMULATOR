package core;

import instruction.Opcode;
import exception.InvalidOpcodeException;

/**
 * Processeur central du simulateur.
 * Implémente le cycle Fetch → Decode → Execute :
 * - Fetch   : lit l'octet pointé par le compteur ordinal (PC) et avance le PC.
 * - Decode  : identifie l'instruction à partir de l'opcode lu.
 * - Execute : lit les opérandes et applique l'opération sur les registres ou la mémoire.
 * L'exécution se répète jusqu'à l'instruction BREAK.
 */
public class CPU {

    private Memory memory;
    private RegisterFile registers;
    private ALU alu;
    private int pc;          // compteur : adresse de la prochaine instruction
    private boolean running; // true pendant que le CPU exécute des instructions

    /**
     * Crée un CPU connecté à la mémoire et au banc de registres fournis.
     * Le PC est positionné à l'adresse 0 et le CPU démarre à l'état arrêté.
     *
     * @param memory    mémoire partagée contenant le programme et les données
     * @param registers banc de registres que le CPU utilisera pour ses opérations
     */
    public CPU(Memory memory, RegisterFile registers) {
        this.memory    = memory;
        this.registers = registers;
        this.alu       = new ALU();
        this.pc        = 0;
        this.running   = false;
    }

    /**
     * Lance l'exécution du programme en mémoire depuis l'adresse courante du PC.
     * S'arrête dès que l'instruction BREAK est rencontrée.
     *
     * @throws InvalidOpcodeException si un octet inconnu est rencontré lors du décodage
     */
    public void run() {
        running = true;
        while (running) {
            byte opcodeByte = fetch();
            decode(opcodeByte);
        }
    }

    /**
     * Exécute une seule instruction (un cycle Fetch-Decode-Execute).
     * Retourne false si le CPU vient de s'arrêter (BREAK rencontré), true sinon.
     *
     * @return true si le CPU continue de tourner, false s'il vient de s'arrêter
     * @throws InvalidOpcodeException si l'opcode lu est inconnu
     */
    public boolean step() {
        running = true;
        byte opcodeByte = fetch();
        decode(opcodeByte);
        return running;
    }

    /**
     * Remet le CPU dans son état initial : PC à 0, running à false.
     * Le contenu de la mémoire et des registres n'est pas modifié.
     */
    public void reset() {
        pc      = 0;
        running = false;
    }

    /**
     * Retourne la valeur courante du compteur ordinal (PC).
     *
     * @return adresse de la prochaine instruction à exécuter
     */
    public int getPC() {
        return pc;
    }

    /**
     * Indique si le CPU est actuellement en train d'exécuter des instructions.
     *
     * @return true si le CPU tourne, false s'il est arrêté
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Lit l'octet à l'adresse courante du PC et avance le PC d'un cran.
     *
     * @return l'octet lu en mémoire
     */
    private byte fetch() {
        byte valeur = memory.read(pc);
        pc = pc + 1;
        return valeur;
    }

    /**
     * Identifie l'opcode et appelle la méthode d'exécution correspondante.
     *
     * @param opcodeByte octet brut lu depuis la mémoire
     * @throws InvalidOpcodeException si l'opcode ne correspond à aucune instruction connue
     */
    private void decode(byte opcodeByte) {
        Opcode opcode = Opcode.fromCode(opcodeByte);

        if (opcode == null) {
            throw new InvalidOpcodeException(opcodeByte);
        }

        if (opcode == Opcode.BREAK) {
            executeBreak();
        } else if (opcode == Opcode.LOAD_CONST) {
            executeLoadConst();
        } else if (opcode == Opcode.LOAD_MEM) {
            executeLoadMem();
        } else if (opcode == Opcode.STORE) {
            executeStore();
        } else if (opcode == Opcode.ADD) {
            executeAdd();
        } else if (opcode == Opcode.SUB) {
            executeSub();
        } else if (opcode == Opcode.MUL) {
            executeMul();
        } else if (opcode == Opcode.DIV) {
            executeDiv();
        } else if (opcode == Opcode.AND) {
            executeAnd();
        } else if (opcode == Opcode.OR) {
            executeOr();
        } else if (opcode == Opcode.XOR) {
            executeXor();
        } else if (opcode == Opcode.JUMP) {
            executeJump();
        } else if (opcode == Opcode.BEQ) {
            executeBeq();
        } else if (opcode == Opcode.BNE) {
            executeBne();
        } else if (opcode == Opcode.LOAD_INDEXED) {
            executeLoadIndexed();
        } else if (opcode == Opcode.STORE_INDEXED) {
            executeStoreIndexed();
        }
    }

    /**
     * BREAK : arrête le CPU en passant running à false.
     */
    private void executeBreak() {
        running = false;
    }

    /**
     * LOAD_CONST rDest, valeur : charge une constante 8 bits dans un registre.
     * Format en mémoire : [opcode][dest][valeur]
     */
    private void executeLoadConst() {
        int dest   = fetch();
        byte value = fetch();
        registers.set(dest, value);
    }

    /**
     * LOAD_MEM rDest, @adresse : copie un octet depuis la mémoire dans un registre.
     * L'adresse est lue sur 16 bits en big-endian.
     * Format en mémoire : [opcode][dest][adresse_haut][adresse_bas]
     */
    private void executeLoadMem() {
        int dest    = fetch();
        int adresse = memory.readWord(pc);
        pc = pc + 2;
        byte valeur = memory.read(adresse);
        registers.set(dest, valeur);
    }

    /**
     * STORE rSrc, @adresse : copie la valeur d'un registre en mémoire.
     * L'adresse est lue sur 16 bits en big-endian.
     * Format en mémoire : [opcode][src][adresse_haut][adresse_bas]
     */
    private void executeStore() {
        int src     = fetch();
        int adresse = memory.readWord(pc);
        pc = pc + 2;
        byte valeur = registers.get(src);
        memory.write(adresse, valeur);
    }

    /**
     * ADD rDest, rA, rB : calcule r[A] + r[B] et stocke le résultat dans r[dest].
     * Format en mémoire : [opcode][dest][regA][regB]
     */
    private void executeAdd() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        byte valeurA  = registers.get(regA);
        byte valeurB  = registers.get(regB);
        byte resultat = alu.add(valeurA, valeurB);
        registers.set(dest, resultat);
    }

    /**
     * SUB rDest, rA, rB : calcule r[A] - r[B] et stocke le résultat dans r[dest].
     * Format en mémoire : [opcode][dest][regA][regB]
     */
    private void executeSub() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        byte valeurA  = registers.get(regA);
        byte valeurB  = registers.get(regB);
        byte resultat = alu.sub(valeurA, valeurB);
        registers.set(dest, resultat);
    }

    /**
     * MUL rHaut, rBas, rA, rB : multiplie r[A] par r[B] sur 16 bits.
     * L'octet de poids fort est stocké dans r[destHigh], le poids faible dans r[destLow].
     * Format en mémoire : [opcode][destHigh][destLow][regA][regB]
     */
    private void executeMul() {
        int destHaut = fetch();
        int destBas  = fetch();
        int regA     = fetch();
        int regB     = fetch();
        byte valeurA    = registers.get(regA);
        byte valeurB    = registers.get(regB);
        byte[] resultat = alu.mul(valeurA, valeurB);
        registers.set(destHaut, resultat[0]);
        registers.set(destBas,  resultat[1]);
    }

    /**
     * DIV rQuotient, rReste, rA, rB : divise r[A] par r[B].
     * Le quotient est stocké dans r[destQ] et le reste dans r[destR].
     * Format en mémoire : [opcode][destQ][destR][regA][regB]
     *
     * @throws ArithmeticException si r[B] vaut zéro
     */
    private void executeDiv() {
        int destQ = fetch();
        int destR = fetch();
        int regA  = fetch();
        int regB  = fetch();
        byte valeurA    = registers.get(regA);
        byte valeurB    = registers.get(regB);
        byte[] resultat = alu.div(valeurA, valeurB);
        registers.set(destQ, resultat[0]);
        registers.set(destR, resultat[1]);
    }

    /**
     * AND rDest, rA, rB : calcule r[A] & r[B] bit à bit et stocke le résultat dans r[dest].
     * Format en mémoire : [opcode][dest][regA][regB]
     */
    private void executeAnd() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        byte valeurA  = registers.get(regA);
        byte valeurB  = registers.get(regB);
        byte resultat = alu.and(valeurA, valeurB);
        registers.set(dest, resultat);
    }

    /**
     * OR rDest, rA, rB : calcule r[A] | r[B] bit à bit et stocke le résultat dans r[dest].
     * Format en mémoire : [opcode][dest][regA][regB]
     */
    private void executeOr() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        byte valeurA  = registers.get(regA);
        byte valeurB  = registers.get(regB);
        byte resultat = alu.or(valeurA, valeurB);
        registers.set(dest, resultat);
    }

    /**
     * XOR rDest, rA, rB : calcule r[A] ^ r[B] bit à bit et stocke le résultat dans r[dest].
     * Format en mémoire : [opcode][dest][regA][regB]
     */
    private void executeXor() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        byte valeurA  = registers.get(regA);
        byte valeurB  = registers.get(regB);
        byte resultat = alu.xor(valeurA, valeurB);
        registers.set(dest, resultat);
    }

    /**
     * JUMP @adresse : saut inconditionnel vers l'adresse cible.
     * L'adresse est lue sur 16 bits en big-endian immédiatement après l'opcode.
     * Format en mémoire : [opcode][adresse_haut][adresse_bas]
     */
    private void executeJump() {
        int adresse = memory.readWord(pc);
        pc = adresse;
    }

    /**
     * BEQ rA, rB, @adresse : saute vers l'adresse si r[A] == r[B], sinon continue.
     * Format en mémoire : [opcode][regA][regB][adresse_haut][adresse_bas]
     */
    private void executeBeq() {
        int regA    = fetch();
        int regB    = fetch();
        int adresse = memory.readWord(pc);
        pc = pc + 2;

        if (registers.get(regA) == registers.get(regB)) {
            pc = adresse;
        }
    }

    /**
     * BNE rA, rB, @adresse : saute vers l'adresse si r[A] != r[B], sinon continue.
     * Format en mémoire : [opcode][regA][regB][adresse_haut][adresse_bas]
     */
    private void executeBne() {
        int regA    = fetch();
        int regB    = fetch();
        int adresse = memory.readWord(pc);
        pc = pc + 2;

        if (registers.get(regA) != registers.get(regB)) {
            pc = adresse;
        }
    }

    /**
     * LOAD_INDEXED rDest, @base, rOffset : charge l'octet à l'adresse (base + r[offset]).
     * Le registre d'offset est traité comme non signé (0 à 255) grâce au masque & 0xFF.
     * Format en mémoire : [opcode][dest][base_haut][base_bas][regOffset]
     */
    private void executeLoadIndexed() {
        int dest      = fetch();
        int base      = memory.readWord(pc);
        pc = pc + 2;
        int regOffset = fetch();

        int offset  = registers.get(regOffset) & 0xFF;
        int adresse = base + offset;

        byte valeur = memory.read(adresse);
        registers.set(dest, valeur);
    }

    /**
     * STORE_INDEXED rSrc, @base, rOffset : écrit r[src] à l'adresse (base + r[offset]).
     * Le registre d'offset est traité comme non signé (0 à 255) grâce au masque & 0xFF.
     * Format en mémoire : [opcode][src][base_haut][base_bas][regOffset]
     */
    private void executeStoreIndexed() {
        int src       = fetch();
        int base      = memory.readWord(pc);
        pc = pc + 2;
        int regOffset = fetch();

        int offset  = registers.get(regOffset) & 0xFF;
        int adresse = base + offset;

        byte valeur = registers.get(src);
        memory.write(adresse, valeur);
    }
}
