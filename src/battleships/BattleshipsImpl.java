package battleships;

import exceptions.GameException;
import exceptions.StatusException;
import network.GameSessionEstablishedListener;
import view.BattleshipsPrintStreamView;
import view.PrintStreamView;

import java.util.ArrayList;
import java.util.List;

public class BattleshipsImpl implements Battleships, BattleShipsLocalBoard, GameSessionEstablishedListener {

    private static final String WRONG_GAME_STATUS = "the method called in wrong game Status";
    private static final String COORDS_OUTSIDE = "the coordinates are outside the board";
    private static final String SHIP_ALREADY = "there is already a ship";
    private static final String PLACED_ALL_SHIPS = "u placed all your ships already - please wait for second player";
    private static final String NOT_YOUR_TURN = "its not your turn - please wait";
    private static final String ALREADY_ATTACKED = "you already attacked there";
    private static final String GAME_SESSION_ESTABLISHED = "game session established with: ";

    private Status status = Status.SET;
    private int[] playerHealth = {0, 0};
    private boolean firstDonePlacing;
    private boolean secondDonePlacing;
    private final String localPlayerName;
    public boolean firstPlayerDead;
    public boolean secondPlayerDead;
    Tile[][][] board = buildBoard();
    private PlayerRole localRole;
    private PlayerRole remoteRole;
    private String remotePlayerName;
    private final List<LocalBoardChangeListener> boardChangeListenerList = new ArrayList<>();
    private BattleshipsProtocolEngine protocolEngine;

    @Override
    public boolean setShip(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException {


        //check player
        int player = (pR == PlayerRole.FIRST) ? 0 : 1;
        //local
        if (pR == localRole) {

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

            //check ship
            if (this.board[player][xCoord][yCoord].isShip()) {
                throw new GameException(SHIP_ALREADY);
            }
            //place ship
            this.board[player][xCoord][yCoord].setShip(true);
            //increase shipCounter;
            playerHealth[player]++;

            //check shipCounter for status change


            this.protocolEngine.setShip(pR, xCoord, yCoord);
        }
        //remote
        else {
            //place ship
            this.board[player][xCoord][yCoord].setShip(true);
            this.playerHealth[player]++;

        }
        if (playerHealth[player] == 3) {
            switch (pR) {
                case FIRST -> this.firstDonePlacing = true;
                case SECOND -> this.secondDonePlacing = true;
            }
        }
        if (firstDonePlacing && secondDonePlacing){
            this.status = Status.ATTACK_FIRST;
        }




        return true;
    }

    private void notifyBoardChanged() {

        if(this.boardChangeListenerList.isEmpty()) return;

        (new Thread(new Runnable() {
            @Override
            public void run() {

                for(LocalBoardChangeListener listener : BattleshipsImpl.this.boardChangeListenerList){
                    listener.changed();
                }

            }
        })).start();

    }


    @Override
    public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException {

        int defendingPlayer = (pR == PlayerRole.FIRST) ? 1 : 0;

        if (pR == localRole) {
            //check status
            if (status != Status.ATTACK_FIRST && status != Status.ATTACK_SECOND) {
                throw new StatusException(WRONG_GAME_STATUS);
            }
            if (pR == PlayerRole.FIRST && status != Status.ATTACK_FIRST ||
                    pR == PlayerRole.SECOND && status != Status.ATTACK_SECOND) {
                throw new GameException(NOT_YOUR_TURN);
            }
            //check coords
            checkCoords(xCoord, yCoord);
            //check attackedYet
            if (this.board[defendingPlayer][xCoord][yCoord].isAttacked()) {
                throw new GameException(ALREADY_ATTACKED);
            }
            //check ship
            if (this.board[defendingPlayer][xCoord][yCoord].isShip()) {
                this.board[defendingPlayer][xCoord][yCoord].setAttacked(true);
                playerHealth[defendingPlayer]--;
            } else {
                //no ship
                this.board[defendingPlayer][xCoord][yCoord].setAttacked(true);
                changeStatus();
                this.notifyBoardChanged();
                this.protocolEngine.attack(pR, xCoord, yCoord);
                return false;
            }

            //check HP
            if (playerHealth[defendingPlayer] == 0) {
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
            this.protocolEngine.attack(pR, xCoord, yCoord);

            changeStatus();
            this.notifyBoardChanged();

        } else {
            this.board[defendingPlayer][xCoord][yCoord].setAttacked(true);
            changeStatus();
            this.notifyBoardChanged();

        }


        return true;

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
        return (
                (this.getStatus() == Status.ATTACK_FIRST && this.localRole == PlayerRole.FIRST) ||
                        (this.getStatus() == Status.ATTACK_SECOND && this.localRole == PlayerRole.SECOND) ||
                        (this.getStatus() == Status.SET)
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
    public boolean placementDone() {

        return (localRole == PlayerRole.FIRST && firstDonePlacing) || (localRole == PlayerRole.SECOND && secondDonePlacing);
    }

    @Override
    public void subscribeChangeListener(LocalBoardChangeListener changeListener) {

        this.boardChangeListenerList.add(changeListener);

    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {

        System.out.println(GAME_SESSION_ESTABLISHED + partnerName);
        this.localRole = oracle ? PlayerRole.FIRST : PlayerRole.SECOND;
        this.remoteRole = this.localRole == PlayerRole.FIRST ? PlayerRole.SECOND : PlayerRole.FIRST;
        this.remotePlayerName = partnerName;
        status = Status.SET;

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

    private void checkCoords(int xCoord, int yCoord) throws GameException {

        if (xCoord >= this.board[0].length || yCoord >= this.board[0].length)
            throw new GameException(COORDS_OUTSIDE);

    }

    public void setProtocolEngine(BattleshipsProtocolEngine protocolEngine) {

        this.protocolEngine = protocolEngine;
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);
    }

    public BattleshipsImpl(String localPlayerName) {

        this.localPlayerName = localPlayerName;

    }

    public PrintStreamView getPrintStreamView(){
        return new BattleshipsPrintStreamView(this.board, localRole);
    }


}
