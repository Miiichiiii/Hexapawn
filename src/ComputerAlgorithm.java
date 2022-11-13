import enums.State;
import enums.Turn;
import enums.Win;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ComputerAlgorithm {
    public static Node root = null;
    public static Node current = null;
    public static Implementation implementation;
    public static Thread thread;

    public static void startThread() {
        thread = new Thread(() -> findMove(root));
        thread.start();
    }

    public static State[][] createStateArray() {
        State[][] res = new State[3][3];
        for(int y = 0; y < 3; y++) {
            State[] row = new State[3];
            for(int x = 0; x < 3; x++) {
                row[x] = Label.labelList.get(y * 3 + x).getState();
            }
            res[y] = row;
        }
        return res;
    }

    public static void onMove() {
        thread.interrupt();
    }
    public static ArrayList<Label[]> getMoves() {
        Label LabelObj;
        ArrayList<Label[]> allMoves = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                LabelObj = Label.retrieveLabel((short) x, (short) y);
                short targetRow = (LabelObj.getState() == State.BLACK) ? (short) (LabelObj.y + 1) : (short) (LabelObj.y - 1);

                if ((LabelObj.getState() == State.BLACK && Implementation.turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && Implementation.turn == Turn.WHITE)) {
                    if (targetRow > 2 || targetRow < 0) continue;
                    if (Move.forwardPossible(Label.retrieveLabel(LabelObj.x, targetRow))) {
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel(LabelObj.x, targetRow)});
                    }
                    if (Move.rightPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x + 1), targetRow))) {
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel((short) (LabelObj.x + 1), targetRow)});
                    }
                    if (Move.leftPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x - 1), targetRow))) {
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel((short) (LabelObj.x - 1), targetRow)});
                    }
                }
            }
        }
        return allMoves;

    }

    public static boolean childExists(Node root, State[][] nextState) {
        return getChild(root, nextState) != null;
    }

    public static Node getChild(Node root, State[][] nextState) {
        for(Node child: root.children) {
            if(compareStateArrays(child.value, nextState)) {
                return child;
            }
        }
        return null;
    }

    public static boolean findMove(Node root) {
        current = root;
        if(root == null || root.turn == Turn.WHITE) {
            try {
                synchronized (thread) {
                    thread.wait(); //Wait for new move
                }
            } catch (InterruptedException e) {}
        }
        if(root == null) {
            root = ComputerAlgorithm.root;
        }

        Win rv = implementation.checkWin();
        if(rv == Win.WHITEWIN) {
            return true;
        }
        else if(rv == Win.BLACKWIN) {
            return false;
        }

        if(root.turn == Turn.WHITE) {
            Node child = getChild(root, createStateArray());
            if(findMove(child)) {
                root.children.remove(child);
                return root.children.size() == 0;
            }
        }
        else {
            ArrayList<Label[]> possibleMoves = getMoves(); //Get all possible moves
            createChildren(root, possibleMoves); //Creates children if they don't exist
            State[][] currentState = createStateArray();
            Random random = new Random();
            Label[] currentMove;
            Node child;

            do {
                int rand = random.nextInt(possibleMoves.size()); //Get random move
                currentMove = possibleMoves.get(rand);
                currentState[currentMove[0].y][currentMove[0].x] = State.EMPTY;
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[0].getState();
                child = getChild(root, currentState); //Get the child of the random move
                currentState[currentMove[0].y][currentMove[0].x] = currentMove[0].getState();
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[1].getState();
            } while (child == null || child.deleted); //Check if the child has already been deleted
            implementation.movePawn(currentMove[0], currentMove[1]);
            if(findMove(child)) {
                root.children.remove(child); //TODO
                return root.children.size() == 0;
            }
        }
        return false;
    }

    public static void createChildren(Node current, ArrayList<Label[]> possibleMoves) {
        if(current == null) {
            root = new Node(createStateArray(), Turn.WHITE); //If there is no tree, create one
        }
        else {
            root = current;
        }
        State[][] currentState = createStateArray();
        //Create tree possibilities
        for (Label[] possibleMove : possibleMoves) {
            //Basically check if the child already exists and if not add it to tree
            currentState[possibleMove[0].y][possibleMove[0].x] = State.EMPTY;
            currentState[possibleMove[1].y][possibleMove[1].x] = possibleMove[0].getState();
            if (!childExists(root, currentState)) {
                State[][] deepCopy = new State[3][3];
                for(int y = 0; y < 3; y++) {
                    State[] row = new State[3];
                    for(int x = 0; x < 3; x++) {
                        row[x] = currentState[y][x];
                    }
                    deepCopy[y] = row;
                }
                root.addChildren(new Node(deepCopy, (root.turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK));
            }
            currentState[possibleMove[0].y][possibleMove[0].x] = possibleMove[0].getState();
            currentState[possibleMove[1].y][possibleMove[1].x] = possibleMove[1].getState();
        }
    }

    public static boolean compareStateArrays(State[][] a1, State[][] a2) {
        return Arrays.deepEquals(a1, a2);
    }

}