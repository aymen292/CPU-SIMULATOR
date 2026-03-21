# Prompt Conception — Projet CPU Simulator

## IMPORTANT : Règles strictes

**Tu ne dois PAS implémenter la logique des méthodes.** Chaque méthode doit avoir un corps vide, ou retourner une valeur par défaut (`return 0;`, `return null;`, `return false;`, etc.) avec un commentaire `// TODO : à implémenter`.

Tu dois UNIQUEMENT :
- Créer les packages nécessaires
- Créer les fichiers Java avec les bonnes déclarations de classe/enum/interface
- Déclarer les attributs
- Déclarer les signatures de méthodes (avec les bons paramètres et types de retour)
- Écrire les commentaires Javadoc complets sur chaque classe et chaque méthode publique (en français)
- Déclarer les imports nécessaires
- Mettre à jour le pom.xml

**Aucune logique métier. Aucun algorithme. Que des squelettes.**

---

## Contexte

Le projet `cpu-simulator` est un projet Maven Java. C'est un simulateur de processeur avec assembleur. Voici l'arborescence actuelle :

```
cpu-simulator/
├─ pom.xml
├─ README.md
└─ src/
   ├─ main/java/
   │  ├─ app/Main.java
   │  ├─ assembler/Assembler.java
   │  ├─ core/CPU.java
   │  ├─ core/Memory.java
   │  ├─ core/RegisterFile.java
   │  └─ instruction/Opcode.java   ← classe vide actuellement
   └─ test/java/                    ← vide actuellement
```

---

## Modifications à appliquer

### 1. Transformer `instruction/Opcode.java` en enum

Remplace la classe vide par un enum. Chaque constante a un attribut `int code`.

Constantes à déclarer (dans cet ordre) :

```
BREAK(0)
LOAD_CONST(1)
LOAD_MEM(2)
STORE(3)
ADD(4)
SUB(5)
MUL(6)
DIV(7)
AND(8)
OR(9)
XOR(10)
JUMP(11)
BEQ(12)
BNE(13)
LOAD_INDEXED(14)
STORE_INDEXED(15)
```

Déclare :
- Attribut privé `int code`
- Constructeur privé `Opcode(int code)`
- Getter `public int getCode()`
- Méthode statique `public static Opcode fromCode(int code)` → corps avec `// TODO : à implémenter`, retourne `null` pour l'instant

Javadoc sur l'enum et sur chaque méthode.

---

### 2. Créer le package `exception/` avec 3 fichiers

**`src/main/java/exception/InvalidOpcodeException.java`**
```java
package exception;

/**
 * Exception levée lorsqu'un code d'instruction inconnu est rencontré.
 */
public class InvalidOpcodeException extends RuntimeException {

    /**
     * Construit une exception pour un code opcode invalide.
     * @param code le code numérique inconnu
     */
    public InvalidOpcodeException(int code) {
        super("Opcode inconnu : " + code);
    }
}
```

**`src/main/java/exception/MemoryOutOfBoundsException.java`**
```java
package exception;

/**
 * Exception levée lors d'un accès mémoire hors limites.
 */
public class MemoryOutOfBoundsException extends RuntimeException {

    /**
     * Construit une exception pour une adresse mémoire invalide.
     * @param address l'adresse hors limites
     */
    public MemoryOutOfBoundsException(int address) {
        super("Adresse mémoire hors limites : " + address);
    }
}
```

**`src/main/java/exception/RegisterOutOfBoundsException.java`**
```java
package exception;

/**
 * Exception levée lors d'un accès à un registre invalide.
 */
public class RegisterOutOfBoundsException extends RuntimeException {

    /**
     * Construit une exception pour un index de registre invalide.
     * @param index l'index du registre hors limites
     */
    public RegisterOutOfBoundsException(int index) {
        super("Registre hors limites : " + index + ". Les registres valides vont de 0 à 15.");
    }
}
```

Note : ces 3 classes sont les seules où le code est complet car ce sont des classes triviales (juste un constructeur qui appelle super).

---

### 3. Modifier `core/Memory.java` — squelette uniquement

