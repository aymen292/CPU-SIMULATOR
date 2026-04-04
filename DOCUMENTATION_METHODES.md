# Documentation Détaillée des Classes et Méthodes — CPU Simulator

> **Projet** : Simulateur de processeur (CPU Simulator)
> **Langage** : Java
> **Architecture** : Simulateur d'un processeur 8 bits avec 16 registres et 64 Ko de mémoire, suivant le cycle Fetch → Decode → Execute

---

## Table des matières

1. [Package `app` — Point d'entrée](#1-package-app--point-dentrée)
   - [Classe `Main`](#classe-main)
2. [Package `core` — Composants du processeur](#2-package-core--composants-du-processeur)
   - [Classe `CPU`](#classe-cpu)
   - [Classe `ALU`](#classe-alu)
   - [Classe `Memory`](#classe-memory)
   - [Classe `RegisterFile`](#classe-registerfile)
3. [Package `instruction` — Jeu d'instructions](#3-package-instruction--jeu-dinstructions)
   - [Enum `Opcode`](#enum-opcode)
4. [Package `assembler` — Assembleur](#4-package-assembler--assembleur)
   - [Classe `Assembler`](#classe-assembler)
5. [Package `exception` — Exceptions personnalisées](#5-package-exception--exceptions-personnalisées)
   - [Classe `InvalidOpcodeException`](#classe-invalidopcodeexception)
   - [Classe `RegisterOutOfBoundsException`](#classe-registeroutofboundsexception)
   - [Classe `MemoryOutOfBoundsException`](#classe-memoryoutofboundsexception)
6. [Tests unitaires](#6-tests-unitaires)
   - [Classe `CPUTest`](#classe-cputest)
   - [Classe `ALUTest`](#classe-alutest)
   - [Classe `MemoryTest`](#classe-memorytest)
   - [Classe `RegisterFileTest`](#classe-registerfiletest)
   - [Classe `AssemblerTest`](#classe-assemblertest)

---

## 1. Package `app` — Point d'entrée

### Classe `Main`

**Fichier** : `src/main/java/app/Main.java`

**Rôle général** : C'est le point d'entrée de l'application. Son unique responsabilité est de lancer l'exécution du simulateur en orchestrant la création des composants principaux (mémoire, registres, CPU, assembleur), puis d'y charger un programme et de l'exécuter.

---

#### `public static void main(String[] args)`

| Attribut       | Détail                          |
|----------------|---------------------------------|
| **Visibilité** | `public static`                 |
| **Paramètre**  | `args` — arguments ligne de commande (non utilisés) |
| **Retour**     | `void`                          |

**Description détaillée** :
Méthode principale Java, appelée automatiquement au lancement de la JVM. Elle est destinée à :

1. **Instancier les composants** : créer un objet `Memory` (représentant les 64 Ko de RAM du simulateur), un objet `RegisterFile` (les 16 registres du processeur), un objet `CPU` en passant la mémoire et les registres, et un objet `Assembler` associé à la mémoire.
2. **Définir un programme de démonstration** : écrire un programme en langage assembleur sous forme de `String` multi-lignes.
3. **Assembler le programme** : appeler `assembler.assemble(programme)` pour traduire le texte assembleur en opcodes binaires stockés dans la mémoire simulée.
4. **Lancer l'exécution** : appeler `cpu.run()` pour démarrer le cycle Fetch-Decode-Execute jusqu'à rencontrer l'instruction `BREAK`.
5. **Afficher les résultats** : lire et afficher les valeurs des registres et de la mémoire après exécution.

> **Note** : Toute la logique de cette méthode est à implémenter (TODO).

---

## 2. Package `core` — Composants du processeur

### Classe `CPU`

**Fichier** : `src/main/java/core/CPU.java`

**Rôle général** : Le CPU (Central Processing Unit) est le cœur du simulateur. Il implémente le cycle classique **Fetch → Decode → Execute** : il lit une instruction depuis la mémoire à l'adresse pointée par le compteur de programme (`pc`), décode l'opcode récupéré, puis délègue l'exécution à la méthode spécialisée correspondante. Il maintient son propre état d'exécution via le booléen `running`.

**Attributs importants** :
- `Memory memory` — la mémoire système partagée
- `RegisterFile registers` — les 16 registres du processeur
- `ALU alu` — l'unité arithmétique et logique
- `int pc` — le compteur de programme (Program Counter), pointe vers la prochaine instruction à lire
- `boolean running` — indique si le processeur est en cours d'exécution

---

#### `public CPU(Memory memory, RegisterFile registers)`

| Attribut       | Détail                                            |
|----------------|---------------------------------------------------|
| **Visibilité** | `public`                                          |
| **Type**       | Constructeur                                      |
| **Paramètres** | `memory` — mémoire du système, `registers` — banc de registres |

**Description détaillée** :
Initialise le CPU en stockant les références aux composants partagés (`memory` et `registers`), en créant une instance interne de `ALU`, en positionnant le compteur de programme `pc` à 0 (début de la mémoire) et en mettant `running` à `false`. Le CPU est prêt mais pas encore démarré.

---

#### `public void run()`

| Attribut       | Détail     |
|----------------|------------|
| **Visibilité** | `public`   |
| **Retour**     | `void`     |

**Description détaillée** :
Lance la boucle d'exécution principale du processeur. Elle positionne `running = true` puis entre dans une boucle `while(running)`. À chaque itération :
1. Appelle `fetch()` pour récupérer l'octet de l'opcode suivant et avancer `pc`.
2. Passe cet octet à `decode(opcodeByte)` qui identifie et exécute l'instruction.

La boucle s'arrête quand `running` passe à `false`, ce qui se produit lors de l'exécution d'une instruction `BREAK`.

---

#### `public void reset()`

| Attribut       | Détail     |
|----------------|------------|
| **Visibilité** | `public`   |
| **Retour**     | `void`     |

**Description détaillée** :
Remet le CPU dans son état initial sans modifier la mémoire ni les registres. Concrètement :
- Remet `pc` à `0` (le processeur recommencera à lire depuis le début de la mémoire).
- Positionne `running` à `false`.

Utile pour relancer un programme déjà chargé en mémoire, ou pour les tests unitaires.

---

#### `public int getPC()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Retour**     | `int` — valeur courante du compteur de programme |

**Description détaillée** :
Simple accesseur (getter) retournant la valeur actuelle de `pc`. Utilisé notamment dans les tests pour vérifier qu'un saut (`JUMP`, `BEQ`, `BNE`) a correctement modifié le compteur.

---

#### `public boolean isRunning()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Retour**     | `boolean` — `true` si le CPU exécute, `false` sinon |

**Description détaillée** :
Accesseur retournant l'état d'exécution courant du CPU. Permet à du code externe (ex. `Main`) de savoir si le simulateur est encore actif.

---

#### `private byte fetch()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `byte` — l'octet lu à l'adresse `pc` |

**Description détaillée** :
Étape **Fetch** du cycle. Lit un octet depuis `memory.read(pc)`, puis incrémente `pc` de 1 pour que la prochaine lecture récupère l'octet suivant. Retourne l'octet lu, qui sera interprété comme un opcode ou un opérande selon le contexte d'appel.

---

#### `private void decode(byte opcodeByte)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `opcodeByte` — l'octet brut représentant l'opcode |
| **Retour**     | `void`  |

**Description détaillée** :
Étape **Decode** du cycle. Convertit `opcodeByte` en `Opcode` via `Opcode.fromCode(int)`, puis utilise un `switch` (ou chaîne de `if`) pour appeler la méthode d'exécution correspondante :
- `BREAK` → `executeBreak()`
- `LOAD_CONST` → `executeLoadConst()`
- `LOAD_MEM` → `executeLoadMem()`
- `STORE` → `executeStore()`
- `ADD` → `executeAdd()`, etc.

Si le code est inconnu, lève une `InvalidOpcodeException`.

---

#### `private void executeBreak()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **BREAK** (opcode 0). Positionne `running = false`, ce qui provoque la sortie de la boucle dans `run()`. C'est l'instruction de terminaison du programme : elle indique au CPU de stopper toute exécution.

---

#### `private void executeLoadConst()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **LOAD_CONST** (opcode 1). Lit successivement depuis la mémoire :
1. Un octet : l'**index du registre destination** (ex. `r2`).
2. Un octet : la **valeur constante** à charger (ex. `42`).

Puis appelle `registers.set(destReg, constValue)` pour écrire la constante dans le registre. Cette instruction permet d'initialiser un registre avec une valeur immédiate.

---

#### `private void executeLoadMem()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **LOAD_MEM** (opcode 2). Lit depuis la mémoire :
1. Un octet : l'**index du registre destination**.
2. Deux octets (16 bits, big-endian) : l'**adresse mémoire source**.

Puis lit la valeur à cette adresse mémoire (`memory.read(address)`) et l'écrit dans le registre destination. Permet de charger une valeur stockée en RAM dans un registre.

---

#### `private void executeStore()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **STORE** (opcode 3). Lit depuis la mémoire :
1. Un octet : l'**index du registre source**.
2. Deux octets (16 bits, big-endian) : l'**adresse mémoire destination**.

Puis écrit la valeur du registre source dans la mémoire à l'adresse donnée (`memory.write(address, registers.get(srcReg))`). Opération inverse de `LOAD_MEM`.

---

#### `private void executeAdd()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **ADD** (opcode 4). Lit depuis la mémoire :
1. Un octet : l'**index du registre destination**.
2. Un octet : l'**index du registre A** (premier opérande).
3. Un octet : l'**index du registre B** (second opérande).

Calcule `alu.add(registers.get(regA), registers.get(regB))` et stocke le résultat dans le registre destination.

---

#### `private void executeSub()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **SUB** (opcode 5). Structure identique à `executeAdd()` mais appelle `alu.sub(a, b)`. Calcule `regA - regB` et stocke dans le registre destination.

---

#### `private void executeMul()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **MUL** (opcode 6). Lit depuis la mémoire :
1. Un octet : l'**index du registre destination haut** (bits 15–8 du résultat).
2. Un octet : l'**index du registre destination bas** (bits 7–0 du résultat).
3. Un octet : l'**index du registre A**.
4. Un octet : l'**index du registre B**.

Appelle `alu.mul(a, b)` qui retourne un tableau `[highByte, lowByte]`. Stocke chaque moitié dans le registre correspondant. Nécessite deux registres destination car le produit de deux valeurs 8 bits peut atteindre 16 bits.

---

#### `private void executeDiv()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **DIV** (opcode 7). Lit depuis la mémoire :
1. Un octet : l'**index du registre quotient**.
2. Un octet : l'**index du registre reste**.
3. Un octet : l'**index du registre dividende** (A).
4. Un octet : l'**index du registre diviseur** (B).

Appelle `alu.div(a, b)` qui retourne `[quotient, remainder]`. Stocke chaque valeur dans son registre dédié. Propage l'`ArithmeticException` si B vaut 0.

---

#### `private void executeAnd()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **AND** (opcode 8). Structure identique aux instructions arithmétiques à 3 registres. Appelle `alu.and(a, b)` et stocke le résultat du ET logique bit à bit dans le registre destination.

---

#### `private void executeOr()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **OR** (opcode 9). Appelle `alu.or(a, b)` et stocke le résultat du OU logique bit à bit dans le registre destination.

---

#### `private void executeXor()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **XOR** (opcode 10). Appelle `alu.xor(a, b)` et stocke le résultat du OU exclusif bit à bit dans le registre destination.

---

#### `private void executeJump()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **JUMP** (opcode 11). Lit depuis la mémoire deux octets (16 bits, big-endian) formant l'**adresse de saut**. Affecte directement cette valeur à `pc`. Saut **inconditionnel** : le CPU reprendra son exécution à cette adresse sans condition.

---

#### `private void executeBeq()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **BEQ** — Branch if Equal (opcode 12). Lit depuis la mémoire :
1. Un octet : l'**index du registre A**.
2. Un octet : l'**index du registre B**.
3. Deux octets : l'**adresse de saut**.

Si `registers.get(regA) == registers.get(regB)`, affecte l'adresse de saut à `pc`. Sinon, `pc` continue normalement (il a déjà avancé lors des lectures). Permet de créer des structures conditionnelles (if, while).

---

#### `private void executeBne()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **BNE** — Branch if Not Equal (opcode 13). Structure identique à `executeBeq()` mais la condition est inversée : le saut se produit si les deux registres sont **différents**. Utile pour les boucles (continuer tant que le compteur n'a pas atteint une valeur cible).

---

#### `private void executeLoadIndexed()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **LOAD_INDEXED** (opcode 14). Lit depuis la mémoire :
1. Un octet : l'**index du registre destination**.
2. Deux octets : l'**adresse de base**.
3. Un octet : l'**index du registre offset** (décalage).

Calcule l'adresse effective : `baseAddress + registers.get(offsetReg)`, lit la valeur à cette adresse et la stocke dans le registre destination. Permet l'accès indexé aux tableaux en mémoire.

---

#### `private void executeStoreIndexed()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Retour**     | `void`  |

**Description détaillée** :
Exécute l'instruction **STORE_INDEXED** (opcode 15). Lit depuis la mémoire :
1. Un octet : l'**index du registre source**.
2. Deux octets : l'**adresse de base**.
3. Un octet : l'**index du registre offset**.

Calcule l'adresse effective : `baseAddress + registers.get(offsetReg)`, puis écrit la valeur du registre source à cette adresse. Opération inverse de `LOAD_INDEXED`, permet l'écriture indexée dans les tableaux.

---

### Classe `ALU`

**Fichier** : `src/main/java/core/ALU.java`

**Rôle général** : L'ALU (Arithmetic Logic Unit — Unité Arithmétique et Logique) est le composant qui réalise tous les **calculs** du processeur. Elle ne possède aucun état propre (pas d'attributs) : elle reçoit des opérandes, effectue une opération, et retourne un résultat. Toutes ses méthodes sont **pures** (sans effets de bord).

---

#### `public byte add(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a` — premier opérande 8 bits, `b` — second opérande 8 bits |
| **Retour**     | `byte` — résultat de `a + b` tronqué à 8 bits |

**Description détaillée** :
Additionne deux valeurs `byte`. En Java, les `byte` sont signés (–128 à 127). Le calcul est effectué en `int` puis recast en `byte`, ce qui simule naturellement le comportement d'un overflow 8 bits (ex. 127 + 1 = -128 en complément à 2).

---

#### `public byte sub(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a` — minuende, `b` — soustracteur |
| **Retour**     | `byte` — résultat de `a - b` |

**Description détaillée** :
Soustrait `b` de `a`. Même comportement d'overflow que `add()`. Retourne `(byte)(a - b)`.

---

#### `public byte[] mul(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a` — premier facteur, `b` — second facteur |
| **Retour**     | `byte[]` — tableau de 2 éléments : `[highByte, lowByte]` |

**Description détaillée** :
Multiplie deux valeurs 8 bits. Le résultat peut atteindre 16 bits (ex. 255 × 255 = 65025). La méthode :
1. Convertit `a` et `b` en `int` non signés (`& 0xFF`).
2. Calcule le produit en `int`.
3. Extrait les 8 bits de poids fort : `(byte)(product >> 8)`.
4. Extrait les 8 bits de poids faible : `(byte)(product & 0xFF)`.
5. Retourne `new byte[]{highByte, lowByte}`.

---

#### `public byte[] div(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a` — dividende, `b` — diviseur |
| **Retour**     | `byte[]` — tableau de 2 éléments : `[quotient, reste]` |
| **Exception**  | `ArithmeticException` si `b == 0` |

**Description détaillée** :
Effectue la division euclidienne de `a` par `b`. Si `b` vaut zéro, lève une `ArithmeticException` (division par zéro). Sinon, calcule le quotient (`a / b`) et le reste (`a % b`) puis retourne `new byte[]{quotient, remainder}`. Les deux valeurs tiennent sur 8 bits car elles sont toujours inférieures au dividende.

---

#### `public byte and(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a`, `b` — opérandes |
| **Retour**     | `byte` — `a & b` |

**Description détaillée** :
Effectue un ET logique bit à bit entre `a` et `b`. Chaque bit du résultat vaut 1 si et seulement si le bit correspondant dans `a` ET dans `b` vaut 1. Utile pour masquer des bits (isoler certains bits d'une valeur).

---

#### `public byte or(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a`, `b` — opérandes |
| **Retour**     | `byte` — `a | b` |

**Description détaillée** :
Effectue un OU logique bit à bit. Chaque bit du résultat vaut 1 si au moins un des deux bits correspondants vaut 1. Utile pour combiner des flags ou activer des bits spécifiques.

---

#### `public byte xor(byte a, byte b)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `a`, `b` — opérandes |
| **Retour**     | `byte` — `a ^ b` |

**Description détaillée** :
Effectue un OU exclusif bit à bit. Un bit du résultat vaut 1 si les deux bits correspondants sont **différents**. Propriété notable : `a XOR a = 0`, ce qui permet de mettre un registre à zéro sans instruction dédiée. Également utilisé en cryptographie légère et pour inverser des bits ciblés.

---

### Classe `Memory`

**Fichier** : `src/main/java/core/Memory.java`

**Rôle général** : Modélise la mémoire vive (RAM) du simulateur. Elle est implémentée comme un tableau de 65 536 octets (`byte[]`), ce qui représente 64 Ko adressables de 0x0000 à 0xFFFF. Elle gère les accès en lecture et en écriture, avec vérification systématique des bornes.

**Constante** :
- `MEMORY_SIZE = 65536` — taille totale de la mémoire en octets.

---

#### `public Memory()`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |

**Description détaillée** :
Alloue le tableau `data = new byte[65536]`. En Java, les tableaux de primitifs sont initialisés à `0` par défaut, donc toute la mémoire commence à zéro, simulant une mémoire vierge.

---

#### `public byte read(int address)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètre**  | `address` — adresse mémoire (0 à 65535) |
| **Retour**     | `byte` — valeur stockée à cette adresse |
| **Exception**  | `MemoryOutOfBoundsException` si `address < 0 || address >= 65536` |

**Description détaillée** :
Vérifie que l'adresse est dans les bornes valides. Si non, lève `MemoryOutOfBoundsException(address)`. Sinon, retourne `data[address]`. C'est l'opération de lecture mémoire de base, appelée par le CPU lors du fetch et de la lecture des opérandes.

---

#### `public void write(int address, byte value)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `address` — adresse cible, `value` — valeur à écrire |
| **Retour**     | `void`  |
| **Exception**  | `MemoryOutOfBoundsException` si l'adresse est invalide |

**Description détaillée** :
Vérifie les bornes de l'adresse puis effectue `data[address] = value`. Utilisée par le CPU pour les instructions `STORE` et `STORE_INDEXED`, et par l'assembleur pour écrire le programme en mémoire.

---

#### `public int readWord(int address)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètre**  | `address` — adresse du premier octet |
| **Retour**     | `int` — valeur 16 bits non signée (0 à 65535) |

**Description détaillée** :
Lit deux octets consécutifs en **big-endian** (l'octet de poids fort en premier) et reconstruit un entier 16 bits :
```
result = (read(address) & 0xFF) << 8 | (read(address + 1) & 0xFF)
```
Le masque `& 0xFF` est crucial car les `byte` Java sont signés : sans lui, un octet avec le bit 7 à 1 serait étendu en signe vers un `int` négatif. Utilisé par le CPU pour lire les adresses 16 bits encodées dans les instructions.

---

#### `public void writeWord(int address, int value)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `address` — adresse du premier octet, `value` — valeur 16 bits |
| **Retour**     | `void`  |

**Description détaillée** :
Écrit une valeur 16 bits en deux octets consécutifs en **big-endian** :
```
write(address,     (byte)(value >> 8))      // octet de poids fort
write(address + 1, (byte)(value & 0xFF))    // octet de poids faible
```
Utilisé par l'assembleur pour écrire les adresses dans le code machine.

---

#### `public void reset()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Retour**     | `void`  |

**Description détaillée** :
Remet tous les octets de la mémoire à zéro. Implémenté via `java.util.Arrays.fill(data, (byte) 0)` ou une boucle équivalente. Permet de réinitialiser complètement l'état de la mémoire entre deux exécutions de programmes différents.

---

### Classe `RegisterFile`

**Fichier** : `src/main/java/core/RegisterFile.java`

**Rôle général** : Représente le **banc de registres** du processeur : 16 registres 8 bits numérotés de 0 à 15 (r0 à r15). C'est une zone de stockage ultra-rapide, directement accessible par le CPU, contrairement à la mémoire principale. Implémentée comme un tableau `byte[16]`.

**Constante** :
- `NUM_REGISTERS = 16` — nombre total de registres.

---

#### `public RegisterFile()`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |

**Description détaillée** :
Alloue le tableau `registers = new byte[16]`, initialisé à 0 par Java. Tous les registres partent à zéro.

---

#### `public byte get(int index)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètre**  | `index` — numéro du registre (0 à 15) |
| **Retour**     | `byte` — valeur du registre |
| **Exception**  | `RegisterOutOfBoundsException` si `index < 0 || index > 15` |

**Description détaillée** :
Vérifie que l'index est valide (entre 0 et `NUM_REGISTERS - 1`). Si non, lève `RegisterOutOfBoundsException(index)`. Sinon, retourne `registers[index]`. Appelé massivement par le CPU pour lire les opérandes des instructions arithmétiques et logiques.

---

#### `public void set(int index, byte value)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètres** | `index` — numéro du registre, `value` — valeur à affecter |
| **Retour**     | `void`  |
| **Exception**  | `RegisterOutOfBoundsException` si l'index est invalide |

**Description détaillée** :
Vérifie les bornes puis effectue `registers[index] = value`. Appelé par le CPU après chaque instruction qui produit un résultat à stocker dans un registre (`LOAD_CONST`, `ADD`, `MUL`, etc.).

---

#### `public void reset()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Retour**     | `void`  |

**Description détaillée** :
Remet tous les registres à zéro. Comme `Memory.reset()`, utile pour les tests unitaires (état initial garanti) ou pour réinitialiser le CPU avant une nouvelle exécution.

---

## 3. Package `instruction` — Jeu d'instructions

### Enum `Opcode`

**Fichier** : `src/main/java/instruction/Opcode.java`

**Rôle général** : Définit l'**ISA** (Instruction Set Architecture) du simulateur sous forme d'une énumération Java. Chaque constante associe un nom mnémonique lisible (ex. `ADD`) à son code numérique (ex. `4`), qui est l'octet effectivement stocké en mémoire. C'est le contrat entre l'assembleur (qui produit ces codes) et le CPU (qui les interprète).

**Constantes et codes** :

| Constante       | Code | Description                                         |
|-----------------|------|-----------------------------------------------------|
| `BREAK`         | 0    | Arrêt de l'exécution                                |
| `LOAD_CONST`    | 1    | Charger une constante dans un registre              |
| `LOAD_MEM`      | 2    | Charger une valeur depuis la mémoire                |
| `STORE`         | 3    | Écrire un registre en mémoire                       |
| `ADD`           | 4    | Addition de deux registres                          |
| `SUB`           | 5    | Soustraction de deux registres                      |
| `MUL`           | 6    | Multiplication 8 × 8 → 16 bits                      |
| `DIV`           | 7    | Division euclidienne avec quotient et reste         |
| `AND`           | 8    | ET logique bit à bit                                |
| `OR`            | 9    | OU logique bit à bit                                |
| `XOR`           | 10   | OU exclusif bit à bit                               |
| `JUMP`          | 11   | Saut inconditionnel                                 |
| `BEQ`           | 12   | Saut si deux registres sont égaux                   |
| `BNE`           | 13   | Saut si deux registres sont différents              |
| `LOAD_INDEXED`  | 14   | Lecture mémoire à adresse base + offset registre    |
| `STORE_INDEXED` | 15   | Écriture mémoire à adresse base + offset registre   |

---

#### `private Opcode(int code)`

| Attribut       | Détail      |
|----------------|-------------|
| **Visibilité** | `private`   |
| **Type**       | Constructeur d'enum |
| **Paramètre**  | `code` — code numérique associé |

**Description détaillée** :
Constructeur privé des enums Java. Chaque constante de l'enum l'appelle implicitement avec son code. Stocke la valeur dans le champ `final int code`.

---

#### `public int getCode()`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Retour**     | `int` — code numérique de l'opcode (0 à 15) |

**Description détaillée** :
Retourne le code numérique associé à l'opcode. Utilisé par l'assembleur pour convertir un mnémonique textuel en son octet machine correspondant à écrire en mémoire.

---

#### `public static Opcode fromCode(int code)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public static` |
| **Paramètre**  | `code` — code numérique à rechercher |
| **Retour**     | `Opcode` correspondant, ou `null` si inconnu |

**Description détaillée** :
Méthode de recherche inverse : à partir d'un code numérique lu en mémoire (lors du fetch), retrouve l'`Opcode` correspondant. Parcourt `Opcode.values()` et compare chaque `.getCode()` à la valeur cherchée. Retourne la constante trouvée ou `null` si aucune ne correspond (ce qui déclenchera une `InvalidOpcodeException` dans le CPU).

---

## 4. Package `assembler` — Assembleur

### Classe `Assembler`

**Fichier** : `src/main/java/assembler/Assembler.java`

**Rôle général** : Traduit un **programme en langage assembleur** (texte humainement lisible) en **code machine binaire** directement écrit dans la mémoire simulée. C'est le composant qui fait le lien entre le code source et le CPU. Il parcourt le programme ligne par ligne, identifie chaque instruction, ses opérandes, et écrit les octets correspondants en mémoire à partir de l'adresse 0.

**Attributs** :
- `Memory memory` — référence à la mémoire du simulateur où écrire le code machine.
- `int currentAddress` — pointeur d'écriture, initialisé à 0, incrémenté à chaque octet écrit.

---

#### `public Assembler(Memory memory)`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |
| **Paramètre**  | `memory` — mémoire cible |

**Description détaillée** :
Stocke la référence à la mémoire et initialise `currentAddress = 0`. L'assembleur commencera toujours à écrire à l'adresse 0, ce qui correspond au point d'entrée du programme (où le CPU démarre après `reset()`).

---

#### `public void assemble(String program)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `public` |
| **Paramètre**  | `program` — programme assembleur complet sous forme de `String` multi-lignes |
| **Retour**     | `void`  |

**Description détaillée** :
Méthode principale de l'assembleur. Découpe le programme en lignes via `program.split("\n")`. Pour chaque ligne :
1. Supprime les espaces superflus (`trim()`).
2. Ignore les lignes vides et les commentaires (commençant par `;` ou `#`).
3. Appelle `parseLine(line)` pour traduire et écrire les octets.

Après traitement complet, `currentAddress` pointe après le dernier octet écrit.

---

#### `private void parseLine(String line)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `line` — ligne assembleur à traiter |
| **Retour**     | `void`  |

**Description détaillée** :
Cœur de l'assembleur. Découpe la ligne en tokens (séparés par des espaces). Le premier token est le **mnémonique** de l'instruction (ex. `"ADD"`), les suivants sont les **opérandes**.

Exemples de lignes prises en charge :
```
LOAD_CONST r2 42          ; Charge 42 dans r2
ADD r0 r1 r2              ; r0 = r1 + r2
STORE r0 @100             ; Écrit r0 à l'adresse 100
JUMP @200                 ; Saute à l'adresse 200
BEQ r0 r1 @50             ; Si r0 == r1, saute à 50
.data 0xFF                ; Écrit l'octet 0xFF en mémoire
.string "Hello"           ; Écrit les codes ASCII de "Hello"
```

Pour chaque mnémonique reconnu, la méthode appelle `writeByte()` avec l'opcode et les opérandes parsés via `parseRegister()` et `parseValue()`.

---

#### `private void writeByte(byte value)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `value` — octet à écrire |
| **Retour**     | `void`  |

**Description détaillée** :
Appelle `memory.write(currentAddress, value)` puis incrémente `currentAddress`. C'est la primitive de base de l'assembleur : chaque opcode et chaque opérande passe par cette méthode, garantissant que les octets sont écrits séquentiellement sans trou.

---

#### `private void writeAddress(int address)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `address` — adresse 16 bits à encoder |
| **Retour**     | `void`  |

**Description détaillée** :
Encode une adresse 16 bits en deux octets consécutifs en **big-endian** en appelant deux fois `writeByte()` :
```java
writeByte((byte)(address >> 8));     // octet de poids fort
writeByte((byte)(address & 0xFF));   // octet de poids faible
```
Appelée pour chaque adresse apparaissant dans les instructions `LOAD_MEM`, `STORE`, `JUMP`, `BEQ`, `BNE`, etc.

---

#### `private int parseRegister(String token)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `token` — chaîne de la forme `"rX"` (ex. `"r3"`, `"r15"`) |
| **Retour**     | `int` — numéro du registre (0 à 15) |

**Description détaillée** :
Extrait le numéro de registre d'un token de la forme `rX`. Supprime le préfixe `"r"` puis parse l'entier restant : `Integer.parseInt(token.substring(1))`. Peut lever une `NumberFormatException` si le format est invalide, ou une `RegisterOutOfBoundsException` si le numéro est hors bornes.

---

#### `private int parseValue(String token)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `token` — valeur décimale (`"42"`), hexadécimale (`"0xFF"`), ou adresse (`"@100"`) |
| **Retour**     | `int` — valeur numérique parsée |

**Description détaillée** :
Méthode flexible de parsing numérique. Gère trois formats :
- **Adresse** (commence par `@`) : supprime le `@` et parse le reste comme entier décimal.
- **Hexadécimal** (commence par `0x` ou `0X`) : utilise `Integer.parseInt(token.substring(2), 16)`.
- **Décimal** : utilise `Integer.parseInt(token)`.

---

#### `private boolean isAddress(String token)`

| Attribut       | Détail  |
|----------------|---------|
| **Visibilité** | `private` |
| **Paramètre**  | `token` — token à tester |
| **Retour**     | `boolean` — `true` si le token représente une adresse mémoire |

**Description détaillée** :
Prédicat simple : retourne `token.startsWith("@")`. Permet à `parseLine()` de distinguer une valeur immédiate d'une adresse mémoire lorsque plusieurs formats d'opérandes sont possibles.

---

## 5. Package `exception` — Exceptions personnalisées

### Classe `InvalidOpcodeException`

**Fichier** : `src/main/java/exception/InvalidOpcodeException.java`

**Rôle général** : Exception levée par le CPU lorsqu'il rencontre en mémoire un octet qui ne correspond à aucun opcode connu (valeur > 15 ou code non défini dans l'enum `Opcode`). Étend `RuntimeException` (exception non vérifiée).

---

#### `public InvalidOpcodeException(int code)`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |
| **Paramètre**  | `code` — code numérique invalide rencontré |

**Description détaillée** :
Appelle `super("Opcode inconnu : " + code)`, produisant un message d'erreur explicite indiquant le code fautif. Permet au développeur de diagnostiquer rapidement si le CPU a tenté d'exécuter une zone de données comme du code.

---

### Classe `RegisterOutOfBoundsException`

**Fichier** : `src/main/java/exception/RegisterOutOfBoundsException.java`

**Rôle général** : Exception levée par `RegisterFile` lorsqu'une instruction tente d'accéder à un registre avec un index invalide (< 0 ou > 15). Étend `RuntimeException`.

---

#### `public RegisterOutOfBoundsException(int index)`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |
| **Paramètre**  | `index` — index de registre invalide |

**Description détaillée** :
Appelle `super("Registre hors limites : " + index + ". Les registres valides vont de 0 à 15.")`. Fournit un message clair indiquant à la fois l'index erroné et la plage valide, facilitant le débogage de programmes assembleurs mal formés.

---

### Classe `MemoryOutOfBoundsException`

**Fichier** : `src/main/java/exception/MemoryOutOfBoundsException.java`

**Rôle général** : Exception levée par `Memory` lorsqu'une lecture ou écriture est tentée à une adresse hors de la plage 0–65535. Étend `RuntimeException`.

---

#### `public MemoryOutOfBoundsException(int address)`

| Attribut       | Détail      |
|----------------|-------------|
| **Type**       | Constructeur |
| **Paramètre**  | `address` — adresse mémoire invalide |

**Description détaillée** :
Appelle `super("Adresse mémoire hors limites : " + address)`. Permet d'identifier immédiatement l'adresse fautive lors d'un accès hors bornes, que ce soit pendant l'exécution du CPU ou lors de l'assemblage.

---

## 6. Tests unitaires

> Les tests sont écrits avec **JUnit 5**. Chaque classe de test suit le patron `@BeforeEach setUp()` + méthodes `@Test`. Les méthodes ne sont pas encore implémentées (corps vides ou TODO).

---

### Classe `CPUTest`

**Fichier** : `src/test/java/core/CPUTest.java`

**Rôle** : Valide le comportement correct du CPU instruction par instruction. Chaque test monte un programme minimal en mémoire, l'exécute, et vérifie l'état résultant des registres, de la mémoire ou du compteur de programme.

| Méthode                   | Ce qu'elle valide |
|---------------------------|-------------------|
| `setUp()`                 | Crée une `Memory`, un `RegisterFile`, et un `CPU` frais avant chaque test |
| `testBreak()`             | Un programme `[0x00]` (BREAK) doit stopper le CPU immédiatement, `isRunning()` → `false` |
| `testLoadConst()`         | `LOAD_CONST r2 42` doit mettre 42 dans `registers.get(2)` |
| `testLoadMem()`           | Écrit une valeur en mémoire, puis vérifie que `LOAD_MEM` la charge dans le registre attendu |
| `testStore()`             | Met une valeur dans un registre, puis vérifie que `STORE` l'écrit à la bonne adresse mémoire |
| `testAdd()`               | `r0=3, r1=4`, après `ADD r2 r0 r1` → `registers.get(2) == 7` |
| `testJump()`              | Après `JUMP @addr`, vérifie que `getPC() == addr` |
| `testBeq()`               | Si `r0 == r1`, le saut a lieu ; si `r0 != r1`, le saut n'a pas lieu |
| `testBne()`               | Inverse de `testBeq()` |

---

### Classe `ALUTest`

**Fichier** : `src/test/java/core/ALUTest.java`

**Rôle** : Valide chaque opération de l'ALU isolément, avec des cas nominaux et des cas limites (overflow, division par zéro).

| Méthode               | Ce qu'elle valide |
|-----------------------|-------------------|
| `setUp()`             | Crée une nouvelle `ALU` |
| `testAdd()`           | `add(3, 4)` → `7` |
| `testAddOverflow()`   | `add(127, 1)` → `-128` (overflow 8 bits) |
| `testSub()`           | `sub(10, 3)` → `7` |
| `testMul()`           | `mul(3, 4)` → `[0, 12]` |
| `testMulLargeResult()`| `mul(100, 100)` → `[39, 16]` (10000 = 0x2710) |
| `testDiv()`           | `div(10, 3)` → `[3, 1]` (quotient=3, reste=1) |
| `testDivByZero()`     | `div(5, 0)` → lève `ArithmeticException` |
| `testAnd()`           | `and(0b1100, 0b1010)` → `0b1000` |
| `testOr()`            | `or(0b1100, 0b1010)` → `0b1110` |
| `testXor()`           | `xor(0b1100, 0b1010)` → `0b0110` |

---

### Classe `MemoryTest`

**Fichier** : `src/test/java/core/MemoryTest.java`

**Rôle** : Valide les accès lecture/écriture à la mémoire, les valeurs par défaut, les adresses limites, et la détection des accès hors bornes.

| Méthode                  | Ce qu'elle valide |
|--------------------------|-------------------|
| `setUp()`                | Crée une nouvelle `Memory` |
| `testReadWriteByte()`    | `write(100, 42)` puis `read(100)` → `42` |
| `testReadWriteWord()`    | `writeWord(200, 1000)` puis `readWord(200)` → `1000` |
| `testReadDefaultValue()` | `read(500)` sans écriture préalable → `0` |
| `testBoundaryAddresses()`| `read(0)` et `read(65535)` fonctionnent sans exception |
| `testOutOfBoundsRead()`  | `read(-1)` ou `read(65536)` → `MemoryOutOfBoundsException` |
| `testOutOfBoundsWrite()` | `write(-1, ...)` ou `write(65536, ...)` → `MemoryOutOfBoundsException` |
| `testReset()`            | Après écriture puis `reset()`, `read(addr)` → `0` pour tout addr |

---

### Classe `RegisterFileTest`

**Fichier** : `src/test/java/core/RegisterFileTest.java`

**Rôle** : Valide les accès aux registres, leurs valeurs initiales, et la gestion des index invalides.

| Méthode                 | Ce qu'elle valide |
|-------------------------|-------------------|
| `setUp()`               | Crée un nouveau `RegisterFile` |
| `testGetSetRegister()`  | `set(3, 99)` puis `get(3)` → `99` |
| `testAllRegisters()`    | Boucle sur 0–15 : tous les registres sont lisibles et modifiables |
| `testDefaultValue()`    | `get(i)` sans affectation → `0` pour tout i de 0 à 15 |
| `testOutOfBoundsGet()`  | `get(-1)` ou `get(16)` → `RegisterOutOfBoundsException` |
| `testOutOfBoundsSet()`  | `set(-1, ...)` ou `set(16, ...)` → `RegisterOutOfBoundsException` |
| `testReset()`           | Après affectation puis `reset()`, `get(i)` → `0` pour tout i |

---

### Classe `AssemblerTest`

**Fichier** : `src/test/java/assembler/AssemblerTest.java`

**Rôle** : Valide que l'assembleur traduit correctement chaque instruction en séquence d'octets attendue en mémoire.

| Méthode                    | Ce qu'elle valide |
|----------------------------|-------------------|
| `setUp()`                  | Crée une `Memory` et un `Assembler` |
| `testAssembleLoadConst()`  | `"LOAD_CONST r2 42"` → mémoire[0]=1, mémoire[1]=2, mémoire[2]=42 |
| `testAssembleLoadMem()`    | `"LOAD_MEM r0 @256"` → opcode + reg + 2 octets d'adresse big-endian |
| `testAssembleStore()`      | `"STORE r1 @512"` → séquence correcte en mémoire |
| `testAssembleHexAddress()` | `"JUMP @0xFF"` → adresse 255 correctement encodée |
| `testAssembleAdd()`        | `"ADD r0 r1 r2"` → opcode 4 + registres 0, 1, 2 |
| `testAssembleJump()`       | `"JUMP @1000"` → opcode 11 + adresse 1000 en big-endian |
| `testAssembleData()`       | `".data 0xAB"` → octet 0xAB directement en mémoire |
| `testAssembleString()`     | `'.string "Hi"'` → codes ASCII 72 ('H') et 105 ('i') en mémoire |
| `testIgnoreComments()`     | `"; ceci est un commentaire"` → aucun octet écrit |
| `testFullProgram()`        | Programme complet multi-instructions → séquence d'octets complète vérifiée |

---

## Vue d'ensemble de l'architecture

```
┌─────────────────────────────────────────────────────────┐
│                      app.Main                           │
│  Point d'entrée : instancie et orchestre les composants │
└───────────────────────┬─────────────────────────────────┘
                        │
          ┌─────────────┴──────────────┐
          │                            │
   ┌──────▼──────┐             ┌───────▼───────┐
   │  Assembler  │             │     CPU       │
   │  Traduit le │             │  Fetch/Decode │
   │  code ASM   │             │  /Execute     │
   └──────┬──────┘             └───┬───────────┘
          │                        │
          │    ┌───────────────────┼──────────────┐
          │    │                   │              │
     ┌────▼────▼┐           ┌──────▼──────┐  ┌───▼───┐
     │  Memory  │           │ RegisterFile│  │  ALU  │
     │  64 Ko   │           │  16 × 8bits │  │  Calc │
     └──────────┘           └─────────────┘  └───────┘
          │
   ┌──────▼──────┐
   │   Opcode    │
   │  (enum ISA) │
   └─────────────┘
```

**Flux d'exécution** :
1. `Assembler.assemble(programme)` → écrit le code machine dans `Memory`
2. `CPU.run()` → boucle Fetch-Decode-Execute :
   - **Fetch** : `memory.read(pc++)` → opcode brut
   - **Decode** : `Opcode.fromCode(opcode)` → dispatch vers la méthode `executeXxx()`
   - **Execute** : lectures d'opérandes via `fetch()`, calculs via `ALU`, résultats dans `RegisterFile` ou `Memory`
3. Arrêt sur instruction `BREAK` (opcode 0)
