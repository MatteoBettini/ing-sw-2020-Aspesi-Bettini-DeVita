package it.polimi.ingsw.server.virtualView;

import it.polimi.ingsw.server.ServerLogger;
import it.polimi.ingsw.server.communication.ConnectionToClient;
import it.polimi.ingsw.server.model.ObservableModel;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Yhe virtual view, which acts as a bridge between the controller and the connection to the client
 */
public class VirtualView implements Observer<Object> {

    private final ConnectionToClient connectionToClient;

    private final List<Observer<PacketMove>> packetMoveObservers;
    private final List<Observer<PacketBuild>> packetBuildObservers;
    private final List<Observer<PacketCardsFromClient>> packetCardsFromClientObservers;
    private final List<Observer<PacketStartPlayer>> packetStartPlayerObservers;
    private final List<Observer<PacketWorkersPositions>> packetWorkersPositionsObservers;

    private final Logger serverLogger = Logger.getLogger(ServerLogger.LOGGER_NAME);


    /**
     * This is the constructor for the Virtual View
     * The virtual view subscribes to all the events notified
     * by the model and sends messages to its connection to client
     * accordingly to filtering and sending policies
     * @param connectionToClient the connection to client associated to the virtual view (the association is 1:1)
     * @param model the observable model to which it subscribes
     */
    public VirtualView(ConnectionToClient connectionToClient, ObservableModel model){
        this.connectionToClient = connectionToClient;

        this.packetBuildObservers = new ArrayList<>();
        this.packetMoveObservers = new ArrayList<>();
        this.packetCardsFromClientObservers = new ArrayList<>();
        this.packetStartPlayerObservers = new ArrayList<>();
        this.packetWorkersPositionsObservers = new ArrayList<>();

        connectionToClient.addObserver(this);

        model.addPacketCardsFromServerObserver((packetCardsFromServer) -> {
            boolean withTimer = false;
            if(packetCardsFromServer.getTo().equals(connectionToClient.getClientNickname())) {
                withTimer = true;
                serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending cards to choose");
            }
            connectionToClient.send(packetCardsFromServer, withTimer);

        });
        model.addPacketDoActionObserver((packetDoAction) -> {
            boolean withTimer = false;
            if(packetDoAction.getTo().equals(connectionToClient.getClientNickname())) {
                withTimer = true;
                serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending do action request");
            }
            connectionToClient.send(packetDoAction, withTimer);

        });
        model.addPacketPossibleBuildsObserver((packetPossibleBuilds)-> {
            if (packetPossibleBuilds.getTo().equals(connectionToClient.getClientNickname())) {
                serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending possible builds");
                connectionToClient.send(packetPossibleBuilds, false);
            }
        });
        model.addPacketPossibleMovesObserver((packetPossibleMoves)-> {
            if (packetPossibleMoves.getTo().equals(connectionToClient.getClientNickname())) {
                serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending possible moves");
                connectionToClient.send(packetPossibleMoves, false);
            }
        });
        model.addPacketSetupObserver(packetSetup -> {
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending setup packet");
            connectionToClient.send(packetSetup, false);
        });
        model.addPacketUpdateBoardObserver(packetUpdateBoard -> {
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending an update of the board ");
            connectionToClient.send(packetUpdateBoard, false);
        });

    }

    /**
     * Update method called upon arrival of a packet to the connection to client associated
     * @param packetFromClient the packet arriving from the connection
     */
    @Override
    public void update(Object packetFromClient) {
        if(packetFromClient instanceof PacketMove){
            PacketMove packetMove = (PacketMove) packetFromClient;
            if(!packetMove.isSimulate())
                connectionToClient.stopTimer();
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: received Packet Move");
            notifyPacketMoveObservers(packetMove);
        }else if(packetFromClient instanceof PacketBuild) {
            PacketBuild packetBuild = (PacketBuild) packetFromClient;
            if(!packetBuild.isSimulate())
                connectionToClient.stopTimer();
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: received Packet Build");
            notifyPacketBuildObservers(packetBuild);
        }else if(packetFromClient instanceof PacketStartPlayer) {
            connectionToClient.stopTimer();
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: received chosen starting player");
            notifyPacketStartPlayerObservers((PacketStartPlayer) packetFromClient);
        }else if(packetFromClient instanceof PacketCardsFromClient) {
            connectionToClient.stopTimer();
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: received chosen cards");
            notifyPacketCardsFromClientObservers((PacketCardsFromClient) packetFromClient);
        }else if(packetFromClient instanceof PacketWorkersPositions){
            connectionToClient.stopTimer();
            serverLogger.info("[" + connectionToClient.getClientNickname() + "]: received Workers' positions");
            notifyPacketWorkersPositionsObservers((PacketWorkersPositions) packetFromClient);
        }else{
            assert false;
            sendInvalidPacketMessage();
        }
    }

    /**
     * This method sends an invalid packet answer to the client
     */
    public void sendInvalidPacketMessage(){
        serverLogger.info("[" + connectionToClient.getClientNickname() + "]: sending invalid packet message");
        connectionToClient.send(ConnectionMessages.INVALID_PACKET, true);
    }

    /**
     * Returns the associated client nickname
     * @return  the client nickname
     */
    public String getClientNickname(){
        return connectionToClient.getClientNickname();
    }

    public void addPacketMoveObserver(Observer<PacketMove> o){
        this.packetMoveObservers.add(o);
    }
    public void addPacketBuildObserver(Observer<PacketBuild> o){
        this.packetBuildObservers.add(o);
    }
    public void addPacketCardsFromClientObserver(Observer<PacketCardsFromClient> o){
        this.packetCardsFromClientObservers.add(o);
    }
    public void addPacketStartPlayerObserver(Observer<PacketStartPlayer> o){
        this.packetStartPlayerObservers.add(o);
    }
    public void addPacketWorkersPositionsObserver(Observer<PacketWorkersPositions> o){
        this.packetWorkersPositionsObservers.add(o);
    }

    public void notifyPacketMoveObservers(PacketMove p){
        for(Observer<PacketMove> o : packetMoveObservers){
            o.update(p);
        }
    }
    public void notifyPacketBuildObservers(PacketBuild p){
        for(Observer<PacketBuild> o : packetBuildObservers){
            o.update(p);
        }
    }
    public void notifyPacketCardsFromClientObservers(PacketCardsFromClient p){
        for(Observer<PacketCardsFromClient> o : packetCardsFromClientObservers){
            o.update(p);
        }
    }
    public void notifyPacketStartPlayerObservers(PacketStartPlayer p){
        for(Observer<PacketStartPlayer> o : packetStartPlayerObservers){
            o.update(p);
        }
    }
    public void notifyPacketWorkersPositionsObservers(PacketWorkersPositions p){
        for(Observer<PacketWorkersPositions> o : packetWorkersPositionsObservers){
            o.update(p);
        }
    }
}
