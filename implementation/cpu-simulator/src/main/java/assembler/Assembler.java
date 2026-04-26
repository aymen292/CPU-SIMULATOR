package assembler;

import core.Memory;
import instruction.Opcode;

/**
 * Assembleur textuel pour le simulateur de processeur.
 * Transforme un programme écrit en langage d'assemblage en une suite d'octets
 * déposés directement dans la mémoire fournie, en débutant à l'adresse 0.
 */
public class Assembler {

    private Memory memory;
    private int currentAddress; // adresse où sera écrit le prochain octet produit

    /**
     * Crée un assembleur qui écrira le code machine dans la mémoire fournie,
     * en débutant à l'adresse 0.
     *
     * @param memory mémoire cible qui recevra les octets assemblés
     */
    public Assembler(Memory memory) {
        this.memory = memory;
        this.currentAddress = 0; // initalisation à l'adresse 0
    }

    /**
     * Assemble un programme complet composé de plusieurs lignes.
     * Les lignes vides et les lignes de commentaire (commençant par ; ou #) sont ignorées.
     *
     * @param program programme en langage d'assemblage, lignes séparées par '\n'
     * @throws IllegalArgumentException si une ligne contient une instruction inconnue
     */
    public void assemble(String program) {
        String[] lignes = program.split("\n"); // Découpe le programme ligne par ligne (séparé par le /n) et les range dans un tableau

        for (int i = 0; i < lignes.length; i++) {
            String ligne = lignes[i].trim(); // supprime les espaces vides au début et à la fin des lignes 

            if (ligne.isEmpty()) {
                continue; // On ignore la vide si elle est vide
            }
            if (ligne.startsWith(";")) {
                continue; // On ignore la ligne si elle commence par ; car commentaire
            }
            if (ligne.startsWith("#")) {
                continue; // On ignore la ligne si elle commence par ; car commentaire
            }

            parseLine(ligne);
        }
    }

    /**
     * Analyse et encode une seule ligne d'assemblage.
     * Supprime les commentaires en fin de ligne, identifie l'instruction
     * et ses opérandes, puis produit les octets correspondants en mémoire.
     * La directive "string" est traitée séparément car elle peut contenir
     * des virgules dans le littéral de chaîne.
     *
     * @param line ligne d'assemblage non vide
     * @throws IllegalArgumentException si l'instruction n'est pas reconnue
     */
    private void parseLine(String line) {
        // on enlève les commentaires en fin de ligne
        int indexCommentaire = line.indexOf(';');
        if (indexCommentaire >= 0) {
            line = line.substring(0, indexCommentaire).trim();
        }
        if (line.isEmpty()) {
            return;
        }

        // cas spécial : string "texte" (on ne peut pas découper sur les virgules)
        if (line.toLowerCase().startsWith("string")) {
            int debut = line.indexOf('"') + 1;
            int fin   = line.lastIndexOf('"');
            String texte = line.substring(debut, fin);

            for (char c : texte.toCharArray()) {
                writeByte((byte) c);
            }
            return;
        }

        // on remplace les virgules par des espaces puis on découpe
        String lineNettoye = line.replace(",", " ");
        String[] elements    = lineNettoye.split("\\s+");
        String instruction = elements[0].toLowerCase();

        if (instruction.equals("break")) {

            writeByte((byte) Opcode.BREAK.getCode());

        } else if (instruction.equals("load")) {

            int dest = parseRegister(elements[1]);

            if (elements.length == 3) {
                if (isAddress(elements[2])) {
                    // load rX, @adresse
                    int adresse = parseValue(elements[2]);
                    writeByte((byte) Opcode.LOAD_MEM.getCode());
                    writeByte((byte) dest);
                    writeAddress(adresse);
                } else {
                    // load rX, valeur (constante)
                    int valeur = parseValue(elements[2]);
                    writeByte((byte) Opcode.LOAD_CONST.getCode());
                    writeByte((byte) dest);
                    writeByte((byte) valeur);
                }
            } else {
                // load rX, @base, rOffset (adressage indexé)
                int base      = parseValue(elements[2]);
                int regOffset = parseRegister(elements[3]);
                writeByte((byte) Opcode.LOAD_INDEXED.getCode());
                writeByte((byte) dest);
                writeAddress(base);
                writeByte((byte) regOffset);
            }

        } else if (instruction.equals("store")) {

            int src     = parseRegister(elements[1]);
            int adresse = parseValue(elements[2]);

            if (elements.length == 3) {
                // store rX, @adresse
                writeByte((byte) Opcode.STORE.getCode());
                writeByte((byte) src);
                writeAddress(adresse);
            } else {
                // store rX, @base, rOffset (adressage indexé)
                int regOffset = parseRegister(elements[3]);
                writeByte((byte) Opcode.STORE_INDEXED.getCode());
                writeByte((byte) src);
                writeAddress(adresse);
                writeByte((byte) regOffset);
            }

        } else if (instruction.equals("add")) {

            writeInstructionRRR(Opcode.ADD, elements);

        } else if (instruction.equals("sub")) {

            writeInstructionRRR(Opcode.SUB, elements);

        } else if (instruction.equals("and")) {

            writeInstructionRRR(Opcode.AND, elements);

        } else if (instruction.equals("or")) {

            writeInstructionRRR(Opcode.OR, elements);

        } else if (instruction.equals("xor")) {

            writeInstructionRRR(Opcode.XOR, elements);

        } else if (instruction.equals("mul")) {

            writeInstructionRRRR(Opcode.MUL, elements);

        } else if (instruction.equals("div")) {

            writeInstructionRRRR(Opcode.DIV, elements);

        } else if (instruction.equals("jump")) {

            int adresse = parseValue(elements[1]);
            writeByte((byte) Opcode.JUMP.getCode());
            writeAddress(adresse);

        } else if (instruction.equals("beq")) {

            writeBranch(Opcode.BEQ, elements);

        } else if (instruction.equals("bne")) {

            writeBranch(Opcode.BNE, elements);

        } else if (instruction.equals("data")) {

            for (int i = 1; i < elements.length; i++) {
                int valeur = parseValue(elements[i]);
                writeByte((byte) valeur);
            }

        } else {
            throw new IllegalArgumentException("Instruction inconnue : " + instruction);
        }
    }

