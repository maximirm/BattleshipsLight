package battleships;

import exceptions.GameException;
import exceptions.StatusException;

public interface Battleships {

    int DEFAULT_PORT = 5555;

    /**
     * place a ship on the board
     * @param pR     PlayerRole
     * @param xCoord xCoord
     * @param yCoord yCoord
     * @throws StatusException if method is called in wrong GameStatus
     * @throws GameException   if player placed all ships already
     */
    void setShip(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException;

    /**
     * attack a ship on the board
     * @param pR     PlayerRole attacking Player
     * @param xCoord xCoord
     * @param yCoord yCoord
     * @return AttackResult
     * @throws StatusException if method is called in wrong GameStatus
     * @throws GameException   if wrong player wants to attack
     */
    boolean attack(PlayerRole pR, int xCoord, int yCoord) throws StatusException, GameException;

}
