# Projet CPO 
##  But du projet (version simplifiée)

Le but de ce projet est de **créer un mini processeur en Java**.

Ce processeur n’est pas réel :  
c’est une **simulation logicielle** qui fonctionne comme un vrai CPU, mais de manière simplifiée.


<br> 

Explication du rôle d'un CPU : 
* Récupérer : il va chercher une instruction dans la mémoire ("additionner deux chiffres" ou bien "afficher cette lettre") 
* Décoder : il traduit cette instruction afin de comprendre ce qu'on lui demande de faire 
* Éxécuter : il effectue l'action   


Le CPU lit en mémoire une instruction encodée (**opcode** + paramètres) puis l’exécute.  
Opcode : C’est la partie d’une instruction machine qui dit quelle action le CPU doit faire.

En gros , une instruction c'est comme une phrase : [verbe] + [complément]  
* Le verbe correspond à l'**opcode** (ex: `LOAD`,`ADD`,`STORE`,`BREAK`) 
* les compléments , ce sont les paramètres (ex: registres,valeurs,adresses)   


*Résumé :* 

mon processeur fera toujours ceci : 
- lire une instruction en mémoire 
- lire ses paramètres 
- executer l'instruction 
- passer à la suivante 
- s'arreter sur `break`. 

Par exemple , quand j'écris en `python`: 
```python
x = 10 
y = 20 
z = x +y 
```
le CPU éxécute en réalité un truc comme ca : 
```
LOAD R1, 10      ; x = 10
LOAD R2, 20      ; y = 20
ADD R3, R1, R2   ; z = x + y
STORE [z], R3    ; Sauvegarder z
```

* `LOAD R1, 10`
   * Opcode : `LOAD`
   * Paramètres : `R1`et `10`

---

##  Que doit faire le programme ?

Le programme doit être capable de :

- Disposer d’une **mémoire** pour stocker des données
- Disposer de **registres** pour manipuler rapidement des valeurs
- **Lire des instructions** stockées en mémoire
- **Exécuter ces instructions une par une**
- S’arrêter lorsqu’une instruction de type `break` est rencontrée

---

##  À quoi sert l’assembleur ?

L’assembleur sert à **faciliter l’écriture des programmes**.

Il permet de :

- Écrire des instructions avec des mots simples (`load`, `store`, etc.)
- Traduire ces instructions en **codes numériques**
- Les placer en mémoire pour qu’elles soient exécutées par le processeur

L’assembleur est donc un **traducteur entre l’humain et le processeur**.

L'assembleur agit avant que le processeur commencer son cycle lire -> éxécuter

---

## Résumé en une phrase

> Ce projet consiste à créer un processeur simulé capable d’exécuter des programmes écrits dans un langage assembleur simplifié.


---- 

## Arborescence du projet v1 : 
```
cpu-simulator/                         # Racine du projet Maven 
├─ pom.xml                             # Configuration Maven (dépendances, version Java, build, tests)
├─ README.md                           # Présentation du projet + comment compiler/lancer
└─ src/                                # Sources du projet
   ├─ main/                             # Code principal (ce qui sera exécuté/packagé)
   │  ├─ java/                          # Code Java de l’application
   │  │  ├─ app/                        # Point d’entrée et scénarios de démonstration
   │  │  │  └─ Main.java                # Lance le simulateur (création CPU/mémoire, chargement programme, run)
   │  │  ├─ assembler/                  # Assembleur 
   │  │  │  └─ Assembler.java           
   │  │  ├─ core/                       # Cœur du simulateur (composants du processeur)
   │  │  │  ├─ CPU.java                 # Boucle d’exécution (fetch/decode/execute), PC, exécute les instructions
   │  │  │  ├─ Memory.java              # Mémoire (read/write par adresse, stockage des instructions et données)
   │  │  │  └─ RegisterFile.java        # Registres (get/set), utilisés par les instructions
   │  │  └─ instruction/                # Jeu d’instructions (définition “machine” comprise par le CPU)
   │  │     └─ Opcode.java              # Codes numériques des instructions (BREAK, LOAD, STORE, etc.)
   │  └─ resources/                     
   └─ test/                             # Code de tests automatisés (JUnit)
      └─ java/                          # Tests Java (ex: MemoryTest, RegisterFileTest, CPUTest)
         └─ (tests à ajouter)           
```

Explication du registre :   

c'est une case de stockage à l'intérieur du CPU .   
Son but : garder les valeurs "sous la main" pour calculer viter , sans aller en mémoire à chaque fois .   


-- 
# Projet CPO — Simulateur de CPU avec Assembleur
> **« Carré Petit Utile »** — Université de Toulouse

---

## 1. But du projet

Le but de ce projet est de **créer un simulateur de processeur (CPU) accompagné d'un assembleur**, entièrement en Java.

Ce processeur n'est pas réel : c'est une **simulation logicielle** qui reproduit le comportement d'un vrai CPU de manière simplifiée. Le système se compose de deux parties indissociables :

- **Le simulateur CPU** : exécute des instructions encodées en mémoire selon le cycle classique Fetch / Decode / Execute.
- **L'assembleur** : traduit un programme écrit en langage lisible (mnémoniques) en codes numériques chargés en mémoire, avant que le CPU ne les exécute.

