package view;

import battleships.PlayerRole;
import battleships.Tile;

import java.io.IOException;
import java.io.PrintStream;

public class BattleshipsPrintStreamView implements PrintStreamView{

    private final Tile[][][] board;
    PlayerRole localRole;

    public BattleshipsPrintStreamView(Tile[][][] board, PlayerRole localRole) {

        this.localRole = localRole;
        this.board = board;
    }

    @Override
    public void print(PrintStream pS) throws IOException {


        int player = localRole == PlayerRole.FIRST ? 0 : 1;

        pS.println("   0  1  2");

        for (int i = 0; i < 3; i++) {
            pS.print(i + " ");
            for (int j = 0; j < 3; j++) {
                boolean ship = this.board[player][j][i].isShip();
                if (!ship) {
                    pS.print(" - ");
                } else {
                    pS.print(" X ");
                }
            }
        pS.print("\n");
        }
    }

}
