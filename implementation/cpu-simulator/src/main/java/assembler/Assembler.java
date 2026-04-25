package assembler;

import core.Memory;
import instruction.Opcode;

/**
 * Assembleur textuel pour le simulateur de processeur.
 * Transforme un programme écrit en langage d'assemblage en une suite d'octets
 * déposés directement dans la mémoire fournie, en débutant à l'adresse 0.
 *
 * Syntaxe acceptée :
 * - Mnémoniques en minuscules : load, add, store, break, etc.
 * - Opérandes séparées par des virgules ou des espaces.
 * - Registres notés rX où X est un entier entre 0 et 15.
 * - Adresses mémoire préfixées par @ (exemple : @1000, @0xFF).
 * - Constantes entières en décimal ou en hexadécimal (0x...).
 * - Commentaires introduits par ; ou #, ignorés jusqu'en fin de ligne.
 * - Directive "data val1, val2, ..." pour écrire des octets bruts.
 * - Directive "string "texte"" pour écrire les codes ASCII d'une chaîne.
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
        this.memory         = memory;
        this.currentAddress = 0;
    }

    /**
     * Assemble un programme complet composé de plusieurs lignes.
     * Les lignes vides et les lignes de commentaire (commençant par ; ou #) sont ignorées.
     *
     * @param program programme en langage d'assemblage, lignes séparées par '\n'
     * @throws IllegalArgumentException si une ligne contient un mnémonique inconnu
     */
    public void assemble(String program) {
        String[] lignes = program.split("\n");

        for (int i = 0; i < lignes.length; i++) {
            String ligne = lignes[i].trim();

            if (ligne.isEmpty()) {
                continue;
            }
            if (ligne.startsWith(";") || ligne.startsWith("#")) {
                continue;
            }

            parseLine(ligne);
        }
    }

    /**
     * Analyse et encode une seule ligne d'assemblage.
     * Supprime les commentaires en fin de ligne, identifie le mnémonique
     * et ses opérandes, puis produit les octets correspondants en mémoire.
     * La directive "string" est traitée séparément car elle peut contenir
     * des virgules dans le littéral de chaîne.
     *
     * @param line ligne d'assemblage non vide
     * @throws IllegalArgumentException si le mnémonique n'est pas reconnu
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

            for (int i = 0; i < texte.length(); i++) {
                char c = texte.charAt(i);
                writeByte((byte) c);
            }
            return;
        }

        // on remplace les virgules par des espaces puis on découpe
        String lineNettoye = line.replace(",", " ");
        String[] tokens    = lineNettoye.split("\\s+");
        String mnemonic    = tokens[0].toLowerCase();

        if (mnemonic.equals("break")) {

            writeByte((byte) Opcode.BREAK.getCode());

        } else if (mnemonic.equals("load")) {

            int dest = parseRegister(tokens[1]);

            if (tokens.length == 3) {
                if (isAddress(tokens[2])) {
                    // load rX, @adresse
                    int adresse = parseValue(tokens[2]);
                    writeByte((byte) Opcode.LOAD_MEM.getCode());
                    writeByte((byte) dest);
                    writeAddress(adresse);
                } else {
                    // load rX, valeur (constante)
                    int valeur = parseValue(tokens[2]);
                    writeByte((byte) Opcode.LOAD_CONST.getCode());
                    writeByte((byte) dest);
                    writeByte((byte) valeur);
                }
            } else {
                // load rX, @base, rOffset (adressage indexé)
                int base      = parseValue(tokens[2]);
                int regOffset = parseRegister(tokens[3]);
                writeByte((byte) Opcode.LOAD_INDEXED.getCode());
                writeByte((byte) dest);
                writeAddress(base);
                writeByte((byte) regOffset);
            }

        } else if (mnemonic.equals("store")) {

            int src     = parseRegister(tokens[1]);
            int adresse = parseValue(tokens[2]);

            if (tokens.length == 3) {
                // store rX, @adresse
                writeByte((byte) Opcode.STORE.getCode());
                writeByte((byte) src);
                writeAddress(adresse);
            } else {
                // store rX, @base, rOffset (adressage indexé)
                int regOffset = parseRegister(tokens[3]);
                writeByte((byte) Opcode.STORE_INDEXED.getCode());
                writeByte((byte) src);
                writeAddress(adresse);
                writeByte((byte) regOffset);
            }

        } else if (mnemonic.equals("add")) {

            writeInstructionRRR(Opcode.ADD, tokens);

        } else if (mnemonic.equals("sub")) {

            writeInstructionRRR(Opcode.SUB, tokens);

        } else if (mnemonic.equals("and")) {

            writeInstructionRRR(Opcode.AND, tokens);

        } else if (mnemonic.equals("or")) {

            writeInstructionRRR(Opcode.OR, tokens);

        } else if (mnemonic.equals("xor")) {

            writeInstructionRRR(Opcode.XOR, tokens);

        } else if (mnemonic.equals("mul")) {

            int destHaut = parseRegister(tokens[1]);
            int destBas  = parseRegister(tokens[2]);
            int regA     = parseRegister(tokens[3]);
            int regB     = parseRegister(tokens[4]);

            writeByte((byte) Opcode.MUL.getCode());
            writeByte((byte) destHaut);
            writeByte((byte) destBas);
            writeByte((byte) regA);
            writeByte((byte) regB);

        } else if (mnemonic.equals("div")) {

            int destQ = parseRegister(tokens[1]);
            int destR = parseRegister(tokens[2]);
            int regA  = parseRegister(tokens[3]);
            int regB  = parseRegister(tokens[4]);

            writeByte((byte) Opcode.DIV.getCode());
            writeByte((byte) destQ);
            writeByte((byte) destR);
            writeByte((byte) regA);
            writeByte((byte) regB);

        } else if (mnemonic.equals("jump")) {

            int adresse = parseValue(tokens[1]);
            writeByte((byte) Opcode.JUMP.getCode());
            writeAddress(adresse);

        } else if (mnemonic.equals("beq")) {

            writeBranch(Opcode.BEQ, tokens);

        } else if (mnemonic.equals("bne")) {

            writeBranch(Opcode.BNE, tokens);

        } else if (mnemonic.equals("data")) {

            for (int i = 1; i < tokens.length; i++) {
                int valeur = parseValue(tokens[i]);
                writeByte((byte) valeur);
            }

        } else {
            throw new IllegalArgumentException("Mnémonique inconnu : " + mnemonic);
        }
    }

    /**
     * Encode une instruction au format op rDest, rA, rB (trois registres).
     * Utilisé pour ADD, SUB, AND, OR et XOR.
     * Octets produits : [opcode][dest][regA][regB]
     *
     * @param opcode opcode de l'instruction à encoder
     * @param tokens tableau de tokens de la ligne (tokens[0] = mnémonique, tokens[1..3] = registres)
     */
    private void writeInstructionRRR(Opcode opcode, String[] tokens) {
        int dest = parseRegister(tokens[1]);
        int regA = parseRegister(tokens[2]);
        int regB = parseRegister(tokens[3]);

        writeByte((byte) opcode.getCode());
        writeByte((byte) dest);
        writeByte((byte) regA);
        writeByte((byte) regB);
    }

    /**
     * Encode une instruction de branchement au format op rA, rB, @adresse.
     * Utilisé pour BEQ et BNE.
     * Octets produits : [opcode][regA][regB][adresse_haut][adresse_bas]
     *
     * @param opcode opcode du branchement (BEQ ou BNE)
     * @param tokens tableau de tokens de la ligne (tokens[1..2] = registres, tokens[3] = adresse)
     */
    private void writeBranch(Opcode opcode, String[] tokens) {
        int regA    = parseRegister(tokens[1]);
        int regB    = parseRegister(tokens[2]);
        int adresse = parseValue(tokens[3]);

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
     * Extrait le numéro entier d'un token de registre de la forme rX.
     * Exemple : "r5" retourne 5, "r15" retourne 15.
     *
     * @param token token de registre commençant par 'r'
     * @return numéro du registre sous forme d'entier
     */
    private int parseRegister(String token) {
        String numero = token.substring(1);
        return Integer.parseInt(numero);
    }

    /**
     * Convertit un token représentant une valeur numérique ou une adresse.
     * Formats acceptés : décimal ("42"), hexadécimal ("0xFF"), adresse ("@1000", "@0x3E8").
     * Le préfixe @ est retiré avant l'analyse si présent.
     *
     * @param token token à analyser
     * @return valeur entière correspondante
     * @throws NumberFormatException si le token n'est pas un nombre valide
     */
    private int parseValue(String token) {
        if (isAddress(token)) {
            token = token.substring(1);
        }

        if (token.startsWith("0x") || token.startsWith("0X")) {
            String hexa = token.substring(2);
            return Integer.parseInt(hexa, 16);
        }

        return Integer.parseInt(token);
    }

    /**
     * Indique si un token représente une adresse mémoire.
     * Un token est considéré comme une adresse s'il commence par '@'.
     *
     * @param token token à tester
     * @return true si le token débute par '@', false sinon
     */
    private boolean isAddress(String token) {
        return token.startsWith("@");
    }
}
