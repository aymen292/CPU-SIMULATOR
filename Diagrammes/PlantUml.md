# Diagrammes PlantUML — Simulateur de CPU avec Assembleur
## Projet CPO — « Carré Petit Utile »

Ce fichier regroupe l'ensemble des prompts PlantUML permettant de générer les diagrammes UML du projet. Chaque diagramme est précédé d'une courte explication de son rôle et de ce qu'il montre.

---

## Diagramme 1 — Cas d'utilisation (Use Case)

Ce diagramme représente les fonctionnalités offertes par le système du point de vue de ses utilisateurs. On identifie deux acteurs : l'**Utilisateur** (développeur qui écrit et exécute des programmes), et le **Système** (le simulateur CPU + l'assembleur). Ce diagramme permet de valider que toutes les exigences fonctionnelles sont bien couvertes.

```plantuml
@startuml DiagrammeCasUtilisation

skinparam actorStyle awesome
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LightYellow
  BorderColor DarkGoldenRod
  ArrowColor DarkGoldenRod
}

left to right direction

actor "Utilisateur" as U
actor "Système JUnit\n(Tests)" as T

rectangle "Simulateur CPU — Carré Petit Utile" {

  package "Gestion Mémoire & Registres" {
    usecase "Lire une valeur en mémoire" as UC1
    usecase "Écrire une valeur en mémoire" as UC2
    usecase "Lire un registre" as UC3
    usecase "Écrire dans un registre" as UC4
  }

  package "Assembleur" {
    usecase "Écrire un programme assembleur" as UC5
    usecase "Assembler le programme\n(traduction en codes)" as UC6
    usecase "Charger le programme en mémoire" as UC7
  }

  package "Exécution CPU" {
    usecase "Lancer l'exécution du CPU" as UC8
    usecase "Exécuter une instruction LOAD" as UC9
    usecase "Exécuter une instruction STORE" as UC10
    usecase "Exécuter une instruction arithmétique\n(ADD, SUB, MUL, DIV)" as UC11
    usecase "Exécuter une instruction logique\n(OR, AND, XOR)" as UC12
    usecase "Exécuter un saut\n(JUMP, BEQ, BNE)" as UC13
    usecase "Exécuter un accès indexé\n(LOAD_IDX, STORE_IDX)" as UC14
    usecase "Arrêter le CPU (BREAK)" as UC15
  }

  package "Consultation & Tests" {
    usecase "Afficher l'état des registres" as UC16
    usecase "Afficher l'état de la mémoire" as UC17
    usecase "Exécuter les tests unitaires" as UC18
    usecase "Exécuter les tests d'intégration" as UC19
  }
}

U --> UC5
U --> UC8
U --> UC16
U --> UC17

UC5 ..> UC6 : <<include>>
UC6 ..> UC7 : <<include>>
UC8 ..> UC9 : <<extend>>
UC8 ..> UC10 : <<extend>>
UC8 ..> UC11 : <<extend>>
UC8 ..> UC12 : <<extend>>
UC8 ..> UC13 : <<extend>>
UC8 ..> UC14 : <<extend>>
UC8 ..> UC15 : <<include>>

UC9 ..> UC1 : <<include>>
UC9 ..> UC4 : <<include>>
UC10 ..> UC3 : <<include>>
UC10 ..> UC2 : <<include>>

T --> UC18
T --> UC19
UC18 ..> UC1 : <<include>>
UC18 ..> UC3 : <<include>>
UC19 ..> UC8 : <<include>>

@enduml
```

---

## Diagramme 2 — Diagramme de classes

Ce diagramme montre la structure statique complète de l'application : les classes, leurs attributs, leurs méthodes, et les relations qui les unissent. C'est la pièce maîtresse de la conception orientée objet — il doit refléter fidèlement le code Java produit.