> **Résumé en une phrase** : Ce projet consiste à créer un processeur simulé capable d'exécuter des programmes écrits dans un langage assembleur simplifié.

---

## 2. Fonctionnement d'un CPU — Rappel théorique

Un processeur réel effectue en permanence trois opérations en boucle :

| Étape | Nom | Description |
|-------|-----|-------------|
| 1 | **Fetch** | Aller chercher l'instruction suivante en mémoire à l'adresse indiquée par le PC |
| 2 | **Decode** | Identifier l'opération à réaliser (ADD ? LOAD ? BREAK ?) à partir de l'opcode |
| 3 | **Execute** | Effectuer l'opération sur les registres et/ou la mémoire |

Ce cycle se répète instruction par instruction jusqu'à rencontrer l'instruction `BREAK`, qui arrête l'exécution.

### Qu'est-ce qu'une instruction ?

Une instruction est composée de deux parties : un **opcode** (le « verbe », ce qu'on doit faire) et des **paramètres** (le « complément », sur quoi on le fait).

```
LOAD R1, 10      →  opcode = LOAD   |  paramètres = R1, 10
ADD  R3, R1, R2  →  opcode = ADD    |  paramètres = R3, R1, R2
STORE R0, @101   →  opcode = STORE  |  paramètres = R0, adresse 101
BREAK            →  opcode = BREAK  |  pas de paramètre
```

### Exemple concret

Quand on écrit en Python :
```python
x = 10
y = 20
z = x + y
```
Le CPU exécute en réalité :
```
LOAD  R1, 10       ; Charger la constante 10 dans le registre R1
LOAD  R2, 20       ; Charger la constante 20 dans le registre R2
ADD   R3, R1, R2   ; Additionner R1 + R2, résultat dans R3
STORE @z, R3       ; Sauvegarder le résultat en mémoire
BREAK              ; Fin du programme
```

---

## 3. Les composants du simulateur

### 3.1 La mémoire (`Memory.java`)

La mémoire est un tableau de **65 536 cellules** (64 Ko) de type `byte`. Elle stocke à la fois les instructions du programme (chargées par l'assembleur) et les données manipulées pendant l'exécution.

- Les adresses sont des entiers sur **16 bits** (de 0 à 65 535).
- Opérations : `read(address)` et `write(address, value)`.
- Tout accès hors borne lève une `MemoryOutOfBoundsException`.

### 3.2 Les registres (`RegisterFile.java`)

Les registres sont des **cases de stockage internes au CPU**, ultra-rapides. Le simulateur dispose de **16 registres de 8 bits** (type `byte`), numérotés de R0 à R15.

Leur rôle est de garder les valeurs « sous la main » pendant un calcul, sans avoir à aller lire en mémoire à chaque fois — ce qui serait beaucoup plus lent.

- Opérations : `get(index)` et `set(index, value)`.
- Tout accès à un index hors de [0, 15] lève une `RegisterOutOfBoundsException`.

### 3.3 L'unité arithmétique et logique (`ALU.java`)

L'ALU est la partie du CPU qui effectue tous les **calculs arithmétiques et logiques** :

| Opération | Description |
|-----------|-------------|
| `ADD` | Addition de deux registres |
| `SUB` | Soustraction |
| `MUL` | Multiplication (résultat sur 2 registres car peut dépasser 8 bits) |
| `DIV` | Division entière (quotient + reste dans deux registres distincts) |
| `OR`  | OU logique bit à bit |
| `AND` | ET logique bit à bit |
| `XOR` | OU exclusif bit à bit |

La séparation de l'ALU dans une classe dédiée permet de tester ses opérations indépendamment du reste du CPU (`ALUTest.java`).

### 3.4 Le processeur (`CPU.java`)

Le CPU orchestre l'ensemble. Il contient :
- Le **compteur de programme (PC)** : entier 16 bits, initialisé à 0, qui pointe sur la prochaine instruction à lire en mémoire.
- La **boucle d'exécution** (`run()`) : implémente le cycle Fetch / Decode / Execute jusqu'à `BREAK`.
- Des références vers `Memory`, `RegisterFile` et `ALU`.

En cas d'opcode inconnu, une `InvalidOpcodeException` est levée.

### 3.5 Le jeu d'instructions (`Opcode.java`)

L'énumération `Opcode` définit tous les codes numériques reconnus par le CPU, répartis sur les 5 étapes du projet :

| Code | Mnémonique | Étape |
|------|------------|-------|
| 0 | `BREAK` | 1 |
| 1 | `LOAD_CONST` | 1 |
| 2 | `LOAD_MEM` | 1 |
| 3 | `STORE` | 1 |
| 4–10 | `ADD`, `SUB`, `MUL`, `DIV`, `OR`, `AND`, `XOR` | 3 |
| 11–13 | `JUMP`, `BEQ`, `BNE` | 4 |
| 14–15 | `LOAD_IDX`, `STORE_IDX` | 5 |

### 3.6 L'assembleur (`Assembler.java`)

L'assembleur sert à **faciliter l'écriture des programmes**. Sans lui, il faudrait écrire directement les codes numériques en mémoire, ce qui serait très fastidieux.

Il permet de :
- Écrire des instructions avec des mots simples (`load`, `add`, `store`…).
- Traduire ces mnémoniques en **séquences d'octets** chargées en mémoire.
- Gérer les adresses décimales et hexadécimales (format `0x...`).

**L'assembleur agit avant le CPU** : il remplit la mémoire avec le programme traduit, puis le CPU lit et exécute ce contenu.

Exemple de traduction :

```
Assembleur  :  load r0, 5
En mémoire  :  [1] [0] [5]
               opcode  reg  valeur
```

```
Assembleur  :  load r2, @100
En mémoire  :  [2] [2] [0] [100]
               opcode  reg  addrHigh  addrLow
```

---

## 4. Les 5 étapes du projet

Le développement est progressif : chaque étape étend la précédente sans la remettre en cause.

| Étape | Contenu | Points |
|-------|---------|--------|
| **1** | Registres, mémoire, BREAK / LOAD / STORE | 4 pts |
| **2** | Assembleur de base (traduction des instructions de l'étape 1) | 4 pts |
| **3** | ALU : ADD, SUB, MUL, DIV, OR, AND, XOR + assembleur | 4 pts |
| **4** | Sauts : JUMP, BEQ, BNE (boucles et conditionnelles) + assembleur | 4 pts |
| **5** | Adressage indexé, directives `data` et `string` + assembleur | 4 pts |

---

## 5. Arborescence du projet

```
cpu-simulator/
├─ pom.xml                              # Configuration Maven
├─ README.md                            # Présentation + instructions de build
└─ src/
   ├─ main/java/
   │  ├─ app/
   │  │  └─ Main.java                   # Point d'entrée — instancie et orchestre tout
   │  ├─ assembler/
   │  │  └─ Assembler.java              # Traducteur mnémoniques → codes en mémoire
   │  ├─ core/
   │  │  ├─ ALU.java                    # Opérations arithmétiques et logiques
   │  │  ├─ CPU.java                    # Boucle Fetch/Decode/Execute, PC
   │  │  ├─ Memory.java                 # Mémoire 64 Ko (read/write)
   │  │  └─ RegisterFile.java           # 16 registres 8 bits (get/set)
   │  ├─ exception/
   │  │  ├─ InvalidOpcodeException.java
   │  │  ├─ MemoryOutOfBoundsException.java
   │  │  └─ RegisterOutOfBoundsException.java
   │  └─ instruction/
   │     └─ Opcode.java                 # Codes numériques des instructions (enum)
   └─ test/java/
      ├─ assembler/
      │  └─ AssemblerTest.java
      └─ core/
         ├─ ALUTest.java
         ├─ CPUTest.java
         ├─ MemoryTest.java
         └─ RegisterFileTest.java
```

---

## 6. Relations entre les composants

```
Main
 ├── instancie → Memory
 ├── instancie → RegisterFile
 ├── instancie → ALU
 ├── instancie → CPU(Memory, RegisterFile, ALU)
 └── instancie → Assembler(Memory)
          │
          └── assemble(programme) → écrit les codes en mémoire
                                          │
                                    CPU.run() → lit et exécute les instructions
                                          ├── Memory.read(pc)     [Fetch]
                                          ├── Opcode.fromCode()   [Decode]
                                          ├── RegisterFile.get()  [Execute]
                                          ├── ALU.add() / sub()…  [Execute]
                                          └── Memory.write()      [Execute]
```

**Point clé** : l'assembleur et le CPU partagent la **même instance de `Memory`**. L'assembleur écrit dans la mémoire, puis le CPU lit exactement ce qui y a été écrit — sans copie ni transfert intermédiaire.

---

## 7. Gestion des erreurs

| Exception | Déclenchée par | Cause |
|-----------|---------------|-------|
| `InvalidOpcodeException` | `CPU` | Opcode non reconnu lors du décodage |
| `MemoryOutOfBoundsException` | `Memory` | Adresse hors de [0, 65535] |
| `RegisterOutOfBoundsException` | `RegisterFile` | Index hors de [0, 15] |

---

## 8. Plan de tests

Chaque composant dispose de sa propre classe de test unitaire, indépendante des autres :

- **`MemoryTest`** : lecture/écriture, cas limites (adresse 0, adresse 65535), exception hors borne.
- **`RegisterFileTest`** : lecture/écriture sur les 16 registres, initialisation à 0, exception hors borne.
- **`ALUTest`** : chaque opération (add, sub, mul, div, or, and, xor) avec valeurs nominales et cas limites.
- **`CPUTest`** : programmes courts (BREAK seul, LOAD + BREAK, ADD complet).
- **`AssemblerTest`** : traduction de lignes simples, programme multi-lignes, erreur de syntaxe.

Un **test d'intégration** assemble et exécute un programme complet, puis vérifie l'état final des registres :

```
load r0, 5
load r1, 6
add  r2, r0, r1    → r2 doit valoir 11 après exécution
break
```