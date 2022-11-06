import javax.swing.*;

public class GUI extends JFrame{
    protected JLabel label_0_0;
    protected JLabel label_0_1;
    protected JLabel label_0_2;
    protected JLabel label_1_0;
    protected JLabel label_1_1;
    protected JLabel label_1_2;
    protected JLabel label_2_0;
    protected JLabel label_2_1;
    protected JLabel label_2_2;
    protected JPanel mainPanel;
    private JButton newGameButton;
    private JCheckBox computerCheckBox;
    private JTextArea textArea1;
    private JLabel winnerLabel;
    public JLabel[][] labelList = {{label_0_0, label_0_1, label_0_2},
                                   {label_1_0, label_1_1, label_1_2},
                                   {label_2_0, label_2_1, label_2_2}};

    public GUI() {
        setContentPane(mainPanel);
        setTitle("Hexapawn"); //Sets the name of the Application
        setSize(1150, 818);
        setLocationRelativeTo(null); //Set spawn point to the middle of the screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
