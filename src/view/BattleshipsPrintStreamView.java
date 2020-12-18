package view;

import battleships.PlayerRole;
import battleships.Tile;

import java.io.IOException;
import java.io.PrintStream;

public class BattleshipsPrintStreamView implements PrintStreamView {

    private final Tile[][][] board;
    PlayerRole localRole;

    public BattleshipsPrintStreamView(Tile[][][] board, PlayerRole localRole) {

        this.localRole = localRole;
        this.board = board;
    }

    @Override
    public void printOwnBoard(PrintStream pS) throws IOException {

        int player = localRole == PlayerRole.FIRST ? 0 : 1;
        pS.print("\n");
        pS.println("__________");
        pS.print("\n");
        pS.println("My Board: ");
        pS.println("__________");
        pS.println("   0  1  2");

        for (int i = 0; i < 3; i++) {
            pS.print(i + " ");
            for (int j = 0; j < 3; j++) {
                boolean ship = this.board[player][j][i].isShip();
                boolean attacked = this.board[player][j][i].isAttacked();
                if (attacked) {
                    pS.print(" O ");
                } else if (!ship){
                    pS.print(" - ");
                } else {
                    pS.print(" X ");
                }
            }
            pS.print("\n");
        }
        pS.println("__________");
        pS.print("\n");

    }

    @Override
    public void printEnemyBoard(PrintStream pS) throws IOException {

        int enemy = localRole == PlayerRole.FIRST ? 1 : 0;
        pS.print("\n");
        pS.println("__________");
        pS.print("\n");
        pS.println("Enemy board: ");
        pS.println("__________");
        pS.println("   0  1  2");

        for (int i = 0; i < 3; i++) {
            pS.print(i + " ");
            for (int j = 0; j < 3; j++) {
                boolean ship = this.board[enemy][j][i].isShip();
                boolean attacked = this.board[enemy][j][i].isAttacked();
                if (!attacked) {
                    pS.print(" - ");
                } else if (!ship) {
                    pS.print(" O ");
                } else {
                    pS.print(" X ");
                }
            }
            pS.print("\n");
        }
        pS.println("__________");
        pS.print("\n");

    }
}
