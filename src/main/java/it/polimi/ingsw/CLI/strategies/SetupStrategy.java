package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketSetup;

public interface SetupStrategy {
    void handleSetup(PacketSetup packetSetup, CLI cli);
}