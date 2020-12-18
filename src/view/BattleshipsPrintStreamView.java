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
    public void print(PrintStream printStream) throws IOException {

        printStream.print("HIER SIEHT MAN BALD EIN BOARD");

    }
}
