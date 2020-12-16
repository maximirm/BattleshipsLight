package battleships;

public class Tile {

    boolean ship;
    boolean attacked;

    public Tile() {

    }

    public boolean isShip() {

        return ship;
    }

    public void setShip(boolean ship) {

        this.ship = ship;
    }

    public boolean isAttacked() {

        return attacked;
    }

    public void setAttacked(boolean attacked) {

        this.attacked = attacked;
    }
}
