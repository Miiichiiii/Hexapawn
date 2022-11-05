import javax.swing.*;
import java.util.ArrayList;

public class Label {
    protected static ArrayList<JLabel> jLabelList = new ArrayList<JLabel>();
    public static ArrayList<Label> labelList = new ArrayList<Label>();
    protected State state;
    protected final JLabel label;
    protected final short x, y;
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

    public JLabel getLabel() {
        return label;
    }

    public short getX() {
        return this.x;
    }
    public short getY() {
        return this.y;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }


}
