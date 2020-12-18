package network;

public interface GameSessionEstablishedListener {

    /**
     * is called when oracle was created
     * @param partnerName partner name
     * @param oracle    oracle
     */
    void gameSessionEstablished(boolean oracle, String partnerName);

}
