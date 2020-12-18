///*
// * This Java source file was generated by the Gradle 'init' task.
// */
//package battleshipsTests;
//
//import battleships.Battleships;
//import battleships.BattleshipsImpl;
//import battleships.PlayerRole;
//import exceptions.GameException;
//import exceptions.StatusException;
//import org.junit.Assert;
//import org.junit.Test;
//
//public class AppTest {
//    private final String ALICE = "alice";
//    private final String BOB = "bob";
//
//    private Battleships getBattleships( ) {
//
//        return new BattleshipsImpl(ALICE);
//    }
//
//
//    @Test
//    public void set1() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        Battleships bs2 = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        Assert.assertTrue(bs.setShip(alice, 0, 0));
//        Assert.assertTrue(bs.setShip(alice, 0, 1));
//        Assert.assertTrue(bs.setShip(alice, 0, 2));
//        Assert.assertTrue(bs2.setShip(bob, 2, 0));
//        Assert.assertTrue(bs2.setShip(bob, 2, 1));
//        Assert.assertTrue(bs2.setShip(bob, 2, 2));
//    }
//
//    @Test
//    public void set2() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole bob = PlayerRole.SECOND;
//        Assert.assertTrue(bs.setShip(bob, 2, 0));
//        Assert.assertTrue(bs.setShip(bob, 2, 1));
//        Assert.assertTrue(bs.setShip(bob, 2, 2));
//    }
//
//    @Test(expected = GameException.class)
//    public void setFail1() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        Assert.assertTrue(bs.setShip(alice, 0, 0));
//        Assert.assertTrue(bs.setShip(alice, 0, 1));
//        Assert.assertTrue(bs.setShip(alice, 0, 2));
//        Assert.assertTrue(bs.setShip(alice, 1, 2));
//    }
//
//
//    @Test(expected = NullPointerException.class)
//    public void setFail2() throws StatusException, GameException, NullPointerException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        Assert.assertTrue(bs.setShip(alice, 3, 0));
//        Assert.assertTrue(bs.setShip(bob, 1, 3));
//    }
//
//    @Test
//    public void attack1() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        setShips(alice, bob, bs);
//        Assert.assertTrue(bs.attack(alice, 0, 0));
//        Assert.assertTrue(bs.attack(bob, 0, 0));
//        Assert.assertTrue(bs.attack(alice, 0, 1));
//        Assert.assertTrue(bs.attack(bob, 0, 1));
//        Assert.assertTrue(bs.attack(alice, 0, 2));
//    }
//
//    @Test
//    public void attack2() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        setShips(alice, bob, bs);
//        Assert.assertFalse(bs.attack(alice, 1, 0));
//        Assert.assertTrue(bs.attack(bob, 0, 0));
//        Assert.assertFalse(bs.attack(alice, 1, 0));
//        Assert.assertTrue(bs.attack(bob, 0, 1));
//
//    }
//
//    @Test(expected = GameException.class)
//    public void attack3() throws StatusException, GameException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        setShips(alice, bob, bs);
//        bs.attack(bob, 2, 0);
//        bs.attack(alice, 2, 0);
//    }
//
//    @Test(expected = StatusException.class)
//    public void firstWin() throws StatusException, GameException, NullPointerException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        setShips(alice, bob, bs);
//        Assert.assertTrue(bs.attack(alice, 0, 0));
//        Assert.assertTrue(bs.attack(bob, 0, 0));
//        Assert.assertTrue(bs.attack(alice, 0, 1));
//        Assert.assertTrue(bs.attack(bob, 0, 1));
//        Assert.assertTrue(bs.attack(alice, 0, 2));
//        bs.attack(bob, 0, 2);
//    }
//
//    @Test(expected = StatusException.class)
//    public void secondWin() throws StatusException, GameException, NullPointerException {
//
//        Battleships bs = this.getBattleships();
//        PlayerRole alice = PlayerRole.FIRST;
//        PlayerRole bob = PlayerRole.SECOND;
//        setShips(alice, bob, bs);
//        Assert.assertTrue(bs.attack(alice, 0, 0));
//        Assert.assertTrue(bs.attack(bob, 0, 0));
//        Assert.assertTrue(bs.attack(alice, 0, 1));
//        Assert.assertTrue(bs.attack(bob, 0, 1));
//        Assert.assertFalse(bs.attack(alice, 1, 2));
//        Assert.assertTrue(bs.attack(bob, 0, 2));
//        bs.attack(alice, 0, 2);
//    }
//
//
//    private void setShips(PlayerRole alice, PlayerRole bob, Battleships bs) throws StatusException, GameException {
//
//        bs.setShip(alice, 0, 0);
//        bs.setShip(alice, 0, 1);
//        bs.setShip(alice, 0, 2);
//        bs.setShip(bob, 0, 0);
//        bs.setShip(bob, 0, 1);
//        bs.setShip(bob, 0, 2);
//
//    }
//
//}