    /**
     * Encode une instruction au format op rDest, rA, rB (trois registres).
     * Utilisé pour ADD, SUB, AND, OR et XOR.
     * Octets produits : [opcode][dest][regA][regB]
     *
     * @param opcode opcode de l'instruction à encoder
     * @param elements tableau de elements de la ligne (elements[0] = instruction, elements[1..3] = registres)
     */
    private void writeInstructionRRR(Opcode opcode, String[] elements) {
        int dest = parseRegister(elements[1]);
        int regA = parseRegister(elements[2]);
        int regB = parseRegister(elements[3]);

        writeByte((byte) opcode.getCode());
        writeByte((byte) dest);
        writeByte((byte) regA);
        writeByte((byte) regB);
    }

    /**
     * Encode une instruction au format op rDest1, rDest2, rA, rB (quatre registres).
     * Utilisé pour MUL et DIV.
     * Octets produits : [opcode][dest1][dest2][regA][regB]
     *
     * @param opcode opcode de l'instruction à encoder
     * @param elements tableau de elements de la ligne
     */
    private void writeInstructionRRRR(Opcode opcode, String[] elements) {
        int dest1 = parseRegister(elements[1]);
        int dest2 = parseRegister(elements[2]);
        int regA  = parseRegister(elements[3]);
        int regB  = parseRegister(elements[4]);

        writeByte((byte) opcode.getCode());
        writeByte((byte) dest1);
        writeByte((byte) dest2);
        writeByte((byte) regA);
        writeByte((byte) regB);
    }

    /**
     * Encode une instruction de branchement au format op rA, rB, @adresse.
     * Utilisé pour BEQ et BNE.
     * Octets produits : [opcode][regA][regB][adresse_haut][adresse_bas]
     *
     * @param opcode opcode du branchement (BEQ ou BNE)
     * @param elements tableau de elements de la ligne (elements[1..2] = registres, elements[3] = adresse)
     */
    private void writeBranch(Opcode opcode, String[] elements) {
        int regA    = parseRegister(elements[1]);
        int regB    = parseRegister(elements[2]);
        int adresse = parseValue(elements[3]);

        writeByte((byte) opcode.getCode());
        writeByte((byte) regA);
        writeByte((byte) regB);
        writeAddress(adresse);
    }

    /**
     * Écrit un octet en mémoire à la position courante et avance le curseur d'un cran.
     *
     * @param value octet à écrire
     */
    private void writeByte(byte value) {
        memory.write(currentAddress, value);
        currentAddress = currentAddress + 1;
    }

    /**
     * Écrit une adresse 16 bits en big-endian sur deux octets consécutifs.
     * L'octet de poids fort est écrit en premier, suivi de l'octet de poids faible.
     *
     * @param address valeur 16 bits à encoder, comprise entre 0 et 65 535
     */
    private void writeAddress(int address) {
        int octetHaut = address / 256;
        int octetBas  = address % 256;

        writeByte((byte) octetHaut);
        writeByte((byte) octetBas);
    }

    /**
     * Extrait le numéro entier d'un element de registre de la forme rX.
     * Exemple : "r5" retourne 5, "r15" retourne 15.
     *
     * @param element element de registre commençant par 'r'
     * @return numéro du registre sous forme d'entier
     */
    private int parseRegister(String element) {
        String numero = element.substring(1);
        return Integer.parseInt(numero);
    }

    /**
     * Convertit un element représentant une valeur numérique ou une adresse.
     *
     * @param element element à analyser
     * @return valeur entière correspondante
     * @throws NumberFormatException si le element n'est pas un nombre valide
     */
    private int parseValue(String element) {
        if (isAddress(element)) {
            element = element.substring(1);
        }

        if (element.startsWith("0x") || element.startsWith("0X")) {
            String hexa = element.substring(2);
            return Integer.parseInt(hexa, 16);
        }

        return Integer.parseInt(element);
    }

    /**
     * Indique si un element représente une adresse mémoire.
     * Un element est considéré comme une adresse s'il commence par '@'.
     *
     * @param element element à tester
     * @return true si le element débute par '@', false sinon
     */
    private boolean isAddress(String element) {
        return element.startsWith("@");
    }
}
