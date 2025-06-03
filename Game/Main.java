// Mason Sabin 
import java.io.*;
import java.util.*;

public class Main 
{
    public static void main(String[] args) {
        // this main instance should now allow me to call display rules from game.java since displayRules originated here
        Main mainInstance = new Main();
        Game game = new Game(mainInstance);
        mainMenu(game);
    }
    // main menu before entering the game where players can read rules and add players
    public static void mainMenu(Game game) {
        Scanner scan = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("===== YAHTZEE MAIN MENU =====");
            System.out.println("1. Add Players");
            System.out.println("2. Read Rules");
            System.out.println("3. Play Game");
            System.out.println("4. Exit Game");
            System.out.println("Choose an option: ");

            try {
                int choice = scan.nextInt();
                scan.nextLine();
                
                if (choice == 1) {
                    addPlayers(game, scan);
                } 
                else if (choice == 2) {
                    try {// calls display rules method from main
                        displayRules();
                    } catch (FileNotFoundException e) {
                        System.out.println("Rules file not found. Please verify the rules file exists.");
                    }
                } // this is where the players will enter the game, if they have been added 
                else if (choice == 3) {
                    if (game.getPlayerCount() == 0) {
                        System.out.println("No players added yet. Please add a player.");
                    } else {
                        game.playGame(scan); // calls play game method from game
                    }
                } 
                else if (choice == 4) {
                    System.out.println("Exiting the game...");
                    running = false;
                } 
                else {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scan.next(); 
            }
        }
        scan.close();
    }
    // this method asks users how many players then adds them via game.java
    private static void addPlayers(Game game, Scanner scan) {
        int numPlayers = 0; 
        boolean isValid = false; 
        // this loop will run while not valid to keep user from going back to main menu if wrong input.
        while (!isValid) { 
            System.out.println("How many players would you like to add? (add up to 6 players)");
    
            if (scan.hasNextInt()) {
                numPlayers = scan.nextInt();
                scan.nextLine(); 
                //if number is between 1 or 6 then loop becomes true and moves on
                if (numPlayers >= 1 && numPlayers <= 6) {
                    isValid = true; 
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scan.next(); 
            }
        }
    
        // this should continue the loop after the input is validated 
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("Enter the name of player " + (i + 1) + ":");
            String name = scan.nextLine();
            game.addPlayer(name);
        }
    
        game.displayPlayers();
    }
    //Co-pilot from GitHub made me realize that in order for displayRules to work across files it needed to be Public, this makes sense to me but wondering if there is a better standard practice
    // previously was: private static void displayRules() throws FileNotFoundException {
    public static void displayRules() throws FileNotFoundException {
        File rulesFile = new File("yahtzee_rules.txt");
        Scanner fileScan = new Scanner(rulesFile);

        System.out.println("==== YAHTZEE RULES ====");
        while (fileScan.hasNextLine()) {
            System.out.println(fileScan.nextLine());
        }
        fileScan.close();
    }
}
