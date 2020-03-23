package src_menor_molinero_alejandro;

import tools.Vector2d;

import java.util.ArrayList;
import java.util.Random;

public class Planner {
    ArrayList<Vector2d> goals;
    int[][] distanceMatrix;
    char[][] grid;
    Vector2d current;
    Planner(ArrayList<Vector2d> goals, Vector2d current, char[][] grid){
        this.goals = goals;
        this.current = current;
        this.grid = grid;
        setDistanceMatrix();
    }

    private void setDistanceMatrix (){
        int n = goals.size();
        distanceMatrix = new int [n+1][n+1];
        ArrayList<Vector2d> toMeasure = new ArrayList<>();

        toMeasure.add(current);

        toMeasure.addAll(goals);

        for (int i = 0 ; i < toMeasure.size() - 1; i++){
            for(int j = i + 1 ; j < toMeasure.size() ; j++){
                Vector2d start = toMeasure.get(i);
                Vector2d end = toMeasure.get(j);
                Vector2d orientation = new Vector2d(1, -1);
               AStar algorithm = new AStar(grid,start, orientation, end );
               int distance = algorithm.computePlan().size();
               distanceMatrix[i][j] = distanceMatrix[j][i] = distance;
            }
        }
    }

    public ArrayList<Integer> getPlan(){
        return localSearch();
    }

    private ArrayList<Integer> localSearch(){
        ArrayList<Integer> order = genRandomOrder();


        /*
        ArrayList<Integer> order = new ArrayList<>();

        for (int i = 0 ; i < goals.size() + 1 ; i++)
            order.add(i);

        System.out.println(cost(order));

         */
        /*

        for (int i = 1 ; i < order.size() - 2 ; i++){
            for (int j = i + 1 ; j < order.size() - 1 ; j++){
                int inc = computeInc(i, j, order);
                if (inc < 0) {
                    int aux = order.get(i);
                    order.set(i, order.get(j));
                    order.set(j, aux);
                    i = 0;
                    break;
                }
            }
        }
         */

        if (order.size() - 2 <= 0)
            return order;

        Random rand = new Random();

        int iteration = 0;
        int MAX_ITERATIONS = 5000;

        boolean notFoundAnythingBetter = false;

        while (!notFoundAnythingBetter) {
            notFoundAnythingBetter = true;

            while (iteration < MAX_ITERATIONS && notFoundAnythingBetter) {
                int i = rand.nextInt(order.size() - 2) + 1;
                int j = rand.nextInt(order.size() - 2) + 1;

                if (i != j) {

                    int inc = computeInc(i, j, order);
                    if (inc < 0) {
                        int aux = order.get(i);
                        order.set(i, order.get(j));
                        order.set(j, aux);
                        notFoundAnythingBetter = false;
                    }

                    iteration++;
                }

            }
        }

        order.remove(0);
        return order;

    }

    private int computeInc(int i , int j, ArrayList<Integer> order){
        ArrayList<Integer> swapOrder = new ArrayList<>(order);
        int aux = order.get(i);
        swapOrder.set(i, order.get(j));
        swapOrder.set(j, aux);

        int current = costIndex(i, order) + costIndex(j, order);

        int swapping = costIndex(i, swapOrder) + costIndex(j, swapOrder);

        return swapping - current;



    }

    private int costIndex(int i, ArrayList<Integer> order){
        int point = order.get(i);
        int pointForward = order.get(i+1);
        int pointBehind = order.get(i-1);

        return distanceMatrix[point][pointForward] + distanceMatrix[point][pointBehind];
    }

    private int cost(ArrayList<Integer> order){
        int cost = 0;

        for (int i = 0  ; i < order.size() - 1 ; i++){
            cost += distanceMatrix[order.get(i)][order.get(i +1)];
        }

        return cost;
    }


    private ArrayList<Integer> genRandomOrder(){
        ArrayList<Integer> order = new ArrayList<>();

        for(int i = 0 ; i < goals.size() +1 ; i++)
            order.add(-1);


        order.set(0,0);
        order.set(order.size()-1,order.size()- 1);

        Random rand = new Random();

        for (int i = 1 ; i < order.size() - 1 ; i++){
            int pos = rand.nextInt( order.size() - 2);
            pos++;

            if (order.get(pos) != -1)
                i--;
            else
                order.set(pos, i);
        }

        return order;
    }


}
