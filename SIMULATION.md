# Exemple : calculer 10 + 20 et stocker le résultat en mémoire

---

## Étape 1 — Lancer le programme

Dans l'IDE, exécuter la classe `Main`. Le terminal affiche :

```
=== Simulateur de processeur - Carre Petit Utile ===

--- Menu principal ---
1 - Ecrire un programme en assembleur
2 - Assembler le programme
3 - Executer le programme
4 - Executer pas a pas
5 - Consulter l'etat du simulateur
6 - Reinitialiser le CPU
7 - Quitter
Votre choix :
```

---

## Étape 2 — Écrire le programme (option 1)

Taper `1` puis entrée. Saisir les instructions suivantes, une par ligne. Laisser une **ligne vide** pour terminer.

```
Votre choix : 1

  > load r0, 10
  > load r1, 20
  > add r2, r0, r1
  > store r2, @100
  > break
  >

5 ligne(s) saisie(s).
```

**Ce que fait ce programme :**
- `load r0, 10` → met la valeur 10 dans le registre R0
- `load r1, 20` → met la valeur 20 dans le registre R1
- `add r2, r0, r1` → R2 = R0 + R1 = 30
- `store r2, @100` → écrit R2 (= 30) à l'adresse mémoire 100
- `break` → arrête le CPU

---

## Étape 3 — Assembler le programme (option 2)

```
Votre choix : 2

Assemblage reussi.
Le programme a ete charge en memoire.
Vous pouvez maintenant l'executer (option 3 ou 4).
```

---

## Étape 4 — Exécuter pas à pas (option 4)

Taper `4` plusieurs fois pour avancer instruction par instruction et observer l'évolution des registres.

**Premier appui sur 4 :**
```
Votre choix : 4

Instruction executee.
Appuyez sur 4 pour executer l'instruction suivante.
```
→ `load r0, 10` vient d'être exécutée.

Vérifier avec **option 5 → b** :
```
Votre choix : 5

  a - Etat de la memoire
  b - Etat des registres
  c - Compteur de programme (PC)
Votre choix : b

Etat des registres :
  R0 = 10
  R1 = 0
  R2 = 0
  ...
```
✅ R0 vaut bien 10.

**Deuxième appui sur 4 :**
```
Votre choix : 4

Instruction executee.
```
→ `load r1, 20` vient d'être exécutée. Vérifier R1 = 20 avec l'option 5 → b.

**Troisième appui sur 4 :**
→ `add r2, r0, r1` exécutée. Vérifier R2 = 30.

**Quatrième appui sur 4 :**
→ `store r2, @100` exécutée. Vérifier la mémoire à l'adresse 100.

**Cinquième appui sur 4 :**
```
Votre choix : 4

Instruction executee.
BREAK atteint : le programme est termine.
```

---

## Étape 5 — Vérifier l'état final (option 5)

**Vérifier les registres (5 → b) :**
```
Etat des registres :
  R0 = 10
  R1 = 20
  R2 = 30
  R3 = 0
  ...
```

**Vérifier la mémoire à l'adresse 100 (5 → a) :**
```
Adresse de debut : 100
Nombre de cases : 1

Etat de la memoire (adresses 100 a 100) :
  Adresse | Valeur
  --------|-------
  100     | 30
```
✅ L'adresse 100 contient bien 30.

**Vérifier le PC (5 → c) :**
```
Compteur de programme (PC) = 11
```
Le PC vaut 11 car le programme occupe 11 octets en mémoire (chaque instruction prend plusieurs octets).

---

## Étape 6 — Réinitialiser et recommencer (option 6)

```
Votre choix : 6

CPU, registres et memoire remis a zero.
Le programme saisi est conserve.
```

Retourner à l'option 2 pour ré-assembler et tester une nouvelle exécution depuis le début.

---

## Récapitulatif de la démarche

```
1  →  écrire le programme
2  →  assembler
4  →  avancer pas à pas  (autant de fois que nécessaire)
5b →  vérifier les registres à chaque étape
5a →  vérifier la mémoire à la fin
6  →  réinitialiser pour un nouveau test
```