```plantuml
@startuml DiagrammeClasses

skinparam classAttributeIconSize 0
skinparam class {
  BackgroundColor LightCyan
  BorderColor SteelBlue
  ArrowColor SteelBlue
  FontSize 12
}
skinparam note {
  BackgroundColor LightYellow
  BorderColor DarkGoldenRod
}

package "instruction" {
  enum Opcode {
    BREAK = 0
    LOAD_CONST = 1
    LOAD_MEM = 2
    STORE = 3
    ADD = 4
    SUB = 5
    MUL = 6
    DIV = 7
    OR = 8
    AND = 9
    XOR = 10
    JUMP = 11
    BEQ = 12
    BNE = 13
    LOAD_IDX = 14
    STORE_IDX = 15
    --
    - code : int
    --
    + getCode() : int
    + fromCode(int) : Opcode
  }
}

package "core" {
  class Memory {
    - {static} SIZE : int = 65536
    - data : byte[]
    --
    + Memory()
    + read(address : int) : byte
    + write(address : int, value : byte) : void
    + readWord(address : int) : int
    + writeWord(address : int, value : int) : void
    + dump(from : int, to : int) : void
    - checkAddress(address : int) : void
  }

  class RegisterFile {
    - {static} COUNT : int = 16
    - registers : byte[]
    --
    + RegisterFile()
    + get(index : int) : byte
    + set(index : int, value : byte) : void
    + reset() : void
    + display() : void
    - checkIndex(index : int) : void
  }

  class CPU {
    - pc : int
    - running : boolean
    - memory : Memory
    - registers : RegisterFile
    --
    + CPU(memory : Memory, registers : RegisterFile)
    + run() : void
    + reset() : void
    + getPC() : int
    - fetch() : int
    - decode(code : int) : Opcode
    - execute(opcode : Opcode) : void
    - executeLoadConst() : void
    - executeLoadMem() : void
    - executeStore() : void
    - executeAdd() : void
    - executeSub() : void
    - executeMul() : void
    - executeDiv() : void
    - executeOr() : void
    - executeAnd() : void
    - executeXor() : void
    - executeJump() : void
    - executeBeq() : void
    - executeBne() : void
    - executeLoadIdx() : void
    - executeStoreIdx() : void
    - readNextByte() : byte
    - readNextAddress() : int
  }
}

package "assembler" {
  class Assembler {
    - memory : Memory
    - writePointer : int
    --
    + Assembler(memory : Memory)
    + assemble(program : String) : void
    + assembleLine(line : String) : void
    - encodeLoad(tokens : String[]) : void
    - encodeStore(tokens : String[]) : void
    - encodeALU(tokens : String[], opcode : Opcode) : void
    - encodeJump(tokens : String[]) : void
    - encodeBranch(tokens : String[], opcode : Opcode) : void
    - encodeLoadIdx(tokens : String[]) : void
    - encodeData(tokens : String[]) : void
    - encodeString(tokens : String[]) : void
    - parseRegister(token : String) : byte
    - parseValue(token : String) : int
    - emitByte(value : byte) : void
    - emitWord(value : int) : void
  }
}

package "app" {
  class Main {
    + {static} main(args : String[]) : void
    - {static} runDemo1BasicLoad() : void
    - {static} runDemo2ALU() : void
    - {static} runDemo3Loop() : void
    - {static} runDemo4Array() : void
  }
}

package "exception" {
  class IllegalAddressException {
    + IllegalAddressException(address : int)
  }

  class UnknownOpcodeException {
    + UnknownOpcodeException(code : int)
  }

  class AssemblerException {
    + AssemblerException(message : String, line : int)
  }

  IllegalAddressException --|> RuntimeException
  UnknownOpcodeException --|> RuntimeException
  AssemblerException --|> RuntimeException
}

CPU "1" o--> "1" Memory : utilise
CPU "1" o--> "1" RegisterFile : utilise
CPU ..> Opcode : décode
CPU ..> UnknownOpcodeException : <<throws>>
CPU ..> IllegalAddressException : <<throws>>
Assembler "1" o--> "1" Memory : écrit dans
Assembler ..> Opcode : traduit vers
Assembler ..> AssemblerException : <<throws>>
Memory ..> IllegalAddressException : <<throws>>
RegisterFile ..> IllegalAddressException : <<throws>>
Main ..> CPU : crée et utilise
Main ..> Memory : crée
Main ..> RegisterFile : crée
Main ..> Assembler : crée et utilise

note right of CPU
  Le PC (Program Counter) est un entier
  sur 16 bits géré par la boucle run().
  La méthode fetch() lit un octet depuis
  memory[pc] puis incrémente pc.
end note

note right of Memory
  data est un tableau de 65 536 bytes.
  Les adresses sur 2 octets (16 bits)
  permettent d'adresser tout l'espace.
end note

@enduml
```

---

## Diagramme 3 — Séquence système : Exécution d'un programme

Ce diagramme de séquence **système** montre les interactions entre l'utilisateur et le système vu comme une boîte noire. Il se concentre sur *quoi* l'utilisateur demande, sans entrer dans les détails d'implémentation. C'est le premier niveau d'analyse.

```plantuml
@startuml SeqSystemeExecution

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor SteelBlue
  LifeLineBorderColor SteelBlue
  ParticipantBackgroundColor LightCyan
  ParticipantBorderColor SteelBlue
  BoxBackgroundColor LightYellow
}

actor "Utilisateur" as U
boundary "Système\n(CPU Simulateur)" as S

title Diagramme de séquence système — Exécution d'un programme

U -> S : écrire programme assembleur (texte)
S --> U : confirmation de réception

U -> S : lancer l'assemblage
S --> U : programme chargé en mémoire / erreur de syntaxe

U -> S : lancer l'exécution du CPU
activate S
  loop jusqu'à BREAK
    S -> S : lire instruction suivante en mémoire
    S -> S : décoder l'opcode
    S -> S : exécuter l'instruction
  end
S --> U : exécution terminée (état final)
deactivate S

U -> S : consulter l'état des registres
S --> U : valeurs des 16 registres

U -> S : consulter l'état de la mémoire (plage optionnelle)
S --> U : contenu de la plage mémoire demandée

@enduml
```

---

## Diagramme 4 — Séquence système : Assemblage d'un programme

