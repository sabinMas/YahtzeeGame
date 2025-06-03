
// Mason Sabin
// Basic yahtzee laws from logical perspective
// this is how my brain was working when mapping out how I wanted the game to work logically
// I worked in stages and tested each stage before moving on
//TODO: before final date
// users add players, then enter game = COMPLETE
// within each game there needs to be 13 rounds = COMPLETE
// NEW TODO: add some sort of turn menu, allowing users to choose what they want to do at the start of their turn
// NEW TODO: turn menu should consist of roll dice, view current score, view available categories, and view rules.
// !QUESTION for Everett: Can i add the displayRules method from main to playersTurn within Game using a mainInstance?
// within each round, each player needs a turn of up to 3 rolls = COMPLETE
// each roll a player needs to be able to select dice they would like to keep, and re-roll the rest = COMPLETE
// after 3 rolls or user decides not to re-roll, then the user needs to choose a scoring method = COMPLETE
// each of the 13 scoring methods can only be used once per game per player = COMPLETE
// each scoring method needs to be unique and has its own rules 
// after a scoring method is chosen by a user I should remove it from the list of available scoring methods
// after 13 rounds the game should end and display the final scores for everyone that played 
// the final scores should also be able to take into account bonuses from upper section as well as multiple yahtzees (low priority, if not done then it will be my take on yahtzee)
// scores at the end of the game from each round should be SUMMED and displayed *work on summing the array of scores and each column of the array for each round\
import java.io.*;
import java.util.*;

// this class and object will handle the majority of the game logic  
public class Game {
    private String[] players;
    private int[][] scores; // scores i used a 2d array for so it can be 6 by 13 for each player and each category
    private int playerCount;
    private int[] dice;
    // this mainInstance should allow me to call displayRules from Main.java while
    private Main mainInstance;
    //everett helped me finalize and understand naming my categories within categoryNames array
    private final String[] categoryNames = {
            "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Three of a Kind", "Four of a Kind",
            "Full House", "Small Straight", "Large Straight", "Yahtzee", "Chance"
    };

    // Score values for each category within the lower half of the score sheet
    private final int FULL_HOUSE_SCORE = 25;
    private final int SMALL_STRAIGHT_SCORE = 30;
    private final int LARGE_STRAIGHT_SCORE = 40;
    private final int YAHTZEE_SCORE = 50;

    // this Game constructor will initialize the players and scores arrays
    // main mainInstance is used to call displayRules from Main.java
    public Game(Main mainInstance) {
        players = new String[6];
        scores = new int[6][13]; // Each player has 13 score categories
        playerCount = 0;
        this.dice = new int[5];
        this.mainInstance = mainInstance;
    }
    // this getter method will return the player count if players have been added
    public int getPlayerCount() {
        return playerCount;
    }
    // this method will add players to the game and check if the player count is less than 6 as defined by the array
    public void addPlayer(String name) {
        if (playerCount < players.length) {
            players[playerCount] = name;
            playerCount++;
        } else {
            System.out.println("Cannot add more players. Maximum limit reached.");
        }
    }
    // this method will display the players that have been added to the game 
    public void displayPlayers() {  
        if (playerCount == 0) {
            System.out.println("No players added yet.");
        } else {
            System.out.println("==== PLAYERS ====");
            for (int i = 0; i < playerCount; i++) {
                System.out.println("Player " + (i + 1) + ": " + players[i]);
            }
        }
    }

