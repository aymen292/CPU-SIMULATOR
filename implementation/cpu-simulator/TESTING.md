# Guide de test — CPU Simulator

Ce document explique comment installer les outils nécessaires, lancer les tests unitaires et exécuter le programme principal.

---

## Prérequis

### Java 24
Le projet est configuré pour Java 24.

```bash
java -version
# Résultat attendu : java version "24" ...
```

Si Java n'est pas installé : [https://adoptium.net](https://adoptium.net) (Temurin JDK 24).

### Maven 3.9+
Les tests sont gérés par Maven. Pour vérifier son installation :

```bash
mvn -version
# Résultat attendu : Apache Maven 3.x.x ...
```

**Installation sur macOS (Homebrew) :**
```bash
brew install maven
```

**Installation sur Linux (apt) :**
```bash
sudo apt install maven
```

**Installation manuelle :**
Télécharger depuis [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi), décompresser et ajouter `bin/` au `PATH`.

---

## Structure du projet

```
cpu-simulator/
├── pom.xml                          ← configuration Maven (Java 24, JUnit 5.10)
└── src/
    ├── main/java/
    │   ├── app/
    │   │   └── Main.java            ← programme de démonstration
    │   ├── assembler/
    │   │   └── Assembler.java       ← traduction texte → code machine
    │   ├── core/
    │   │   ├── ALU.java             ← unité arithmétique et logique
    │   │   ├── CPU.java             ← cycle Fetch-Decode-Execute
    │   │   ├── Memory.java          ← RAM 64 Ko
    │   │   └── RegisterFile.java    ← banc de 16 registres 8 bits
    │   ├── exception/
    │   │   ├── InvalidOpcodeException.java
    │   │   ├── MemoryOutOfBoundsException.java
    │   │   └── RegisterOutOfBoundsException.java
    │   └── instruction/
    │       └── Opcode.java          ← énumération des 16 instructions
    └── test/java/
        ├── assembler/
        │   └── AssemblerTest.java   ← 10 tests
        ├── core/
        │   ├── ALUTest.java         ← 10 tests
        │   ├── CPUTest.java         ← 10 tests
        │   ├── MemoryTest.java      ← 7 tests
        │   └── RegisterFileTest.java← 6 tests
        ├── exception/
        │   ├── InvalidOpcodeExceptionTest.java    ← 3 tests
        │   ├── MemoryOutOfBoundsExceptionTest.java← 3 tests
        │   └── RegisterOutOfBoundsExceptionTest.java← 3 tests
        └── instruction/
            └── OpcodeTest.java      ← 4 tests
```

---

## Lancer les tests

### Tous les tests d'un coup

Depuis la racine du projet (`cpu-simulator/`) :

```bash
mvn test
```

Résultat attendu en fin de sortie :
```
[INFO] Tests run: 56, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Tests d'un seul package

```bash
# Uniquement les tests de l'ALU
mvn test -Dtest="core.ALUTest"

# Uniquement les tests du CPU
mvn test -Dtest="core.CPUTest"

# Uniquement les tests de la mémoire
mvn test -Dtest="core.MemoryTest"

# Uniquement les tests des registres
mvn test -Dtest="core.RegisterFileTest"

# Uniquement les tests de l'assembleur
mvn test -Dtest="assembler.AssemblerTest"

# Uniquement les tests des exceptions
mvn test -Dtest="exception.*"

# Uniquement les tests des opcodes
mvn test -Dtest="instruction.OpcodeTest"
```

---

### Tests d'une seule méthode

```bash
mvn test -Dtest="core.ALUTest#testDivByZero"
mvn test -Dtest="assembler.AssemblerTest#testFullProgram"
```

---

## Description des tests par classe

### `ALUTest` — 10 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testAdd` | 10 + 20 = 30 |
| `testAddOverflow` | 127 + 1 = -128 (overflow sur byte signé) |
| `testSub` | 15 - 10 = 5 |
| `testMul` | 3 × 4 = [0, 12] (résultat 16 bits) |
| `testMulLargeResult` | 50 × 10 = 500 = [1, 0xF4] (octet haut ≠ 0) |
| `testDiv` | 10 ÷ 3 = [quotient=3, reste=1] |
| `testDivByZero` | lève `ArithmeticException` |
| `testAnd` | 0b1111 & 0b1010 = 0b1010 |
| `testOr` | 0b0101 \| 0b1010 = 0b1111 |
| `testXor` | 0b1111 ^ 0b1010 = 0b0101 |

