package battleshipsTests;

import battleships.Battleships;
import battleships.BattleshipsProtocolEngine;
import battleships.PlayerRole;
import exceptions.GameException;
import exceptions.StatusException;
import network.ProtocolEngine;
import network.TCPStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EngineTests {


    private static final int SLEEP_DURATION = 1000;
    private static final int PORT_NUMBER = 7777;
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";

    private Battleships getBSEngine(InputStream is, OutputStream os, Battleships gameEngine) {

        return new BattleshipsProtocolEngine(is, os, gameEngine);
    }

    @Test
    public void networkTest() throws IOException, InterruptedException, StatusException, GameException {
        //alice' game engine tester
        BSReadTester aliceGameEngineTester = new BSReadTester();
        // protocol engine on alice' side
        BattleshipsProtocolEngine aliceBattleshipsProtocolEngine = new BattleshipsProtocolEngine(aliceGameEngineTester);
        // same protocol engine -> protocol engine
        ProtocolEngine aliceProtocolEngine = aliceBattleshipsProtocolEngine;
        // -> and game engine
        Battleships aliceGameEngineSide = aliceBattleshipsProtocolEngine;

        BSReadTester bobGameEngineTester = new BSReadTester();
        ProtocolEngine bobProtocolEngine = new BattleshipsProtocolEngine(bobGameEngineTester);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                              tcp                                                       //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TCPStream aliceServer = new TCPStream(PORT_NUMBER, true, ALICE);
        TCPStream bobClient = new TCPStream(PORT_NUMBER, false, BOB);
        aliceServer.start(); bobClient.start();
        aliceServer.waitForConnection(); bobClient.waitForConnection();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                              protocol engine                                           //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        aliceProtocolEngine.handleConnection(aliceServer.getInputStream(),aliceServer.getOutputStream());
        bobProtocolEngine.handleConnection(bobClient.getInputStream(),bobClient.getOutputStream());
        System.out.println("threads need a few seconds to launch");
        Thread.sleep(SLEEP_DURATION);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                                run                                                     //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aliceGameEngineSide.setShip(PlayerRole.FIRST,0,0);
        Assert.assertTrue(bobGameEngineTester.lastCallSet);
        Assert.assertEquals(PlayerRole.FIRST, bobGameEngineTester.pR);
        Assert.assertEquals(0, bobGameEngineTester.xCoord);
        Assert.assertEquals(0, bobGameEngineTester.yCoord);

    }



//    @Test
//    public void setTest() throws StatusException, GameException {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Battleships bsProtocolEngine = this.getBSEngine(null, baos, null);
//
//        PlayerRole alice = PlayerRole.FIRST;
//        bsProtocolEngine.setShip(alice, 0, 0);
//
//        byte[] serializedBytes = baos.toByteArray();
//        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
//
//        BSReadTester bsReceiver = new BSReadTester();
//        Battleships bsProtocolReceiver = this.getBSEngine(bais, null, bsReceiver);
//
//        BattleshipsProtocolEngine bsEngine = (BattleshipsProtocolEngine) bsProtocolReceiver;
//        bsEngine.read();
//
//        Assert.assertTrue(bsReceiver.lastCallSet);
//        Assert.assertEquals(PlayerRole.FIRST, bsReceiver.pR);
//        Assert.assertEquals(0, bsReceiver.xCoord);
//        Assert.assertEquals(0, bsReceiver.yCoord);
//    }
//
//    @Test
//    public void attackTest() throws StatusException, GameException {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Battleships bsProtocolEngine = this.getBSEngine(null, baos, null);
//
//        PlayerRole alice = PlayerRole.SECOND;
//        bsProtocolEngine.attack(alice, 1, 1);
//
//        byte[] serializedBytes = baos.toByteArray();
//        ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
//
//        BSReadTester bsReceiver = new BSReadTester();
//        Battleships bsProtocolReceiver = this.getBSEngine(bais, null, bsReceiver);
//
//        BattleshipsProtocolEngine bsEngine = (BattleshipsProtocolEngine) bsProtocolReceiver;
//        bsEngine.read();
//
//        Assert.assertTrue(bsReceiver.lastCallAttack);
//        Assert.assertEquals(PlayerRole.SECOND, bsReceiver.pR);
//        Assert.assertEquals(1, bsReceiver.xCoord);
//        Assert.assertEquals(1, bsReceiver.yCoord);
//    }
//
    private class BSReadTester implements Battleships {

        private boolean lastCallSet = false;
        private boolean lastCallAttack = false;
        private PlayerRole pR = null;
        private int xCoord;
        private int yCoord;

        @Override
        public boolean setShip(PlayerRole pR, int xCoord, int yCoord) throws NullPointerException {

            this.lastCallSet = true;
            this.lastCallAttack = false;
            this.pR = pR;
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            return false;
        }

        @Override
        public boolean attack(PlayerRole pR, int xCoord, int yCoord) throws NullPointerException {

            this.lastCallSet = false;
            this.lastCallAttack = true;
            this.pR = pR;
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            return false;
        }
    }

}
