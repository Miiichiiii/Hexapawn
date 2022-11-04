import javax.swing.*;
import java.util.ArrayList;

public class Label {
    protected static ArrayList<JLabel> JlabelList = new ArrayList<JLabel>();
    protected static ArrayList<Label> labelList = new ArrayList<Label>();
    protected State state;
    protected JLabel label;
    public Label(JLabel label, State state) {
        this.state = state;
        this.label = label;
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
    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