    // this is the random dice generator seemed pretty simple to to use random.Nextint(6) + 1
    public void rollDice() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            this.dice[i] = random.nextInt(6) + 1;
        }
        displayDice();
    }
    // using arrays.toString to display the dice array seemed very simple and effective 
    private void displayDice() {
        System.out.println("Current Dice: " + Arrays.toString(dice));
    }

    // ability to re-roll dice and store using a boolean array, i looked on stackoverflow for a solution to storing my dice and re-rolling them
    //https://stackoverflow.com/questions/2364856/initializing-a-boolean-array-in-java
    private void reRollDice(boolean[] storedDice) {
        Random rand = new Random();
        System.out.print("Re-rolling dice: ");
        for (int i = 0; i < 5; i++) {
            if (!storedDice[i]) {
                dice[i] = rand.nextInt(6) + 1;
                System.out.print((i + 1) + " ");
            }
        }
        System.out.println();
        displayDice();
    }

    // here is where I will calculate and handle logic for scoring each category the
    // upper section scoring will be handled in one simple if statement
    private int calculateScore(int category) {
        int score = 0;

        // upper section scoring all the same 1-6's or aces through sixes
        if (category >= 0 && category <= 5) {
            int targetNumber = category + 1;
            for (int die : dice) {
                if (die == targetNumber) {
                    score += die;
                }
            }
        }
        // three of a kind and four of a kind both have same conditions, sum of all dice if 3 or 4 dice match
        else if (category == 6 || category == 7) {
            int[] counts = new int[7]; // index 1-6 should correspond to the dice values
            for (int die : dice)
                counts[die]++;
            for (int i = 1; i <= 6; i++) {
                if (counts[i] >= (category == 6 ? 3 : 4)) {
                    score = Arrays.stream(dice).sum(); // using arrays.stream allows to sum all the dice in the array
                    break;
                }
            }
        }
        // full house will be worth 25 points and need 3 of a kind and a pair in order to be scored
        else if (category == 8) {
            int[] counts = new int[7];
            for (int die : dice)
                counts[die]++;
            boolean hasThree = false;
            boolean hasTwo = false;
            for (int i = 1; i <= 6; i++) {
                // booleans for the 3 and 2 matching dice checking the array specifically
                if (counts[i] == 3)
                    hasThree = true;
                if (counts[i] == 2)
                    hasTwo = true;
            }
            if (hasThree && hasTwo)
                score = FULL_HOUSE_SCORE;
        }
        // small straight is 30 points and can only be 3 different possibilites 1-4, 2-5,3-6
        // i looked into using HashSet and using Arrays.asList to check for the 3 different possibilities
        // https://www.w3schools.com/java/java_hashset.asp
        else if (category == 9) {
            Set<Integer> diceSet = new HashSet<>();
            for (int die : dice)
                diceSet.add(die);
            if (diceSet.containsAll(Arrays.asList(1, 2, 3, 4)) ||
                    diceSet.containsAll(Arrays.asList(2, 3, 4, 5)) ||
                    diceSet.containsAll(Arrays.asList(3, 4, 5, 6))) {
                score = SMALL_STRAIGHT_SCORE;
            }
        }
        // Large straights only have 2 possible options 1-5 or 2-6 and worth 40 points
        else if (category == 10) {
            Set<Integer> diceSet = new HashSet<>();
            for (int die : dice)
                diceSet.add(die);
            if (diceSet.containsAll(Arrays.asList(1, 2, 3, 4, 5)) ||
                    diceSet.containsAll(Arrays.asList(2, 3, 4, 5, 6))) {
                score = LARGE_STRAIGHT_SCORE;
            }
        }
        // yahtzee if all 5 dice math and worth 50 points
        else if (category == 11) {
            if (Arrays.stream(dice).distinct().count() == 1) {
                score = YAHTZEE_SCORE;
            }
        }
        // chance is the sum of all dice and used when no other category works
        else if (category == 12) {
            score = Arrays.stream(dice).sum();
        }
        return score;

    }
    // this will allow users to see what categories they can still score in
    private void displayRemainingCategories(int playerIndex) {
        System.out.println("=== Available Scoring Categories ===");
        for (int i = 0; i < categoryNames.length; i++) {
            if (scores[playerIndex][i] == 0) {
                System.out.println((i + 1) + ". " + categoryNames[i]);
            }
        }
    }
    // this method will display the current score for each player and the categories they have scored in
    private void displayCurrentScore(int playerIndex) {
        int totalScore = 0; 
        System.out.println("=== Current Score for " + players[playerIndex] + " ===");
        for (int i = 0; i < categoryNames.length; i++) {
            System.out.println(categoryNames[i] + ": "
                    + (scores[playerIndex][i] == 0 ? "You haven't scored yet" : scores[playerIndex][i]));
            totalScore += scores[playerIndex][i];
        }
        System.out.println("Total Score: " + totalScore);
    }

    private void playersTurn(int playerIndex, Scanner scan) {
        boolean turnActive = true; // checks to make sure the player gets a turn rolling dice and playing
        boolean hasRolled = false; // this makes sure the player has rolled dice during their turn

        while (turnActive) {
            System.out.println("\n=== " + players[playerIndex] + "'s Turn ===");
            System.out.println("Choose one of the following:");
            System.out.println("1. Roll Dice");
            System.out.println("2. View Current Score");
            System.out.println("3. View Available Categories");
            System.out.println("4. View Rules");
            System.out.println("5. End Turn");

            int choice = scan.nextInt();
            scan.nextLine();

            if (choice == 1) {
                rollDice();
                takeTurn(playerIndex, scan);
                chooseScoringCategory(playerIndex, scan);
                turnActive = false;
                hasRolled = true;
            } else if (choice == 2) {
                displayCurrentScore(playerIndex);
                System.out.println("Press Enter to return to your turn menu...");
                scan.nextLine();
            } else if (choice == 3) {
                displayRemainingCategories(playerIndex);
                System.out.println("Press Enter to return to your turn menu...");
                scan.nextLine();
            } else if (choice == 4) {
                try {
                    mainInstance.displayRules();
                } catch (FileNotFoundException e) {
                    System.out.println("Rules file not found.");
                }
                System.out.println("Press Enter to return to your turn menu...");
                scan.nextLine();
            } else if (choice == 5) {
                if (!hasRolled) {
                    System.out.println("You must roll the dice at least once before ending your turn.");
                } else {
                    turnActive = false;
                }
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Method for choosing a scoring category after rolling the dice
    // In testing I found i needed to refine my scoring method to handle 0's because
    // many games players have to score 0 in random categories
    private void chooseScoringCategory(int playerIndex, Scanner scan) {
        System.out.println("Choose a scoring category:");
        displayRemainingCategories(playerIndex);
        boolean validChoice = false;
        int category = -1;
        // this checks to make sure the player selects a valid category that has not
        // been used yet
        while (!validChoice) {
            category = scan.nextInt() - 1;
            scan.nextLine();
            if (category >= 0 && category < categoryNames.length && scores[playerIndex][category] == 0) {
                validChoice = true;
            } else {
                System.out.println("Invalid choice or category already used. Choose again:");
            }
        }
        int score = calculateScore(category);
        // If no conditions are met in calculateScore, explicitly assign 0
        if (score == 0) {
            System.out.println("No valid scoring condition met. Assigning 0 to " + categoryNames[category] + ".");
        }
        scores[playerIndex][category] = score;
        System.out.println("Score of " + score + " recorded in " + categoryNames[category] + ".");
    }

    private void takeTurn(int playerIndex, Scanner scan) {
        for (int reRoll = 1; reRoll < 3; reRoll++) { // here three rolls per person using 1-3 instead of 0-2 
            System.out.println("Do you want to re-roll any dice? (y/n)");
            if (!scan.nextLine().equalsIgnoreCase("y"))
                break;
    
            System.out.println("Enter dice positions to KEEP (1-5, separated by spaces):");
            String[] input = scan.nextLine().split(" ");
            boolean[] storedDice = new boolean[5];
            for (String s : input) {
                try {
                    int dieIndex = Integer.parseInt(s) - 1;
                    if (dieIndex >= 0 && dieIndex < 5) {
                        storedDice[dieIndex] = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numbers only.");
                }
            }
            reRollDice(storedDice);
        }
    }
    

    private void displayFinalScores() {
        System.out.println("\n===== GAME OVER! FINAL SCORES =====");
        // array to store player scores and index, not sure if i needed to make new ones
        // or if i should have called other arrays
        int[][] playerScores = new int[playerCount][2]; // [playerIndex, totalScore]
        for (int i = 0; i < playerCount; i++) {
            int totalScore = Arrays.stream(scores[i]).sum();
            playerScores[i][0] = i; // Store player index
            playerScores[i][1] = totalScore; // Store total score
        }
        // should sort by total score in descending order
        Arrays.sort(playerScores, (a, b) -> Integer.compare(b[1], a[1]));
        // down here the scores will be displayed based on player index and decsending
        // order
        for (int i = 0; i < playerScores.length; i++) {
            int playerIndex = playerScores[i][0];
            int totalScore = playerScores[i][1];
            System.out.println((i + 1) + ". " + players[playerIndex] + " - " + totalScore + " points");
        }
    }

    // main game loop that runs for 13 rounds
    public void playGame(Scanner scan) {
        final int MAX_ROUNDS = 13;
        for (int round = 1; round <= MAX_ROUNDS; round++) {
            System.out.println("===== ROUND " + round + " =====");
            for (int i = 0; i < playerCount; i++) {
                playersTurn(i, scan);
            }
        }
        displayFinalScores();

        System.out.println("Press enter to return to the main menu");
        scan.nextLine();
    }
}
