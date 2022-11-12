import enums.State;
import enums.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ComputerAlgorithm {
    public static Node root = null;
    public static ArrayList<Node> path = new ArrayList<>();
    public static Implementation implementation;

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
        //Find the optimal move
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
        for(Node child: root.children) {
            if(compareStateArrays(child.value, nextState)) {
                return true;
            }
        }
        return false;
    }

    public static void findMove() {
        if(path.size() == 0) {
            if(root == null) {
                root = new Node(createStateArray(), Turn.WHITE);
            }
            //Get Possible moves
            ArrayList<Label[]> possibleMoves = new ArrayList<>();
            State[][] currentState = createStateArray();
            Label[] currentMove;
            //Create tree possibilities
            for (int i = 0; i < possibleMoves.size(); i++) {
                currentMove = possibleMoves.get(i);
                currentState[currentMove[0].y][currentMove[0].x] = State.EMPTY;
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[0].getState();
                if(!childExists(root, currentState)) {
                    root.addChildren(new Node(currentState, (root.turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK));
                }
                currentState[currentMove[0].y][currentMove[0].x] = currentMove[0].getState();
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[1].getState();
            }
            Random random = new Random();
            while(true) {
                int rand = random.nextInt(possibleMoves.size());
                currentMove = possibleMoves.get(rand);
                currentState[currentMove[0].y][currentMove[0].x] = State.EMPTY;
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[0].getState();
                if(childExists(root, currentState)) {
                    break;
                }
                currentState[currentMove[0].y][currentMove[0].x] = currentMove[0].getState();
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[1].getState();
            }
            implementation.movePawn(currentMove[0], currentMove[1]);
        }
    }


    public static boolean compareStateArrays(State[][] a1, State[][] a2) {
        return Arrays.deepEquals(a1, a2);
    }

}