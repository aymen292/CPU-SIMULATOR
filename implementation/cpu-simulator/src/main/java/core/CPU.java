package core;

import instruction.Opcode;
import exception.InvalidOpcodeException;

/**
 Le CPU est le cœur du simulateur . Il implémente le cycle classique Fetch -> Decode -> Execute : il lit
 une instruction depuis la mémoire à l'adresse pointée par le compteur de programme (pc) , décode l'opcode
 récupéré, puis délégue l'exécution à la méthode spécialisée correspondante. Il maintient son propre état
 d'exécution via le booléen running
 */
public class CPU {
    

    private Memory memory; // la mémoire système partagé
    private RegisterFile registers; // Banc de registres du processeur
    private ALU alu; // l'unité arithmétique et logique
    private int pc; // le compteur de programme, pointe vers la prochaine instruction à lire
    private boolean running; // indique si le processeur est en cours d'éxécution

    /**
     * Construit un nouveau CPU avec la mémoire et les registres donnés.
     * @param memory la mémoire du système
     * @param registers le banc de registres
     */
    public CPU(Memory memory, RegisterFile registers) {
       this.memory = memory;
       this.registers = registers;
       this.alu = new ALU();
       this.pc = 0;
       this.running = false;
    }

    /**
     * Lance la boucle d'exécution jusqu'à l'instruction BREAK.
     */
    public void run() {
        running = true;
        while (running) {
            byte opcodeByte = fetch();
            decode(opcodeByte);
        }
    }

    /**
     * Remet le compteur de programme à 0 et arrête l'exécution.
     */
    public void reset() {
        this.pc = 0;
        this.running = false;
    }

    /**
     * Retourne la valeur du compteur de programme.
     * @return la valeur du compteur de programme
     */
    public int getPC() {
        return pc;
    }

    /**
     * Indique si le CPU est en cours d'exécution.
     * @return true si le CPU est en cours d'exécution
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Lit l'octet à l'adresse PC et incrémente le PC.
     * @return l'octet lu
     */
    private byte fetch() {
        byte value = memory.read(pc);
        pc++;
        return value;
    }

    /**
     * Décode l'opcode et exécute l'instruction correspondante.
     * @param opcodeByte l'octet représentant l'opcode à décoder
     */
    private void decode(byte opcodeByte) {
        int code = opcodeByte ;
        Opcode opcode = Opcode.fromCode(code);
        if (opcode == null) {
            throw new InvalidOpcodeException(code);
        }
        switch (opcode) {
            case BREAK:
                executeBreak();
                break;
            case LOAD_CONST:
                executeLoadConst();
                break;
            case LOAD_MEM:
                executeLoadMem();
                break;
            case STORE:
                executeStore();
                break;
            case ADD:
                executeAdd();
                break;
            case SUB:
                executeSub();
                break;
            case MUL:
                executeMul();
                break;
            case DIV:
                executeDiv();
                break;
            case AND:
                executeAnd();
                break;
            case OR:
                executeOr();
                break;
            case XOR:
                executeXor();
                break;
            case JUMP:
                executeJump();
                break;
            case BEQ:
                executeBeq();
                break;
            case BNE:
                executeBne();
                break;
            case LOAD_INDEXED:
                executeLoadIndexed();
                break;
            case STORE_INDEXED:
                executeStoreIndexed();
                break;
            default:
                throw new InvalidOpcodeException(code);
        }
    }

    /**
     * Exécute l'instruction BREAK : arrête l'exécution du CPU.
     */
    private void executeBreak() {
        running = false;
    }

    /**
     * Exécute l'instruction LOAD_CONST : charge une constante dans un registre.
     * Lit en mémoire : [registre destination, valeur constante].
     */
    private void executeLoadConst() {
        int dest = fetch();
        byte value = fetch();
        registers.set(dest, value);
    }

    /**
     * Exécute l'instruction LOAD_MEM : charge une valeur depuis une adresse mémoire dans un registre.
     * Lit en mémoire : [registre destination, adresse 16 bits].
     */
    private void executeLoadMem() {
        int dest = fetch();
        int address = memory.readWord(pc);
        pc+=2;
        registers.set(dest,memory.read(address));
    }

    /**
     * Exécute l'instruction STORE : écrit la valeur d'un registre en mémoire.
     * Lit en mémoire : [registre source, adresse 16 bits].
     */
    private void executeStore() {
        int src = fetch() ;
        int address = memory.readWord(pc);
        pc += 2;
        memory.write(address, registers.get(src));
    }

