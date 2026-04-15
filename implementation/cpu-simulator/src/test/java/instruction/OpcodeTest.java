package instruction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests pour l'enum Opcode.
 */
public class OpcodeTest {

    // chaque opcode a bien le code attendu (0 a 15)
    @Test
    public void testGetCode() {
        assertEquals(0,  Opcode.BREAK.getCode());
        assertEquals(1,  Opcode.LOAD_CONST.getCode());
        assertEquals(2,  Opcode.LOAD_MEM.getCode());
        assertEquals(3,  Opcode.STORE.getCode());
        assertEquals(4,  Opcode.ADD.getCode());
        assertEquals(5,  Opcode.SUB.getCode());
        assertEquals(6,  Opcode.MUL.getCode());
        assertEquals(7,  Opcode.DIV.getCode());
        assertEquals(8,  Opcode.AND.getCode());
        assertEquals(9,  Opcode.OR.getCode());
        assertEquals(10, Opcode.XOR.getCode());
        assertEquals(11, Opcode.JUMP.getCode());
        assertEquals(12, Opcode.BEQ.getCode());
        assertEquals(13, Opcode.BNE.getCode());
        assertEquals(14, Opcode.LOAD_INDEXED.getCode());
        assertEquals(15, Opcode.STORE_INDEXED.getCode());
    }

    // fromCode(n) renvoie le bon opcode
    @Test
    public void testFromCodeValide() {
        assertEquals(Opcode.BREAK,         Opcode.fromCode(0));
        assertEquals(Opcode.LOAD_CONST,    Opcode.fromCode(1));
        assertEquals(Opcode.ADD,           Opcode.fromCode(4));
        assertEquals(Opcode.JUMP,          Opcode.fromCode(11));
        assertEquals(Opcode.STORE_INDEXED, Opcode.fromCode(15));
    }

    // pour un code inconnu on doit avoir null
    @Test
    public void testFromCodeInconnu() {
        assertNull(Opcode.fromCode(99));
        assertNull(Opcode.fromCode(-1));
        assertNull(Opcode.fromCode(16));
    }

    // aller-retour : fromCode(op.getCode()) == op pour tous les opcodes
    @Test
    public void testAllerRetour() {
        for (Opcode op : Opcode.values()) {
            assertEquals(op, Opcode.fromCode(op.getCode()));
        }
    }
}