Ce diagramme se concentre sur la phase d'assemblage vue de l'extérieur, en montrant les échanges entre l'utilisateur et le sous-système assembleur. Il complète le diagramme précédent en détaillant spécifiquement ce que l'assembleur offre comme service.

```plantuml
@startuml SeqSystemeAssemblage

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor DarkGoldenRod
  LifeLineBorderColor DarkGoldenRod
  ParticipantBackgroundColor LightYellow
  ParticipantBorderColor DarkGoldenRod
}

actor "Utilisateur" as U
boundary "Assembleur" as A
boundary "Mémoire" as M

title Diagramme de séquence système — Assemblage

U -> A : fournir texte du programme
activate A

loop pour chaque ligne
  A -> A : parser la ligne (mnémonique + arguments)
  alt syntaxe valide
    A -> M : écrire octet(s) à l'adresse courante
    M --> A : écriture OK
  else erreur de syntaxe
    A --> U : AssemblerException (ligne N, message)
  end
end

A --> U : assemblage terminé (pointeur final en mémoire)
deactivate A

U -> A : demander rapport (adresses utilisées)
A --> U : plage mémoire [0, pointeur-1]

@enduml
```

---

## Diagramme 5 — Séquence détaillée : Cycle Fetch/Decode/Execute du CPU

Ce diagramme est le plus important pour comprendre le fonctionnement interne du simulateur. Il montre, pas à pas, comment le CPU interagit avec la mémoire et le fichier de registres lors de l'exécution d'une instruction `ADD r2, r0, r1`. Il illustre parfaitement le cycle classique d'un processeur.

```plantuml
@startuml SeqDetailCPU

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor SteelBlue
  LifeLineBorderColor SteelBlue
  ParticipantBackgroundColor LightCyan
  ParticipantBorderColor SteelBlue
}

participant "Main" as MAIN
participant "CPU" as CPU
participant "Memory" as MEM
participant "RegisterFile" as REG

title Séquence détaillée — Cycle CPU (instruction ADD r2, r0, r1)

MAIN -> CPU : run()
activate CPU

note over CPU : PC = adresse de l'instruction ADD en mémoire

== Phase FETCH ==
CPU -> MEM : read(pc)
MEM --> CPU : opcode_byte (ex: 4 = ADD)
note right of CPU : PC est incrémenté

== Phase DECODE ==
CPU -> CPU : decode(opcode_byte)
note right of CPU : Opcode.fromCode(4) → Opcode.ADD

== Phase EXECUTE — lecture des paramètres ==
CPU -> MEM : read(pc)
MEM --> CPU : indexDest (ex: 2 → r2)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : indexSrc1 (ex: 0 → r0)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : indexSrc2 (ex: 1 → r1)
note right of CPU : PC++

== Phase EXECUTE — calcul et écriture ==
CPU -> REG : get(indexSrc1)
REG --> CPU : valeur_r0 (ex: 5)

CPU -> REG : get(indexSrc2)
REG --> CPU : valeur_r1 (ex: 6)

note over CPU : result = (byte)(valeur_r0 + valeur_r1) = 11

CPU -> REG : set(indexDest, result)
REG --> CPU : OK

note over CPU : retour au début du cycle\nPC pointe maintenant sur l'instruction suivante

CPU --> MAIN : (continue ou s'arrête sur BREAK)
deactivate CPU

@enduml
```

---

## Diagramme 6 — Séquence détaillée : Traduction d'une ligne par l'assembleur

Ce diagramme illustre en détail comment l'assembleur traite une ligne de code assembleur, ici `load r2, @100`, qui correspond à un chargement depuis la mémoire (LOAD_MEM). On voit comment la valeur d'adresse sur 16 bits est décomposée en deux octets.

```plantuml
@startuml SeqDetailAssembler

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor DarkGoldenRod
  LifeLineBorderColor DarkGoldenRod
  ParticipantBackgroundColor LightYellow
  ParticipantBorderColor DarkGoldenRod
}

participant "Main" as MAIN
participant "Assembler" as ASM
participant "Memory" as MEM

title Séquence détaillée — Assembleur (traitement de "load r2, @100")

MAIN -> ASM : assemble("load r2, @100\nbreak")
activate ASM

== Traitement ligne 1 : "load r2, @100" ==
ASM -> ASM : assembleLine("load r2, @100")
ASM -> ASM : split(" ") → tokens = ["load", "r2,", "@100"]
ASM -> ASM : mnémonique = "load"
ASM -> ASM : encodeLoad(tokens)

note over ASM : l'argument "@100" commence par "@"\n→ c'est un accès mémoire (LOAD_MEM, opcode 2)

ASM -> ASM : parseRegister("r2,") → 2
ASM -> ASM : parseValue("100") → 100

note over ASM : 100 en décimal = 0x0064\noctet haut = 0x00, octet bas = 0x64

ASM -> MEM : write(writePointer=0, byte(2))
MEM --> ASM : OK
note right of ASM : writePointer → 1

ASM -> MEM : write(writePointer=1, byte(2))
MEM --> ASM : OK
note right of ASM : numéro du registre cible (r2)\nwritePointer → 2

ASM -> MEM : write(writePointer=2, byte(0))
MEM --> ASM : OK
note right of ASM : octet haut de l'adresse 100\nwritePointer → 3

ASM -> MEM : write(writePointer=3, byte(100))
MEM --> ASM : OK
note right of ASM : octet bas de l'adresse 100\nwritePointer → 4

== Traitement ligne 2 : "break" ==
ASM -> ASM : assembleLine("break")
ASM -> ASM : mnémonique = "break" → Opcode.BREAK (code 0)
ASM -> MEM : write(writePointer=4, byte(0))
MEM --> ASM : OK

ASM --> MAIN : assemblage terminé (5 octets écrits)
deactivate ASM

@enduml
```

