import javax.swing.*;

public class Label {
    protected State state;
    protected JLabel label;
    public Label(JLabel label, State state) {
        this.state = state;
        this.label = label;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
