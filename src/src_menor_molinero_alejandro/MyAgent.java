package src_menor_molinero_alejandro;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;

public class MyAgent extends AbstractPlayer{

    private char [][] myGrid;
    private Vector2d doorPosition;


    public MyAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        generateStaticMap(stateObs);
        for(int i = 0 ; i < myGrid.length ; i++) {
            for (int j = 0; j < myGrid[i].length; j++)
                System.out.print(myGrid[i][j] + " ");
            System.out.println();
        }
    }

    private void generateStaticMap (StateObservation stateOBs){
        Dimension worldDimension = stateOBs.getWorldDimension();
        int xLength = worldDimension.width / 30;
        int yLength = worldDimension.height / 30;

        myGrid = new char [xLength][yLength];

        ArrayList<Observation> walls = stateOBs.getImmovablePositions()[0];

        for (Observation wall : walls){
            int wallX = ((int)wall.position.x) / 30;
            int wallY = ((int)wall.position.y) / 30;

            myGrid[wallX][wallY] = 'w';
        }

        System.out.println(stateOBs.getPortalsPositions()[0]);
    }

    private void getDoorPosition(StateObservation stateObs){
       Observation observation = stateObs.getPortalsPositions()[0].get(0);
       this.doorPosition = observation.position;
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        return Types.ACTIONS.ACTION_NIL;
    }
}