---

### `MemoryTest` — 7 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testReadWriteByte` | écriture puis lecture d'un octet |
| `testReadWriteWord` | écriture/lecture d'un mot 16 bits (big-endian) |
| `testReadDefaultValue` | valeur par défaut = 0 |
| `testBoundaryAddresses` | accès aux adresses 0 et 65535 (limites valides) |
| `testOutOfBoundsRead` | lève `MemoryOutOfBoundsException` pour -1 et 65536 |
| `testOutOfBoundsWrite` | lève `MemoryOutOfBoundsException` pour -1 et 65536 |
| `testReset` | reset() remet toute la mémoire à zéro |

---

### `RegisterFileTest` — 6 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testGetSetRegister` | set/get sur un registre unique |
| `testAllRegisters` | accès en écriture/lecture sur les 16 registres |
| `testDefaultValue` | tous les registres valent 0 à la création |
| `testOutOfBoundsGet` | lève `RegisterOutOfBoundsException` pour -1 et 16 |
| `testOutOfBoundsSet` | lève `RegisterOutOfBoundsException` pour -1 et 16 |
| `testReset` | reset() remet tous les registres à zéro |

---

### `CPUTest` — 10 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testBreak` | BREAK arrête l'exécution |
| `testLoadConst` | LOAD_CONST r0 42 → r0 contient 42 |
| `testLoadMem` | LOAD_MEM r1 @1000 → r1 contient la valeur à l'adresse 1000 |
| `testStore` | STORE r2 @2000 → adresse 2000 contient la valeur de r2 |
| `testAdd` | ADD r2 r0 r1 → r2 = r0 + r1 |
| `testSub` | SUB r2 r0 r1 → r2 = r0 - r1 |
| `testJump` | JUMP @5 → le PC saute à l'adresse 5 |
| `testBeq` | BEQ saute si r0 == r1 |
| `testBeqNoJump` | BEQ ne saute pas si r0 ≠ r1 |
| `testBne` | BNE saute si r0 ≠ r1 |
| `testReset` | reset() remet le PC à 0 et arrête l'exécution |

---

### `AssemblerTest` — 10 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testAssembleLoadConst` | `LOAD_CONST r3 42` → octets [1, 3, 42] |
| `testAssembleLoadMem` | `LOAD_MEM r0 @1000` → octets [2, 0, 0x03, 0xE8] |
| `testAssembleStore` | `STORE r1 @500` → octets [3, 1, 0x01, 0xF4] |
| `testAssembleHexAddress` | `LOAD_MEM r0 @0x100` → adresse parsée comme 256 |
| `testAssembleAdd` | `ADD r2 r0 r1` → octets [4, 2, 0, 1] |
| `testAssembleJump` | `JUMP @50` → octets [11, 0, 50] |
| `testAssembleData` | `.data 0xFF 0x01 42` → octets bruts |
| `testAssembleString` | `.string "hello"` → codes ASCII de 'h','e','l','l','o' |
| `testIgnoreComments` | ligne `;` ignorée, BREAK toujours en adresse 0 |
| `testFullProgram` | programme complet de 5 instructions vérifié octet par octet |

---

### `OpcodeTest` — 4 tests

| Méthode | Ce qu'elle vérifie |
|---|---|
| `testGetCode` | chaque opcode retourne son code attendu (0 à 15) |
| `testFromCodeValide` | `fromCode(n)` retourne le bon opcode |
| `testFromCodeInconnu` | `fromCode(99)` retourne `null` |
| `testAllerRetour` | `fromCode(op.getCode()) == op` pour chaque opcode |

---

### Tests des exceptions — 3 × 3 tests

Chaque classe d'exception est testée sur :
1. Le contenu du message (présence de la valeur invalide)
2. La variation du message selon la valeur passée
3. L'héritage de `RuntimeException` (exception non vérifiée)

---

## Exécuter le programme principal (Main)

La classe `Main` assemble et exécute un programme de démonstration qui calcule `10 + 20` et stocke le résultat.

