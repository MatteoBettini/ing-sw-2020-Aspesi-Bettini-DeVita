package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;

public interface SetWorkersPositionStrategy {
    public void handleSetWorkersPosition(CLI cli, boolean isRetry);
}