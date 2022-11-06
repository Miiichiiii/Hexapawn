import enums.State;
import javax.swing.*;
import java.util.ArrayList;

public class Label {
    private static ArrayList<JLabel> jLabelList = new ArrayList<JLabel>();
    public static ArrayList<Label> labelList = new ArrayList<Label>();
    private State state;
    private final JLabel label;
    public final short x, y;

    private boolean movable = true;
    public Label(JLabel label, short x, short y, State state) {
        this.state = state;
        this.label = label;
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

    public static Label retrieveLabel(short x, short y) {
        if (y > 2 || y < 0 || x > 2 || x < 0) return null;
        return labelList.get(y * 3 + x);
    }

    public JLabel getLabel() {
        return label;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean getMovable() {
        return this.movable; // Not Sure yet if these are still relevant, will delete later if no use is found
    }

    public static void ResetLabels() {
        for (short i= 0; i < 3; i++) {
            retrieveLabel(i, (short) 0).setState(State.BLACK);
            retrieveLabel(i, (short) 1).setState(State.EMPTY);
            retrieveLabel(i, (short) 2).setState(State.WHITE);
        }

    }

}