---

## Diagramme 7 — Séquence détaillée : Instruction STORE

Ce diagramme montre l'exécution d'une instruction `store r0, @101`, illustrant comment le CPU lit le numéro de registre et l'adresse de destination, récupère la valeur dans le registre, puis l'écrit en mémoire.

```plantuml
@startuml SeqDetailStore

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor SeaGreen
  LifeLineBorderColor SeaGreen
  ParticipantBackgroundColor Honeydew
  ParticipantBorderColor SeaGreen
}

participant "CPU" as CPU
participant "Memory" as MEM
participant "RegisterFile" as REG

title Séquence détaillée — Exécution de STORE r0, @101

== FETCH ==
CPU -> MEM : read(pc)
MEM --> CPU : 3 (opcode STORE)
note right of CPU : PC++

== DECODE ==
CPU -> CPU : decode(3) → Opcode.STORE

== EXECUTE ==
CPU -> MEM : read(pc)
MEM --> CPU : 0 (index du registre source = r0)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : 0 (octet haut de l'adresse 101 → 0x00)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : 101 (octet bas de l'adresse 101 → 0x65)
note right of CPU : PC++

note over CPU : adresse = (0 << 8) | 101 = 101

CPU -> REG : get(0)
REG --> CPU : value (valeur contenue dans r0, ex: 42)

CPU -> MEM : write(101, 42)
MEM --> CPU : OK

note over CPU : instruction STORE terminée\nle CPU passe à l'instruction suivante

@enduml
```

---

## Diagramme 8 — Séquence détaillée : Instruction BEQ (saut conditionnel)

Ce diagramme illustre le mécanisme de saut conditionnel, fondamental pour les boucles. Il montre les deux branches possibles : le saut (quand les registres sont égaux) et la non-exécution du saut (quand ils diffèrent).

```plantuml
@startuml SeqDetailBEQ

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor Purple
  LifeLineBorderColor Purple
  ParticipantBackgroundColor LavenderBlush
  ParticipantBorderColor Purple
}

participant "CPU" as CPU
participant "Memory" as MEM
participant "RegisterFile" as REG

title Séquence détaillée — Exécution de BEQ r0, r1, adresse

== FETCH ==
CPU -> MEM : read(pc)
MEM --> CPU : 12 (opcode BEQ)
note right of CPU : PC++

== DECODE ==
CPU -> CPU : decode(12) → Opcode.BEQ

== EXECUTE — lecture des paramètres ==
CPU -> MEM : read(pc)
MEM --> CPU : idx1 (index r0)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : idx2 (index r1)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : addrHigh (octet haut de l'adresse cible)
note right of CPU : PC++

CPU -> MEM : read(pc)
MEM --> CPU : addrLow (octet bas de l'adresse cible)
note right of CPU : PC++

note over CPU : targetAddr = (addrHigh << 8) | addrLow

== EXECUTE — comparaison et décision ==
CPU -> REG : get(idx1)
REG --> CPU : val1

CPU -> REG : get(idx2)
REG --> CPU : val2

alt val1 == val2
  note over CPU : Saut effectué\nPC = targetAddr
  CPU -> CPU : pc = targetAddr
else val1 != val2
  note over CPU : Pas de saut\nPC reste inchangé (pointe instruction suivante)
end

@enduml
```

---

## Diagramme 9 — Diagramme d'états : Cycle de vie de l'exécution CPU

Ce diagramme montre les différents états par lesquels passe le CPU au fil de son exécution. Il est particulièrement utile pour comprendre le comportement global du simulateur, notamment les transitions entre le repos, l'exécution et les états d'erreur.

