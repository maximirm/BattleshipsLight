package battleships;

import exceptions.GameException;
import exceptions.StatusException;

public class BattleshipsImpl implements Battleships {

    private static final String WRONG_GAME_STATUS = "the method called in wrong game Status";
    private static final String COORDS_OUTSIDE = "the coordinates are outside the board";
    private static final String SHIP_ALREADY = "there is already a ship";
    private static final String PLACED_ALL_SHIPS = "u placed all your ships already - please wait for second player";
    private static final String NOT_YOUR_TURN = "its not your turn - please wait";
    private static final String ALREADY_ATTACKED = "you already attacked there";
    private Status status = Status.SET;
    Tile[][][] board = buildBoard();
    private int[] playerHealth = {0, 0};
    private PlayerRole localRole;


    private final String localPlayerName;
    private boolean firstDonePlacing;
    private boolean secondDonePlacing;
    public boolean firstPlayerDead;
    public boolean secondPlayerDead;

    public BattleshipsImpl(String localPlayerName) {

        this.localPlayerName = localPlayerName;

    }

    @Override
    public boolean setShip(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException, NullPointerException {

        //check status
        if (this.status != Status.SET) {
            throw new StatusException(WRONG_GAME_STATUS);
        }
        //check shipCounter
        if (pR == PlayerRole.FIRST && firstDonePlacing || pR == PlayerRole.SECOND && secondDonePlacing) {
            throw new GameException(PLACED_ALL_SHIPS);
        }
        //check coords
        checkCoords(xCoord, yCoord);
        //check player
        int player = (pR == PlayerRole.FIRST) ? 0 : 1;
        //check ship
        if (this.board[player][xCoord][yCoord].isShip()) {
            throw new GameException(SHIP_ALREADY);
        }
        //place ship
        this.board[player][xCoord][yCoord].setShip(true);
        //increase shipCounter;
        playerHealth[player]++;
        //check shipCounter for status change
        if (playerHealth[player] == 3) {
            switch (pR) {
                case FIRST -> firstDonePlacing = true;
                case SECOND -> secondDonePlacing = true;
            }
        }
        if (firstDonePlacing && secondDonePlacing) {
            this.status = Status.ATTACK_FIRST;
        }
        return true;
    }


    @Override
    public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException, NullPointerException {

        //check status
        if (this.status != Status.ATTACK_FIRST && this.status != Status.ATTACK_SECOND) {
            throw new StatusException(WRONG_GAME_STATUS);
        }
        //check player
        if (pR == PlayerRole.FIRST && this.status != Status.ATTACK_FIRST ||
                pR == PlayerRole.SECOND && this.status != Status.ATTACK_SECOND) {
            throw new GameException(NOT_YOUR_TURN);
        }
        //check coords
        checkCoords(xCoord, yCoord);

        int player = (pR == PlayerRole.FIRST) ? 1 : 0;
        //check attackedYet
        if (this.board[player][xCoord][yCoord].isAttacked()) {
            throw new GameException(ALREADY_ATTACKED);
        }
        //check ship
        if (this.board[player][xCoord][yCoord].isShip()) {
            this.board[player][xCoord][yCoord].setAttacked(true);
            this.playerHealth[player]--;
        } else {
            //no ship
            changeStatus();
            return false;
        }
        //check HP
        if (this.playerHealth[player] == 0) {
            switch (pR) {
                case FIRST -> {
                    secondPlayerDead = true;
                    this.status = Status.END;
                }
                case SECOND -> {
                    firstPlayerDead = true;
                    this.status = Status.END;
                }
            }
        }
        changeStatus();
        return true;

    }

    private void changeStatus() {

        switch (this.status) {
            case ATTACK_FIRST -> this.status = Status.ATTACK_SECOND;
            case ATTACK_SECOND -> this.status = Status.ATTACK_FIRST;
        }

    }

    private Tile[][][] buildBoard() {

        Tile[][][] board = new Tile[2][3][3];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    board[i][j][k] = new Tile();
                }
            }
        }
        return board;
    }

    private void checkCoords(int xCoord, int yCoord) {

        if (xCoord >= this.board[0].length || yCoord >= this.board[0].length)
            throw new NullPointerException(COORDS_OUTSIDE);

    }

}
