package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;

/**
 * Demo du simulateur. On fait un petit test pour chaque instruction.
 * TODO : rajouter des tests plus complets sur les cas limites
 */
public class Main {

    private static Memory mem;
    private static RegisterFile reg;
    private static CPU cpu;
    private static Assembler asm;

    private static int nbOk = 0;
    private static int nbEchec = 0;

    // remet tout a zero avant chaque test
    private static void init() {
        mem = new Memory();
        reg = new RegisterFile();
        cpu = new CPU(mem, reg);
        asm = new Assembler(mem);
    }

    // compare la valeur attendue et la valeur obtenue
    private static void verifier(String description, int attendu, int obtenu) {
        if (attendu == obtenu) {
            System.out.println("  OK    : " + description);
            nbOk++;
        } else {
            System.out.println("  ECHEC : " + description + " (attendu=" + attendu + ", obtenu=" + obtenu + ")");
            nbEchec++;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Simulateur CPU : test des instructions ===");

        testLoadConst();
        testAdd();
        testSub();
        testMul();
        testDiv();
        testLogique(); // and, or, xor dans la meme methode
        testStore();
        testLoadMem();
        testJump();
        testBeq();
        testBne();
        testAccesIndexe();

        System.out.println();
        System.out.println("=== Bilan : " + nbOk + " OK / " + nbEchec + " ECHEC sur " + (nbOk + nbEchec) + " tests ===");
    }

    // test de load rX, valeur (la constante va directement dans le registre)
    private static void testLoadConst() {
        System.out.println();
        System.out.println("-- Test 1 : load (constante) --");
        init();
        asm.assemble(
            "load r0, 10\n" +
            "load r1, 42\n" +
            "load r15, 127\n" +
            "break"
        );
        cpu.run();
        verifier("r0 = 10", 10, reg.get(0));
        verifier("r1 = 42", 42, reg.get(1));
        verifier("r15 = 127", 127, reg.get(15));
    }

    // addition : r2 = r0 + r1
    private static void testAdd() {
        System.out.println();
        System.out.println("-- Test 2 : add --");
        init();
        asm.assemble(
            "load r0, 10\n" +
            "load r1, 20\n" +
            "add r2, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r2 = 10 + 20 = 30", 30, reg.get(2));
    }

    private static void testSub() {
        System.out.println();
        System.out.println("-- Test 3 : sub --");
        init();
        asm.assemble(
            "load r0, 15\n" +
            "load r1, 5\n" +
            "sub r2, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r2 = 15 - 5 = 10", 10, reg.get(2));
    }

    // multiplication : le resultat peut etre grand alors on le met sur 2 registres
    // 30 * 10 = 300 et 300 = 0x012C donc :
    //   - l'octet du haut (0x01 = 1) va dans r4
    //   - l'octet du bas  (0x2C = 44) va dans r5
    private static void testMul() {
        System.out.println();
        System.out.println("-- Test 4 : mul --");
        init();
        asm.assemble(
            "load r0, 30\n" +
            "load r1, 10\n" +
            "mul r4, r5, r0, r1\n" +
            "break"
        );
        cpu.run();
        //System.out.println("r4=" + reg.get(4) + " r5=" + reg.get(5));
        verifier("r4 = octet haut de 300 = 1", 1, reg.get(4));
        verifier("r5 = octet bas de 300 = 44", 44, reg.get(5));
    }

    // division entiere : 30 / 7 donne quotient = 4, reste = 2
    private static void testDiv() {
        System.out.println();
        System.out.println("-- Test 5 : div --");
        init();
        asm.assemble(
            "load r0, 30\n" +
            "load r1, 7\n" +
            "div r6, r7, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r6 = quotient de 30/7 = 4", 4, reg.get(6));
        verifier("r7 = reste de 30/7 = 2", 2, reg.get(7));
    }

    // les 3 operations logiques (and, or, xor) dans le meme test pour pas se repeter
    private static void testLogique() {
        System.out.println();
        System.out.println("-- Test 6 : operations logiques (and / or / xor) --");

        // AND : 60 = 0b00111100, 15 = 0b00001111, 60 & 15 = 0b00001100 = 12
        init();
        asm.assemble("load r0, 60\nload r1, 15\nand r2, r0, r1\nbreak");
        cpu.run();
        verifier("and : 60 & 15 = 12", 12, reg.get(2));

        // OR : 60 | 15 = 0b00111111 = 63
        init();
        asm.assemble("load r0, 60\nload r1, 15\nor r2, r0, r1\nbreak");
        cpu.run();
        verifier("or : 60 | 15 = 63", 63, reg.get(2));

        // XOR : 60 ^ 15 = 0b00110011 = 51
        init();
        asm.assemble("load r0, 60\nload r1, 15\nxor r2, r0, r1\nbreak");
        cpu.run();
        verifier("xor : 60 ^ 15 = 51", 51, reg.get(2));
    }

    // store : ecrit la valeur d'un registre dans la memoire
    private static void testStore() {
        System.out.println();
        System.out.println("-- Test 7 : store --");
        init();
        asm.assemble("load r0, 99\nstore r0, @100\nbreak");
        cpu.run();
        verifier("memoire[100] = 99", 99, mem.read(100));
    }

    // load rX, @addr : lit un octet de la memoire dans un registre
    private static void testLoadMem() {
        System.out.println();
        System.out.println("-- Test 8 : load (memoire) --");
        init();
        mem.write(200, (byte) 77); // on met une valeur en memoire avant
        asm.assemble("load r1, @200\nbreak");
        cpu.run();
        verifier("r1 = memoire[200] = 77", 77, reg.get(1));
    }

    // jump : saut inconditionnel (on passe par dessus le code entre les deux)
    private static void testJump() {
        System.out.println();
        System.out.println("-- Test 9 : jump --");
        init();
        asm.assemble(
            "load r0, 5\n" +     // 0-2
            "jump @9\n" +        // 3-5
            "load r1, 99\n" +    // 6-8 (saute, donc pas execute)
            "break"              // 9
        );
        cpu.run();
        verifier("r0 = 5", 5, reg.get(0));
        verifier("r1 = 0 (saute par le jump)", 0, reg.get(1));
    }

    // beq : teste les 2 cas (saut pris / saut non pris)
    private static void testBeq() {
        System.out.println();
        System.out.println("-- Test 10 : beq --");

        // cas 1 : r0 == r1 => saut pris, r2 reste a 0
        init();
        asm.assemble(
            "load r0, 7\n" +
            "load r1, 7\n" +
            "beq r0, r1, @15\n" +
            "load r2, 1\n" +
            "break\n" +
            "break"
        );
        cpu.run();
        verifier("beq pris : r2 = 0", 0, reg.get(2));

        // cas 2 : r0 != r1 => saut non pris, r2 = 1
        init();
        asm.assemble(
            "load r0, 3\n" +
            "load r1, 5\n" +
            "beq r0, r1, @15\n" +
            "load r2, 1\n" +
            "break\n" +
            "break"
        );
        cpu.run();
        verifier("beq non pris : r2 = 1", 1, reg.get(2));
    }

    // bne : teste les 2 cas (saut pris / saut non pris)
    private static void testBne() {
        System.out.println();
        System.out.println("-- Test 11 : bne --");

        // r0 != r1 => saut pris
        init();
        asm.assemble(
            "load r0, 3\n" +
            "load r1, 8\n" +
            "bne r0, r1, @15\n" +
            "load r2, 1\n" +
            "break\n" +
            "break"
        );
        cpu.run();
        verifier("bne pris : r2 = 0", 0, reg.get(2));

        // r0 == r1 => saut non pris
        init();
        asm.assemble(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "bne r0, r1, @15\n" +
            "load r2, 1\n" +
            "break\n" +
            "break"
        );
        cpu.run();
        verifier("bne non pris : r2 = 1", 1, reg.get(2));
    }

    // load / store indexes : l'adresse c'est base + contenu d'un registre
    private static void testAccesIndexe() {
        System.out.println();
        System.out.println("-- Test 12 : load / store indexes --");
        init();
        asm.assemble(
            "load r0, 10\n" +
            "load r1, 5\n" +
            "store r0, @100, r1\n" + // memoire[100+5] = 10
            "load r2, @100, r1\n" +  // r2 = memoire[100+5]
            "break"
        );
        cpu.run();
        verifier("memoire[105] = 10", 10, mem.read(105));
        verifier("r2 = 10", 10, reg.get(2));
    }
}
