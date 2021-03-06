package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.CardFactory;
import it.polimi.ingsw.server.cards.exceptions.InvalidCardException;
import it.polimi.ingsw.server.model.enums.SetupPhase;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * The concrete model used through interfaces as the interchange waypoint
 * from the model classes to the virtual view, the controller and the match classes
 */
public class ConcreteModel implements ObservableModel, Model {

    private CardFactory factory;
    private final SetupManager setupManager;
    private final TurnLogic turnLogic;
    private final InternalModel internalModel;

    /**
     * Initializes the model and creates the setup manager, the internal model nad the turn logic components.
     * @param players  the players in game
     * @param isHardCore the game mode
     */
    public ConcreteModel(List<String> players, boolean isHardCore){
        this.factory = null;
        try {
            this.factory = CardFactory.getInstance();
        } catch (InvalidCardException e) {
            assert false;
        }
        this.internalModel = new InternalModel(players, factory, isHardCore);
        this.setupManager = new SetupManager(internalModel, factory.getCards());
        this.turnLogic = new TurnLogic(internalModel);
    }

    /**
     * Used by the match class to start a game
     */
    public void start(){
        if(setupManager.getSetupPhase() == SetupPhase.STARTING)
            setupManager.start();
    }

    @Override
    public void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer) {
        setupManager.addPacketCardsFromServerObserver(observer);
    }

    @Override
    public void addPacketDoActionObserver(Observer<PacketDoAction> observer) {
        setupManager.addPacketDoActionObserver(observer);
        turnLogic.addPacketDoActionObserver(observer);
    }

    @Override
    public void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer) {
        turnLogic.addPacketPossibleBuildsObserver(observer);
    }

    @Override
    public void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer) {
        turnLogic.addPacketPossibleMovesObserver(observer);
    }

    @Override
    public void addPacketSetupObserver(Observer<PacketSetup> observer) {
        setupManager.addPacketSetupObserver(observer);
    }

    @Override
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer) {
        setupManager.addPacketUpdateBoardObserver(observer);
        turnLogic.addPacketUpdateBoardObserver(observer);
    }

    @Override
    public synchronized void makeMove(String senderID, PacketMove packetMove) throws InvalidPacketException {
        assert (!packetMove.isSimulate());
        if(setupManager.getSetupPhase() == SetupPhase.SETUP_FINISHED)
            turnLogic.consumePacketMove(senderID,packetMove);
    }

    @Override
    public synchronized void makeBuild(String senderID, PacketBuild packetBuild) throws InvalidPacketException {
        assert (!packetBuild.isSimulate());
        if(setupManager.getSetupPhase() == SetupPhase.SETUP_FINISHED)
            turnLogic.consumePacketBuild(senderID, packetBuild);
    }

    @Override
    public synchronized void getPossibleMoves(String senderID, PacketMove packetMove) {
        assert (packetMove.isSimulate());
        if(setupManager.getSetupPhase() == SetupPhase.SETUP_FINISHED)
            turnLogic.getPossibleMoves(senderID,packetMove);
    }

    @Override
    public synchronized void getPossibleBuilds(String senderID, PacketBuild packetBuild) {
        assert (packetBuild.isSimulate());
        if(setupManager.getSetupPhase() == SetupPhase.SETUP_FINISHED)
            turnLogic.getPossibleBuilds(senderID,packetBuild);
    }

    @Override
    public synchronized void setSelectedCards(String senderID, List<String> selectedCards) throws InvalidPacketException {
        setupManager.setSelectedCards(senderID,selectedCards);
    }

    @Override
    public synchronized void setStartPlayer(String senderID, String startPlayer) throws InvalidPacketException {
        setupManager.setStartPlayer(senderID,startPlayer);
    }

    @Override
    public synchronized void setWorkersPositions(String senderID, Map<String, Point> workersPositions) throws InvalidPacketException {
        setupManager.setWorkersPositions(senderID,workersPositions);
        if(setupManager.getSetupPhase() == SetupPhase.SETUP_FINISHED && !turnLogic.isStarted()) {
            internalModel.compileCardStrategy();
            turnLogic.start();
        }
    }

    @Override
    public void setGameFinishedHandler(Observer<String> gameFinishedHandler) {
        turnLogic.setGameFinishedHandler(gameFinishedHandler);
    }
}
