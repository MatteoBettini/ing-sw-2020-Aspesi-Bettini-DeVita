package it.polimi.ingsw.client.CLI.buildings;

import it.polimi.ingsw.client.CLI.CharFigure;
import it.polimi.ingsw.client.CLI.CharStream;
import it.polimi.ingsw.client.CLI.colors.BackColor;
import it.polimi.ingsw.client.CLI.colors.ForeColor;

class ThirdFloorBuilding implements CharFigure {
    private final CharStream stream;
    private final int RATEOX;
    private final int RATEOY;

    /**
     * /**
     * This method is the constructor for the graphical third floor that implements the CharFigure interface.
     * @param stream is the object used by the graphical third floor in order to display itself.
     * @param RATEOX is the length on the X axis.
     * @param RATEOY is the length on the Y axis.
     */
    public ThirdFloorBuilding(CharStream stream, int RATEOX, int RATEOY){
        this.RATEOX = RATEOX;
        this.RATEOY = RATEOY;
        this.stream = stream;
    }

    /**
     * This method is overridden from the the CharFigure interface.
     * Since the building position on the stream is relative to the one of the graphical board this method is not used.
     */
    @Override
    public void draw() { }

    /**
     * This method will set colors and characters used to display the third floor through the stream.
     * Colors of the Third Floor: Grey.
     * @param relX is the position on the X axis and it is relative to a certain position on the board.
     * @param relY is the position on the Y axis and it is relative to a certain position on the board.
     */
    @Override
    public void draw(int relX, int relY) {
        relX += 8;
        relY+= 3;
        BackColor backColor = BackColor.ANSI_BG_WHITE;
        ForeColor foreColor = ForeColor.ANSI_BLACK;
        for(int i = 0; i <= RATEOX; ++i){
            for(int j = 0; j <= RATEOY; ++j){
                if(i == 0 && j == 0) stream.addChar('╔', i + relX, j + relY, foreColor, backColor);
                else if(i == 0 && j ==  RATEOY) stream.addChar('╚', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == 0) stream.addChar('╗', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX && j == RATEOY) stream.addChar('╝', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == 0) stream.addChar('═', i + relX, j + relY, foreColor, backColor);
                else if(i > 0 && i < RATEOX && j == RATEOY) stream.addChar('═', i +relX, j + relY, foreColor, backColor);
                else if(i == 0) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else if(i == RATEOX) stream.addChar('║', i + relX, j + relY, foreColor, backColor);
                else stream.addColor(i + relX, j + relY, null , backColor);
            }
        }
    }
}