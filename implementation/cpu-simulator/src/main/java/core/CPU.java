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
            //System.out.println("pc=" + pc + " opcode=" + opcodeByte); // debug
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

    // on regarde quel opcode c'est et on appelle la bonne methode
    private void decode(byte opcodeByte) {
        Opcode opcode = Opcode.fromCode(opcodeByte);
        if (opcode == null) {
            throw new InvalidOpcodeException(opcodeByte);
        }
        switch (opcode) {
            case BREAK: executeBreak(); break;
            case LOAD_CONST: executeLoadConst(); break;
            case LOAD_MEM: executeLoadMem(); break;
            case STORE: executeStore(); break;
            case ADD: executeAdd(); break;
            case SUB: executeSub(); break;
            case MUL: executeMul(); break;
            case DIV: executeDiv(); break;
            case AND: executeAnd(); break;
            case OR: executeOr(); break;
            case XOR: executeXor(); break;
            case JUMP: executeJump(); break;
            case BEQ: executeBeq(); break;
            case BNE: executeBne(); break;
            case LOAD_INDEXED: executeLoadIndexed(); break;
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

    // mul : le resultat tient sur 16 bits donc on a besoin de 2 registres (high + low)
    private void executeMul() {
        int destHigh = fetch();
        int destLow = fetch();
        int regA = fetch();
        int regB = fetch();
        byte[] result = alu.mul(registers.get(regA), registers.get(regB));
        registers.set(destHigh, result[0]);
        registers.set(destLow, result[1]);
    }

    // div : on stocke le quotient dans un registre et le reste dans un autre
    private void executeDiv() {
        int destQ = fetch();
        int destR = fetch();
        int regA = fetch();
        int regB = fetch();
        byte[] res = alu.div(registers.get(regA), registers.get(regB));
        registers.set(destQ, res[0]);
        registers.set(destR, res[1]);
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

    // beq : on saute seulement si r[A] == r[B]
    private void executeBeq() {
        int regA = fetch();
        int regB = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) == registers.get(regB)) {
            pc = address;
        }
    }

    // bne : on saute seulement si r[A] != r[B]
    private void executeBne() {
        int regA = fetch();
        int regB = fetch();
        int address = memory.readWord(pc);
        pc += 2;
        if (registers.get(regA) != registers.get(regB)) {
            pc = address;
        }
    }

    // load indexe : adresse = base + valeur du registre offset
    private void executeLoadIndexed() {
        int dest = fetch();
        int base = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch();
        // le & 0xFF c'est pour traiter le registre comme non signe (0 a 255 au lieu de -128 a 127)
        int address = base + (registers.get(regOffset) & 0xFF);
        registers.set(dest, memory.read(address));
    }

    // store indexe : pareil que load indexe mais dans l'autre sens
    private void executeStoreIndexed() {
        int src = fetch();
        int base = memory.readWord(pc);
        pc += 2;
        int regOffset = fetch();
        int address = base + (registers.get(regOffset) & 0xFF);
        memory.write(address, registers.get(src));
    }
}
