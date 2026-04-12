package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;

/**
 * Programme de démonstration qui teste les 16 instructions du simulateur CPU.
 * Pour chaque test, le programme assembleur utilisé est affiché, ainsi que
 * la valeur attendue et la valeur réellement obtenue après exécution.
 */
public class Main {

    private static Memory       mem;
    private static RegisterFile reg;
    private static CPU          cpu;
    private static Assembler    asm;
    private static int          numTest    = 0;
    private static int          nbOk       = 0;
    private static int          nbEchec    = 0;

    // -----------------------------------------------------------------------

    /** Réinitialise tous les composants avant chaque sous-test. */
    private static void init() {
        mem = new Memory();
        reg = new RegisterFile();
        cpu = new CPU(mem, reg);
        asm = new Assembler(mem);
    }

    /** Affiche une ligne de résultat avec la valeur attendue et la valeur obtenue. */
    private static void verifier(String description, long attendu, long obtenu) {
        boolean ok = attendu == obtenu;
        if (ok) nbOk++; else nbEchec++;
        System.out.printf("    %-48s attendu=%-5d obtenu=%-5d [%s]%n",
                description, attendu, obtenu, ok ? "OK   " : "ECHEC");
    }

    /** Affiche le titre d'un bloc de test. */
    private static void titreTest(String instruction, String description) {
        numTest++;
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────────────────────────────┐");
        System.out.printf( "  │ TEST %-2d : %-57s│%n", numTest, instruction);
        System.out.printf( "  │ Objectif : %-52s│%n", description);
        System.out.println("  └─────────────────────────────────────────────────────────────────┘");
    }

    /** Affiche le code assembleur du test. */
    private static void programme(String... lignes) {
        System.out.println("  Programme assembleur :");
        for (String l : lignes) {
            System.out.println("    " + l);
        }
        System.out.println("  Résultats :");
    }

    // =======================================================================
    //  TESTS
    // =======================================================================

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        SIMULATEUR CPU — TEST DE TOUTES LES INSTRUCTIONS             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

