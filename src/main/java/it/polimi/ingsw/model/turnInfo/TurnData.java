package it.polimi.ingsw.model.turnInfo;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;


/**
 * Data common in each packet proposal used to pass and validate info in the model strategy.
 */
public abstract class TurnData {

    private final Player player;
    private final Worker worker;

    public TurnData(Player player, Worker worker) {
        assert (player != null && worker != null);
        this.player = player;
        this.worker = worker;
    }

    /**
     * Get packet player
     * @return Player instance contained in this packet
     */
    public Player getPlayer() {
        return  player;
    }

    /**
     * Get packet worker
     * @return Worker instance contained in this packet
     */
    public Worker getWorker() {
        return worker;
    }



}