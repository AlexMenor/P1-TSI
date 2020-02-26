package src_menor_molinero_alejandro;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class MyAgent extends AbstractPlayer{

    private char [][] myGrid;
    Stack<ACTIONS> plan;
    Vector2d doorPosition;

    public MyAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

        generateStaticMap(stateObs);

        printGrid();
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (plan == null) {
            Vector2d current = transformPixelToGridValues(stateObs.getAvatarPosition());
            AStar searchAlgorithm = new AStar(myGrid, current, doorPosition);
            plan = searchAlgorithm.computePlan();
        }
        else if (plan.isEmpty())
            return ACTIONS.ACTION_NIL;

        return plan.pop();
    }

    private void printGrid(){

        for (int i = 0 ; i < myGrid.length ; i++) {
            for (int j = 0; j < myGrid[i].length; j++)
                System.out.print(myGrid[i][j] + " ");
            System.out.println();
        }

    }

    private Vector2d transformPixelToGridValues (Vector2d v){
        int x = (int) v.x / 30;
        int y = (int) v.y / 30;

        return new Vector2d(x,y);
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

        System.out.println("Las dimensiones son: " + xLength + " x " + yLength);

        myGrid = new char [xLength][yLength];
    }

    private void setWallPositions (StateObservation stateObs){

        ArrayList<Observation> walls = stateObs.getImmovablePositions()[0];

        for (Observation wall : walls){
            Vector2d wallGridCords = transformPixelToGridValues(wall.position);

            myGrid[(int)wallGridCords.x][(int)wallGridCords.y] = 'w';
        }

    }

    private void setDoorPosition(StateObservation stateObs){
       Observation observation = stateObs.getPortalsPositions()[0].get(0);
       doorPosition = transformPixelToGridValues(observation.position);

        myGrid[(int) doorPosition.x][(int) doorPosition.y] = 'd';

        System.out.println("La puerta está en: " + doorPosition.x + " x " + doorPosition.y);
    }

    private void setGemPositions(StateObservation stateObs){
        if (stateObs.getResourcesPositions() == null)
            return;

        ArrayList<Observation>  gems = stateObs.getResourcesPositions()[0];

        for (Observation gem : gems){
            Vector2d gemPos = transformPixelToGridValues(gem.position);

            myGrid[(int)gemPos.x][(int)gemPos.y] = 'g';

        }
    }

}