        testLoadConst();
        testAdd();
        testSub();
        testMul();
        testDiv();
        testAnd();
        testOr();
        testXor();
        testStore();
        testLoadMem();
        testJump();
        testBeqPris();
        testBeqNonPris();
        testBnePris();
        testBneNonPris();
        testAccesIndexe();

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.printf( "║  BILAN : %d test(s) OK  /  %d test(s) ECHEC  /  %d au total%n",
                nbOk, nbEchec, nbOk + nbEchec);
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
    }

    // ---------------------------------------------------------------- LOAD_CONST

    /**
     * LOAD_CONST charge une valeur immédiate (constante) directement dans un registre,
     * sans passer par la mémoire. Aucun calcul n'est effectué.
     */
    private static void testLoadConst() {
        titreTest("LOAD_CONST", "Charger des constantes dans les registres r0, r1 et r15");
        programme(
            "LOAD_CONST r0 10     ; r0 reçoit la valeur 10",
            "LOAD_CONST r1 42     ; r1 reçoit la valeur 42",
            "LOAD_CONST r15 127   ; r15 reçoit la valeur 127 (max byte signé)",
            "BREAK                ; arrêt du processeur"
        );
        init();
        asm.assemble("LOAD_CONST r0 10\nLOAD_CONST r1 42\nLOAD_CONST r15 127\nBREAK");
        cpu.run();
        verifier("r0  après LOAD_CONST r0  10",  10,  reg.get(0));
        verifier("r1  après LOAD_CONST r1  42",  42,  reg.get(1));
        verifier("r15 après LOAD_CONST r15 127", 127, reg.get(15));
    }

    // ---------------------------------------------------------------------- ADD

    /**
     * ADD additionne deux registres et place le résultat dans un troisième.
     * Ici : r0=10, r1=20, r2 = r0 + r1 = 30.
     */
    private static void testAdd() {
        titreTest("ADD", "Addition de r0 (10) et r1 (20), résultat dans r2");
        programme(
            "LOAD_CONST r0 10     ; r0 = 10",
            "LOAD_CONST r1 20     ; r1 = 20",
            "ADD r2 r0 r1         ; r2 = r0 + r1 = 10 + 20 = 30",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 10\nLOAD_CONST r1 20\nADD r2 r0 r1\nBREAK");
        cpu.run();
        verifier("r2 = r0 + r1 = 10 + 20", 30, reg.get(2));
    }

    // ---------------------------------------------------------------------- SUB

    /**
     * SUB soustrait le deuxième registre du premier et place le résultat dans le troisième.
     * Ici : r0=15, r1=5, r2 = r0 - r1 = 10.
     */
    private static void testSub() {
        titreTest("SUB", "Soustraction de r0 (15) moins r1 (5), résultat dans r2");
        programme(
            "LOAD_CONST r0 15     ; r0 = 15",
            "LOAD_CONST r1 5      ; r1 = 5",
            "SUB r2 r0 r1         ; r2 = r0 - r1 = 15 - 5 = 10",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 15\nLOAD_CONST r1 5\nSUB r2 r0 r1\nBREAK");
        cpu.run();
        verifier("r2 = r0 - r1 = 15 - 5", 10, reg.get(2));
    }

    // ---------------------------------------------------------------------- MUL

    /**
     * MUL multiplie deux registres sur 8 bits et retourne un résultat 16 bits
     * stocké dans DEUX registres : le registre de poids fort (high) et de poids faible (low).
     *
     * Ici : r0=30, r1=10.
     * 30 × 10 = 300 = 0x012C
     *   → octet haut = 0x01 = 1   stocké dans r4
     *   → octet bas  = 0x2C = 44  stocké dans r5
     */
    private static void testMul() {
        titreTest("MUL", "Multiplication de r0 (30) × r1 (10) → résultat 16 bits dans r4:r5");
        programme(
            "LOAD_CONST r0 30     ; r0 = 30",
            "LOAD_CONST r1 10     ; r1 = 10",
            "MUL r4 r5 r0 r1      ; 30 x 10 = 300 = 0x012C",
            "                     ;   → r4 = octet haut = 0x01 = 1",
            "                     ;   → r5 = octet bas  = 0x2C = 44",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 30\nLOAD_CONST r1 10\nMUL r4 r5 r0 r1\nBREAK");
        cpu.run();
        verifier("r4 = octet haut de (30 x 10 = 300) = 300 / 256 = 1",  1,  reg.get(4));
        verifier("r5 = octet bas  de (30 x 10 = 300) = 300 % 256 = 44", 44, reg.get(5));
    }

    // ---------------------------------------------------------------------- DIV

    /**
     * DIV divise deux registres et retourne le quotient ET le reste dans deux registres séparés.
     * Ici : r0=30, r1=7.
     * 30 ÷ 7 = quotient 4, reste 2.
     */
    private static void testDiv() {
        titreTest("DIV", "Division de r0 (30) ÷ r1 (7) → quotient dans r6, reste dans r7");
        programme(
            "LOAD_CONST r0 30     ; r0 = 30 (dividende)",
            "LOAD_CONST r1 7      ; r1 = 7  (diviseur)",
            "DIV r6 r7 r0 r1      ; 30 / 7 : quotient=4, reste=2",
            "                     ;   → r6 = quotient = 4",
            "                     ;   → r7 = reste    = 2",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 30\nLOAD_CONST r1 7\nDIV r6 r7 r0 r1\nBREAK");
        cpu.run();
        verifier("r6 = quotient de (30 / 7) = 4", 4, reg.get(6));
        verifier("r7 = reste    de (30 / 7) = 2", 2, reg.get(7));
    }

    // ---------------------------------------------------------------------- AND

    /**
     * AND effectue un ET logique bit à bit entre deux registres.
     * Ici : r0 = 60 = 0b00111100, r1 = 15 = 0b00001111
     * r0 AND r1 = 0b00001100 = 12
     * (seuls les bits à 1 dans LES DEUX opérandes restent à 1)
     */
    private static void testAnd() {
        titreTest("AND", "ET logique bit à bit entre r0 (60 = 0b00111100) et r1 (15 = 0b00001111)");
        programme(
            "LOAD_CONST r0 60     ; r0 = 60 = 0b00111100",
            "LOAD_CONST r1 15     ; r1 = 15 = 0b00001111",
            "AND r2 r0 r1         ; r2 = 0b00111100",
            "                     ;       & 0b00001111",
            "                     ;       = 0b00001100 = 12",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 60\nLOAD_CONST r1 15\nAND r2 r0 r1\nBREAK");
        cpu.run();
        verifier("r2 = 60 AND 15 = 0b00001100 = 12", 12, reg.get(2));
    }

    // ----------------------------------------------------------------------- OR

    /**
     * OR effectue un OU logique bit à bit entre deux registres.
     * Ici : r0 = 60 = 0b00111100, r1 = 15 = 0b00001111
     * r0 OR r1 = 0b00111111 = 63
     * (un bit reste à 1 si au moins UN des deux opérandes l'a à 1)
     */
    private static void testOr() {
        titreTest("OR", "OU logique bit à bit entre r0 (60 = 0b00111100) et r1 (15 = 0b00001111)");
        programme(
            "LOAD_CONST r0 60     ; r0 = 60 = 0b00111100",
            "LOAD_CONST r1 15     ; r1 = 15 = 0b00001111",
            "OR r2 r0 r1          ; r2 = 0b00111100",
            "                     ;       | 0b00001111",
            "                     ;       = 0b00111111 = 63",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 60\nLOAD_CONST r1 15\nOR r2 r0 r1\nBREAK");
        cpu.run();
        verifier("r2 = 60 OR 15 = 0b00111111 = 63", 63, reg.get(2));
    }

    // ---------------------------------------------------------------------- XOR

    /**
     * XOR effectue un OU exclusif bit à bit entre deux registres.
     * Ici : r0 = 60 = 0b00111100, r1 = 15 = 0b00001111
     * r0 XOR r1 = 0b00110011 = 51
     * (un bit est à 1 seulement si les deux opérandes sont DIFFÉRENTS sur ce bit)
     */
    private static void testXor() {
        titreTest("XOR", "OU exclusif bit à bit entre r0 (60 = 0b00111100) et r1 (15 = 0b00001111)");
        programme(
            "LOAD_CONST r0 60     ; r0 = 60 = 0b00111100",
            "LOAD_CONST r1 15     ; r1 = 15 = 0b00001111",
            "XOR r2 r0 r1         ; r2 = 0b00111100",
            "                     ;       ^ 0b00001111",
            "                     ;       = 0b00110011 = 51",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 60\nLOAD_CONST r1 15\nXOR r2 r0 r1\nBREAK");
        cpu.run();
        verifier("r2 = 60 XOR 15 = 0b00110011 = 51", 51, reg.get(2));
    }

    // -------------------------------------------------------------------- STORE

    /**
     * STORE copie la valeur d'un registre vers une adresse en mémoire.
     * Ici : r0 = 99, on l'écrit à l'adresse mémoire 100.
     */
    private static void testStore() {
        titreTest("STORE", "Écrire la valeur de r0 (99) à l'adresse mémoire 100");
        programme(
            "LOAD_CONST r0 99     ; r0 = 99",
            "STORE r0 @100        ; mémoire[100] ← valeur de r0 = 99",
            "BREAK"
        );
        init();
        asm.assemble("LOAD_CONST r0 99\nSTORE r0 @100\nBREAK");
        cpu.run();
        verifier("mémoire[100] = 99 après STORE r0 @100", 99, mem.read(100));
    }

    // ----------------------------------------------------------------- LOAD_MEM

    /**
     * LOAD_MEM lit une valeur depuis une adresse mémoire et la charge dans un registre.
     * Ici : on place 77 à l'adresse 200 directement, puis LOAD_MEM va le lire dans r1.
     */
    private static void testLoadMem() {
        titreTest("LOAD_MEM", "Lire la valeur à l'adresse mémoire 200 (= 77) et la mettre dans r1");
        programme(
            "; la valeur 77 est déjà présente à l'adresse 200 en mémoire",
            "LOAD_MEM r1 @200     ; r1 ← mémoire[200] = 77",
            "BREAK"
        );
        init();
        mem.write(200, (byte) 77); // prépare la valeur en mémoire avant assemblage
        asm.assemble("LOAD_MEM r1 @200\nBREAK");
        cpu.run();
        verifier("r1 = mémoire[200] = 77 après LOAD_MEM r1 @200", 77, reg.get(1));
    }

    // -------------------------------------------------------------------- JUMP

    /**
     * JUMP effectue un saut inconditionnel : le CPU saute à l'adresse indiquée,
     * en ignorant tout ce qui se trouve entre l'instruction JUMP et la cible.
     *
     * Layout mémoire :
     *   addr 0-2 : LOAD_CONST r0 5    (exécuté)
     *   addr 3-5 : JUMP @9            (saut vers l'adresse 9)
     *   addr 6-8 : LOAD_CONST r1 99   (IGNORÉ : jamais exécuté)
     *   addr 9   : BREAK              (cible du saut)
     */
    private static void testJump() {
        titreTest("JUMP", "Saut inconditionnel de l'adresse 3 vers l'adresse 9 (ignore addr 6-8)");
        programme(
            "LOAD_CONST r0 5      ; addr 0-2 : exécuté → r0 = 5",
            "JUMP @9              ; addr 3-5 : saut vers l'adresse 9",
            "LOAD_CONST r1 99     ; addr 6-8 : JAMAIS exécuté (sauté par JUMP)",
            "BREAK                ; addr 9   : cible du saut, arrêt"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 5\n" +
            "JUMP @9\n"         +
            "LOAD_CONST r1 99\n"+
            "BREAK"
        );
        cpu.run();
        verifier("r0 = 5  (instruction AVANT le saut : exécutée)",      5, reg.get(0));
        verifier("r1 = 0  (instruction SAUTÉE : jamais exécutée → 0)", 0, reg.get(1));
    }

    // -------------------------------------------------------------------- BEQ pris

    /**
     * BEQ saute à une adresse si les deux registres sont ÉGAUX.
     * Cas 1 : r0 = r1 = 7 → les registres sont égaux → le saut EST effectué.
     * L'instruction après BEQ (LOAD_CONST r2 1) est ignorée → r2 reste à 0.
     *
     * Layout :
     *   addr 0-2   : LOAD_CONST r0 7
     *   addr 3-5   : LOAD_CONST r1 7
     *   addr 6-10  : BEQ r0 r1 @15
     *   addr 11-13 : LOAD_CONST r2 1   ← ignoré si saut pris
     *   addr 14    : BREAK
     *   addr 15    : BREAK              ← cible du saut
     */
    private static void testBeqPris() {
        titreTest("BEQ (saut pris)", "r0=7, r1=7 : r0 == r1 → saut vers addr 15, r2 reste à 0");
        programme(
            "LOAD_CONST r0 7      ; r0 = 7",
            "LOAD_CONST r1 7      ; r1 = 7  (même valeur que r0)",
            "BEQ r0 r1 @15        ; r0 == r1 → SAUT vers addr 15",
            "LOAD_CONST r2 1      ; addr 11-13 : SAUTÉ, r2 reste à 0",
            "BREAK                ; addr 14",
            "BREAK                ; addr 15 : cible du saut"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 7\n" +
            "LOAD_CONST r1 7\n" +
            "BEQ r0 r1 @15\n"   +
            "LOAD_CONST r2 1\n" +
            "BREAK\n"           +
            "BREAK"
        );
        cpu.run();
        verifier("r2 = 0 : l'instruction après BEQ a été sautée (saut pris)", 0, reg.get(2));
    }

    // ---------------------------------------------------------------- BEQ non pris

    /**
     * BEQ cas 2 : r0 = 3, r1 = 5 → les registres sont différents → le saut N'EST PAS effectué.
     * L'instruction après BEQ (LOAD_CONST r2 1) est exécutée normalement → r2 = 1.
     */
    private static void testBeqNonPris() {
        titreTest("BEQ (saut non pris)", "r0=3, r1=5 : r0 != r1 → pas de saut, r2 prend la valeur 1");
        programme(
            "LOAD_CONST r0 3      ; r0 = 3",
            "LOAD_CONST r1 5      ; r1 = 5  (valeur différente de r0)",
            "BEQ r0 r1 @15        ; r0 != r1 → PAS de saut, on continue",
            "LOAD_CONST r2 1      ; exécuté car pas de saut → r2 = 1",
            "BREAK                ; arrêt",
            "BREAK                ; addr 15 : non atteinte"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 3\n" +
            "LOAD_CONST r1 5\n" +
            "BEQ r0 r1 @15\n"   +
            "LOAD_CONST r2 1\n" +
            "BREAK\n"           +
            "BREAK"
        );
        cpu.run();
        verifier("r2 = 1 : l'instruction après BEQ a été exécutée (saut non pris)", 1, reg.get(2));
    }

    // -------------------------------------------------------------------- BNE pris

    /**
     * BNE saute si les deux registres sont DIFFÉRENTS.
     * Cas 1 : r0 = 3, r1 = 8 → différents → le saut EST effectué → r2 reste 0.
     */
    private static void testBnePris() {
        titreTest("BNE (saut pris)", "r0=3, r1=8 : r0 != r1 → saut vers addr 15, r2 reste à 0");
        programme(
            "LOAD_CONST r0 3      ; r0 = 3",
            "LOAD_CONST r1 8      ; r1 = 8  (différent de r0)",
            "BNE r0 r1 @15        ; r0 != r1 → SAUT vers addr 15",
            "LOAD_CONST r2 1      ; SAUTÉ → r2 reste à 0",
            "BREAK                ; addr 14",
            "BREAK                ; addr 15 : cible du saut"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 3\n" +
            "LOAD_CONST r1 8\n" +
            "BNE r0 r1 @15\n"   +
            "LOAD_CONST r2 1\n" +
            "BREAK\n"           +
            "BREAK"
        );
        cpu.run();
        verifier("r2 = 0 : l'instruction après BNE a été sautée (saut pris)", 0, reg.get(2));
    }

    // ---------------------------------------------------------------- BNE non pris

    /**
     * BNE cas 2 : r0 = r1 = 5 → égaux → le saut N'EST PAS effectué → r2 = 1.
     */
    private static void testBneNonPris() {
        titreTest("BNE (saut non pris)", "r0=5, r1=5 : r0 == r1 → pas de saut, r2 prend la valeur 1");
        programme(
            "LOAD_CONST r0 5      ; r0 = 5",
            "LOAD_CONST r1 5      ; r1 = 5  (même valeur que r0)",
            "BNE r0 r1 @15        ; r0 == r1 → PAS de saut, on continue",
            "LOAD_CONST r2 1      ; exécuté → r2 = 1",
            "BREAK                ; arrêt",
            "BREAK                ; addr 15 : non atteinte"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 5\n" +
            "LOAD_CONST r1 5\n" +
            "BNE r0 r1 @15\n"   +
            "LOAD_CONST r2 1\n" +
            "BREAK\n"           +
            "BREAK"
        );
        cpu.run();
        verifier("r2 = 1 : l'instruction après BNE a été exécutée (saut non pris)", 1, reg.get(2));
    }

    // -------------------------------------------------- LOAD_INDEXED / STORE_INDEXED

    /**
     * STORE_INDEXED écrit à l'adresse base + offset_registre.
     * LOAD_INDEXED lit depuis l'adresse base + offset_registre.
     *
     * Ici : base = 100, offset dans r1 = 5 → adresse effective = 100 + 5 = 105.
     * On stocke r0 = 10 à l'adresse 105, puis on le relit dans r2.
     */
    private static void testAccesIndexe() {
        titreTest("LOAD_INDEXED / STORE_INDEXED",
                  "Écrire r0 (10) à l'adresse base(100)+offset(r1=5)=105, puis relire dans r2");
        programme(
            "LOAD_CONST r0 10         ; r0 = 10  (valeur à stocker)",
            "LOAD_CONST r1 5          ; r1 = 5   (offset)",
            "STORE_INDEXED r0 @100 r1 ; mémoire[100 + r1] = mémoire[105] = r0 = 10",
            "LOAD_INDEXED  r2 @100 r1 ; r2 = mémoire[100 + r1] = mémoire[105] = 10",
            "BREAK"
        );
        init();
        asm.assemble(
            "LOAD_CONST r0 10\n"         +
            "LOAD_CONST r1 5\n"          +
            "STORE_INDEXED r0 @100 r1\n" +
            "LOAD_INDEXED  r2 @100 r1\n" +
            "BREAK"
        );
        cpu.run();
        verifier("mémoire[105] = 10 après STORE_INDEXED r0 @100 r1", 10, mem.read(105));
        verifier("r2 = 10 après LOAD_INDEXED r2 @100 r1",            10, reg.get(2));
    }
}