```bash
mvn compile exec:java -Dexec.mainClass="app.Main"
```

Sortie attendue :
```
=== Résultats de l'exécution ===
r0 (premier opérande)  = 10
r1 (deuxième opérande) = 20
r2 (résultat ADD)      = 30
Mémoire[1000]          = 30
PC final               = 15
CPU en cours ?         = false
```

---

## Syntaxe du langage assembleur

L'assembleur supporte les instructions suivantes. Les registres s'écrivent `r0` à `r15`. Les adresses mémoire sont préfixées par `@`.

### Instructions

| Instruction | Syntaxe | Description |
|---|---|---|
| BREAK | `BREAK` | Arrête l'exécution |
| LOAD_CONST | `LOAD_CONST rDest valeur` | Charge une constante dans un registre |
| LOAD_MEM | `LOAD_MEM rDest @adresse` | Charge depuis la mémoire |
| STORE | `STORE rSrc @adresse` | Écrit un registre en mémoire |
| ADD | `ADD rDest rA rB` | rDest = rA + rB |
| SUB | `SUB rDest rA rB` | rDest = rA - rB |
| MUL | `MUL rHigh rLow rA rB` | [rHigh:rLow] = rA × rB (16 bits) |
| DIV | `DIV rQ rR rA rB` | rQ = rA ÷ rB, rR = rA mod rB |
| AND | `AND rDest rA rB` | rDest = rA & rB |
| OR | `OR rDest rA rB` | rDest = rA \| rB |
| XOR | `XOR rDest rA rB` | rDest = rA ^ rB |
| JUMP | `JUMP @adresse` | Saut inconditionnel |
| BEQ | `BEQ rA rB @adresse` | Saut si rA == rB |
| BNE | `BNE rA rB @adresse` | Saut si rA != rB |
| LOAD_INDEXED | `LOAD_INDEXED rDest @base rOffset` | Charge depuis base + rOffset |
| STORE_INDEXED | `STORE_INDEXED rSrc @base rOffset` | Écrit à base + rOffset |

### Directives

| Directive | Syntaxe | Description |
|---|---|---|
| `.data` | `.data 0xFF 42 0x01` | Écrit des octets bruts en mémoire |
| `.string` | `.string "texte"` | Écrit une chaîne ASCII en mémoire |

### Commentaires

Les commentaires commencent par `;` ou `#` et peuvent apparaître en début de ligne ou en fin de ligne.

```
; Ceci est un commentaire de ligne entière
LOAD_CONST r0 10   ; commentaire en fin de ligne
```

### Formats de valeurs

| Format | Exemple | Description |
|---|---|---|
| Décimal | `42` | Entier en base 10 |
| Hexadécimal | `0xFF` | Entier en base 16 |
| Adresse | `@1000` | Adresse mémoire (décimale) |
| Adresse hex | `@0x3E8` | Adresse mémoire (hexadécimale) |

---

## Exemple de programme complet

```asm
; Calcul : r2 = r0 + r1, puis stockage et boucle si résultat != 0

LOAD_CONST r0 10       ; r0 = 10
LOAD_CONST r1 20       ; r1 = 20
ADD r2 r0 r1           ; r2 = 30
STORE r2 @1000         ; mémoire[1000] = 30
LOAD_CONST r3 0        ; r3 = 0 (zéro de référence)
BEQ r2 r3 @50          ; si r2 == 0 sauter à @50
JUMP @100              ; sinon sauter à @100
BREAK                  ; fin
```

---

## Dépannage

**`mvn : command not found`**
Maven n'est pas dans le PATH. Voir la section Prérequis.

**`COMPILATION ERROR` sur `switch` avec des `String`**
Vérifier que le compilateur cible bien Java 24 (`mvn -version` et `pom.xml`).

**Tests en échec sur les valeurs `byte`**
En Java, `byte` est signé (−128 à 127). Une valeur comme `0xFF` vaut `−1` quand castée en `byte`. Les assertions utilisent `(byte) 0xFF` pour rester cohérentes.

**`InvalidOpcodeException` pendant l'exécution**
Le PC a débordé dans une zone mémoire à zéro. Vérifier que le programme se termine bien par `BREAK` (opcode 0).
