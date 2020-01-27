
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.*;

/*
 *  The Mexico dice game
 *  See https://en.wikipedia.org/wiki/Mexico_(game)
 *
 */
public class Mexico {

    public static void main(String[] args) {
        new Mexico().program();
    }

    final Random rand = new Random();
    final Scanner sc = new Scanner(in);
    final int maxRolls = 3;      // No player may exceed this
    final int startAmount = 3;   // Money for a player. Select any
    final int mexico = 1000;     // A value greater than any other
    int nNext = 0;               //

    void program() {
        //test();            // <----------------- UNCOMMENT to test
        int pot = 0;         // What the winner will get
        Player[] players;    // The players (array of Player objects)
        Player current;      // Current player for round
        Player leader;       // Player starting the round

        players = getPlayers();
        current = getRandomPlayer(players);
        leader = current;
        int maxRollsLeader = maxRolls;

        out.println("Mexico Game Started");
        statusMsg(players);

        while (players.length > 1) {   // Game over when only one player left

            // ----- In ----------

            String cmd = getPlayerChoice(current);
            if ("r".equals(cmd)) {

                    // --- Process ------
                boolean mayRoll = current.nRolls < maxRollsLeader && current.nRolls < maxRolls;
                if(mayRoll){
                    rollDice(current);
                }
                else{
                    if(leader == current){
                        maxRollsLeader = current.nRolls;
                    }
                    current = next(players, current);
                }
                    // ---- Out --------
                if(mayRoll) {
                    roundMsg(current);
                }
            } else if ("n".equals(cmd)) {
                 // Process
                if(leader == current){
                    maxRollsLeader = current.nRolls;
                }
                current = next(players, current);
            } else {
                out.println("?");
            }

            if (allRolled(nNext, players)) {
                // --- Process -----
                maxRollsLeader = maxRolls;
                int loserIndex = getLoser(players);
                current = players[loserIndex];
                Player[] playersTmp = players;
                boolean noResources = players[loserIndex].amount == 0;
                if (noResources){
                    players = removeLoser(loserIndex,players);
                    current = next(players, current);
                    pot++;
                }
                leader = current;
                clearRoundResults(players);
                nNext = 0;
                // ----- Out --------------------
                out.println("Round done " + playersTmp[loserIndex].name + " lost!");
                if(noResources){
                    out.println(playersTmp[loserIndex].name + " has no resources will leave game");
                }
                out.println("Next to roll is " + current.name);
                statusMsg(players);
            }
        }
        out.println("Game Over, winner is " + players[0].name + ". Will get " + pot + " from pot");
    }


    // ---- Game logic methods --------------

    Player[] removeLoser(int loser, Player[] players){
        int newLength = players.length - 1;

        Player[] playersTmp = new Player[newLength];
        int k = 0;
        for (int i = 0; i < players.length; i++) {
            if(!(players[i].name.equals(players[loser].name))){
                playersTmp[k] = players[i];
                k++;
            }
        }
        players = playersTmp;
        return players;
    }

    void clearRoundResults(Player[] players){
        for (int i = 0; i < players.length; i++) {
            players[i].nRolls = 0;
            players[i].score = 0;   //onödig
            players[i].fstDice = 0; //onödig
            players[i].secDice = 0; //onödig
        }
    }

    int getLoser(Player[] players){
        int loserIndex = 0;
        for (int i = 0; i < players.length; i++) {
            if(players[i].score < players[loserIndex].score){
                loserIndex = i;
            }
        }
        players[loserIndex].amount = players[loserIndex].amount - 1;
        return loserIndex;
    }

    void getScore(Player current){
        if(current.fstDice > current.secDice){
            current.score = current.fstDice * 10 + current.secDice;
        }
        else{
            current.score = current.secDice * 10 + current.fstDice;
        }
    }

    void rollDice(Player current) {
        current.nRolls = current.nRolls + 1;
        current.fstDice = 1 + rand.nextInt(5);
        current.secDice = 1 + rand.nextInt(5);
        getScore(current);
    }

    boolean allRolled(int nNext, Player[] players) {
        return (nNext == players.length);
    }

    Player next(Player[] players, Player current){
        nNext++;
        if(indexOf(players,current) == (players.length-1)){
            return players[0];
        }
        else{
            return (players[indexOf(players,current)+1]);
        }
    }

    int indexOf(Player[] players, Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    Player getRandomPlayer(Player[] players) {
        return players[rand.nextInt(players.length)];
    }


    // ---------- IO methods -----------------------

    Player[] getPlayers() {
        Player[] players = new Player[3];
        Player p1 = new Player("Olle",startAmount);
        Player p2 = new Player("Fia",startAmount);
        Player p3 = new Player("Lisa",startAmount);
        players[0] = p1;
        players[1] = p2;
        players[2] = p3;
        return players;
    }

    void statusMsg(Player[] players) {
        out.print("Status: ");
        for (int i = 0; i < players.length; i++) {
            out.print(players[i].name + " " + players[i].amount + " ");
        }
        out.println();
    }

    void roundMsg(Player current) {
        out.println(current.name + " got " +
                current.fstDice + " and " + current.secDice);
    }

    String getPlayerChoice(Player player) {
        out.print("Player is " + player.name + " > ");
        return sc.nextLine();
    }

    // Possibly useful utility during development
    String toString(Player p){
        return p.name + ", " + p.amount + ", " + p.fstDice + ", "
                + p.secDice + ", " + p.nRolls;
    }

    // Class for a player
    class Player {
        String name;
        int amount;   // Start amount (money)
        int fstDice;  // Result of first dice
        int secDice;  // Result of second dice
        int nRolls;   // Current number of rolls
        int score;

        public Player(String n, int a){
            name = n;
            amount = a;
        }

    }

    /**************************************************
     *  Testing
     *
     *  Test are logical expressions that should
     *  evaluate to true (and then be written out)
     *  No testing of IO methods
     *  Uncomment in program() to run test (only)
     ***************************************************/
    void test() {
        // A few hard coded player to use for test
        // NOTE: Possible to debug tests from here, very efficient!
        //Player[] ps = {new Player(), new Player(), new Player()};
        //ps[0].fstDice = 2;
       // ps[0].secDice = 6;
        //ps[1].fstDice = 6;
        //ps[1].secDice = 5;
        //ps[2].fstDice = 1;
        //ps[2].secDice = 1;

        //out.println(getScore(ps[0]) == 62);
        //out.println(getScore(ps[1]) == 65);
        //out.println(next(ps, ps[0]) == ps[1]);
        //out.println(getLoser(ps) == ps[0]);

        //exit(0);
    }


}
