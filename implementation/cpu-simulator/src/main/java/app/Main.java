package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;

/**
 * Programme de demo qui teste les 16 instructions du simulateur.
 * Pour chaque instruction on ecrit un petit programme, on l'execute,
 * et on verifie que le resultat est bien celui attendu.
 */
public class Main {

    // composants du simulateur (reinitialises avant chaque test)
    private static Memory       mem;
    private static RegisterFile reg;
    private static CPU          cpu;
    private static Assembler    asm;

    // compteurs de tests
    private static int nbOk    = 0;
    private static int nbEchec = 0;

    // remet tout a zero avant chaque test
    private static void init() {
        mem = new Memory();
        reg = new RegisterFile();
        cpu = new CPU(mem, reg);
        asm = new Assembler(mem);
    }

    // compare attendu et obtenu, affiche OK ou ECHEC
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
        System.out.println("=== Bilan : " + nbOk + " OK / " + nbEchec + " ECHEC sur " + (nbOk + nbEchec) + " tests ===");
    }

    // load rX, valeur : met une constante dans un registre
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
        verifier("r0 = 10",   10,  reg.get(0));
        verifier("r1 = 42",   42,  reg.get(1));
        verifier("r15 = 127", 127, reg.get(15));
    }

    // add r2, r0, r1 : r2 = r0 + r1
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

    // sub r2, r0, r1 : r2 = r0 - r1
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

    // mul : resultat sur 16 bits (donc 2 registres)
    // 30 * 10 = 300 = 0x012C -> r4 = 1 (haut), r5 = 44 (bas)
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
        verifier("r4 = octet haut de 300 = 1",  1,  reg.get(4));
        verifier("r5 = octet bas de 300  = 44", 44, reg.get(5));
    }

    // div : quotient dans un reg, reste dans un autre
    // 30 / 7 -> quotient 4, reste 2
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
        verifier("r7 = reste de 30/7    = 2", 2, reg.get(7));
    }

    // and bit a bit : 60 & 15 = 12
    private static void testAnd() {
        System.out.println();
        System.out.println("-- Test 6 : and --");
        init();
        asm.assemble(
            "load r0, 60\n" +
            "load r1, 15\n" +
            "and r2, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r2 = 60 AND 15 = 12", 12, reg.get(2));
    }

    // or bit a bit : 60 | 15 = 63
    private static void testOr() {
        System.out.println();
        System.out.println("-- Test 7 : or --");
        init();
        asm.assemble(
            "load r0, 60\n" +
            "load r1, 15\n" +
            "or r2, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r2 = 60 OR 15 = 63", 63, reg.get(2));
    }

    // xor bit a bit : 60 ^ 15 = 51
    private static void testXor() {
        System.out.println();
        System.out.println("-- Test 8 : xor --");
        init();
        asm.assemble(
            "load r0, 60\n" +
            "load r1, 15\n" +
            "xor r2, r0, r1\n" +
            "break"
        );
        cpu.run();
        verifier("r2 = 60 XOR 15 = 51", 51, reg.get(2));
    }

    // store rX, @adresse : ecrit le registre en memoire
    private static void testStore() {
        System.out.println();
        System.out.println("-- Test 9 : store --");
        init();
        asm.assemble(
            "load r0, 99\n" +
            "store r0, @100\n" +
            "break"
        );
        cpu.run();
        verifier("memoire[100] = 99", 99, mem.read(100));
    }

    // load rX, @adresse : lit la memoire vers le registre
    private static void testLoadMem() {
        System.out.println();
        System.out.println("-- Test 10 : load (memoire) --");
        init();
        mem.write(200, (byte) 77);  // on prepare la valeur en memoire
        asm.assemble(
            "load r1, @200\n" +
            "break"
        );
        cpu.run();
        verifier("r1 = memoire[200] = 77", 77, reg.get(1));
    }

    // jump : saut inconditionnel (on ignore ce qu'il y a entre les 2)
    private static void testJump() {
        System.out.println();
        System.out.println("-- Test 11 : jump --");
        init();
        asm.assemble(
            "load r0, 5\n" +     // addr 0-2
            "jump @9\n" +        // addr 3-5
            "load r1, 99\n" +    // addr 6-8 : sera saute
            "break"              // addr 9
        );
        cpu.run();
        verifier("r0 = 5 (avant le saut)",       5, reg.get(0));
        verifier("r1 = 0 (saute par le jump)",   0, reg.get(1));
    }

    // beq : saut pris quand les deux registres sont egaux
    private static void testBeqPris() {
        System.out.println();
        System.out.println("-- Test 12 : beq (saut pris) --");
        init();
        asm.assemble(
            "load r0, 7\n" +
            "load r1, 7\n" +
            "beq r0, r1, @15\n" +
            "load r2, 1\n" +     // saute
            "break\n" +          // addr 14
            "break"              // addr 15 (cible)
        );
        cpu.run();
        verifier("r2 = 0 (le saut a ete pris)", 0, reg.get(2));
    }

    // beq : saut non pris quand les deux registres sont differents
    private static void testBeqNonPris() {
        System.out.println();
        System.out.println("-- Test 13 : beq (saut non pris) --");
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
        verifier("r2 = 1 (le saut n'a pas ete pris)", 1, reg.get(2));
    }

    // bne : saut pris quand les deux registres sont differents
    private static void testBnePris() {
        System.out.println();
        System.out.println("-- Test 14 : bne (saut pris) --");
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
        verifier("r2 = 0 (le saut a ete pris)", 0, reg.get(2));
    }

    // bne : saut non pris quand les registres sont egaux
    private static void testBneNonPris() {
        System.out.println();
        System.out.println("-- Test 15 : bne (saut non pris) --");
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
        verifier("r2 = 1 (le saut n'a pas ete pris)", 1, reg.get(2));
    }

    // load / store indexes : adresse = base + registre d'offset
    // base = 100, r1 = 5 -> on ecrit a l'adresse 105 puis on relit
    private static void testAccesIndexe() {
        System.out.println();
        System.out.println("-- Test 16 : load / store indexes --");
        init();
        asm.assemble(
            "load r0, 10\n" +
            "load r1, 5\n" +
            "store r0, @100, r1\n" +  // memoire[100 + 5] = 10
            "load r2, @100, r1\n" +   // r2 = memoire[100 + 5]
            "break"
        );
        cpu.run();
        verifier("memoire[105] = 10", 10, mem.read(105));
        verifier("r2 = 10",           10, reg.get(2));
    }
}
