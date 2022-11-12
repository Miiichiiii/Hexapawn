import enums.State;
import enums.Turn;

import java.util.ArrayList;
import java.util.Arrays;

public class ComputerAlgorithm {
    Node root;

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

    public static boolean compareStateArrays(State[][] a1, State[][] a2) {
        return Arrays.deepEquals(a1, a2);
    }

}
