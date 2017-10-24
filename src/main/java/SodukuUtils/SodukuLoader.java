package main.java.SodukuUtils;

// Update in AppController::CreatePlayfield then adding/removing saves

public class SodukuLoader {
    public static int[][] loadSoduku(String boardname) {
        int[][] board = new int[9][9];

        switch(boardname) {
            case "easy_1":
                board[0] = new int[]{3, 0, 6, 5, 0, 9, 1, 0, 0};
                board[1] = new int[]{0, 0, 0, 1, 0, 3, 2, 7, 6};
                board[2] = new int[]{2, 1, 0, 0, 0, 8, 0, 0, 0};
                board[3] = new int[]{0, 0, 0, 0, 0, 7, 0, 9, 5};
                board[4] = new int[]{0, 9, 0, 2, 0, 4, 0, 6, 0};
                board[5] = new int[]{1, 5, 0, 9, 0, 0, 0, 0, 0};
                board[6] = new int[]{0, 0, 0, 4, 0, 0, 0, 8, 7};
                board[7] = new int[]{4, 2, 7, 6, 0, 5, 0, 0, 0};
                board[8] = new int[]{0, 0, 9, 3, 0, 1, 4, 0, 2};
                break;

            case "vhard_1":
                board[0] = new int[]{0, 7, 0, 2, 8, 0, 0, 0, 0};
                board[1] = new int[]{0, 6, 1, 0, 0, 5, 0, 0, 0};
                board[2] = new int[]{0, 0, 8, 0, 7, 4, 0, 0, 0};
                board[3] = new int[]{6, 0, 0, 0, 5, 0, 0, 9, 0};
                board[4] = new int[]{2, 0, 0, 0, 0, 0, 0, 0, 4};
                board[5] = new int[]{0, 9, 0, 0, 2, 0, 0, 0, 8};
                board[6] = new int[]{0, 0, 0, 5, 4, 0, 9, 0, 0};
                board[7] = new int[]{0, 0, 0, 8, 0, 0, 3, 5, 0};
                board[8] = new int[]{0, 0, 0, 0, 9, 2, 0, 8, 0};
                break;

            case "vhard_2":
                board[0] = new int[]{6, 0, 0, 0, 0, 0, 4, 0, 7};
                board[1] = new int[]{0, 9, 0, 0, 0, 6, 5, 0, 0};
                board[2] = new int[]{0, 2, 0, 9, 5, 0, 0, 6, 0};
                board[3] = new int[]{0, 0, 6, 0, 0, 5, 0, 0, 0};
                board[4] = new int[]{0, 0, 0, 4, 0, 3, 0, 0, 0};
                board[5] = new int[]{0, 0, 0, 1, 0, 0, 9, 0, 0};
                board[6] = new int[]{0, 4, 0, 0, 1, 2, 0, 5, 0};
                board[7] = new int[]{0, 0, 2, 5, 0, 0, 0, 7, 0};
                board[8] = new int[]{7, 0, 9, 0, 0, 0, 0, 0, 3};
                break;

            case "\"the most difficult\"":
                board[0] = new int[]{8, 0, 0, 0, 0, 0, 0, 0, 0};
                board[1] = new int[]{0, 0, 3, 6, 0, 0, 0, 0, 0};
                board[2] = new int[]{0, 7, 0, 0, 9, 0, 2, 0, 0};
                board[3] = new int[]{0, 5, 0, 0, 0, 7, 0, 0, 0};
                board[4] = new int[]{0, 0, 0, 0, 4, 5, 7, 0, 0};
                board[5] = new int[]{0, 0, 0, 1, 0, 0, 0, 3, 0};
                board[6] = new int[]{0, 0, 1, 0, 0, 0, 0, 6, 8};
                board[7] = new int[]{0, 0, 8, 5, 0, 0, 0, 1, 0};
                board[8] = new int[]{0, 9, 0, 0, 0, 0, 4, 0, 0};
                break;

            case "No solution":
                board[0] = new int[]{0, 4, 8, 3, 9, 0, 7, 1, 6};
                board[1] = new int[]{7, 7, 1, 6, 0, 8, 3, 4, 9};
                board[2] = new int[]{9, 3, 6, 7, 4, 1, 0, 8, 0};
                board[3] = new int[]{6, 8, 0, 0, 3, 9, 1, 7, 4};
                board[4] = new int[]{3, 0, 9, 1, 7, 4, 6, 0, 8};
                board[5] = new int[]{7, 1, 4, 8, 6, 0, 9, 0, 3};
                board[6] = new int[]{8, 6, 3, 4, 1, 7, 0, 9, 0};
                board[7] = new int[]{1, 9, 0, 0, 8, 6, 4, 3, 7};
                board[8] = new int[]{4, 0, 7, 9, 0, 3, 8, 6, 1};
                break;

            case "Multiple solutions":
                board[0] = new int[]{0, 4, 8, 3, 9, 0, 7, 1, 6};
                board[1] = new int[]{0, 7, 1, 6, 0, 8, 3, 4, 9};
                board[2] = new int[]{9, 3, 6, 7, 4, 1, 0, 8, 0};
                board[3] = new int[]{6, 8, 0, 0, 3, 9, 1, 7, 4};
                board[4] = new int[]{3, 0, 9, 1, 7, 4, 6, 0, 8};
                board[5] = new int[]{7, 1, 4, 8, 6, 0, 9, 0, 3};
                board[6] = new int[]{8, 6, 3, 4, 1, 7, 0, 9, 0};
                board[7] = new int[]{1, 9, 0, 0, 8, 6, 4, 3, 7};
                board[8] = new int[]{4, 0, 7, 9, 0, 3, 8, 6, 1};
        }
        return board;
    }
}
