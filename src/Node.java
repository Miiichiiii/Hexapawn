import enums.State;
import enums.Turn;

import java.util.ArrayList;

public class Node {
    State[][] value;
    ArrayList<Node> children;
    boolean deleted;
    Turn turn;

    public Node (State[][] value, Turn turn) {
        this.turn = turn;
        this.value = value;
        this.children = new ArrayList<>();
        this.deleted = false;
    }

    public void addChildren (Node currentNode) {
        this.children.add(currentNode);
    }
}