```plantuml
@startuml DiagrammeEtats

skinparam state {
  BackgroundColor LightCyan
  BorderColor SteelBlue
  ArrowColor SteelBlue
  FontSize 12
}

title Diagramme d'états — Cycle de vie du CPU

[*] --> Initialisé : new CPU(memory, registers)

state Initialisé {
  note : PC = 0\nrunning = false\nRegistres = 0\nMémoire = 0
}

Initialisé --> EnAttente : (objet créé, prêt)

state EnAttente {
  note : Le CPU est prêt\nmais n'exécute rien encore
}

EnAttente --> Assemblage : assembler.assemble(programme)

state Assemblage {
  note : L'assembleur parse le texte\net charge les codes en mémoire
}

Assemblage --> PrêtÀExécuter : assemblage réussi
Assemblage --> ErreurAssemblage : syntaxe invalide

state PrêtÀExécuter {
  note : La mémoire contient le programme\nPC = 0, running = false
}

PrêtÀExécuter --> Fetch : cpu.run()

state EnExécution {

  state Fetch {
    note : Lire opcode à memory[pc]\nPC++
  }

  state Decode {
    note : Convertir le code en Opcode
  }

  state Execute {
    note : Lire paramètres, effectuer\nl'opération sur registres/mémoire
  }

  Fetch --> Decode : opcode lu
  Decode --> Execute : Opcode reconnu
  Execute --> Fetch : instruction terminée (pas BREAK)
  Decode --> ErreurOpcode : Opcode inconnu

}

Execute --> Terminé : BREAK rencontré
ErreurOpcode --> ErreurRuntime : UnknownOpcodeException
Execute --> ErreurRuntime : IllegalAddressException (accès hors borne)
ErreurAssemblage --> EnAttente : exception traitée, retour à l'état initial

state Terminé {
  note : running = false\nPC pointe après le BREAK\nRegistres et mémoire consultables
}

state ErreurRuntime {
  note : Exception levée\nExécution interrompue
}

Terminé --> PrêtÀExécuter : cpu.reset()
ErreurRuntime --> Initialisé : réinitialisation manuelle
Terminé --> [*]

@enduml
```

---

## Diagramme 10 — Diagramme de packages

Ce diagramme montre l'organisation des packages du projet et leurs dépendances. Il illustre la hiérarchie des couches et garantit qu'il n'y a pas de dépendances cycliques.

```plantuml
@startuml DiagrammePackages

skinparam packageStyle rectangle
skinparam package {
  BackgroundColor LightYellow
  BorderColor DarkGoldenRod
  FontSize 13
}
skinparam arrow {
  Color SteelBlue
}

package "app" {
  class Main
}

package "assembler" {
  class Assembler
}

package "core" {
  class CPU
  class Memory
  class RegisterFile
}

package "instruction" {
  enum Opcode
}

package "exception" {
  class IllegalAddressException
  class UnknownOpcodeException
  class AssemblerException
}

package "test" {
  class MemoryTest
  class RegisterFileTest
  class CPUTest
  class AssemblerTest
}

app --> assembler : utilise
app --> core : utilise
assembler --> core : écrit dans Memory
assembler --> instruction : traduit vers Opcode
assembler --> exception : lève AssemblerException
core --> instruction : décode Opcode
core --> exception : lève exceptions
test --> core : teste
test --> assembler : teste

note bottom of app
  Point d'entrée.
  Orchestre tous les composants.
end note

note bottom of core
  Cœur du simulateur.
  Aucune dépendance vers app ou assembler.
end note

note bottom of instruction
  Couche la plus basse.
  N'a aucune dépendance vers d'autres packages.
end note

@enduml
```

---

## Récapitulatif des diagrammes

Voici un tableau de synthèse des diagrammes produits et de leur rôle dans la notation :

| Numéro | Nom du diagramme | Type UML | Critère de notation couvert |
|--------|-----------------|----------|-----------------------------|
| 1 | Cas d'utilisation | Use Case | Diagramme des cas d'utilisation (5 pts) |
| 2 | Diagramme de classes | Class Diagram | Diagramme de classes (5 pts) |
| 3 | Séquence système — Exécution | Sequence (système) | Diagrammes de séquence système (5 pts) |
| 4 | Séquence système — Assemblage | Sequence (système) | Diagrammes de séquence système (5 pts) |
| 5 | Séquence détaillée — Cycle CPU | Sequence (détaillé) | Diagrammes de séquence détaillés (5 pts) |
| 6 | Séquence détaillée — Assembleur | Sequence (détaillé) | Diagrammes de séquence détaillés (5 pts) |
| 7 | Séquence détaillée — STORE | Sequence (détaillé) | Diagrammes de séquence détaillés (5 pts) |
| 8 | Séquence détaillée — BEQ | Sequence (détaillé) | Diagrammes de séquence détaillés (5 pts) |
| 9 | Diagramme d'états — CPU | State Machine | Complément conception |
| 10 | Diagramme de packages | Package Diagram | Modularité des classes (5 pts) |

---
---

# ═══════════════════════════════════════════════════════════
# VERSION V2 — DIAGRAMMES ÉQUILIBRÉS
# Arborescence corrigée · Niveau de détail intermédiaire
# ═══════════════════════════════════════════════════════════

> **Note V2 :** Cette section propose des diagrammes à un niveau de détail intermédiaire — techniquement complets (toutes les classes, méthodes clés, interactions importantes) mais sans les annotations de bas niveau qui surchargent la V1. L'arborescence reflète le vrai projet : `ALU.java` dans `core`, exceptions précises dans `exception`.

---

## V2 — Diagramme 1 : Cas d'utilisation

