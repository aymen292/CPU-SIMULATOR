package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;
import exception.InvalidOpcodeException;

import java.util.Scanner;

/**
 * Interface en ligne de commande du simulateur de processeur.
 * ecrire un programme, assembler, executer, consulter l'etat et reinitialiser.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Memory memory = new Memory();
        RegisterFile registers = new RegisterFile();
        CPU cpu = new CPU(memory, registers);
        Assembler assembler = new Assembler(memory);

        String programme = "";
        boolean estAssemble = false;

        System.out.println("=== Simulateur de processeur  ===");
        System.out.println();

        boolean continuer = true;

        while (continuer) {

            System.out.println("--- Menu principal ---");
            System.out.println("1 - Ecrire un programme en assembleur");
            System.out.println("2 - Assembler le programme");
            System.out.println("3 - Executer le programme");
            System.out.println("4 - Executer pas a pas");
            System.out.println("5 - Consulter l'etat du simulateur");
            System.out.println("6 - Reinitialiser le CPU");
            System.out.println("7 - Quitter");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            System.out.println();

            // ----------------------------------------------------------------
            // Cas 1 : Ecrire un programme en assembleur
            // ----------------------------------------------------------------
            if (choix.equals("1")) {

                System.out.println("=== Ecrire un programme en assembleur ===");
                System.out.println();
                System.out.println("Instructions disponibles :");
                System.out.println("  Instructions de base   : load, store, break");
                System.out.println("  Operations ALU         : add, sub, mul, div, and, or, xor");
                System.out.println("  Sauts et branchements  : jump, beq, bne");
                System.out.println("  Adressage indexe       : load rX, @base, rY  /  store rX, @base, rY");
                System.out.println("  Donnees en memoire     : data val1, val2, ...  /  string \"texte\"");
                System.out.println();
                System.out.println("Entrez votre programme ligne par ligne.");
                System.out.println("Laissez une ligne vide pour terminer.");
                System.out.println();

                StringBuilder sb = new StringBuilder();
                int nbLignes = 0;

                while (true) {
                    System.out.print("  > ");
                    String ligne = scanner.nextLine();
                    if (ligne.trim().isEmpty()) {
                        break;
                    }
                    sb.append(ligne).append("\n");
                    nbLignes++;
                }

                if (nbLignes == 0) {
                    System.out.println("Aucune ligne saisie, programme inchange.");
                } else {
                    programme = sb.toString();
                    estAssemble = false;
                    System.out.println(nbLignes + " ligne(s) saisie(s).");
                    System.out.println("Pensez a choisir l'option 2 pour assembler le programme.");
                }

            // ----------------------------------------------------------------
            // Cas 2 : Assembler le programme (inclut le chargement en memoire)
            // ----------------------------------------------------------------
            } else if (choix.equals("2")) {

                System.out.println("=== Assembler le programme ===");
                System.out.println();

                if (programme.isEmpty()) {
                    System.out.println("Aucun programme a assembler. Ecrivez d'abord un programme (option 1).");
                } else {
                    // reinitialisation avant chargement en memoire
                    memory.reset();
                    registers.reset();
                    cpu.reset();
                    assembler = new Assembler(memory);

                    try {
                        assembler.assemble(programme);
                        estAssemble = true;
                        System.out.println("Assemblage reussi.");
                        System.out.println("Le programme a ete charge en memoire.");
                        System.out.println("Vous pouvez maintenant l'executer (option 3 ou 4).");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur lors de l'assemblage : " + e.getMessage());
                        estAssemble = false;
                    }
                }

            // ----------------------------------------------------------------
            // Cas 3 : Executer le programme
            // ----------------------------------------------------------------
            } else if (choix.equals("3")) {

                System.out.println("=== Executer le programme ===");
                System.out.println();

                if (!estAssemble) {
                    System.out.println("Le programme n'est pas encore assemble. Choisissez l'option 2.");
                } else {
                    try {
                        cpu.run();
                        System.out.println("Execution terminee.");
                    } catch (InvalidOpcodeException e) {
                        System.out.println("Erreur d'execution : " + e.getMessage());
                    }
                }

            // ----------------------------------------------------------------
            // Cas 4 : Executer pas a pas
            // ----------------------------------------------------------------
            } else if (choix.equals("4")) {

                System.out.println("=== Executer pas a pas ===");
                System.out.println();

                if (!estAssemble) {
                    System.out.println("Le programme n'est pas encore assemble. Choisissez l'option 2.");
                } else {
                    try {
                        boolean enCours = cpu.step();
                        System.out.println("Instruction executee.");
                        if (!enCours) {
                            System.out.println("BREAK atteint : le programme est termine.");
                        } else {
                            System.out.println("Appuyez sur 4 pour executer l'instruction suivante.");
                        }
                    } catch (InvalidOpcodeException e) {
                        System.out.println("Erreur d'execution : " + e.getMessage());
                    }
                }

            // ----------------------------------------------------------------
            // Cas 5 : Consulter l'etat du simulateur
            // ----------------------------------------------------------------
            } else if (choix.equals("5")) {

                System.out.println("=== Consulter l'etat du simulateur ===");
                System.out.println();
                System.out.println("Que voulez-vous consulter ?");
                System.out.println("  a - Etat de la memoire");
                System.out.println("  b - Etat des registres");
                System.out.println("  c - Compteur de programme (PC)");
                System.out.print("Votre choix : ");

                String sousChoix = scanner.nextLine().trim().toLowerCase();
                System.out.println();

                if (sousChoix.equals("a")) {

                    System.out.print("Adresse de debut (par defaut 0) : ");
                    String saisieAdresse = scanner.nextLine().trim();
                    int debut = 0;
                    if (!saisieAdresse.isEmpty()) {
                        try {
                            debut = Integer.parseInt(saisieAdresse);
                        } catch (NumberFormatException e) {
                            System.out.println("Adresse invalide, utilisation de 0.");
                        }
                    }

                    System.out.print("Nombre de cases a afficher (par defaut 16) : ");
                    String saisieNombre = scanner.nextLine().trim();
                    int nombre = 16;
                    if (!saisieNombre.isEmpty()) {
                        try {
                            nombre = Integer.parseInt(saisieNombre);
                        } catch (NumberFormatException e) {
                            System.out.println("Nombre invalide, utilisation de 16.");
                        }
                    }

                    System.out.println();
                    afficherMemoire(memory, debut, nombre);

                } else if (sousChoix.equals("b")) {

                    afficherRegistres(registers);

                } else if (sousChoix.equals("c")) {

                    System.out.println("Compteur de programme (PC) = " + cpu.getPC());

                } else {
                    System.out.println("Choix invalide.");
                }

            // ----------------------------------------------------------------
            // Cas 6 : Reinitialiser le CPU
            // ----------------------------------------------------------------
            } else if (choix.equals("6")) {

                System.out.println("=== Reinitialiser le CPU ===");
                System.out.println();

                memory.reset();
                registers.reset();
                cpu.reset();
                assembler = new Assembler(memory);
                estAssemble = false;
                System.out.println("CPU, registres et memoire remis a zero.");
                System.out.println("Le programme saisi est conserve (option 1 pour le modifier).");

            // ----------------------------------------------------------------
            // Cas 7 : Quitter
            // ----------------------------------------------------------------
            } else if (choix.equals("7")) {

                continuer = false;

            } else {
                System.out.println("Choix invalide. Entrez un nombre entre 1 et 7.");
            }

            System.out.println();
        }

        scanner.close();
    }

    /**
     * Affiche la valeur des 16 registres.
     *
     * @param registers le banc de registres a afficher
     */
    private static void afficherRegistres(RegisterFile registers) {
        System.out.println("Etat des registres :");
        for (int i = 0; i < 16; i++) {
            System.out.println("  R" + i + " = " + (registers.get(i) & 0xFF));
        }
    }

    /**
     * Affiche un bloc de cases memoire.
     *
     * @param memory  la memoire a inspecter
     * @param debut   adresse de la premiere case a afficher
     * @param nombre  nombre de cases consecutives a afficher
     */
    private static void afficherMemoire(Memory memory, int debut, int nombre) {
        System.out.println("Etat de la memoire (adresses " + debut + " a " + (debut + nombre - 1) + ") :");
        System.out.println("  Adresse | Valeur");
        System.out.println("  --------|-------");
        for (int i = 0; i < nombre; i++) {
            int adresse = debut + i;
            if (adresse >= Memory.MEMORY_SIZE) {
                break;
            }
            System.out.println("  " + adresse + "       | " + (memory.read(adresse) & 0xFF));
        }
    }
}