    /**
     * Exécute l'instruction ADD : additionne deux registres et stocke le résultat.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeAdd() {
        int dest = fetch() ;
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.add(registers.get(regA), registers.get(regB)));
    }

    /**
     * Exécute l'instruction SUB : soustrait deux registres et stocke le résultat.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeSub() {
        int dest = fetch();
        int regA = fetch() ;
        int regB = fetch() ;
        registers.set(dest, alu.sub(registers.get(regA), registers.get(regB)));
    }

    /**
     * Exécute l'instruction MUL : multiplie deux registres et stocke le résultat sur 16 bits.
     * Lit en mémoire : [registre high destination, registre low destination, registre A, registre B].
     */
    private void executeMul() {
        int destHigh = fetch() ;
        int destLow  = fetch();
        int regA     = fetch() ;
        int regB     = fetch() ;
        byte[] result = alu.mul(registers.get(regA), registers.get(regB));
        registers.set(destHigh, result[0]);
        registers.set(destLow,  result[1]);
    }

    /**
     * Exécute l'instruction DIV : divise deux registres et stocke le quotient et le reste.
     * Lit en mémoire : [registre quotient, registre reste, registre A, registre B].
     */
    private void executeDiv() {
        int destQuotient  = fetch();
        int destRemainder = fetch();
        int regA          = fetch() ;
        int regB          = fetch();
        byte[] result = alu.div(registers.get(regA), registers.get(regB));
        registers.set(destQuotient,  result[0]);
        registers.set(destRemainder, result[1]);
    }

    /**
     * Exécute l'instruction AND : effectue un ET logique entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeAnd() {
        int dest = fetch();
        int regA = fetch() ;
        int regB = fetch() ;
        registers.set(dest, alu.and(registers.get(regA), registers.get(regB)));
    }

    /**
     * Exécute l'instruction OR : effectue un OU logique entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeOr() {
        int dest = fetch();
        int regA = fetch() ;
        int regB = fetch() ;
        registers.set(dest, alu.or(registers.get(regA), registers.get(regB)));
    }

    /**
     * Exécute l'instruction XOR : effectue un OU exclusif entre deux registres.
     * Lit en mémoire : [registre destination, registre A, registre B].
     */
    private void executeXor() {
        int dest = fetch();
        int regA = fetch() ;
        int regB = fetch() ;
        registers.set(dest, alu.xor(registers.get(regA), registers.get(regB)));
    }

    /**
     * Exécute l'instruction JUMP : effectue un saut inconditionnel à une adresse.
     * Lit en mémoire : [adresse 16 bits de destination].
     */
    private void executeJump() {
        int address = memory.readWord(pc);
        pc = address;
    }

    /**
     * Exécute l'instruction BEQ : saut conditionnel si deux registres sont égaux.
     * Lit en mémoire : [registre A, registre B, adresse 16 bits de destination].
     */
    private void executeBeq() {
        int regA    = fetch() ;
        int regB    = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) == registers.get(regB)) {
            pc = address;
        }
    }

    /**
     * Exécute l'instruction BNE : saut conditionnel si deux registres sont différents.
     * Lit en mémoire : [registre A, registre B, adresse 16 bits de destination].
     */
    private void executeBne() {
        int regA    = fetch() ;
        int regB    = fetch() ;
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) != registers.get(regB)) {
            pc = address;
        }
    }

    /**
     * Exécute l'instruction LOAD_INDEXED : charge depuis l'adresse base + offset registre.
     * Lit en mémoire : [registre destination, adresse 16 bits de base, registre offset].
     */
    private void executeLoadIndexed() {
        int dest    = fetch() ;
        int base    = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch() ;
        int address   = base + (registers.get(regOffset) );
        registers.set(dest, memory.read(address));
    }

    /**
     * Exécute l'instruction STORE_INDEXED : écrit à l'adresse base + offset registre.
     * Lit en mémoire : [registre source, adresse 16 bits de base, registre offset].
     */
    private void executeStoreIndexed() {
        int src     = fetch() & 0xFF;
        int base    = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch() & 0xFF;
        int address   = base + (registers.get(regOffset) & 0xFF);
        memory.write(address, registers.get(src));
    }
}
