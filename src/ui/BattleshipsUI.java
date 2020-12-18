package ui;

import battleships.*;
import exceptions.GameException;
import exceptions.StatusException;
import network.GameSessionEstablishedListener;
import network.TCPStream;
import network.TCPStreamCreatedListener;

import java.io.IOException;
import java.util.LinkedList;

public class BattleshipsUI implements LocalBoardChangeListener, GameSessionEstablishedListener, TCPStreamCreatedListener {


    private static final String OPEN_COMMAND = "Open a new Game";
    private static final String CONNECT_COMMAND = "Connect to a game";
    private static final String EXIT_COMMAND = "leave the game";
    private static final String SET_COMMAND = "Set a ship";
    private static final String ATTACK_COMMAND = "Attack";
    private static final String OWN_BOARD_COMMAND = "Show my Own Board";
    private static final String OWN_BOARD = "X: your ships\n O: where u got attacked";
    private static final String ENTER_X = "enter x-coordinate";
    private static final String ENTER_Y = "enter y-coordinate";
    private static final String WAIT_FOR_OTHER_PLAYER_TO_CONNECT = "Opened a new game - wait until other player is connected";
    private static final String CONNECTED = "Connected";
    private static final String ENTER_HOST_NAME = "Enter the hostname (localhost)";
    private static final String YOU_WON = "you won";
    private static final String YOU_LOST = "you lost";
    private static final String ERROR = "unexpected error";
    private static final String YOUR_TURN = "its your turn";
    private static final String WAIT_FOR_OTHER_PLAYER = "its not your turn. please wait";
    private static final String EXIT_MESSAGE = "Game closed";
    private static final String PLACEMENT_DONE = "You placed all your ships - wait a few secs until opponent has finished";
    private static final String HIT = "great - you hit something";
    private static final String MISS = "too bad - u missed";
    private final BattleshipsImpl gameEngine;
    private final String playerName;
    BattleShipsLocalBoard localBoard;
    private TCPStream tcpStream;
    private BattleshipsProtocolEngine protocolEngine;

    public BattleshipsUI(String playerName) {

        this.playerName = playerName;
        this.gameEngine = new BattleshipsImpl(playerName);
        this.localBoard = this.gameEngine;
        this.localBoard.subscribeChangeListener(this);

    }

    public LinkedList<Command> returnNetworkList() {

        LinkedList<Command> list = new LinkedList<>();
        list.add(createExitCommand());
        list.add(createOpenCommand());
        list.add(createConnectCommand());
        return list;
    }

    public LinkedList<Command> returnSetList() {

        LinkedList<Command> list = new LinkedList<>();
        list.add(createExitCommand());
        list.add(createSetCommand());
        return list;
    }

    public LinkedList<Command> returnAttackList() {

        LinkedList<Command> list = new LinkedList<>();
        list.add(createExitCommand());
        list.add(createAttackCommand());
        list.add(createOwnBoardCommand());
        return list;
    }

    private Command createOwnBoardCommand() {

        return new Command() {
            @Override
            public String execute() {

                try {
                    printOwnBoard();
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                return OWN_BOARD;
            }

            @Override
            public String description() {

                return OWN_BOARD_COMMAND;
            }
        };

    }


    private Command createOpenCommand() {

        return new Command() {
            @Override
            public String execute() {

                doOpen();
                return WAIT_FOR_OTHER_PLAYER_TO_CONNECT;
            }

            @Override
            public String description() {

                return OPEN_COMMAND;
            }
        };

    }

    private Command createConnectCommand() {

        return new Command() {
            @Override
            public String execute() {

                doConnect();
                return CONNECTED;
            }

            @Override
            public String description() {

                return CONNECT_COMMAND;
            }
        };

    }

    private Command createSetCommand() {

        return new Command() {
            @Override
            public String execute() {

                try {
                    if (localBoard.placementDone()) {
                        return PLACEMENT_DONE;
                    }
                    doSet();
                    printOwnBoard();
                } catch (StatusException | GameException | IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                return "\n";
            }

            @Override
            public String description() {

                return SET_COMMAND;
            }
        };

    }

    private Command createAttackCommand() {

        return new Command() {
            @Override
            public String execute() {

                try {
                    if (!localBoard.isActive()) {
                        return WAIT_FOR_OTHER_PLAYER;
                    }
                    boolean result = doAttack();
                    printEnemyBoard();
                    if (result) {
                        return HIT;
                    } else {
                        return MISS;
                    }
                } catch (StatusException | GameException | IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
                return "\n";
            }

            @Override
            public String description() {

                return ATTACK_COMMAND;
            }
        };

    }

    private Command createExitCommand() {

        return new Command() {
            @Override
            public String execute() {

                try {
                    doExit();
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(ERROR);
                }
                return EXIT_MESSAGE;
            }

            @Override
            public String description() {

                return EXIT_COMMAND;
            }
        };

    }

    @Override
    public void changed() {

        try {
            this.doPrint();
        } catch (IOException e) {
            System.err.println(ERROR);
        }

    }

    public boolean doAttack() throws StatusException, GameException {

        int x = Console.readInteger(ENTER_X);
        int y = Console.readInteger(ENTER_Y);
        return localBoard.attack(this.localBoard.getLocalRole(), x, y);

    }

    public void doSet() throws StatusException, GameException {

        int x = Console.readInteger(ENTER_X);
        int y = Console.readInteger(ENTER_Y);
        localBoard.setShip(this.localBoard.getLocalRole(), x, y);

    }

    public void doExit() throws IOException {

        this.protocolEngine.close();

    }

    public void doPrint() throws IOException {

        if (this.gameEngine.getStatus() == Status.END) {
            if (this.gameEngine.hasWon()) {
                System.out.println(YOU_WON);
            } else {
                System.out.println(YOU_LOST);
            }
        } else if (this.gameEngine.isActive()) {
            System.out.println(YOUR_TURN);
        }
    }

    public void printOwnBoard() throws IOException {

        this.gameEngine.getPrintStreamView().printOwnBoard(System.out);

    }

    public void printEnemyBoard() throws IOException {

        this.gameEngine.getPrintStreamView().printEnemyBoard(System.out);

    }


    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
        //not needed
    }

    @Override
    public void streamCreated(TCPStream stream) {

        this.protocolEngine = new BattleshipsProtocolEngine(this.gameEngine, this.playerName);
        this.gameEngine.setProtocolEngine(protocolEngine);
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);

        try {
            protocolEngine.handleConnection(stream.getInputStream(), stream.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void doOpen() {

        if (this.alreadyConnected()) return;
        this.tcpStream = new TCPStream(Battleships.DEFAULT_PORT, true, this.playerName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doConnect() {

        if (this.alreadyConnected()) return;
        String hostName = Console.readString(ENTER_HOST_NAME);
        this.tcpStream = new TCPStream(Battleships.DEFAULT_PORT, false, this.playerName);
        this.tcpStream.setRemoteEngine(hostName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();

    }

    private boolean alreadyConnected() {

        return this.tcpStream != null;
    }

    public boolean gameStarted() {

        return this.localBoard.getLocalRole() != null;

    }

    public Status getStatus() {

        return this.localBoard.getStatus();
    }

}
