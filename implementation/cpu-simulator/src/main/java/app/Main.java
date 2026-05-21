package app;

import assembler.Assembler;
import core.CPU;
import core.Memory;
import core.RegisterFile;
import exception.InvalidOpcodeException;
import exception.MemoryOutOfBoundsException;

import java.util.Scanner;

/**
 * Point d'entree du simulateur de processeur.
 * Permet d'ecrire, assembler, executer et inspecter un programme en assembleur.
 */
public class Main {

    /**
     * Lance le menu principal en boucle jusqu'a ce que l'utilisateur quitte.
     */
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        Memory memory = new Memory();
        RegisterFile registers = new RegisterFile();
        CPU cpu = new CPU(memory, registers);
        Assembler assembler = new Assembler(memory);

        String programme = "";
        boolean estAssemble = false;
        boolean programmeTermine = false;
        boolean continuer = true;

        System.out.println("Simulateur de processeur  ");
        System.out.println();

        while (continuer) {

            // Affichage du menu
            System.out.println("Menu principal ");
            System.out.println("1 : Ecrire un programme en assembleur");
            System.out.println("2 : Assembler le programme");
            System.out.println("3 : Executer le programme");
            System.out.println("4 : Executer pas a pas");
            System.out.println("5 : Consulter l'etat du simulateur");
            System.out.println("6 : Reinitialiser le CPU");
            System.out.println("7 : Quitter");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            System.out.println();

            // Option 1 : saisie du programme
            if (choix.equals("1")) {

                System.out.println(" Ecrire un programme en assembleur");
                System.out.println();
                System.out.println("Instructions disponibles : load, store, add, sub, mul, div, and, or, xor, jump, beq, bne, break, data");
                System.out.println();
                System.out.println("Entrez votre programme ligne par ligne");
                System.out.println("Laissez une ligne vide pour terminer");
                System.out.println();

                StringBuilder sb = new StringBuilder(); // Accumulateur des lignes du programme saisi par l'utilisateur
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
                // dans le cas ou l'utilisateur ne saisit rien 
                if (nbLignes == 0) {
                    System.out.println("Aucune ligne saisie, programme inchange.");
                } else {
                    // Sinon on affiche le nombre de ligne qu'il a saisi et qu'il faut assemble
                    programme = sb.toString();
                    estAssemble = false;
                    System.out.println(nbLignes + " ligne(s) saisie(s).");
                    System.out.println("Pensez a choisir l'option 2 pour assembler le programme.");
                }

            // Option 2 : assemblage du programme
            } else if (choix.equals("2")) {

                System.out.println("Assembler le programme ");
                System.out.println();

                // Vérification qu'il ya bien un programme qui a été saisi afin d'assembler
                if (programme.isEmpty()) {
                    System.out.println("Aucun programme a assembler. Ecrivez d'abord un programme (option 1).");
                } else {
                    // Reinitialisation avant chargement en memoire
                    memory.reset();
                    registers.reset();
                    cpu.reset();
                    assembler = new Assembler(memory);

                    try {
                        assembler.assemble(programme);
                        estAssemble = true;
                        programmeTermine = false;
                        System.out.println("Assemblage reussi.");
                        System.out.println("Le programme a ete charge en memoire.");
                        System.out.println("Vous pouvez maintenant l'executer (option 3 ou 4).");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur lors de l'assemblage : " + e.getMessage());
                        estAssemble = false;
                    }
                }

            // Option 3 : execution complete
            } else if (choix.equals("3")) {

                System.out.println("Executer le programme ");
                System.out.println();

                if (!estAssemble) {
                    System.out.println("Le programme n'est pas encore assemble. Choisissez l'option 2.");
                } else if (programmeTermine) {
                    System.out.println("Le programme est deja termine. Utilisez l'option 6 pour reinitialiser.");
                } else {
                    try {
                        cpu.run();
                        programmeTermine = true;
                        System.out.println("Execution terminee.");
                    } catch (InvalidOpcodeException e) {
                        System.out.println("Erreur d'execution : opcode inconnu - " + e.getMessage());
                    } catch (ArithmeticException e) {
                        System.out.println("Erreur d'execution : " + e.getMessage());
                    } catch (MemoryOutOfBoundsException e) {
                        System.out.println("Erreur d'execution : acces memoire hors limites - " + e.getMessage());
                    }
                }

            // Option 4 : execution pas a pas
            } else if (choix.equals("4")) {

                System.out.println("Executer pas a pas ");
                System.out.println();

                if (!estAssemble) {
                    System.out.println("Le programme n'est pas encore assemble. Choisissez l'option 2.");
                } else if (programmeTermine) {
                    System.out.println("Le programme est deja termine. Utilisez l'option 6 pour reinitialiser.");
                } else {
                    try {
                        boolean enCours = cpu.step();
                        System.out.println("Instruction executee.");
                        if (!enCours) {
                            programmeTermine = true;
                            System.out.println("BREAK atteint : le programme est termine.");
                            System.out.println("Utilisez l'option 6 pour reinitialiser avant une nouvelle execution.");
                        } else {
                            System.out.println("Appuyez sur 4 pour executer l'instruction suivante.");
                        }
                    } catch (InvalidOpcodeException e) {
                        System.out.println("Erreur d'execution : opcode inconnu - " + e.getMessage());
                    } catch (ArithmeticException e) {
                        System.out.println("Erreur d'execution : " + e.getMessage());
                    } catch (MemoryOutOfBoundsException e) {
                        System.out.println("Erreur d'execution : acces memoire hors limites - " + e.getMessage());
                    }
                }

            // Option 5 : consultation de l'etat
            } else if (choix.equals("5")) {

                System.out.println("=== Consulter l'etat du simulateur ===");
                System.out.println();
                System.out.println("Que voulez-vous consulter ?");
                System.out.println("  a : Etat de la memoire");
                System.out.println("  b : Etat des registres");
                System.out.println("  c : Compteur de programme (PC)");
                System.out.print("Votre choix : ");

                String sousChoix = scanner.nextLine().trim().toLowerCase();
                System.out.println();

                if (sousChoix.equals("a")) {

                    System.out.print("Adresse de debut (par defaut 0) : ");
                    String saisieAdresse = scanner.nextLine().trim();
                    int debut = 0;
                    if (!saisieAdresse.isEmpty()) {
                        try {
                            int valeurSaisie = Integer.parseInt(saisieAdresse);
                            if (valeurSaisie < 0 || valeurSaisie >= Memory.MEMORY_SIZE) {
                                System.out.println("Adresse hors limites [0, " + (Memory.MEMORY_SIZE - 1) + "], utilisation de 0.");
                            } else {
                                debut = valeurSaisie;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Adresse invalide, utilisation de 0.");
                        }
                    }

                    System.out.print("Nombre de cases a afficher (par defaut 16) : ");
                    String saisieNombre = scanner.nextLine().trim();
                    int nombre = 16;
                    if (!saisieNombre.isEmpty()) {
                        try {
                            int valeurSaisie = Integer.parseInt(saisieNombre);
                            if (valeurSaisie <= 0) {
                                System.out.println("Nombre invalide (doit etre > 0), utilisation de 16.");
                            } else {
                                nombre = valeurSaisie;
                            }
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

            // Option 6 : reinitialisation
            } else if (choix.equals("6")) {

                System.out.println("=== Reinitialiser le CPU ===");
                System.out.println();

                memory.reset();
                registers.reset();
                cpu.reset();
                assembler = new Assembler(memory);
                estAssemble = false;
                programmeTermine = false;
                System.out.println("CPU, registres et memoire remis a zero.");
                System.out.println("Le programme saisi est conserve (option 1 pour le modifier).");

            // Option 7 : quitter
            } else if (choix.equals("7")) {

                continuer = false;

            } else {
                // cas utilisateur choisi un numéro non compris entre 1 et 7 
                System.out.println("Choix invalide. Entrez un nombre entre 1 et 7.");
            }

            System.out.println();
        }

        scanner.close();
    }

    /**
     * Affiche la valeur des 16 registres.
     *
     * @param registers le banc de registres
     */
    private static void afficherRegistres(RegisterFile registers) throws exception.RegisterOutOfBoundsException {
        System.out.println("Etat des registres :");
        for (int i = 0; i < 16; i++) {
            System.out.println("  R" + i + " = " + (registers.get(i) & 0xFF));
        }
    }

    /**
     * Affiche un bloc de cases memoire.
     *
     * @param memory  la memoire
     * @param debut   adresse de depart
     * @param nombre  nombre de cases a afficher
     */
    private static void afficherMemoire(Memory memory, int debut, int nombre) throws exception.MemoryOutOfBoundsException {
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
