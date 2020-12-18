package ui;

import battleships.*;
import network.GameSessionEstablishedListener;
import network.TCPStream;
import network.TCPStreamCreatedListener;

import java.io.*;
import java.util.LinkedList;

public class BattleshipsUI implements LocalBoardChangeListener, GameSessionEstablishedListener, TCPStreamCreatedListener {



    private static final String OPEN_NEW_GAME = "Open a new Game";
    private static final String WAIT_FOR_OTHER_PLAYER_TO_CONNECT = "Opened a new game - wait until other player is connected";
    private static final String CONNECTED_TO_A_GAME = "Connect to a game";
    private static final String CONNECTED = "Connected";
    private static final String ENTER_HOST_NAME = "Enter the hostname";
    private static final String YOU_WON = "you won";
    private static final String YOU_LOST = "you lost";
    private static final String ERROR = "unexpected error";
    private static final String GAME_SESSION_CREATED = "unexpected error";
    private static final String YOUR_TURN = "its your turn";
    private static final String WAIT_FOR_OTHER_PLAYER = "its not your turn. please wait";
    private static final String EXIT = "leave the game";
    private static final String EXIT_MESSAGE = "Game closed";
    private String playerName;
    private String partnerName;
    private final BattleshipsImpl gameEngine;
    BattleShipsLocalBoard localBoard;
    private TCPStream tcpStream;
    private BattleshipsProtocolEngine protocolEngine;

    public BattleshipsUI(String playerName){
        this.playerName = playerName;
        this.gameEngine = new BattleshipsImpl(playerName);
        this.localBoard = this.gameEngine;
        this.localBoard.subscribeChangeListener(this);

    }

    public LinkedList<Command> returnCommandList() {
        LinkedList<Command> list = new LinkedList<>();
        list.add(createExitCommand());
        list.add(createOpenCommand());
        list.add(createConnectCommand());
        list.add(createSetCommand());
        list.add(createAttackCommand());
        return list;

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
                return OPEN_NEW_GAME;
            }
        };

    }

    private Command createConnectCommand() {
        return new Command() {
            @Override
            public String execute() {

                doConnect();
                return CONNECTED_TO_A_GAME;
            }

            @Override
            public String description() {

                return CONNECTED;
            }
        };

    }

    private Command createSetCommand() {

    }

    private Command createAttackCommand() {

    }

    private Command createExitCommand() {
        return new Command() {
            @Override
            public String execute() {

                try {
                    doExit();
                } catch (IOException e) {
                    System.out.println(ERROR);
                }
                return EXIT_MESSAGE;
            }

            @Override
            public String description() {

                return EXIT;
            }
        };

    }

    @Override
    public void changed() {

        try{
            this.doPrint();
        } catch(IOException e){
            System.err.println(ERROR);
        }

    }

    public void doExit() throws IOException {
        this.protocolEngine.close();

    }

    private void doPrint() throws IOException {
        this.gameEngine.getPrintStreamView().print(System.out);

        if(this.gameEngine.getStatus() == Status.END){
            if (this.gameEngine.hasWon()){
                System.out.println(YOU_WON);
            } else {
                System.out.println(YOU_LOST);
            }
        } else if (this.gameEngine.isActive()){
            System.out.println(YOUR_TURN);
        } else {
            System.out.println(WAIT_FOR_OTHER_PLAYER);
        }


    }

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {

        System.out.println(GAME_SESSION_CREATED);
        this.partnerName = partnerName;
        if(oracle){
            System.out.println(YOUR_TURN);
        } else {
            System.out.println(WAIT_FOR_OTHER_PLAYER);
        }

    }

    @Override
    public void streamCreated(TCPStream stream) {

        this.protocolEngine = new BattleshipsProtocolEngine(this.gameEngine, this.playerName);
        this.gameEngine.setProtocolEngine(protocolEngine);
        this.protocolEngine.subscribeGameSessionEstablishedListener(this);

        try{
            protocolEngine.handleConnection(stream.getInputStream(), stream.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void doOpen(){
        if (this.alreadyConnected()) return;
        this.tcpStream = new TCPStream(Battleships.DEFAULT_PORT, true, this.playerName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doConnect(){
        if (this.alreadyConnected()) return;
        String hostName = Console.readString(ENTER_HOST_NAME);
        this.tcpStream = new TCPStream(Battleships.DEFAULT_PORT, false, this.playerName);
        this.tcpStream.setRemoteEngine(hostName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();

    }

    private boolean alreadyConnected(){

        return this.tcpStream != null;
    }
}
