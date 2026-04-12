package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires pour la classe ALU.
 */
public class ALUTest {

    private ALU alu;

    @BeforeEach
    public void setUp() {
        alu = new ALU();
    }

    /** 10 + 20 = 30 */
    @Test
    public void testAdd() {
        assertEquals((byte) 30, alu.add((byte) 10, (byte) 20));
    }

    /** 127 + 1 déborde sur 8 bits signés : résultat = -128 */
    @Test
    public void testAddOverflow() {
        assertEquals((byte) -128, alu.add((byte) 127, (byte) 1));
    }

    /** 15 - 10 = 5 */
    @Test
    public void testSub() {
        assertEquals((byte) 5, alu.sub((byte) 15, (byte) 10));
    }

    /** 3 × 4 = 12 : tient sur 8 bits → octet haut = 0, octet bas = 12 */
    @Test
    public void testMul() {
        byte[] result = alu.mul((byte) 3, (byte) 4);
        assertEquals((byte) 0,  result[0]);
        assertEquals((byte) 12, result[1]);
    }

    /** 50 × 10 = 500 = 0x01F4 : octet haut = 1, octet bas = 244 */
    @Test
    public void testMulLargeResult() {
        byte[] result = alu.mul((byte) 50, (byte) 10);
        assertEquals((byte) 1,   result[0]);
        assertEquals((byte) -12, result[1]); // 244 en non-signé = -12 en byte signé Java
    }

    /** 10 ÷ 3 = quotient 3, reste 1 */
    @Test
    public void testDiv() {
        byte[] result = alu.div((byte) 10, (byte) 3);
        assertEquals((byte) 3, result[0]);
        assertEquals((byte) 1, result[1]);
    }

    /** Division par zéro → ArithmeticException */
    @Test
    public void testDivByZero() {
        assertThrows(ArithmeticException.class, () -> alu.div((byte) 5, (byte) 0));
    }

    /** 0b00111100 & 0b00001111 = 0b00001100 = 12 */
    @Test
    public void testAnd() {
        assertEquals((byte) 12, alu.and((byte) 0b00111100, (byte) 0b00001111));
    }

    /** 0b00110000 | 0b00001111 = 0b00111111 = 63 */
    @Test
    public void testOr() {
        assertEquals((byte) 63, alu.or((byte) 0b00110000, (byte) 0b00001111));
    }

    /** 0b11111111 ^ 0b00001111 = 0b11110000 = -16 (signé) */
    @Test
    public void testXor() {
        assertEquals((byte) -16, alu.xor((byte) 0b11111111, (byte) 0b00001111));
    }
}
