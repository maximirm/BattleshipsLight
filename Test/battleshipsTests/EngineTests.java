package battleshipsTests;

import battleships.*;
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
    public static final String USE_PORT = "use port: ";
    private static final String THREAD_SLEEP = "thread launching";
    private static int port = 0;

    private Battleships getBSEngine(InputStream is, OutputStream os, Battleships gameEngine) {

        return new BattleshipsProtocolEngine(is, os, gameEngine);
    }
//    @Test
//    public void integrationTest1() throws IOException, InterruptedException {
//
//        BattleshipsImpl aliceGameEngine = new BattleshipsImpl(ALICE);
//        BattleshipsProtocolEngine aliceBSProtocolEngine = new BattleshipsProtocolEngine(aliceGameEngine,ALICE);
//        aliceGameEngine.setProtocolEngine(aliceBSProtocolEngine);
//
//        BattleshipsImpl bobGameEngine = new BattleshipsImpl(BOB);
//        BattleshipsProtocolEngine bobBSProtocolEngine = new BattleshipsProtocolEngine(bobGameEngine,BOB);
//        bobGameEngine.setProtocolEngine(bobBSProtocolEngine);
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                    tcp                                                                 //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        int port = this.getPortNumber();
//        TCPStream aliceSide = new TCPStream(port, true, "aliceSide");
//        TCPStream bobSide = new TCPStream(port, false, "bobSide");
//
//        aliceSide.start(); bobSide.start();
//        aliceSide.waitForConnection();bobSide.waitForConnection();
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                        launch protocol engine                                          //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        aliceBSProtocolEngine.handleConnection(aliceSide.getInputStream(), aliceSide.getOutputStream());
//        bobBSProtocolEngine.handleConnection(bobSide.getInputStream(), bobSide.getOutputStream());
//        System.out.println(THREAD_SLEEP);
//        Thread.sleep(SLEEP_DURATION);
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                         testResult                                                     //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        Assert.assertTrue(aliceGameEngine.getStatus() == bobGameEngine.getStatus());
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                          tidy up                                                       //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        aliceBSProtocolEngine.close();
//        bobBSProtocolEngine.close();
//        Thread.sleep(SLEEP_DURATION);
//
//    }

    @Test
    public void integrationTestFullGame() throws IOException, InterruptedException, StatusException, GameException {

        BattleshipsImpl aliceGameEngine = new BattleshipsImpl(ALICE);
        BattleshipsProtocolEngine aliceBSProtocolEngine = new BattleshipsProtocolEngine(aliceGameEngine,ALICE);
        aliceGameEngine.setProtocolEngine(aliceBSProtocolEngine);

        BattleshipsImpl bobGameEngine = new BattleshipsImpl(BOB);
        BattleshipsProtocolEngine bobBSProtocolEngine = new BattleshipsProtocolEngine(bobGameEngine,BOB);
        bobGameEngine.setProtocolEngine(bobBSProtocolEngine);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                    tcp                                                                 //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int port = this.getPortNumber();
        TCPStream aliceSide = new TCPStream(port, true, "aliceSide");
        TCPStream bobSide = new TCPStream(port, false, "bobSide");

        aliceSide.start(); bobSide.start();
        aliceSide.waitForConnection();bobSide.waitForConnection();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                        launch protocol engine                                          //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        aliceBSProtocolEngine.handleConnection(aliceSide.getInputStream(), aliceSide.getOutputStream());
        bobBSProtocolEngine.handleConnection(bobSide.getInputStream(), bobSide.getOutputStream());
        System.out.println(THREAD_SLEEP);
        Thread.sleep(SLEEP_DURATION);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                         scenario                                                       //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        BattleShipsLocalBoard playerFirst = aliceGameEngine.isActive() ? aliceGameEngine : bobGameEngine;
        BattleShipsLocalBoard playerSecond = aliceGameEngine.isActive() ? bobGameEngine : aliceGameEngine;
        PlayerRole alice = playerFirst.getLocalRole();
        PlayerRole bob = playerSecond.getLocalRole();
        playerFirst.setShip(alice, 0, 0);
        playerFirst.setShip(alice, 0, 1);
        playerFirst.setShip(alice, 0, 2);
        playerSecond.setShip(bob, 0, 0);
        playerSecond.setShip(bob, 0, 1);
        playerSecond.setShip(bob, 0, 2);

        if(alice == PlayerRole.FIRST){
            playerFirst.attack(alice, 0, 0);
            playerSecond.attack(bob, 0, 0);
            playerFirst.attack(alice, 0, 1);
            playerSecond.attack(bob, 0, 1);
            playerFirst.attack(alice, 0, 2);

            Assert.assertTrue(playerFirst.hasWon());
            System.out.println("first won");

        } else if (alice == PlayerRole.SECOND){
            playerSecond.attack(bob, 0, 0);
            playerFirst.attack(alice, 0, 0);
            playerSecond.attack(bob, 0, 1);
            playerFirst.attack(alice, 0, 1);
            playerSecond.attack(bob, 0, 2);

            Assert.assertTrue(playerSecond.hasWon());
            System.out.println("second won");
        } else System.out.println("fail");










        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                          tidy up                                                       //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        aliceBSProtocolEngine.close();
        bobBSProtocolEngine.close();
        Thread.sleep(SLEEP_DURATION);

    }

    private int getPortNumber(){
        if(EngineTests.port == 0){
            EngineTests.port = PORT_NUMBER;
        } else{
            EngineTests.port++;
        }
        System.out.println(USE_PORT+ EngineTests.port);
        return EngineTests.port;
    }

//    @Test
//    public void networkTest() throws IOException, InterruptedException, StatusException, GameException {
//        //alice' game engine tester
//        BSReadTester aliceGameEngineTester = new BSReadTester();
//        // protocol engine on alice' side
//        BattleshipsProtocolEngine aliceBattleshipsProtocolEngine = new BattleshipsProtocolEngine(aliceGameEngineTester);
//        // same protocol engine -> protocol engine
//        ProtocolEngine aliceProtocolEngine = aliceBattleshipsProtocolEngine;
//        // -> and game engine
//        Battleships aliceGameEngineSide = aliceBattleshipsProtocolEngine;
//
//        BSReadTester bobGameEngineTester = new BSReadTester();
//        ProtocolEngine bobProtocolEngine = new BattleshipsProtocolEngine(bobGameEngineTester);
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                              tcp                                                       //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        TCPStream aliceServer = new TCPStream(PORT_NUMBER, true, ALICE);
//        TCPStream bobClient = new TCPStream(PORT_NUMBER, false, BOB);
//        aliceServer.start(); bobClient.start();
//        aliceServer.waitForConnection(); bobClient.waitForConnection();
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                              protocol engine                                           //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        aliceProtocolEngine.handleConnection(aliceServer.getInputStream(),aliceServer.getOutputStream());
//        bobProtocolEngine.handleConnection(bobClient.getInputStream(),bobClient.getOutputStream());
//        System.out.println("threads need a few seconds to launch");
//        Thread.sleep(SLEEP_DURATION);
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //                                                run                                                     //
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        aliceGameEngineSide.setShip(PlayerRole.FIRST,0,0);
//        Assert.assertTrue(bobGameEngineTester.lastCallSet);
//        Assert.assertEquals(PlayerRole.FIRST, bobGameEngineTester.pR);
//        Assert.assertEquals(0, bobGameEngineTester.xCoord);
//        Assert.assertEquals(0, bobGameEngineTester.yCoord);
//
//    }



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
