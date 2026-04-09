package assembler;

import core.Memory;
import instruction.Opcode;

/**
 * Assembleur qui traduit un programme textuel en codes machine écrits en mémoire.
 * Chaque ligne du programme correspond à une instruction ou une directive de données.
 * Les commentaires (lignes commençant par ';' ou '#') sont ignorés lors de l'assemblage.
 */
public class Assembler {

    private Memory memory; //  Mémoire cible dans laquelle écrire les instructions
    private int currentAddress;  // Adresse courante d'écriture en mémoire

    /**
     * Construit un nouvel assembleur associé à une mémoire.
     * @param memory la mémoire cible
     */
    public Assembler(Memory memory) {
        this.memory = memory;
        this.currentAddress = 0;
    }

    /**
     * Assemble un programme assembleur multiligne et écrit le code machine en mémoire.
     * Chaque ligne est traitée séparément. Les lignes vides et les commentaires sont ignorés.
     * @param program le programme assembleur sous forme de chaîne multiligne
     */
    public void assemble(String program) {
        // On découpe le programme en lignes individuelles
        String[] lines = program.split("\n");
        for (String line : lines) {
            // On supprime les espaces en début et fin de ligne
            String trimmed = line.trim();
            // On ignore les lignes vides et les commentaires
            if (trimmed.isEmpty() || trimmed.startsWith(";") || trimmed.startsWith("#")) {
                continue;
            }
            parseLine(trimmed);
        }
    }

