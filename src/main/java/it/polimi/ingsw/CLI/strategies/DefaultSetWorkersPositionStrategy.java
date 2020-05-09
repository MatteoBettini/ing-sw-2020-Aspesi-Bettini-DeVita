package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultSetWorkersPositionStrategy implements SetWorkersPositionStrategy {
    private static final String POSITIONS_REGEXP = "(([A-E]|[a-e])[1-5])";
    private static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);

    @Override
    public void handleSetWorkersPosition(boolean isRetry) {
        MatchData matchData = MatchData.getInstance();

        Board board = matchData.getBoard();

        if(board.getNumberOfWorkers() == 0) matchData.printMatch();

        Map<String, Point> positions = new HashMap<>();
        List<String> workersID = matchData.getIds().get(matchData.getPlayerName());
        for(int i = 0; i < workersID.size(); ++i){
            String point;
            Point helper;
            boolean error = false;
            do{
                if(error) System.out.println("Invalid position for worker " + (i + 1) + ", retry");
                error = false;
                do{
                    if(i > 0) System.out.print("Choose your worker" + (i + 1) + "'s position: ");
                    else System.out.print("Choose your worker" + (i + 1) + "'s position (ex A1, B2, ...): ");
                    point = InputUtilities.getLine();
                    if(point == null) return;
                }while(!POSITION_PATTERN.matcher(point).matches());
                helper = board.getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0)));
                if(board.getCell(helper) == null || positions.containsValue(helper)) error = true;
            }while(error);
            positions.put(workersID.get(i), helper);
        }

        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        matchData.getClient().send(packetWorkersPositions);

    }
}