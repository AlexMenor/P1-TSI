package src_menor_molinero_alejandro;

import ontology.Types.ACTIONS;
import tools.Vector2d;

import java.util.*;

enum Orientation {
    UP, DOWN, RIGHT, LEFT
}

public class AStar {
    private char [][] grid;
    private Vector2d start;
    private Vector2d end;
    private Orientation initialOrientation;

    AStar(char[][] grid, Vector2d start, Vector2d initialOrientation, Vector2d end){
        this.grid = grid;
        this.start = start;
        this.end = end;
        this.initialOrientation = vector2dToOrientation(initialOrientation);
    }

    public Stack<ACTIONS> computePlan(){
        Node firstNode = new Node(start, grid, 0, end, null, null, initialOrientation);

        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<Node> explored = new HashSet<>();

        frontier.add(firstNode);

        while (!frontier.isEmpty()){
            Node bestNode = frontier.remove();
            if (bestNode.isGoal())
                return getPlanFromGoalNode(bestNode);

            if (!explored.contains(bestNode)){
                ArrayList<Node> nodeOffSpring = bestNode.generateOffSpring();

                frontier.addAll(nodeOffSpring);
                explored.add(bestNode);
            }
        }

        return null;
    }

    private Stack<ACTIONS> getPlanFromGoalNode (Node node){
       Stack<ACTIONS> toReturn = new Stack<>();

       toReturn.push(ACTIONS.ACTION_NIL);
       while (node != null){
           toReturn.push(node.getLastAction());
           node = node.getFather();
       }

       return toReturn;
    }

    private Orientation vector2dToOrientation (Vector2d vectOrientation) {
        double x = vectOrientation.x;
        double y = vectOrientation.y;

        if (x > 0)
            return Orientation.RIGHT;
        else if (x < 0)
            return Orientation.LEFT;
        else if (y > 0)
            return Orientation.UP;
        else
            return Orientation.DOWN;
    }

}

class Node implements Comparable<Node>{
    private Vector2d position;
    private char [][] grid;
    private int coste;
    private int manhattan;
    private Vector2d end;
    private Node father;
    private ACTIONS lastAction;
    private Orientation orientation;

    Node(Vector2d position, char [][] grid, int coste, Vector2d end, Node father, ACTIONS lastAction, Orientation orientation){
        this.position = position;
        this.grid = grid;
        this.coste = coste;
        this.end = end;
        this.lastAction = lastAction;
        this.father = father;
        this.orientation = orientation;
        computeManhattan();
    }

    public boolean isGoal (){
        return position.equals(end);
    }

    public Node getFather(){
        return father;
    }

    public ACTIONS getLastAction(){
        return lastAction;
    }

    public ArrayList<Node> generateOffSpring(){
        ArrayList<Node> offSpring = new ArrayList<>();

        Node upNode = generateNode(ACTIONS.ACTION_UP);
        if (upNode != null)
            offSpring.add(upNode);

        Node downNode = generateNode(ACTIONS.ACTION_DOWN);
        if (downNode != null)
            offSpring.add(downNode);

        Node rightNode = generateNode(ACTIONS.ACTION_RIGHT);
        if  (rightNode != null)
            offSpring.add(rightNode);

        Node leftNode = generateNode(ACTIONS.ACTION_LEFT);
        if (leftNode != null)
            offSpring.add(leftNode);

        return offSpring;
    }

    private Node generateNode(ACTIONS action){
        int x = (int)position.x;
        int y = (int)position.y;
        Orientation newOrientation = this.orientation;

        switch (action) {
            case ACTION_RIGHT:
                if (orientation == Orientation.RIGHT)
                    x++;
                else
                    newOrientation = Orientation.RIGHT;
                break;
            case ACTION_LEFT:
                if (orientation == Orientation.LEFT)
                    x--;
                else
                    newOrientation = Orientation.LEFT;
                break;
            case ACTION_UP:
                if (orientation == Orientation.DOWN)
                    y--;
                else
                    newOrientation = Orientation.DOWN;
                break;
            case ACTION_DOWN:
                if (orientation == Orientation.UP)
                    y++;
                else
                    newOrientation = Orientation.UP;
                break;
        }

            if (x < 0 || x >= grid.length || y >= grid[0].length || y < 0)
                return null;
            if (grid[x][y] == 'w')
                return null;

            Vector2d newPosition = new Vector2d(x,y);
            Node newNode = new Node(newPosition, grid, coste + 1, end, this, action, newOrientation);

            return newNode;
    }

    private void computeManhattan(){
        int xDiff = (int) Math.abs(end.x - position.x);
        int yDiff = (int) Math.abs(end.y - position.y);
        manhattan = xDiff + yDiff;
    }


    @Override
    public int compareTo(Node node) {
        return (coste + manhattan) - (node.coste + node.manhattan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return position.equals(node.position);
    }

    @Override
    public int hashCode() {
        int [] arr = new int[4];
        arr[0] = (int) position.x;
        arr[1] = (int) position.y;
        arr[2] = orientation == Orientation.RIGHT ? 1 : orientation == Orientation.LEFT ? -1 : 0;
        arr[3] = orientation == Orientation.UP ? 1 : orientation == Orientation.DOWN ? -1 : 0;
        return Arrays.hashCode(arr);
    }
}