```
package core;

Javadoc : "Représente la mémoire du simulateur (64 Ko = 65536 octets)."

Attributs :
- public static final int MEMORY_SIZE = 65536;
- private byte[] data;

Méthodes (toutes avec Javadoc, toutes avec corps TODO) :
- public Memory()                          → constructeur
- public byte read(int address)            → @param address, @return la valeur lue, @throws MemoryOutOfBoundsException
- public void write(int address, byte value) → @param address, @param value, @throws MemoryOutOfBoundsException
- public int readWord(int address)         → @param address, @return valeur 16 bits non signée (2 octets big-endian)
- public void writeWord(int address, int value) → @param address, @param value
- public void reset()                      → remet la mémoire à zéro
```

---

### 4. Modifier `core/RegisterFile.java` — squelette uniquement

```
package core;

Javadoc : "Représente le banc de 16 registres 8 bits du processeur."

Attributs :
- public static final int NUM_REGISTERS = 16;
- private byte[] registers;

Méthodes (toutes avec Javadoc, toutes avec corps TODO) :
- public RegisterFile()                    → constructeur
- public byte get(int index)               → @param index, @return valeur du registre, @throws RegisterOutOfBoundsException
- public void set(int index, byte value)   → @param index, @param value, @throws RegisterOutOfBoundsException
- public void reset()                      → remet tous les registres à zéro
```

---

### 5. Créer `core/ALU.java` — squelette uniquement

```
package core;

Javadoc : "Unité Arithmétique et Logique (ALU). Effectue les opérations de calcul sur les registres."

Méthodes (toutes avec Javadoc, toutes avec corps TODO) :
- public byte add(byte a, byte b)          → @return a + b
- public byte sub(byte a, byte b)          → @return a - b
- public byte[] mul(byte a, byte b)        → @return tableau [highByte, lowByte] du résultat 16 bits
- public byte[] div(byte a, byte b)        → @return tableau [quotient, reste], @throws ArithmeticException si b == 0
- public byte and(byte a, byte b)          → @return a & b
- public byte or(byte a, byte b)           → @return a | b
- public byte xor(byte a, byte b)          → @return a ^ b
```

---

### 6. Modifier `core/CPU.java` — squelette uniquement

```
package core;

Javadoc : "Processeur simulé. Exécute le cycle fetch/decode/execute sur les instructions en mémoire."

Attributs :
- private Memory memory;
- private RegisterFile registers;
- private ALU alu;
- private int pc;           // compteur de programme (program counter)
- private boolean running;

Méthodes publiques (toutes avec Javadoc, toutes avec corps TODO) :
- public CPU(Memory memory, RegisterFile registers) → constructeur, @param memory, @param registers
- public void run()         → lance la boucle d'exécution jusqu'à BREAK
- public void reset()       → remet le PC à 0 et running à false
- public int getPC()        → @return la valeur du compteur de programme
- public boolean isRunning() → @return true si le CPU est en cours d'exécution

Méthodes privées (toutes avec Javadoc, toutes avec corps TODO) :
- private byte fetch()      → lit l'octet à l'adresse PC, incrémente PC, @return l'octet lu
- private void decode(byte opcodeByte) → décode l'opcode et exécute l'instruction correspondante
- private void executeBreak()
- private void executeLoadConst()
- private void executeLoadMem()
- private void executeStore()
- private void executeAdd()
- private void executeSub()
- private void executeMul()
- private void executeDiv()
- private void executeAnd()
- private void executeOr()
- private void executeXor()
- private void executeJump()
- private void executeBeq()
- private void executeBne()
- private void executeLoadIndexed()
- private void executeStoreIndexed()
```

Chaque méthode `executeXxx()` a un commentaire Javadoc qui décrit brièvement ce que l'instruction fait et quels paramètres elle lit en mémoire.

---

### 7. Modifier `assembler/Assembler.java` — squelette uniquement

```
package assembler;

Javadoc : "Assembleur qui traduit un programme textuel en codes machine écrits en mémoire."

Attributs :
- private Memory memory;
- private int currentAddress;  // adresse courante d'écriture

Méthodes publiques (toutes avec Javadoc, toutes avec corps TODO) :
- public Assembler(Memory memory)           → constructeur
- public void assemble(String program)      → @param program le programme assembleur multiligne

Méthodes privées (toutes avec Javadoc, toutes avec corps TODO) :
- private void parseLine(String line)       → parse et traduit une ligne d'assembleur
- private void writeByte(byte value)        → écrit un octet en mémoire à currentAddress et incrémente
- private void writeAddress(int address)    → écrit une adresse 16 bits en big-endian (2 octets)
- private int parseRegister(String token)   → parse un token "rX" et retourne le numéro du registre
- private int parseValue(String token)      → parse une valeur décimale, hexadécimale ou une adresse @...
- private boolean isAddress(String token)   → retourne true si le token commence par @
```

