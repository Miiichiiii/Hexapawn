import enums.State;

import java.util.ArrayList;

public class Node {
    State[][] value;
    ArrayList<Node> children;
    boolean deleted;

    public Node (State[][] value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.deleted = false;
    }

    public void addChildren (Node currentNode) {
        this.children.add(currentNode);
    }
}
