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
    private int [][] heatMap;
    private Stack<ACTIONS> plan;
    private int xLen;
    private int yLen;
    private ArrayList<Vector2d> goals;
    private ArrayList<Integer> goalOrder;
    private int currentGoal = 0;

    public MyAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

        generateStaticMap(stateObs);

        setGoals(stateObs);
        Vector2d currentPosition = transformPixelToGridValues(stateObs.getAvatarPosition());

        Planner planner = new Planner(goals, currentPosition, myGrid);

        goalOrder = planner.getPlan();

    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        ACTIONS action;
        if(!goals.isEmpty()) {
            if (plan == null) {
                int order = goalOrder.get(currentGoal);
                Vector2d currentPosition = transformPixelToGridValues(stateObs.getAvatarPosition());
                Vector2d currentOrientation = stateObs.getAvatarOrientation();

                AStar searchAlgorithm = new AStar(myGrid, currentPosition, currentOrientation, goals.get(order - 1));
                plan = searchAlgorithm.computePlan();
            } else if (plan.isEmpty()) {
                int order = goalOrder.get(currentGoal);
                Vector2d currentPosition = transformPixelToGridValues(stateObs.getAvatarPosition());
                Vector2d currentOrientation = stateObs.getAvatarOrientation();
                AStar searchAlgorithm = new AStar(myGrid, currentPosition, currentOrientation, goals.get(order - 1));
                plan = searchAlgorithm.computePlan();
            }
            genHeatMap(getEnemyPositions(stateObs));
            printHeatMap();
            Vector2d currentPosition = transformPixelToGridValues(stateObs.getAvatarPosition());
            action = getSafeAction(currentPosition);

            if(action == ACTIONS.ACTION_NIL) {
                action = plan.pop();
                if (plan.isEmpty())
                    currentGoal++;
            }
            else {
                plan.clear();
            }
        }
        else {
            genHeatMap(getEnemyPositions(stateObs));
            Vector2d currentPosition = transformPixelToGridValues(stateObs.getAvatarPosition());
            action = getSafeAction(currentPosition);
        }


        return action;

    }

    private ACTIONS getSafeAction(Vector2d currentPosition){
        int x = (int)currentPosition.x;
        int y = (int)currentPosition.y;
        double currentBest = getIfPossible(x,y);
        ACTIONS bestAction = ACTIONS.ACTION_NIL;

        double right = getRight(x,y);
        double left = getLeft(x,y);
        double up = getUp(x,y);
        double down= getDown(x,y);

        if (right < currentBest){
           currentBest = right;
           bestAction = ACTIONS.ACTION_RIGHT;
        }
        if (left < currentBest){
            currentBest = left;
            bestAction = ACTIONS.ACTION_LEFT;
        }
        if (up < currentBest){
            currentBest = up;
            bestAction = ACTIONS.ACTION_UP;
        }
        if (down < currentBest){
            currentBest = down;
            bestAction = ACTIONS.ACTION_DOWN;
        }

        return bestAction;
    }

    private double getRight(int x,int y){
       int RADIUS = 4;

       int i = 1;

       double toReturn = 0;

       while (i < RADIUS){
           int current = getIfPossible(x+i,y);
           if(current == Integer.MAX_VALUE){
               if (toReturn == 0)
                   return current;
               else
                   return current / (i-1);
           }
           toReturn += current;
           i++;
       }

       return toReturn / RADIUS;
    }
    private double getLeft(int x,int y){
        int RADIUS = 4;

        int i = 1;

        double toReturn = 0;

        while (i < RADIUS){
            int current = getIfPossible(x-i,y);
            if(current == Integer.MAX_VALUE){
                if (toReturn == 0)
                    return current;
                else
                    return current / (i-1);
            }
            toReturn += current;
            i++;
        }

        return toReturn / RADIUS;
    }
    private double getUp(int x,int y){
        int RADIUS = 4;

        int i = 1;

        double toReturn = 0;

        while (i < RADIUS){
            int current = getIfPossible(x,y-i);
            if(current == Integer.MAX_VALUE){
                if (toReturn == 0)
                    return current;
                else
                    return current / (i-1);
            }
            toReturn += current;
            i++;
        }

        return toReturn / RADIUS;
    }
    private double getDown(int x,int y){
        int RADIUS = 4;

        int i = 1;

        double toReturn = 0;

        while (i < RADIUS){
            int current = getIfPossible(x,y+i);
            if(current == Integer.MAX_VALUE){
                if (toReturn == 0)
                    return current;
                else
                    return current / (i-1);
            }
            toReturn += current;
            i++;
        }

        return toReturn / RADIUS;
    }

    private int getIfPossible(int x, int y){
        if(x >= 0 && x < xLen && y >= 0 && y < yLen)
            return heatMap[x][y];
        else
            return Integer.MAX_VALUE;
    }

    private void printGrid(){

        for (int i = 0 ; i < myGrid.length ; i++) {
            for (int j = 0; j < myGrid[i].length; j++)
                System.out.print(myGrid[i][j] + " ");
            System.out.println();
        }
    }

    void printHeatMap(){
        for (int i = 0 ; i < heatMap.length ; i++) {
            for (int j = 0; j < heatMap[i].length; j++)
                System.out.print(heatMap[i][j] + " ");
            System.out.println();
        }

    }


    private void setGoals (StateObservation stateObs){
        this.goals = new ArrayList<>();
        if (stateObs.getResourcesPositions() != null){
            ArrayList<Observation> gems = stateObs.getResourcesPositions()[0];
            for (Observation gem : gems){
                this.goals.add(transformPixelToGridValues(gem.position));
            }
        }

        if (stateObs.getPortalsPositions() != null){
            this.goals.add(transformPixelToGridValues(stateObs.getPortalsPositions()[0].get(0).position));
        }
    }

    private Vector2d transformPixelToGridValues (Vector2d v){
        int x = (int) v.x / 30;
        int y = (int) v.y / 30;

        return new Vector2d(x,y);
    }

    private void generateStaticMap (StateObservation stateObs){
        setWorldDimensions(stateObs);
        createGridFromDimensions(stateObs);
        setWallPositions(stateObs);
        setGemPositions(stateObs);
    }

    private void setWorldDimensions(StateObservation stateObs) {
        Dimension worldDimension = stateObs.getWorldDimension();

        xLen = worldDimension.width / 30;
        yLen = worldDimension.height / 30;
    }

    private void createGridFromDimensions (StateObservation stateObs){
        myGrid = new char [xLen][yLen];
    }

    private void setWallPositions (StateObservation stateObs){
        if (stateObs.getImmovablePositions() == null)
            return;

        ArrayList<Observation> walls = stateObs.getImmovablePositions()[0];

        for (Observation wall : walls){
            Vector2d wallGridCords = transformPixelToGridValues(wall.position);

            myGrid[(int)wallGridCords.x][(int)wallGridCords.y] = 'w';
        }

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

    private void genHeatMap(ArrayList<Vector2d> enemies) {

        heatMap = new int[xLen][yLen];

        for (int i = 0 ; i < xLen ; i++){
            for(int j = 0 ; j < yLen ; j++){
                if(myGrid[i][j] == 'w') {
                    heatMap[i][j] = 40;
                    //radiusOfWall(i,j);
                }
            }
        }

        if (enemies != null) {

            for (Vector2d enemy : enemies) {
                int x = (int) enemy.x;
                int y = (int) enemy.y;
                heatMap[x][y] += 30;
                radiusOfEnemy(x, y);
            }
        }
    }

    private void radiusOfWall(int x, int y){
        writeIfPossible(x+1,y, 2);
        writeIfPossible(x-1,y, 2);
        writeIfPossible(x,y+1, 2);
        writeIfPossible(x,y-1, 2);
    }

    private void radiusOfEnemy(int x,int y){
        int RADIUS = 10;
        int base = heatMap[x][y];

        for (int i = 0 ; i < RADIUS ; i++){
            int value = base - 3*i;
            writeIfPossible(x+i,y, value);
            writeIfPossible(x+i,y+i, value);
            writeIfPossible(x-i,y+i, value);
            writeIfPossible(x+i,y-i, value);
            writeIfPossible(x-i,y-i, value);
            writeIfPossible(x-i,y, value);
            writeIfPossible(x,y+i, value);
            writeIfPossible(x,y-i, value);
        }
    }

    private void writeIfPossible (int x, int y, int value){
        if (x >= 0 && x < xLen && y >= 0 && y < yLen)
            heatMap[x][y] = heatMap[x][y] + value;
    }

    private ArrayList<Vector2d> getEnemyPositions(StateObservation stateObs){
        if (stateObs.getNPCPositions() == null)
            return null;
        ArrayList<Vector2d> enemyPositions = new ArrayList<>();

        ArrayList<Observation> enemies = stateObs.getNPCPositions()[0];

        for (Observation enemy : enemies)
            enemyPositions.add(transformPixelToGridValues(enemy.position));

        return enemyPositions;
    }

}

