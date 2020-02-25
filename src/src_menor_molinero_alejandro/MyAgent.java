package src_menor_molinero_alejandro;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;

public class MyAgent extends AbstractPlayer{

    private char [][] myGrid;
    ArrayList<ACTIONS> plan;
    Vector2d doorPosition;

    public MyAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        plan = new ArrayList<ACTIONS>();
        generateStaticMap(stateObs);

        printGrid();
    }

    private void printGrid(){

        for (int i = 0 ; i < myGrid.length ; i++) {
            for (int j = 0; j < myGrid[i].length; j++)
                System.out.print(myGrid[i][j] + " ");
            System.out.println();
        }

    }

    private void generateStaticMap (StateObservation stateObs){
        createGridFromDimensions(stateObs);
        setWallPositions(stateObs);
        setGemPositions(stateObs);
        setDoorPosition(stateObs);
    }

    private void createGridFromDimensions (StateObservation stateObs){
        Dimension worldDimension = stateObs.getWorldDimension();
        int xLength = worldDimension.width / 30;
        int yLength = worldDimension.height / 30;

        myGrid = new char [xLength][yLength];
    }

    private void setWallPositions (StateObservation stateObs){

        ArrayList<Observation> walls = stateObs.getImmovablePositions()[0];

        for (Observation wall : walls){
            int wallX = ((int)wall.position.x) / 30;
            int wallY = ((int)wall.position.y) / 30;

            myGrid[wallX][wallY] = 'w';
        }

    }

    private void setDoorPosition(StateObservation stateObs){
       Observation observation = stateObs.getPortalsPositions()[0].get(0);
       Vector2d doorPos = observation.position;

        int x = (int) doorPos.x / 30;
        int y = (int) doorPos.y / 30;

        myGrid[x][y] = 'd';
        doorPosition = new Vector2d(x,y);
    }

    private void setGemPositions(StateObservation stateObs){
        ArrayList<Observation>  gems = stateObs.getResourcesPositions()[0];

        for (Observation gem : gems){
            int gemX = ((int)gem.position.x) / 30;
            int gemY = ((int)gem.position.y) / 30;

            myGrid[gemX][gemY] = 'g';

        }
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (plan.isEmpty()) {
            Vector2d current = stateObs.getAvatarPosition();
            AStar searchAlgorithm = new AStar(myGrid, current, doorPosition);
            plan = searchAlgorithm.computePlan();
        }
        return ACTIONS.ACTION_NIL;
    }
}

