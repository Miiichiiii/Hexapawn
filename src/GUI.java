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
    protected JButton newGameButton;
    protected JCheckBox computerCheckBox;
    protected JTextPane scoreBoard;
    protected JLabel winnerLabel;
    protected JMenuBar jMenuBar;
    protected JMenu jMenuFile;
    protected JMenuItem openMenuItem;
    protected JMenuItem newMenuItem;
    protected JMenuItem saveMenuItem;
    protected JFileChooser fileChooser = new JFileChooser();
    public JLabel[][] labelList = {{label_0_0, label_0_1, label_0_2},
                                   {label_1_0, label_1_1, label_1_2},
                                   {label_2_0, label_2_1, label_2_2}};

    public GUI() {
        setContentPane(mainPanel);
        setTitle("Hexapawn"); //Sets the name of the Application
        setSize(1150, 845);
        setLocationRelativeTo(null); //Set spawn point to the middle of the screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