Deux acteurs sont représentés : l'Utilisateur (qui écrit et exécute des programmes) et JUnit (qui pilote les tests). Les cas d'utilisation couvrent les cinq étapes du projet, regroupés en packages fonctionnels, avec les relations `<<include>>` essentielles pour montrer les dépendances entre actions.

```
@startuml V2_CasUtilisation

skinparam actorStyle awesome
skinparam usecase {
  BackgroundColor #EEF4FB
  BorderColor #2E75B6
  ArrowColor #2E75B6
}
skinparam package {
  BorderColor #1F4E79
  BackgroundColor #F7FBFF
}

left to right direction

actor "Utilisateur" as U

rectangle "Simulateur CPU — Carré Petit Utile" {

  package "Assemblage" {
    usecase "Écrire un programme\nassembleur" as UC1
    usecase "Assembler le programme\n(traduit en codes numériques)" as UC2
    usecase "Charger le programme\nen mémoire" as UC3
  }

  package "Exécution CPU" {
    usecase "Lancer l'exécution" as UC4
    usecase "Exécuter LOAD / STORE" as UC5
    usecase "Exécuter opérations ALU\n(ADD, SUB, MUL, DIV, OR, AND, XOR)" as UC6
    usecase "Exécuter sauts\n(JUMP, BEQ, BNE)" as UC7
    usecase "Arrêter le CPU (BREAK)" as UC8
  }

  package "Consultation" {
    usecase "Consulter les registres" as UC9
    usecase "Consulter la mémoire" as UC10
  }

}

U --> UC1
UC1 ..> UC2 : <<include>>
UC2 ..> UC3 : <<include>>
U --> UC4
UC4 ..> UC5 : <<extend>>
UC4 ..> UC6 : <<extend>>
UC4 ..> UC7 : <<extend>>
UC4 ..> UC8 : <<include>>
U --> UC9
U --> UC10

@enduml
```

---

## V2 — Diagramme 2 : Diagramme de classes

Le diagramme de classes reflète l'arborescence réelle du projet. Toutes les classes sont présentes avec leurs attributs principaux et leurs méthodes clés. Les relations de composition, dépendance et héritage sont toutes indiquées. Les méthodes très internes (privatehelpers) sont omises pour ne pas noyer le lecteur.

```plantuml
@startuml V2_DiagrammeClasses

skinparam classAttributeIconSize 0
skinparam class {
  BackgroundColor #EEF4FB
  BorderColor #2E75B6
  ArrowColor #2E75B6
  FontSize 12
}
skinparam package {
  BackgroundColor #F7FBFF
  BorderColor #1F4E79
  FontStyle bold
}

package "instruction" {
  enum Opcode {
    BREAK = 0
    LOAD_CONST = 1
    LOAD_MEM = 2
    STORE = 3
    ADD = 4
    SUB = 5
    MUL = 6
    DIV = 7
    OR = 8
    AND = 9
    XOR = 10
    JUMP = 11
    BEQ = 12
    BNE = 13
    LOAD_IDX = 14
    STORE_IDX = 15
    --
    - code : int
    --
    + getCode() : int
    + {static} fromCode(code : int) : Opcode
  }
}

package "exception" {
  class InvalidOpcodeException {
    + InvalidOpcodeException(code : int)
  }
  class MemoryOutOfBoundsException {
    + MemoryOutOfBoundsException(address : int)
  }
  class RegisterOutOfBoundsException {
    + RegisterOutOfBoundsException(index : int)
  }
  InvalidOpcodeException --|> RuntimeException
  MemoryOutOfBoundsException --|> RuntimeException
  RegisterOutOfBoundsException --|> RuntimeException
}

package "core" {
  class Memory {
    - {static} SIZE : int = 65536
    - data : byte[]
    --
    + read(address : int) : byte
    + write(address : int, value : byte) : void
    + readWord(address : int) : int
    + writeWord(address : int, value : int) : void
  }

  class RegisterFile {
    - {static} COUNT : int = 16
    - registers : byte[]
    --
    + get(index : int) : byte
    + set(index : int, value : byte) : void
    + reset() : void
  }

  class ALU {
    + add(a : byte, b : byte) : byte
    + sub(a : byte, b : byte) : byte
    + mul(a : byte, b : byte) : int
    + div(a : byte, b : byte) : int[]
    + or(a : byte, b : byte) : byte
    + and(a : byte, b : byte) : byte
    + xor(a : byte, b : byte) : byte
  }

  class CPU {
    - pc : int
    - running : boolean
    --
    + CPU(memory : Memory, registers : RegisterFile, alu : ALU)
    + run() : void
    + reset() : void
    + getPC() : int
    - fetch() : int
    - decode(code : int) : Opcode
    - execute(opcode : Opcode) : void
  }
}

package "assembler" {
  class Assembler {
    - writePointer : int
    --
    + Assembler(memory : Memory)
    + assemble(program : String) : void
    - assembleLine(line : String) : void
    - parseRegister(token : String) : byte
    - parseValue(token : String) : int
    - emitByte(value : byte) : void
    - emitWord(value : int) : void
  }
}

package "app" {
  class Main {
    + {static} main(args : String[]) : void
  }
}

CPU "1" o--> "1" Memory
CPU "1" o--> "1" RegisterFile
CPU "1" o--> "1" ALU
CPU ..> Opcode : décode
CPU ..> InvalidOpcodeException : <<throws>>
Memory ..> MemoryOutOfBoundsException : <<throws>>
RegisterFile ..> RegisterOutOfBoundsException : <<throws>>
Assembler "1" o--> "1" Memory
Assembler ..> Opcode : traduit vers
Main ..> CPU : instancie
Main ..> Memory : instancie
Main ..> RegisterFile : instancie
Main ..> ALU : instancie
Main ..> Assembler : instancie

@enduml
```

