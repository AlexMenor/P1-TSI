package src_menor_molinero_alejandro;

import ontology.Types.ACTIONS;
import tools.Vector2d;

import java.util.ArrayList;


public class AStar {
    private char [][] grid;
    private Vector2d start;
    private Vector2d end;

    AStar(char[][] grid, Vector2d start, Vector2d end){
        this.grid = grid;
        this.start = start;
        this.end = end;
    }

    public ArrayList<ACTIONS> computePlan(){
        return null;
    }

}

class Node {
    private Vector2d position;
    private char [][] grid;
    private int coste;
    private int manhattan;
    private Vector2d end;
    private Node father;
    private ACTIONS lastAction;

    Node(Vector2d position, char [][] grid, int coste, Vector2d end, Node father, ACTIONS lastAction){
        this.position = position;
        this.grid = grid;
        this.coste = coste;
        this.end = end;
        this.lastAction = lastAction;

        computeManhattan();
    }

    public ArrayList<Node> generateOffSpring(){
        ArrayList<Node> offSpring = new ArrayList<Node>();

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

        switch (action) {
            case ACTION_RIGHT:
                x++;
                break;
            case ACTION_LEFT:
                x--;
                break;
            case ACTION_UP:
                y++;
                break;
            case ACTION_DOWN:
                y--;
                break;
        }

            if (x < 0 || x >= grid.length || y >= grid[0].length || y < 0)
                return null;
            if (grid[x][y] == 'w')
                return null;

            Vector2d newPosition = new Vector2d(x,y);
            Node newNode = new Node(newPosition, grid, coste + 1, end, this, action);

            return newNode;
    }

    private void computeManhattan(){
        int xDiff = (int) Math.abs(end.x - position.x);
        int yDiff = (int) Math.abs(end.y - position.y);
        manhattan = xDiff + yDiff;
    }


}
