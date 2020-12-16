package battleships;

import exceptions.GameException;
import exceptions.StatusException;
import network.ProtocolEngine;

import java.io.*;


public class BattleshipsProtocolEngine implements Battleships, ProtocolEngine, Runnable {

    private static final int METHOD_ID_SET = 0;
    private static final int METHOD_ID_ATTACK = 1;
    private static final int ROLE_ID_FIRST = 0;
    private static final int ROLE_ID_SECOND = 1;

    private static final String SERIALIZE_FAIL = "couldn't serialize command";
    private static final String DESERIALIZE_FAIL = "couldn't deserialize command";
    private static final String DEFAULT_NAME = "testName";

    private OutputStream os;
    private InputStream is;
    private final Battleships gameEngine;

    private Thread protocolThread = null;
    private String name;

    public BattleshipsProtocolEngine(InputStream is, OutputStream os, Battleships gameEngine) {


        this.gameEngine = gameEngine;
        this.is = is;
        this.os = os;

    }


    public BattleshipsProtocolEngine(Battleships gameEngine, String name){
        this.gameEngine = gameEngine;
        this.name = name;
    }
    public BattleshipsProtocolEngine(Battleships gameEngine){
        this.gameEngine = gameEngine;
        this.name = DEFAULT_NAME;
    }


    @Override
    public boolean setShip(PlayerRole pR, int xCoord, int yCoord) throws GameException, NullPointerException {

        DataOutputStream dos = new DataOutputStream(this.os);
        serialize(pR, xCoord, yCoord, METHOD_ID_SET, dos);
        return true;
    }

    @Override
    public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws GameException, NullPointerException {

        DataOutputStream dos = new DataOutputStream(this.os);
        serialize(pR, xCoord, yCoord, METHOD_ID_ATTACK, dos);
        return true;
    }

    private void deserializeSet() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        deserialize(METHOD_ID_SET, dis);

    }

    private void deserializeAttack() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        deserialize(METHOD_ID_ATTACK, dis);

    }


    public void read() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        try {
            int methodID = dis.readInt();
            switch (methodID) {
                case METHOD_ID_SET -> this.deserializeSet();
                case METHOD_ID_ATTACK -> this.deserializeAttack();
            }

        } catch (IOException e) {
            throw new GameException(DESERIALIZE_FAIL, e);
        }
    }


    private void deserialize(int methodID, DataInputStream dis) throws GameException {

        PlayerRole pR = null;
        try {
            //read Role
            switch (dis.readInt()) {
                case ROLE_ID_FIRST -> pR = PlayerRole.FIRST;
                case ROLE_ID_SECOND -> pR = PlayerRole.SECOND;
            }
            //read xCoord
            int xCoord = dis.readInt();
            //read yCoord
            int yCoord = dis.readInt();
            switch (methodID) {
                case METHOD_ID_SET -> this.gameEngine.setShip(pR, xCoord, yCoord);
                case METHOD_ID_ATTACK -> this.gameEngine.attack(pR, xCoord, yCoord);
            }

        } catch (IOException | StatusException | GameException e) {
            throw new GameException(DESERIALIZE_FAIL, e);
        }

    }


    private void serialize(PlayerRole pR, int xCoord, int yCoord, int methodID, DataOutputStream dos) throws GameException {

        try {
            //write methodID
            dos.writeInt(methodID);
            //write pR
            switch (pR) {
                case FIRST -> dos.writeInt(ROLE_ID_FIRST);
                case SECOND -> dos.writeInt(ROLE_ID_SECOND);
            }
            //write xCoord
            dos.writeInt(xCoord);
            //write yCoord
            dos.writeInt(yCoord);
        } catch (IOException e) {
            throw new GameException(SERIALIZE_FAIL, e);
        }
    }

    @Override
    public void handleConnection(InputStream is, OutputStream os) throws IOException {
        this.is = is;
        this.os = os;
        this.protocolThread = new Thread(this);
        this.protocolThread.start();

    }

//    @Override
//    public void close() throws IOException {
//
//        //TODO: implement
//
//    }
//
//    @Override
//    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {
//
//        //TODO: implement
//
//    }
//
//    @Override
//    public void unsubscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {
//
//        //TODO: implement
//
//    }

    @Override
    public void run() {

        System.out.println("protocol engine started");
        try{
            while(true){
                this.read();
            }
        }catch(GameException e) {
            System.err.println("exception called in protocol engine - stop");
            e.printStackTrace();
        }
    }
}
