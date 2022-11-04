import javax.swing.*;
import java.util.ArrayList;

public class Label {
    protected static ArrayList<JLabel> JlabelList = new ArrayList<JLabel>();
    public static ArrayList<Label> labelList = new ArrayList<Label>();
    protected State state;
    protected JLabel label;
    protected final short x, y;
    public Label(JLabel label, short x, short y, State state) {
        this.state = state;
        this.label = label;
        this.x = x;
        this.y = y;
        JlabelList.add(label);
        labelList.add(this);
    }

    public static Label retrieveLabel(JLabel label) {
        for(int i = 0; i < JlabelList.size(); i++) {
            if (JlabelList.get(i) == label) {
                return labelList.get(i);
            }
        }
        return null;
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