---

## V2 — Diagramme 3 : Séquence système — Exécution d'un programme

Ce diagramme montre les échanges entre l'utilisateur et le système vu comme une boîte noire. On distingue les deux grandes phases (assemblage puis exécution) et on montre le comportement en cas d'erreur, ce qui est important pour la notation.

```@startuml V2_SeqSysteme

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor #2E75B6
  LifeLineBorderColor #2E75B6
  ParticipantBackgroundColor #EEF4FB
  ParticipantBorderColor #2E75B6
}

actor "Utilisateur" as U
boundary "Système\n(CPU Simulateur)" as S

title Séquence système — Assemblage puis Exécution

== Phase 1 : Assemblage ==
U -> S : fournir le programme assembleur (texte)
activate S
  alt syntaxe valide
    S --> U : programme chargé en mémoire
  else erreur de syntaxe
    S --> U : AssemblerException (ligne N)
  end
deactivate S

== Phase 2 : Exécution ==
U -> S : lancer l'exécution du CPU
activate S
  loop jusqu'à BREAK
    S -> S : Fetch → Decode → Execute
  end
  alt exécution normale
    S --> U : exécution terminée
  else opcode invalide / accès mémoire hors borne
    S --> U : exception levée
  end
deactivate S

== Phase 3 : Consultation ==
U -> S : consulter l'état des registres
S --> U : valeurs des 16 registres

U -> S : consulter une zone mémoire
S --> U : contenu de la zone demandée

@enduml
```

---

## V2 — Diagramme 4 : Séquence détaillée — Cycle CPU complet (ADD r2, r0, r1)

Ce diagramme montre le cycle Fetch/Decode/Execute pour une instruction `ADD`. Les trois phases sont clairement séparées. La lecture des paramètres est montrée de façon groupée (trois lectures successives) sans répéter chaque `PC++` individuellement, ce qui allège sans perdre l'information essentielle.

```plantuml
@startuml V2_SeqDetailCPU

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor #2E75B6
  LifeLineBorderColor #2E75B6
  ParticipantBackgroundColor #EEF4FB
  ParticipantBorderColor #2E75B6
}

participant "Main" as MAIN
participant "CPU" as CPU
participant "Memory" as MEM
participant "RegisterFile" as REG
participant "ALU" as ALU

title Séquence détaillée — Cycle CPU (instruction ADD r2, r0, r1)

MAIN -> CPU : run()
activate CPU

== FETCH ==
CPU -> MEM : read(pc++)
MEM --> CPU : 4  [opcode ADD]

== DECODE ==
note over CPU : Opcode.fromCode(4) → Opcode.ADD

== EXECUTE — lecture des paramètres ==
CPU -> MEM : read(pc++) → 2  [registre dest : r2]
CPU -> MEM : read(pc++) → 0  [registre src1 : r0]
CPU -> MEM : read(pc++) → 1  [registre src2 : r1]

== EXECUTE — calcul ==
CPU -> REG : get(0)
REG --> CPU : val_r0 = 5

CPU -> REG : get(1)
REG --> CPU : val_r1 = 6

CPU -> ALU : add(5, 6)
ALU --> CPU : résultat = 11

CPU -> REG : set(2, 11)

note over CPU : PC pointe maintenant sur\nl'instruction suivante → nouveau cycle

CPU --> MAIN : (continue ou s'arrête sur BREAK)
deactivate CPU

@enduml
```

---

## V2 — Diagramme 5 : Séquence détaillée — Assembleur (load r2, @100 + break)

Ce diagramme montre comment l'assembleur traite deux lignes consécutives. Pour l'instruction `load r2, @100`, on voit le parsing, la reconnaissance du mode mémoire, et l'écriture des 4 octets en mémoire (opcode, registre, octet haut d'adresse, octet bas d'adresse). L'instruction `break` illustre le cas minimal.

