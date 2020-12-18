package battleships;

import exceptions.GameException;
import exceptions.StatusException;
import network.GameSessionEstablishedListener;
import network.ProtocolEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BattleshipsProtocolEngine implements Battleships, ProtocolEngine, Runnable {

    private static final int METHOD_ID_SET = 0;
    private static final int METHOD_ID_ATTACK = 1;
    private static final int ROLE_ID_FIRST = 0;
    private static final int ROLE_ID_SECOND = 1;

    private static final String SERIALIZE_FAIL = "couldn't serialize command ";
    private static final String DESERIALIZE_FAIL = "couldn't deserialize command ";
    private static final String UNKNOWN_METHOD_ID = "unknown method id ";
    private static final String CONNECTION_LOSS = "IOException caught - most probably connection close - stop thread / stop engine ";
    private static final String UNKNOWN_ROLE = "unknown role";

    private final List<GameSessionEstablishedListener> sessionCreatedListenerList = new ArrayList<>();
    private final Battleships gameEngine;
    private final String name;
    private OutputStream os;
    private InputStream is;
    private boolean oracle;
    private String partnerName;


    public BattleshipsProtocolEngine(Battleships gameEngine, String name) {

        this.gameEngine = gameEngine;
        this.name = name;
    }

    @Override
    public void setShip(PlayerRole pR, int xCoord, int yCoord) throws GameException, NullPointerException {

        DataOutputStream dos = new DataOutputStream(this.os);
        serialize(pR, xCoord, yCoord, METHOD_ID_SET, dos);

    }

    @Override
    public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws GameException, NullPointerException {

        DataOutputStream dos = new DataOutputStream(this.os);
        serialize(pR, xCoord, yCoord, METHOD_ID_ATTACK, dos);
        return true;
    }

    @Override
    public void run() {

        long seed = this.hashCode() * System.currentTimeMillis();
        Random random = new Random(seed);
        int localInt, remoteInt;

        try {
            DataOutputStream dos = new DataOutputStream(this.os);
            DataInputStream dis = new DataInputStream(this.is);
            do {
                localInt = random.nextInt();
                dos.writeInt(localInt);
                remoteInt = dis.readInt();
            } while (localInt == remoteInt);
            this.oracle = localInt < remoteInt;
            dos.writeUTF(this.name);
            this.partnerName = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!this.sessionCreatedListenerList.isEmpty()) {
            for (GameSessionEstablishedListener ocListener : this.sessionCreatedListenerList) {
                new Thread(() -> {

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        //won't happen
                    }
                    ocListener.gameSessionEstablished(BattleshipsProtocolEngine.this.oracle,
                            BattleshipsProtocolEngine.this.partnerName);
                }).start();
            }
        }

        try {
            boolean again = true;
            while (again) {
                again = this.read();
            }
        } catch (GameException e) {
            System.err.println("exception called in protocol engine - stop");
            e.printStackTrace();
        }

    }

    @Override
    public void handleConnection(InputStream is, OutputStream os) throws IOException {

        this.is = is;
        this.os = os;
        Thread protocolThread = new Thread(this);
        protocolThread.start();

    }

    @Override
    public void close() throws IOException {

        if (this.os != null) {
            this.os.close();
        }
        if (this.is != null) {
            this.is.close();
        }
    }

    @Override
    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {

        this.sessionCreatedListenerList.add(ocListener);

    }

    private void deserializeSet() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        deserialize(METHOD_ID_SET, dis);

    }

    private void deserializeAttack() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        deserialize(METHOD_ID_ATTACK, dis);

    }

    private void deserialize(int methodID, DataInputStream dis) throws GameException {

        PlayerRole pR;
        try {
            //read Role
            switch (dis.readInt()) {
                case ROLE_ID_FIRST -> pR = PlayerRole.FIRST;
                case ROLE_ID_SECOND -> pR = PlayerRole.SECOND;
                default -> throw new GameException(UNKNOWN_ROLE);
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
                default -> throw new GameException(UNKNOWN_ROLE);
            }
            //write xCoord
            dos.writeInt(xCoord);
            //write yCoord
            dos.writeInt(yCoord);
        } catch (IOException e) {
            throw new GameException(SERIALIZE_FAIL, e);
        }
    }

    boolean read() throws GameException {

        DataInputStream dis = new DataInputStream(this.is);
        try {
            int methodID = dis.readInt();
            switch (methodID) {
                case METHOD_ID_SET -> {
                    this.deserializeSet();
                    return true;
                }
                case METHOD_ID_ATTACK -> {
                    this.deserializeAttack();
                    return true;
                }
                default -> {
                    System.out.println(UNKNOWN_METHOD_ID);
                    return false;
                }
            }

        } catch (IOException e) {
            System.out.println(CONNECTION_LOSS);
            try {
                this.close();
            } catch (IOException IOe) {
                //ignore
            }
            return false;
        }
    }
}
