import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class Main {
    static int score = 0;
    static final int SEARCH_DEPTH = 3; 
    public static void main(String[] args) {
        int[][] board = new int[4][4];
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        addRandomTile(board, random);
        addRandomTile(board, random);

        boolean victoryAnnounced = false;

        System.out.println("W = gore, A = levo, S = dole, D = desno");
        System.out.println("H = hint, P = do 10 automatskih poteza");
        System.out.println("N = nova igra, Q = izlaz");

        while (true) {
            System.out.println("Poeni: " + score);
            printBoard(board);

            if(!victoryAnnounced && hasWon(board)) {
                System.out.println("Bravo! Napravila si 2048! Mozes da nastavis igru.");
                victoryAnnounced = true;
            }

            if(!canMove(board)) {
                System.out.println("Kraj igre! Unesi N za novu igru ili Q za izlaz.");
            }

            System.out.print("Unesi potez i pritisni Enter: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("q")) {
                break;
            }

            if(input.equals("n")) {
                board = new int[4][4];
                score = 0;
                victoryAnnounced = false;

                addRandomTile(board, random);
                addRandomTile(board, random);

                System.out.println("\nPocela je nova igra!");
                continue;
            }

            if(input.equals("h")) {
                String bestMove = findBestMove(board);

                if(bestMove == null) {
                    System.out.println("Nema mogucih poteza.");
                } else {
                    System.out.println("Predlog: " + directionName(bestMove));
                }

                continue;
            }

            if(input.equals("p")) {
                autoPlay(board, random, 10);
                continue;
            }

            if(input.equals("w") || input.equals("a") || input.equals("s") || input.equals("d")) {
                boolean changed = playMove(board, input, random);

                if(!changed) {
                    System.out.println("Taj potez ne menja tablu.");
                }
            } else {
                System.out.println("Nepoznata komanda.");
            }
        }

        scanner.close();
        System.out.println("Igra je zatvorena.");
    }

    public static void printBoard(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                System.out.print(board[row][column] + "\t");
            }
            System.out.println();
        }
    }

    public static void moveLeft(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            board[row] = mergeRowLeft(board[row]);
        }
    }
    
    public static int[] mergeRowLeft(int[] row) {
        int[] compact = new int[row.length];
        int count = 0;

        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0) {
                compact[count] = row[i];
                count++;
            }
        }

        int[] result = new int[row.length];
        int position = 0;

        for (int i = 0; i < count; i++) {
            if (i + 1 < count && compact[i] == compact[i + 1]) {
                result[position] = compact[i] * 2;
                score += result[position];
                i++; 
            } else {
                result[position] = compact[i];
            }

            position++;
        }

        return result;
    }

    public static int[] reverseRow(int[] row) {
        int[] reversed = new int[row.length];

        for (int i = 0; i < row.length; i++) {
            reversed[i] = row[row.length - 1 - i];
        }
        return reversed;
    }

    public static void moveRight(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            int[] reversed = reverseRow(board[row]);
            int[] merged = mergeRowLeft(reversed);
            board[row] = reverseRow(merged);
        }
    }

    public static void moveUp(int[][] board) {
        for (int column = 0; column < board[0].length; column++) {
            int[] values = new int[board.length];

            for (int row = 0; row < board.length; row++) {
                values[row] = board[row][column];
            }

            int[] merged = mergeRowLeft(values);

            for (int row = 0; row < board.length; row++) {
                board[row][column] = merged[row];
            }
        }
    }

    public static void moveDown(int[][] board) {
        for (int column = 0; column < board[0].length; column++) {
            int[] values = new int[board.length];

            for (int row = 0; row < board.length; row++) {
                values[row] = board[row][column];
            }

            int[] reversed = reverseRow(values);
            int[] merged = mergeRowLeft(reversed);
            int[] result = reverseRow(merged);

            for (int row = 0; row < board.length; row++) {
                board[row][column] = result[row];
            }
        }
    }

    public static void addRandomTile(int[][] board, Random random) {
        int emptyCount = 0;

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                if (board[row][column] == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) {
            return;
        }

        int selected = random.nextInt(emptyCount);
        int index = 0;

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                if (board[row][column] == 0) {
                    if (index == selected) {
                        int value = 2;

                        if (random.nextInt(10) == 0) {
                            value = 4;
                        }

                        board[row][column] = value;
                        return;
                    }

                    index++;
                }
            }
        }
    }

    public static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][];

        for (int row = 0; row < board.length; row++) {
            copy[row] = board[row].clone();
        }

        return copy;
    }

    public static boolean canMove(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {

                if (board[row][column] == 0) {
                    return true;
                }

                if (column + 1 < board[row].length && board[row][column] == board[row][column + 1]) {
                    return true;
                }

                if (row + 1 < board.length && board[row][column] == board[row + 1][column]) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasWon(int[][] board) {
        for(int row = 0; row < board.length; row++) {
            for(int column = 0; column < board[row].length; column++) {
                if(board[row][column] == 2048) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int evaluateBoard(int[][] board) {
        int emptyCount = 0;

        for(int row = 0; row < board.length; row++) {
            for(int column = 0; column < board[row].length; column++) {
                if(board[row][column] == 0) {
                    emptyCount++;
                }
            }
        }

        return emptyCount;
    }

    public static void applyMove(int[][] board, String direction) {
        switch (direction) {
            case "w":
                moveUp(board);
                break;
            case "a":
                moveLeft(board);
                break;
            case "s":
                moveDown(board);
                break;
            case "d":
                moveRight(board);
                break;
        }
    }

    public static String findBestMove(int[][] board) {
        String[] directions = {"w", "a", "s", "d"};

        String bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for(String direction : directions) {
            int[][] simulatedBoard = simulateMove(board, direction);

            if(Arrays.deepEquals(board, simulatedBoard)) {
                continue;
            }

            double value = expectedSpawnValue(simulatedBoard, SEARCH_DEPTH - 1);

            if(value > bestValue) {
                bestValue = value;
                bestMove = direction;
            }
        }

        return bestMove;
    }

    public static boolean playMove(int[][] board, String direction, Random random) {
        int[][] before = copyBoard(board);

        applyMove(board, direction);

        if(Arrays.deepEquals(before, board)) {
            return false;
        }

        addRandomTile(board, random);
        return true;
    }

    public static String directionName(String direction) {
        switch(direction) {
            case "w":
                return "gore";
            case "a":
                return "levo";
            case "s":
                return "dole";
            case "d":
                return "desno";
            default:
                return "nepoznat smer";
        }
    }

    public static void autoPlay(int[][] board, Random random, int maxMoves) {
        
        for(int step = 0; step < maxMoves; step++) {
            String direction = findBestMove(board);

            if(direction == null) {
                System.out.println("Nema vise mogucih poteza.");
                return;
            }

            playMove(board, direction, random);

            System.out.println("\nAuto potez " + (step + 1) + ": " + directionName(direction));

            System.out.println("Poeni: " + score);
            printBoard(board);
        }
    }

    public static int[][] simulateMove(int[][] board, String direction) {
        int[][] simulatedBoard = copyBoard(board);
        int savedScore = score;

        try {
            applyMove(simulatedBoard, direction);
        } finally {
            score = savedScore;
        }

        return simulatedBoard;
    }

    public static double bestFutureValue(int[][] board, int depth) {
        if(!canMove(board)) {
            return -1000.0;
        }

        if(depth == 0) {
            return evaluateBoard(board);
        }

        String[] directions = {"w", "a", "s", "d"};
        double bestValue = Double.NEGATIVE_INFINITY;

        for(String direction : directions) {
            int[][] simulatedBoard = simulateMove(board, direction);

            if(Arrays.deepEquals(board, simulatedBoard)) {
                continue;
            }

            double value = expectedSpawnValue(simulatedBoard, depth - 1);

            if(value > bestValue) {
                bestValue = value;
            }
        }

        return bestValue;
    }

    public static double expectedSpawnValue(int[][] board, int depth) {
        if(depth == 0) {
            return evaluateBoard(board);
        }

        int emptyCount = 0;
    
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                if (board[row][column] == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) {
            return bestFutureValue(board, depth);
        }
         
        double totalValue = 0.0;

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                if (board[row][column] != 0) {
                    continue;
                }

                int[][] withTwo = copyBoard(board);
                withTwo[row][column] = 2;

                double valueWithTwo = bestFutureValue(withTwo, depth);

                int[][] withFour = copyBoard(board);
                withFour[row][column] = 4;

                double valueWithFour = bestFutureValue(withFour, depth);

                totalValue += 0.9 * valueWithTwo + 0.1 * valueWithFour;
            }
        }

        return totalValue / emptyCount;
    }
}