```plantuml
@startuml V2_SeqDetailAssembler

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor #1F4E79
  LifeLineBorderColor #1F4E79
  ParticipantBackgroundColor #F7FBFF
  ParticipantBorderColor #1F4E79
}

participant "Main" as MAIN
participant "Assembler" as ASM
participant "Memory" as MEM

title Séquence détaillée — Assembleur ("load r2, @100" puis "break")

MAIN -> ASM : assemble("load r2, @100\nbreak")
activate ASM

== Ligne 1 : "load r2, @100" ==
ASM -> ASM : parseLine() → mnémonique="load", args=["r2", "@100"]
note right of ASM : "@100" commence par "@"\n→ mode mémoire → LOAD_MEM (opcode 2)
ASM -> ASM : parseRegister("r2") → 2
ASM -> ASM : parseValue("100") → 100 = 0x0064

ASM -> MEM : write(ptr=0, opcode=2)
ASM -> MEM : write(ptr=1, reg=2)
ASM -> MEM : write(ptr=2, addrHigh=0x00)
ASM -> MEM : write(ptr=3, addrLow=0x64)
note right of ASM : writePointer = 4

== Ligne 2 : "break" ==
ASM -> ASM : parseLine() → mnémonique="break"
ASM -> MEM : write(ptr=4, opcode=0)
note right of ASM : writePointer = 5

ASM --> MAIN : assemblage OK (5 octets écrits)
deactivate ASM

@enduml
```

---

## V2 — Diagramme 6 : Séquence détaillée — Saut conditionnel BEQ

Ce diagramme illustre l'instruction `BEQ r0, r1, adresse`, fondamentale pour comprendre le mécanisme des boucles. On montre les deux cas possibles : le saut effectif (registres égaux) et la continuation normale (registres différents).

```plantuml
@startuml V2_SeqDetailBEQ

skinparam sequenceMessageAlign center
skinparam sequence {
  ArrowColor #1F4E79
  LifeLineBorderColor #1F4E79
  ParticipantBackgroundColor #F7FBFF
  ParticipantBorderColor #1F4E79
}

participant "CPU" as CPU
participant "Memory" as MEM
participant "RegisterFile" as REG

title Séquence détaillée — Instruction BEQ r0, r1, @adresse

== FETCH + DECODE ==
CPU -> MEM : read(pc++)
MEM --> CPU : 12  [opcode BEQ]
note over CPU : Opcode.fromCode(12) → BEQ

== EXECUTE — lecture des paramètres ==
CPU -> MEM : read(pc++) → idx1 = 0  [r0]
CPU -> MEM : read(pc++) → idx2 = 1  [r1]
CPU -> MEM : read(pc++) → addrHigh
CPU -> MEM : read(pc++) → addrLow
note over CPU : targetAddr = (addrHigh << 8) | addrLow

== EXECUTE — comparaison ==
CPU -> REG : get(0)
REG --> CPU : val_r0

CPU -> REG : get(1)
REG --> CPU : val_r1

alt val_r0 == val_r1
  note over CPU : SAUT EFFECTUÉ\nPC ← targetAddr
else val_r0 != val_r1
  note over CPU : PAS DE SAUT\nPC reste sur l'instruction suivante
end

@enduml
```

---

## V2 — Diagramme 7 : Diagramme d'états — Cycle de vie du CPU

Ce diagramme montre les états significatifs par lesquels passe le CPU. On distingue bien les états stables (Initialisé, Prêt, En exécution, Terminé) et les états d'erreur. Les transitions portent des libellés explicites qui indiquent quelle action ou quel événement les déclenche.

```plantuml
@startuml V2_DiagrammeEtats

skinparam state {
  BackgroundColor #EEF4FB
  BorderColor #2E75B6
  ArrowColor #2E75B6
  FontSize 12
}

[*] --> Initialisé : new CPU(memory, registers, alu)\nPC = 0, running = false

Initialisé --> PrêtÀExécuter : assembler.assemble(programme)\nprogramme chargé en mémoire

PrêtÀExécuter --> EnExécution : cpu.run()

state EnExécution {
  [*] --> Fetch
  Fetch --> Decode : opcode lu, PC++
  Decode --> Execute : Opcode reconnu
  Execute --> Fetch : instruction terminée\n(pas BREAK)
  Decode --> [*] : opcode inconnu\n→ InvalidOpcodeException
  Execute --> [*] : accès hors borne\n→ MemoryOutOfBoundsException
}

EnExécution --> Terminé : instruction BREAK rencontrée
EnExécution --> ErreurRuntime : exception non gérée

Terminé --> PrêtÀExécuter : cpu.reset()\nPC = 0

ErreurRuntime : Exception levée\nexécution interrompue

Terminé --> [*]

@enduml
```

---

## Récapitulatif V2

| N° | Diagramme | Type UML | Critère de notation couvert |
|----|-----------|----------|-----------------------------|
| V2-1 | Cas d'utilisation | Use Case | Diagramme des cas d'utilisation (5 pts) |
| V2-2 | Diagramme de classes | Class Diagram | Diagramme de classes (5 pts) |
| V2-3 | Séquence système | Sequence (système) | Séquences système (5 pts) |
| V2-4 | Séquence détaillée — Cycle CPU ADD | Sequence (détaillé) | Séquences détaillées (5 pts) |
| V2-5 | Séquence détaillée — Assembleur | Sequence (détaillé) | Séquences détaillées (5 pts) |
| V2-6 | Séquence détaillée — BEQ | Sequence (détaillé) | Séquences détaillées (5 pts) |
| V2-7 | Diagramme d'états — CPU | State Machine | Complément conception |