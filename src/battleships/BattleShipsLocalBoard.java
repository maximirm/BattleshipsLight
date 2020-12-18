package battleships;

public interface BattleShipsLocalBoard extends Battleships {


    /**
     * @return local PlayerRole
     */
    PlayerRole getLocalRole();

    /**
     * @return GameStatus
     */
    Status getStatus();

    /**
     * @return true if active
     */
    boolean isActive();

    /**
     * @return true if won
     */
    boolean hasWon();

    /**
     * @return true if lost
     */
    boolean hasLost();

    /**
     * subscribe for changes
     * @param changeListener changeListener
     */
    void subscribeChangeListener(LocalBoardChangeListener changeListener);

}
