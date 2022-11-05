import javax.swing.*;
import java.util.ArrayList;

public class Label {
    private static ArrayList<JLabel> jLabelList = new ArrayList<JLabel>();
    public static ArrayList<Label> labelList = new ArrayList<Label>();
    private State state;
    private final JLabel label;
    public final short x, y;
    private Move move;
    public Label(JLabel label, short x, short y, State state) {
        this.state = state;
        this.label = label;
        this.move = Move.NONE;
        this.x = x;
        this.y = y;
        jLabelList.add(label);
        labelList.add(this);
    }

    public static Label retrieveLabel(JLabel label) {
        //This method is used in the onClick method to retrieve the object of the
        //Label class which contains more information than the normal JLabel
        for(int i = 0; i < jLabelList.size(); i++) {
            if (jLabelList.get(i) == label) {
                return labelList.get(i);
            }
        }
        return null;
    }

    public static Label retrieveByCoordinates(short x, short y) {
        return labelList.get(y * 3 + x);
    }

    public JLabel getLabel() {
        return label;
    }

    public static void clearMove() {
        for (Label label : labelList) {
            label.setMove(Move.NONE);
        }
    }

    public Move getMove() {
        return this.move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }


}
