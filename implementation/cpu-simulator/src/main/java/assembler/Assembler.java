package assembler;

import core.Memory;
import instruction.Opcode;

/**
 * L'assembleur transforme du texte en octets (le code machine).
 * Syntaxe : mnemoniques en minuscules, operandes separees par des virgules,
 * rX pour les registres, @adresse pour les adresses.
 */
public class Assembler {

    private Memory memory;
    private int currentAddress;  // ou on ecrit la prochaine instruction

    public Assembler(Memory memory) {
        this.memory = memory;
        this.currentAddress = 0;
    }

    // assemble un programme complet (plusieurs lignes)
    public void assemble(String program) {
        String[] lines = program.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            // on saute les lignes vides et les commentaires
            if (trimmed.isEmpty() || trimmed.startsWith(";") || trimmed.startsWith("#")) {
                continue;
            }
            parseLine(trimmed);
        }
    }

    // traite une ligne d'assembleur
    private void parseLine(String line) {
        // on enleve le commentaire en fin de ligne s'il y en a un
        int commentIndex = line.indexOf(';');
        if (commentIndex >= 0) {
            line = line.substring(0, commentIndex).trim();
        }
        if (line.isEmpty()) {
            return;
        }

        // cas special : la directive string contient du texte entre guillemets
        // donc on la traite a part pour pas couper le texte sur les espaces/virgules
        if (line.toLowerCase().startsWith("string")) {
            int start = line.indexOf('"') + 1;
            int end   = line.lastIndexOf('"');
            String text = line.substring(start, end);
            for (char c : text.toCharArray()) {
                writeByte((byte) c);
            }
            return;
        }

        // on remplace les virgules par des espaces puis on coupe en morceaux
        String[] tokens = line.replace(",", " ").split("\\s+");
        String mnemonic = tokens[0].toLowerCase();

        switch (mnemonic) {

            case "break":
                writeByte((byte) Opcode.BREAK.getCode());
                break;

            case "load": {
                // load a plusieurs formes, on regarde le nombre d'arguments et si y'a un @
                int dest = parseRegister(tokens[1]);
                if (tokens.length == 3) {
                    if (isAddress(tokens[2])) {
                        // load rX, @adresse
                        int address = parseValue(tokens[2]);
                        writeByte((byte) Opcode.LOAD_MEM.getCode());
                        writeByte((byte) dest);
                        writeAddress(address);
                    } else {
                        // load rX, valeur (constante)
                        int value = parseValue(tokens[2]);
                        writeByte((byte) Opcode.LOAD_CONST.getCode());
                        writeByte((byte) dest);
                        writeByte((byte) value);
                    }
                } else {
                    // 4 tokens : load indexe (base + registre)
                    int base = parseValue(tokens[2]);
                    int regOffset = parseRegister(tokens[3]);
                    writeByte((byte) Opcode.LOAD_INDEXED.getCode());
                    writeByte((byte) dest);
                    writeAddress(base);
                    writeByte((byte) regOffset);
                }
                break;
            }

            case "store": {
                // store normal ou indexe (comme load)
                int src = parseRegister(tokens[1]);
                int address = parseValue(tokens[2]);
                if (tokens.length == 3) {
                    writeByte((byte) Opcode.STORE.getCode());
                    writeByte((byte) src);
                    writeAddress(address);
                } else {
                    int regOffset = parseRegister(tokens[3]);
                    writeByte((byte) Opcode.STORE_INDEXED.getCode());
                    writeByte((byte) src);
                    writeAddress(address);
                    writeByte((byte) regOffset);
                }
                break;
            }

            case "add":
                writeInstructionRRR(Opcode.ADD, tokens);
                break;
            case "sub":
                writeInstructionRRR(Opcode.SUB, tokens);
                break;
            case "and":
                writeInstructionRRR(Opcode.AND, tokens);
                break;
            case "or":
                writeInstructionRRR(Opcode.OR, tokens);
                break;
            case "xor":
                writeInstructionRRR(Opcode.XOR, tokens);
                break;

            case "mul": {
                // mul a 4 registres : les 2 premiers c'est pour le resultat (16 bits)
                int destHigh = parseRegister(tokens[1]);
                int destLow = parseRegister(tokens[2]);
                int regA = parseRegister(tokens[3]);
                int regB = parseRegister(tokens[4]);
                writeByte((byte) Opcode.MUL.getCode());
                writeByte((byte) destHigh);
                writeByte((byte) destLow);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "div": {
                // div : quotient dans r1, reste dans r2
                int destQ = parseRegister(tokens[1]);
                int destR = parseRegister(tokens[2]);
                int regA = parseRegister(tokens[3]);
                int regB = parseRegister(tokens[4]);
                writeByte((byte) Opcode.DIV.getCode());
                writeByte((byte) destQ);
                writeByte((byte) destR);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "jump": {
                int address = parseValue(tokens[1]);
                writeByte((byte) Opcode.JUMP.getCode());
                writeAddress(address);
                break;
            }

            case "beq":
                writeBranch(Opcode.BEQ, tokens);
                break;
            case "bne":
                writeBranch(Opcode.BNE, tokens);
                break;

            case "data": {
                // data val1, val2, ... : ecrit des octets bruts
                for (int i = 1; i < tokens.length; i++) {
                    writeByte((byte) parseValue(tokens[i]));
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Mnémonique inconnu : " + mnemonic);
        }
    }

    // petite methode pour eviter de recopier le meme code pour add/sub/and/or/xor
    // (elles marchent toutes pareil : op rDest, rA, rB)
    private void writeInstructionRRR(Opcode opcode, String[] tokens) {
        int dest = parseRegister(tokens[1]);
        int regA = parseRegister(tokens[2]);
        int regB = parseRegister(tokens[3]);
        writeByte((byte) opcode.getCode());
        writeByte((byte) dest);
        writeByte((byte) regA);
        writeByte((byte) regB);
    }

    // meme idee pour beq et bne : op rA, rB, @adresse
    private void writeBranch(Opcode opcode, String[] tokens) {
        int regA = parseRegister(tokens[1]);
        int regB = parseRegister(tokens[2]);
        int address = parseValue(tokens[3]);
        writeByte((byte) opcode.getCode());
        writeByte((byte) regA);
        writeByte((byte) regB);
        writeAddress(address);
    }

    // ecrit un octet et avance la position
    private void writeByte(byte value) {
        memory.write(currentAddress, value);
        currentAddress++;
    }

    // ecrit une adresse 16 bits sur 2 octets (big endian : poids fort d'abord)
    private void writeAddress(int address) {
        writeByte((byte) ((address >> 8) & 0xFF));
        writeByte((byte) (address & 0xFF));
    }

    // parse "rX" et renvoie juste le numero X
    private int parseRegister(String token) {
        return Integer.parseInt(token.substring(1));
    }

    // parse un nombre : decimale, hexa (0x..) ou adresse (@..)
    private int parseValue(String token) {
        if (isAddress(token)) {
            // on enleve le @ du debut
            token = token.substring(1);
        }
        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseInt(token.substring(2), 16);
        }
        return Integer.parseInt(token);
    }

    // dit si c'est une adresse (ca commence par @)
    private boolean isAddress(String token) {
        return token.startsWith("@");
    }
}