    /**
     * Parse et traduit une ligne d'assembleur en code machine.
     * La ligne est découpée en tokens séparés par des espaces.
     * Le premier token est le mnémonique de l'instruction ou une directive (.data, .string).
     * @param line la ligne à parser
     */
    private void parseLine(String line) {
        // On supprime le commentaire en fin de ligne s'il existe
        int commentIndex = line.indexOf(';');
        if (commentIndex >= 0) {
            line = line.substring(0, commentIndex).trim();
        }
        if (line.isEmpty()) {
            return;
        }

        // On découpe la ligne en tokens séparés par des espaces
        String[] tokens = line.split("\\s+");
        String mnemonic = tokens[0].toUpperCase();

        switch (mnemonic) {

            case "BREAK":
                // Format : BREAK (pas d'opérande)
                writeByte((byte) Opcode.BREAK.getCode());
                break;

            case "LOAD_CONST": {
                // Format : LOAD_CONST rDest valeur
                int dest  = parseRegister(tokens[1]);
                int value = parseValue(tokens[2]);
                writeByte((byte) Opcode.LOAD_CONST.getCode());
                writeByte((byte) dest);
                writeByte((byte) value);
                break;
            }

            case "LOAD_MEM": {
                // Format : LOAD_MEM rDest @adresse
                int dest    = parseRegister(tokens[1]);
                int address = parseValue(tokens[2]);
                writeByte((byte) Opcode.LOAD_MEM.getCode());
                writeByte((byte) dest);
                writeAddress(address);
                break;
            }

            case "STORE": {
                // Format : STORE rSrc @adresse
                int src     = parseRegister(tokens[1]);
                int address = parseValue(tokens[2]);
                writeByte((byte) Opcode.STORE.getCode());
                writeByte((byte) src);
                writeAddress(address);
                break;
            }

            case "ADD": {
                // Format : ADD rDest rA rB
                int dest = parseRegister(tokens[1]);
                int regA = parseRegister(tokens[2]);
                int regB = parseRegister(tokens[3]);
                writeByte((byte) Opcode.ADD.getCode());
                writeByte((byte) dest);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "SUB": {
                // Format : SUB rDest rA rB
                int dest = parseRegister(tokens[1]);
                int regA = parseRegister(tokens[2]);
                int regB = parseRegister(tokens[3]);
                writeByte((byte) Opcode.SUB.getCode());
                writeByte((byte) dest);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "MUL": {
                // Format : MUL rDestHigh rDestLow rA rB
                int destHigh = parseRegister(tokens[1]);
                int destLow  = parseRegister(tokens[2]);
                int regA     = parseRegister(tokens[3]);
                int regB     = parseRegister(tokens[4]);
                writeByte((byte) Opcode.MUL.getCode());
                writeByte((byte) destHigh);
                writeByte((byte) destLow);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "DIV": {
                // Format : DIV rQuotient rReste rA rB
                int destQuotient  = parseRegister(tokens[1]);
                int destRemainder = parseRegister(tokens[2]);
                int regA          = parseRegister(tokens[3]);
                int regB          = parseRegister(tokens[4]);
                writeByte((byte) Opcode.DIV.getCode());
                writeByte((byte) destQuotient);
                writeByte((byte) destRemainder);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "AND": {
                // Format : AND rDest rA rB
                int dest = parseRegister(tokens[1]);
                int regA = parseRegister(tokens[2]);
                int regB = parseRegister(tokens[3]);
                writeByte((byte) Opcode.AND.getCode());
                writeByte((byte) dest);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "OR": {
                // Format : OR rDest rA rB
                int dest = parseRegister(tokens[1]);
                int regA = parseRegister(tokens[2]);
                int regB = parseRegister(tokens[3]);
                writeByte((byte) Opcode.OR.getCode());
                writeByte((byte) dest);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "XOR": {
                // Format : XOR rDest rA rB
                int dest = parseRegister(tokens[1]);
                int regA = parseRegister(tokens[2]);
                int regB = parseRegister(tokens[3]);
                writeByte((byte) Opcode.XOR.getCode());
                writeByte((byte) dest);
                writeByte((byte) regA);
                writeByte((byte) regB);
                break;
            }

            case "JUMP": {
                // Format : JUMP @adresse
                int address = parseValue(tokens[1]);
                writeByte((byte) Opcode.JUMP.getCode());
                writeAddress(address);
                break;
            }

            case "BEQ": {
                // Format : BEQ rA rB @adresse
                int regA    = parseRegister(tokens[1]);
                int regB    = parseRegister(tokens[2]);
                int address = parseValue(tokens[3]);
                writeByte((byte) Opcode.BEQ.getCode());
                writeByte((byte) regA);
                writeByte((byte) regB);
                writeAddress(address);
                break;
            }

            case "BNE": {
                // Format : BNE rA rB @adresse
                int regA    = parseRegister(tokens[1]);
                int regB    = parseRegister(tokens[2]);
                int address = parseValue(tokens[3]);
                writeByte((byte) Opcode.BNE.getCode());
                writeByte((byte) regA);
                writeByte((byte) regB);
                writeAddress(address);
                break;
            }

            case "LOAD_INDEXED": {
                // Format : LOAD_INDEXED rDest @base rOffset
                int dest      = parseRegister(tokens[1]);
                int base      = parseValue(tokens[2]);
                int regOffset = parseRegister(tokens[3]);
                writeByte((byte) Opcode.LOAD_INDEXED.getCode());
                writeByte((byte) dest);
                writeAddress(base);
                writeByte((byte) regOffset);
                break;
            }

            case "STORE_INDEXED": {
                // Format : STORE_INDEXED rSrc @base rOffset
                int src       = parseRegister(tokens[1]);
                int base      = parseValue(tokens[2]);
                int regOffset = parseRegister(tokens[3]);
                writeByte((byte) Opcode.STORE_INDEXED.getCode());
                writeByte((byte) src);
                writeAddress(base);
                writeByte((byte) regOffset);
                break;
            }

            case ".DATA": {
                // Directive .data : écrit des octets bruts en mémoire
                // Format : .data val1 val2 ...
                for (int i = 1; i < tokens.length; i++) {
                    writeByte((byte) parseValue(tokens[i]));
                }
                break;
            }

            case ".STRING": {
                // Directive .string : écrit une chaîne de caractères en mémoire sans octet de fin
                // Format : .string "texte"
                int start = line.indexOf('"') + 1;
                int end   = line.lastIndexOf('"');
                String text = line.substring(start, end);
                for (char c : text.toCharArray()) {
                    writeByte((byte) c);
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Mnémonique inconnu : " + mnemonic);
        }
    }

    /**
     * Écrit un octet en mémoire à currentAddress et incrémente l'adresse courante.
     * @param value l'octet à écrire
     */
    private void writeByte(byte value) {
        memory.write(currentAddress, value);
        currentAddress++;
    }

    /**
     * Écrit une adresse 16 bits en big-endian (2 octets) en mémoire.
     * L'octet de poids fort est écrit en premier, suivi de l'octet de poids faible.
     * @param address l'adresse 16 bits à écrire
     */
    private void writeAddress(int address) {
        // Octet de poids fort (bits 15 à 8)
        writeByte((byte) ((address >> 8) & 0xFF));
        // Octet de poids faible (bits 7 à 0)
        writeByte((byte) (address & 0xFF));
    }

    /**
     * Parse un token de la forme "rX" et retourne le numéro du registre.
     * @param token le token à parser (ex : "r3")
     * @return le numéro du registre
     */
    private int parseRegister(String token) {
        // On supprime le préfixe 'r' ou 'R' et on convertit en entier
        return Integer.parseInt(token.substring(1));
    }

    /**
     * Parse une valeur décimale, hexadécimale ou une adresse préfixée par @.
     * @param token le token à parser (ex : "42", "0xFF", "@100")
     * @return la valeur entière parsée
     */
    private int parseValue(String token) {
        // Si le token est une adresse, on enlève le préfixe '@' avant de parser
        if (isAddress(token)) {
            token = token.substring(1);
        }
        // Valeur hexadécimale préfixée par 0x ou 0X
        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseInt(token.substring(2), 16);
        }
        // Valeur décimale
        return Integer.parseInt(token);
    }

    /**
     * Indique si un token représente une adresse mémoire (commence par @).
     * @param token le token à tester
     * @return true si le token commence par @
     */
    private boolean isAddress(String token) {
        return token.startsWith("@");
    }
}
