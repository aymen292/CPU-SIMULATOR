# Scénarios de démonstration — Simulateur de processeur

---

## Rappel des touches

```
1  →  écrire le programme
2  →  assembler
3  →  exécution complète
4  →  pas à pas
5b →  vérifier les registres
5a →  vérifier la mémoire (demande adresse de début + nombre de cases)
5c →  vérifier le PC
6  →  réinitialiser avant le scénario suivant
```

---

## Scénario 1 — Opérations classiques (LOAD_CONST, ADD, SUB, STORE, LOAD_MEM)

### Programme (option 1)

```
load r0, 15
load r1, 7
add r2, r0, r1
sub r3, r0, r1
store r2, @200
load r4, @200
break
```

### Explication instruction par instruction

| Instruction      | Ce qui se passe                              |
|------------------|----------------------------------------------|
| `load r0, 15`    | LOAD_CONST : R0 = 15                         |
| `load r1, 7`     | LOAD_CONST : R1 = 7                          |
| `add r2, r0, r1` | R2 = R0 + R1 = 15 + 7 = **22**              |
| `sub r3, r0, r1` | R3 = R0 − R1 = 15 − 7 = **8**               |
| `store r2, @200` | écrit R2 (= 22) en mémoire à l'adresse 200   |
| `load r4, @200`  | LOAD_MEM : R4 = mem[200] = **22**            |
| `break`          | arrête le CPU                                |

### Résultats attendus

**Registres (5→b) :**
```
R0 = 15   R1 = 7   R2 = 22   R3 = 8   R4 = 22   R5..R15 = 0
```

**Mémoire adresse 200 (5→a, début = 200, nb = 1) :**
```
200  |  22
```

**PC (5→c) :** `23`
> Détail : 3+3+4+4+4+4+1 = 23 octets

### Déroulement conseillé pour la démo

1. Taper le programme (option 1)
2. Assembler (option 2)
3. Exécuter **pas à pas** (option 4) en vérifiant les registres (5→b) après chaque instruction
4. À la fin, vérifier la mémoire (5→a) et le PC (5→c)
5. Réinitialiser (option 6) avant le scénario suivant

---

## Scénario 2 — Boucle avec BNE (saut conditionnel)

### Programme (option 1)

```
load r0, 0
load r1, 5
load r2, 1
add r0, r0, r2
bne r0, r1, @9
break
```

### Explication instruction par instruction

| Adresse | Instruction        | Ce qui se passe                                        |
|---------|--------------------|--------------------------------------------------------|
| 0       | `load r0, 0`       | compteur = 0                                           |
| 3       | `load r1, 5`       | limite = 5                                             |
| 6       | `load r2, 1`       | pas = 1                                                |
| **9**   | `add r0, r0, r2`   | compteur += 1  ← **début de boucle**                  |
| 13      | `bne r0, r1, @9`   | si R0 ≠ R1, retourner à l'adresse 9, sinon continuer  |
| 18      | `break`            | R0 vient d'atteindre 5, on sort de la boucle           |

### Déroulement de la boucle

```
Tour 1 : R0 = 0+1 = 1  →  1 ≠ 5  →  jump @9
Tour 2 : R0 = 1+1 = 2  →  2 ≠ 5  →  jump @9
Tour 3 : R0 = 2+1 = 3  →  3 ≠ 5  →  jump @9
Tour 4 : R0 = 3+1 = 4  →  4 ≠ 5  →  jump @9
Tour 5 : R0 = 4+1 = 5  →  5 = 5  →  pas de saut  →  BREAK
```

### Résultats attendus

**Registres (5→b) :**
```
R0 = 5   R1 = 5   R2 = 1   R3..R15 = 0
```

**PC (5→c) :** `19`

### Déroulement conseillé pour la démo

1. Taper le programme (option 1)
2. Assembler (option 2)
3. Exécuter **en une fois** (option 3)
4. Vérifier avec 5→b que R0 = 5 : preuve que la boucle a tourné exactement 5 fois
5. Vérifier avec 5→c que PC = 19 : preuve que le CPU a bien atteint le BREAK
6. Réinitialiser (option 6) avant le scénario suivant

---

## Scénario 3 — Adressage indexé (simulation d'un tableau)

### Programme (option 1)

```
load r0, 0
load r1, 40
store r1, @100, r0
load r0, 1
load r1, 30
store r1, @100, r0
load r0, 0
load r2, @100, r0
load r0, 1
load r3, @100, r0
add r4, r2, r3
break
```

### Explication instruction par instruction

| Instruction           | Ce qui se passe                                      |
|-----------------------|------------------------------------------------------|
| `load r0, 0`          | index = 0                                            |
| `load r1, 40`         | valeur à stocker                                     |
| `store r1, @100, r0`  | STORE_INDEXED : mem[100 + **0**] = **40**            |
| `load r0, 1`          | index = 1                                            |
| `load r1, 30`         | valeur à stocker                                     |
| `store r1, @100, r0`  | STORE_INDEXED : mem[100 + **1**] = **30**            |
| `load r0, 0`          | index = 0 (pour relire)                              |
| `load r2, @100, r0`   | LOAD_INDEXED : R2 = mem[100 + **0**] = **40**        |
| `load r0, 1`          | index = 1                                            |
| `load r3, @100, r0`   | LOAD_INDEXED : R3 = mem[100 + **1**] = **30**        |
| `add r4, r2, r3`      | R4 = 40 + 30 = **70**                                |
| `break`               | arrête le CPU                                        |

### Résultats attendus

**Registres (5→b) :**
```
R2 = 40   R3 = 30   R4 = 70
```

**Mémoire adresses 100-101 (5→a, début = 100, nb = 2) :**
```
100  |  40
101  |  30
```

**PC (5→c) :** `43`
> Détail : 3+3+5+3+3+5+3+5+3+5+4+1 = 43 octets

### Déroulement conseillé pour la démo

1. Taper le programme (option 1)
2. Assembler (option 2)
3. Exécuter **pas à pas** (option 4) jusqu'après le 2e `store` (6 appuis sur 4)
4. Vérifier la mémoire (5→a, début=100, nb=2) : mem[100]=40 et mem[101]=30 sont déjà écrits
5. Continuer pas à pas jusqu'au BREAK (6 appuis supplémentaires)
6. Vérifier les registres (5→b) : R2=40, R3=30, R4=70

---

## Récapitulatif des résultats finaux

| Scénario | Ce qu'on montre | Résultat clé à vérifier |
|---|---|---|
| 1 — Classique | LOAD_CONST, ADD, SUB, STORE, LOAD_MEM | R2=22, R3=8, mem[200]=22 |
| 2 — Boucle BNE | Saut conditionnel, compteur | R0=5 après 5 tours, PC=19 |
| 3 — Indexé | Tableau en mémoire, STORE/LOAD_INDEXED | R4=70, mem[100]=40, mem[101]=30 |
