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

## Arborescence du projet : 
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
