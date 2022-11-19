import enums.*;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Implementation extends GUI {
    public static Turn turn = Turn.WHITE;
    public static boolean won = false;
    public static ArrayList<Win> score = new ArrayList<>();
    public static int black_wins = 0;
    public static boolean isGameNew = true;
    public static boolean computerCheckBoxEnabled = false;
    public Implementation() {
        super();
        initializeListener();
        initializeLabels();
        initializePictures();
        initializeFileChooserFilter();
        ComputerAlgorithm.createChildren(ComputerAlgorithm.currentNode); //Initializes the ComputerAlgorithm
    }

    private void initializeFileChooserFilter() {
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) {
                    return true;
                }
                String fileName = f.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                return extension.equals("json");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
    }

    private void initializeListener() {
        //For every label in the label list:
        //Add an actionListener and run the onClick method
        for (JLabel[] jLabels : labelList) {
            for (JLabel label : jLabels) {

                label.setOpaque(true);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onClick(e);
                        super.mouseClicked(e);
                    }
                });
            }
        }
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onNewGameClick();
                super.mouseClicked(e);
            }
        });

        openMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenFileClick();
            }
        });

        newMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewFileClick();
            }
        });

        saveMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveFileClick();
            }
        });
        computerCheckBox.addItemListener(this::onComputerCheckBoxClick);
    }

    public void onComputerCheckBoxClick(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            if(isGameNew) {
                ComputerAlgorithm.startThread();
                computerCheckBoxEnabled = true;
            }
            else if (!computerCheckBoxEnabled) {
                computerCheckBox.setSelected(false);
            }
        }
        else {
            if(isGameNew) {
                ComputerAlgorithm.thread.stop();
                computerCheckBoxEnabled = false;
            }
            else if (computerCheckBoxEnabled) {
                computerCheckBox.setSelected(true);
            }
        }
    }

    public void onNewFileClick() {
        if(isGameNew || !computerCheckBoxEnabled) {
            ComputerAlgorithm.root = null; //Reset the AI data
            ComputerAlgorithm.createChildren(ComputerAlgorithm.currentNode); //Creates children if they don't exist
            if(ComputerAlgorithm.thread != null && ComputerAlgorithm.thread.isAlive()) { //Check if the thread is active
                //Reload the thread to use the resetted AI data
                ComputerAlgorithm.thread.stop();
                ComputerAlgorithm.startThread();
            }
        }
    }

    public void onOpenFileClick() {
        int result = this.fileChooser.showOpenDialog(this); //Show the fileChooser dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();  //Get the selected file
            String path = file.getAbsolutePath(); //Get the path of the selected file
            if(isGameNew || !computerCheckBoxEnabled) { //User is only able to load the AI after or at the beginning of a game or if the AI is not enabled
                String json = readFile(path); //Read in the content of the AI save file
                if (json != null) {
                    ComputerAlgorithm.loadJson(json); //Load up the AI with the json content
                    ComputerAlgorithm.createChildren(ComputerAlgorithm.currentNode); //Creates children if they don't exist
                    if(ComputerAlgorithm.thread != null && ComputerAlgorithm.thread.isAlive()) { //Check if the thread is active
                        //Reload the thread to use the newly loaded AI data
                        ComputerAlgorithm.thread.stop();
                        ComputerAlgorithm.startThread();
                    }
                }
            }
        }
    }

    public String readFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            return reader.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public void onSaveFileClick() {
        int result = this.fileChooser.showSaveDialog(this); //Show the fileChooser dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile(); //Get the selected file
            String path = file.getAbsolutePath(); //Get the path of the selected file
            String json;
            if(ComputerAlgorithm.root == null) {
                json = new JSONObject().toString();
            }
            else {
                json = ComputerAlgorithm.getJson(ComputerAlgorithm.root); //Convert the tree to a json representation
            }
            writeToFile(path + ".json", json); //Write the json representation to a file
        }
    }

    public void writeToFile(String path, String text) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void onNewGameClick() {
        resetGame();
    }

    public void updateScoreBoard(Win win) {
        //Create the text, that is displayed on the scoreboard
        if (win == Win.BLACKWIN) black_wins++;
        score.add(win);
        int white_wins = score.size() - black_wins;
        String display = "Scoreboard\nBlack : White\n" + black_wins + " : " + white_wins + "\n";
        for (int i = 0; i < score.size() - 1; i++) {
            display += ((score.get(i) == Win.WHITEWIN) ? 'W' : 'B') + ", ";
        }
        display += (score.get(score.size() - 1) == Win.WHITEWIN) ? 'W' : 'B';

        //Used to center the text
        StyledDocument doc = scoreBoard.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        //Update the text of the scoreboard
        scoreBoard.setText(display);
    }

    public void onClick(MouseEvent e) {
        if(won) return;
        if (move(e)) { //Only check for win if a pawn has been moved
            if(!computerCheckBox.isSelected()) { //If the AI is enabled, win checking is handled by the AI
                Win rv = checkWin();
                if(rv != Win.UNDECIDED) {
                    onWin(rv);
                }
            }
        }
    }

    public void onWin(Win win) {
        isGameNew = true;
        if (win == Win.BLACKWIN) {
            won = true;
            loadImage(winnerLabel, Picture.BLACK_WIN);
            updateScoreBoard(win);
        }
        else if (win == Win.WHITEWIN) {
            won = true;
            loadImage(winnerLabel, Picture.WHITE_WIN);
            updateScoreBoard(win);
        }
    }

    public void movePawn(Label origin, Label target) {
        //Method to move a pawn to another field
        target.setState(origin.getState());
        loadImage(origin.getLabel(), Picture.EMPTY);

        if (origin.getState() == State.BLACK) loadImage(target.getLabel(), Picture.BLACK);
        else loadImage(target.getLabel(), Picture.WHITE);

        origin.setState(State.EMPTY);
    }

    public boolean move(MouseEvent e) {
        //Returns if a pawn has been moved
        JLabel Current_Label = (JLabel) e.getComponent();
        Label LabelObj = Label.retrieveLabel(Current_Label);
        assert LabelObj != null; //IntelliJ cries without this

        if (LabelObj == Move.getForward() || LabelObj == Move.getLeft() || LabelObj == Move.getRight()) { //If Label can be the new position of the pawn
            isGameNew = false;
            movePawn(Move.getSelected(), LabelObj); //Move the pawn to the new field
            Move.resetMove(); //Clear the move variables and color up
            turn = (turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK; //Other player's turn
            if(computerCheckBox.isSelected()) {
                ComputerAlgorithm.onMove(); //Tell the thread that the white move has been completed
            }
            return true;
        }

        Move.resetMove(); //Clear the move variables and color up
        if ((LabelObj.getState() == State.BLACK && turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && turn == Turn.WHITE)) {
            if(computerCheckBox.isSelected()) {
                ComputerAlgorithm.createChildren(ComputerAlgorithm.currentNode); //Creates children if they don't exist
            }
            short targetRow = (LabelObj.getState() == State.BLACK) ? (short) (LabelObj.y + 1) : (short) (LabelObj.y - 1); //The row in which the pawn can potentially move
            // If the field has a pawn and the turn is right, allow moves
            if ((LabelObj.getState() == State.BLACK && turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && turn == Turn.WHITE)) {
                Move.setSelected(LabelObj); //Set the Label to selected
                if (targetRow > 2 || targetRow < 0) return false;
                if (Move.forwardPossible(Label.retrieveLabel(LabelObj.x, targetRow))) { //Only allow forward move if field is empty
                    Move.setForward(Label.retrieveLabel(LabelObj.x, targetRow)); //Set the Label to be a potential new position
                }
                if (Move.rightPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x + 1), targetRow))) { //Only allow diagonal move if there is a pawn and the turn is right
                    Move.setRight(Label.retrieveLabel((short) (LabelObj.x + 1), targetRow)); //Set the Label to be a potential new position
                }
                if (Move.leftPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x - 1), targetRow))) { //Only allow diagonal move if there is a pawn and the turn is right
                    Move.setLeft(Label.retrieveLabel((short) (LabelObj.x - 1), targetRow)); //Set the Label to be a potential new position
                }
            }
        }
        return false;
    }

    public Win checkWin() {
        if(won) return Win.UNDECIDED;
        int AmountWhitePawns = 0, AmountBlackPawns = 0, ImmovableWhitePawn = 0, ImmovableBlackPawn = 0;

        for (short i = 0; i < 3; i++) {
            for (short j = 0; j < 3; j++) {
                Label CheckLabel = Label.retrieveLabel(j, i);
                short targetRow = (CheckLabel.getState() == State.BLACK) ? (short) (CheckLabel.y + 1) : (short) (CheckLabel.y - 1);
                if (CheckLabel.getState() == State.WHITE) {
                    AmountWhitePawns += 1; // Check for amount of Pawns. If 0 after the Loop, declare Win for Opponent
                }
                if (CheckLabel.getState() == State.BLACK) {
                    AmountBlackPawns += 1; // Check for amount of Pawns. If 0 after the Loop, declare Win for Opponent
                }
                if ((CheckLabel.getState() == State.WHITE) && (CheckLabel.y == 0)) {
                    return Win.WHITEWIN; //if a white pawn is at the last rank, declare win for white
                }
                if ((CheckLabel.getState() == State.BLACK) && (CheckLabel.y == 2)) {
                    return Win.BLACKWIN; //if a black pawn is at the first rank, declare win for black
                }
                if (CheckLabel.getState() != State.EMPTY &&
                        (!Move.forwardPossible(Label.retrieveLabel(CheckLabel.x, targetRow)) &&
                        !(Move.leftPossible(CheckLabel, Label.retrieveLabel((short) (CheckLabel.x - 1), targetRow))) &&
                        !(Move.rightPossible(CheckLabel, Label.retrieveLabel((short) (CheckLabel.x + 1), targetRow))))) {
                    if (CheckLabel.getState() == State.WHITE) {
                        ImmovableWhitePawn += 1;
                    }
                    if (CheckLabel.getState() == State.BLACK) {
                        ImmovableBlackPawn += 1;
                    }
                }
            }
        }
        if (AmountWhitePawns == 0) {
            return Win.BLACKWIN;
        }
        if (AmountBlackPawns == 0) {
            return Win.WHITEWIN;
        }
        if (AmountWhitePawns == ImmovableWhitePawn && turn == Turn.WHITE) { //If white has no more move available and it's whites turn
            return Win.BLACKWIN;
        }
        if (AmountBlackPawns == ImmovableBlackPawn && turn == Turn.BLACK) {//If black has no more move available and it's blacks turn
            return Win.WHITEWIN;
        }

        return Win.UNDECIDED;
    }

    private void resetGame() {
        isGameNew = true;
        winnerLabel.setIcon(null);
        won = false;
        initializePictures();
        Move.resetMove();
        Label.resetLabels();
        turn = Turn.WHITE;
        if(computerCheckBox.isSelected() && ComputerAlgorithm.thread != null && !ComputerAlgorithm.thread.isAlive()) {
            ComputerAlgorithm.startThread();
        }
    }

    private void initializeLabels() {
        for (short x = 0; x < labelList[0].length; x++) {
            new Label(labelList[0][x], x, (short) 0, State.BLACK); //Instantiate the label class
        }
        for (short x = 0; x < labelList[1].length; x++) {
            new Label(labelList[1][x], x, (short) 1, State.EMPTY); //Instantiate the label class
        }
        for (short x = 0; x < labelList[2].length; x++) {
            new Label(labelList[2][x], x, (short) 2, State.WHITE); //Instantiate the label class
        }
    }

    private void initializePictures() {
        for (short x = 0; x < labelList[0].length; x++) {
            loadImage(labelList[0][x], Picture.BLACK); //Load the image for every label in the row
        }
        for (short x = 0; x < labelList[1].length; x++) {
            loadImage(labelList[1][x], Picture.EMPTY); //Load the image for every label in the row
        }
        for (short x = 0; x < labelList[2].length; x++) {
            loadImage(labelList[2][x], Picture.WHITE); //Load the image for every label in the row
        }
    }

    private void loadImage(JLabel label, Picture picture) {
        //Method to simplify loading images
        String path = "";
        if (picture == Picture.BLACK) path = "pictures/pawn_black.png";
        else if (picture == Picture.WHITE) path = "pictures/pawn_white.png";
        else if (picture == Picture.EMPTY) path = "pictures/empty_field.png";
        else if (picture == Picture.BLACK_WIN) path = "pictures/black_win.png";
        else if (picture == Picture.WHITE_WIN) path = "pictures/white_win.png";
        _loadPicture(label, path);
    }

    private void _loadPicture(JLabel label, String path) {
        //Catch the exception if something went wrong with loading the image
        try {
            BufferedImage image = ImageIO.read(new File(path)); //Read the image
            ImageIcon icon = new ImageIcon(image); //Convert the image to an icon
            label.setIcon(icon); //Set the icon to the label
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
}