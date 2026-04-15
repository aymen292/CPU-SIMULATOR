package core;

import instruction.Opcode;
import exception.InvalidOpcodeException;

/**
 * Le CPU fait tourner le programme. Il fait le cycle Fetch -> Decode -> Execute :
 * il lit l'instruction a l'adresse du PC, regarde ce qu'elle veut dire, et l'execute.
 * Il tourne jusqu'a rencontrer un BREAK.
 */
public class CPU {

    private Memory memory;
    private RegisterFile registers;
    private ALU alu;
    private int pc;           // compteur de programme (pointe vers la prochaine instruction)
    private boolean running;  // true pendant que le CPU tourne

    public CPU(Memory memory, RegisterFile registers) {
        this.memory = memory;
        this.registers = registers;
        this.alu = new ALU();
        this.pc = 0;
        this.running = false;
    }

    // lance le programme jusqu'au BREAK
    public void run() {
        running = true;
        while (running) {
            byte opcodeByte = fetch();
            decode(opcodeByte);
        }
    }

    // remet le PC a 0 et stoppe le CPU
    public void reset() {
        pc = 0;
        running = false;
    }

    public int getPC() {
        return pc;
    }

    public boolean isRunning() {
        return running;
    }

    // lit l'octet a l'adresse du PC et avance le PC d'un cran
    private byte fetch() {
        byte value = memory.read(pc);
        pc++;
        return value;
    }

    // regarde quel opcode c'est et appelle la bonne methode
    private void decode(byte opcodeByte) {
        Opcode opcode = Opcode.fromCode(opcodeByte);
        if (opcode == null) {
            throw new InvalidOpcodeException(opcodeByte);
        }
        switch (opcode) {
            case BREAK:         executeBreak();        break;
            case LOAD_CONST:    executeLoadConst();    break;
            case LOAD_MEM:      executeLoadMem();      break;
            case STORE:         executeStore();        break;
            case ADD:           executeAdd();          break;
            case SUB:           executeSub();          break;
            case MUL:           executeMul();          break;
            case DIV:           executeDiv();          break;
            case AND:           executeAnd();          break;
            case OR:            executeOr();           break;
            case XOR:           executeXor();          break;
            case JUMP:          executeJump();         break;
            case BEQ:           executeBeq();          break;
            case BNE:           executeBne();          break;
            case LOAD_INDEXED:  executeLoadIndexed();  break;
            case STORE_INDEXED: executeStoreIndexed(); break;
            default:
                throw new InvalidOpcodeException(opcodeByte);
        }
    }

    // BREAK : on arrete le CPU
    private void executeBreak() {
        running = false;
    }

    // load rX, valeur : met une constante dans un registre
    private void executeLoadConst() {
        int dest = fetch();
        byte value = fetch();
        registers.set(dest, value);
    }

    // load rX, @adresse : copie un octet de la memoire dans un registre
    private void executeLoadMem() {
        int dest = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        registers.set(dest, memory.read(address));
    }

    // store rX, @adresse : copie un registre dans la memoire
    private void executeStore() {
        int src = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        memory.write(address, registers.get(src));
    }

    // add : r[dest] = r[A] + r[B]
    private void executeAdd() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.add(registers.get(regA), registers.get(regB)));
    }

    // sub : r[dest] = r[A] - r[B]
    private void executeSub() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.sub(registers.get(regA), registers.get(regB)));
    }

    // mul : resultat sur 16 bits donc il faut 2 registres (high + low)
    private void executeMul() {
        int destHigh = fetch();
        int destLow  = fetch();
        int regA     = fetch();
        int regB     = fetch();
        byte[] result = alu.mul(registers.get(regA), registers.get(regB));
        registers.set(destHigh, result[0]);
        registers.set(destLow,  result[1]);
    }

    // div : quotient dans un registre, reste dans un autre
    private void executeDiv() {
        int destQuotient  = fetch();
        int destRemainder = fetch();
        int regA          = fetch();
        int regB          = fetch();
        byte[] result = alu.div(registers.get(regA), registers.get(regB));
        registers.set(destQuotient,  result[0]);
        registers.set(destRemainder, result[1]);
    }

    private void executeAnd() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.and(registers.get(regA), registers.get(regB)));
    }

    private void executeOr() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.or(registers.get(regA), registers.get(regB)));
    }

    private void executeXor() {
        int dest = fetch();
        int regA = fetch();
        int regB = fetch();
        registers.set(dest, alu.xor(registers.get(regA), registers.get(regB)));
    }

    // jump : on change juste le PC pour continuer ailleurs
    private void executeJump() {
        int address = memory.readWord(pc);
        pc = address;
    }

    // beq : saute si r[A] == r[B]
    private void executeBeq() {
        int regA    = fetch();
        int regB    = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) == registers.get(regB)) {
            pc = address;
        }
    }

    // bne : saute si r[A] != r[B]
    private void executeBne() {
        int regA    = fetch();
        int regB    = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) != registers.get(regB)) {
            pc = address;
        }
    }

    // load indexe : adresse effective = base + offset (offset est dans un registre)
    private void executeLoadIndexed() {
        int dest      = fetch();
        int base      = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch();
        // & 0xFF pour traiter le registre comme un entier non-signe 0..255
        int address   = base + (registers.get(regOffset) & 0xFF);
        registers.set(dest, memory.read(address));
    }

    // store indexe : pareil mais dans l'autre sens
    private void executeStoreIndexed() {
        int src       = fetch();
        int base      = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch();
        int address   = base + (registers.get(regOffset) & 0xFF);
        memory.write(address, registers.get(src));
    }
}