---

### 8. Modifier `app/Main.java` — squelette uniquement

```
package app;

Javadoc : "Point d'entrée du simulateur. Crée les composants, assemble un programme de démonstration et l'exécute."

Méthode :
- public static void main(String[] args)

Corps : juste un commentaire décrivant les étapes à faire :
// TODO : 1. Créer Memory, RegisterFile, CPU, Assembler
// TODO : 2. Définir un programme assembleur de démonstration
// TODO : 3. Assembler le programme
// TODO : 4. Exécuter avec le CPU
// TODO : 5. Afficher les résultats
```

---

### 9. Créer les squelettes de tests JUnit 5

Crée les fichiers suivants. Chaque fichier contient uniquement :
- La déclaration de package
- Les imports JUnit 5 (`org.junit.jupiter.api.Test`, `org.junit.jupiter.api.BeforeEach`, `static org.junit.jupiter.api.Assertions.*`)
- La classe de test avec sa Javadoc
- Les méthodes de test annotées `@Test`, avec Javadoc, mais corps vide (`// TODO : à implémenter`)
- Une fixture `@BeforeEach` si pertinent (avec corps TODO)

**`src/test/java/core/MemoryTest.java`**
Méthodes à déclarer :
- testReadWriteByte()
- testReadWriteWord()
- testReadDefaultValue()
- testBoundaryAddresses()
- testOutOfBoundsRead()
- testOutOfBoundsWrite()
- testReset()

**`src/test/java/core/RegisterFileTest.java`**
Méthodes à déclarer :
- testGetSetRegister()
- testAllRegisters()
- testDefaultValue()
- testOutOfBoundsGet()
- testOutOfBoundsSet()
- testReset()

**`src/test/java/core/ALUTest.java`**
Méthodes à déclarer :
- testAdd()
- testAddOverflow()
- testSub()
- testMul()
- testMulLargeResult()
- testDiv()
- testDivByZero()
- testAnd()
- testOr()
- testXor()

**`src/test/java/core/CPUTest.java`**
Méthodes à déclarer :
- testBreak()
- testLoadConst()
- testLoadMem()
- testStore()
- testAdd()
- testJump()
- testBeq()
- testBne()

**`src/test/java/assembler/AssemblerTest.java`**
Méthodes à déclarer :
- testAssembleLoadConst()
- testAssembleLoadMem()
- testAssembleStore()
- testAssembleHexAddress()
- testAssembleAdd()
- testAssembleJump()
- testAssembleData()
- testAssembleString()
- testIgnoreComments()
- testFullProgram()

---

### 10. Mettre à jour le `pom.xml`

Ajoute la dépendance JUnit 5 :
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

Ajoute le plugin surefire :
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

---

## Arborescence finale attendue

```
cpu-simulator/
├─ pom.xml
├─ README.md
└─ src/
   ├─ main/java/
   │  ├─ app/
   │  │  └─ Main.java
   │  ├─ assembler/
   │  │  └─ Assembler.java
   │  ├─ core/
   │  │  ├─ ALU.java                ← NOUVEAU
   │  │  ├─ CPU.java
   │  │  ├─ Memory.java
   │  │  └─ RegisterFile.java
   │  ├─ exception/                  ← NOUVEAU PACKAGE
   │  │  ├─ InvalidOpcodeException.java
   │  │  ├─ MemoryOutOfBoundsException.java
   │  │  └─ RegisterOutOfBoundsException.java
   │  └─ instruction/
   │     └─ Opcode.java             ← TRANSFORMÉ EN ENUM
   └─ test/java/
      ├─ assembler/                  ← NOUVEAU
      │  └─ AssemblerTest.java
      └─ core/                       ← NOUVEAU
         ├─ ALUTest.java
         ├─ CPUTest.java
         ├─ MemoryTest.java
         └─ RegisterFileTest.java
```

## Rappel final

**NE CODE PAS LA LOGIQUE.** Tous les corps de méthodes doivent contenir uniquement `// TODO : à implémenter` avec un return par défaut si nécessaire. Seules les exceptions (package exception/) sont complètes car triviales.