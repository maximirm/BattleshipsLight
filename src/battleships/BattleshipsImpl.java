package battleships;

import exceptions.GameException;
import exceptions.StatusException;
import network.GameSessionEstablishedListener;

import java.util.ArrayList;
import java.util.List;

public class BattleshipsImpl implements Battleships, BattleShipsLocalBoard, GameSessionEstablishedListener {

    private static final String WRONG_GAME_STATUS = "the method called in wrong game Status";
    private static final String COORDS_OUTSIDE = "the coordinates are outside the board";
    private static final String SHIP_ALREADY = "there is already a ship";
    private static final String PLACED_ALL_SHIPS = "u placed all your ships already - please wait for second player";
    private static final String NOT_YOUR_TURN = "its not your turn - please wait";
    private static final String ALREADY_ATTACKED = "you already attacked there";
    private static final String GAME_SESSION_ESTABLISHED = " game session established " ;

    private static Status status;
    Tile[][][] board = buildBoard();
    private static int[] playerHealth = {0, 0};

    private PlayerRole localRole;
    private PlayerRole remoteRole;
    private final String localPlayerName;
    private String remotePlayerName;

    private List<LocalBoardChangeListener> boardChangeListenerList = new ArrayList<>();



    private static boolean firstDonePlacing;
    private static boolean secondDonePlacing;
    public boolean firstPlayerDead;
    public boolean secondPlayerDead;
    private BattleshipsProtocolEngine protocolEngine;

    public BattleshipsImpl(String localPlayerName) {

        this.localPlayerName = localPlayerName;

    }

    @Override
    public boolean setShip(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException, NullPointerException {

        //check status
        if (status != Status.SET) {
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
            status = Status.ATTACK_FIRST;
        }
        return true;
    }


    @Override
    public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException, NullPointerException {

        //check status
        System.out.println("1");
        if (status != Status.ATTACK_FIRST && status != Status.ATTACK_SECOND) {
            throw new StatusException(WRONG_GAME_STATUS);
        }
        //check player
        System.out.println("2");

        if (pR == PlayerRole.FIRST && status != Status.ATTACK_FIRST ||
                pR == PlayerRole.SECOND && status != Status.ATTACK_SECOND) {
            throw new GameException(NOT_YOUR_TURN);
        }
        System.out.println("3");

        //check coords
        checkCoords(xCoord, yCoord);

        int player = (pR == PlayerRole.FIRST) ? 1 : 0;
        //check attackedYet
        if (this.board[player][xCoord][yCoord].isAttacked()) {
            throw new GameException(ALREADY_ATTACKED);
        }
        //check ship
        System.out.println("4");
        if (this.board[player][xCoord][yCoord].isShip()) {
            this.board[player][xCoord][yCoord].setAttacked(true);
            playerHealth[player]--;
        } else {
            //no ship
            changeStatus();
        System.out.println("5");
            return false;
        }

        //check HP
        if (playerHealth[player] == 0) {
            switch (pR) {
                case FIRST -> {
                    secondPlayerDead = true;
                    status = Status.END;
                }
                case SECOND -> {
                    firstPlayerDead = true;
                    status = Status.END;
                }
            }
        }
        changeStatus();
        return true;

    }

    private void changeStatus() {

        switch (status) {
            case ATTACK_FIRST -> status = Status.ATTACK_SECOND;
            case ATTACK_SECOND -> status = Status.ATTACK_FIRST;
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

    public void setProtocolEngine(BattleshipsProtocolEngine protocolEngine){
        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }

    @Override
    public PlayerRole getLocalRole() {

        return this.localRole;
    }

    @Override
    public Status getStatus() {
        return status;

    }

    @Override
    public boolean isActive() {

        if (this.localRole == null) return false;
        return(
                (this.getStatus() == Status.ATTACK_FIRST && this.localRole == PlayerRole.FIRST) ||
                        (this.getStatus() == Status.ATTACK_SECOND && this.localRole == PlayerRole.SECOND) ||
                        (this.getStatus() == Status.SET )
                );
    }

    @Override
    public boolean hasWon() {

        return (
                (status == Status.END && this.localRole == PlayerRole.FIRST && secondPlayerDead) ||
                    (status == Status.END && this.localRole == PlayerRole.SECOND && firstPlayerDead));
    }

    @Override
    public boolean hasLost() {
        return !this.hasWon();
    }

    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {

        this.boardChangeListenerList.add(changeListener);

    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {

        System.out.println(this.localPlayerName + GAME_SESSION_ESTABLISHED + partnerName );
        this.localRole = oracle ? PlayerRole.FIRST : PlayerRole.SECOND;
        this.remoteRole = this.localRole == PlayerRole.FIRST ? PlayerRole.SECOND : PlayerRole.FIRST;
        this.remotePlayerName = partnerName;
        status = Status.SET;

    }
}
