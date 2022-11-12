import enums.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Implementation extends GUI {
    public static Turn turn = Turn.WHITE;
    public static boolean won = false;
    public static ArrayList<Win> score = new ArrayList<>();
    public static int black_wins = 0;
    public Implementation() {
        super();
        initializeListener();
        if(!initializePictures()) {
            System.out.println("Something went wrong with initializing the pictures");
        }
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


    }

    public void onNewFileClick() {
        //TODO: Reset the AI trained values of the matchbox algorithm
    }

    public void onOpenFileClick() {
        int result = this.fileChooser.showOpenDialog(this); //Show the fileChooser dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();  //Get the selected file
            String path = file.getAbsolutePath();
            //TODO: Load up the AI trained values for the matchbox algorithm
        }
    }

    public void onSaveFileClick() {
        int result = this.fileChooser.showSaveDialog(this); //Show the fileChooser dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile(); //Get the selected file
            String path = file.getAbsolutePath();
            //TODO: Save the AI trained values of the matchbox algorithm
        }
    }

    public void onNewGameClick() {
        ResetGame();
    }

    public void updateScoreBoard(Win win) {
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

        scoreBoard.setText(display);
    }

    public void onClick(MouseEvent e) {
        if(won) return;
        if (move(e)) {//Only check for win if a pawn has been moved
            Win rv = checkWin();
            if (rv == Win.BLACKWIN) {
                won = true;
                loadImage(winnerLabel, Picture.BLACK_WIN);
                updateScoreBoard(rv);
            }
            else if (rv == Win.WHITEWIN) {
                won = true;
                loadImage(winnerLabel, Picture.WHITE_WIN);
                updateScoreBoard(rv);
            }
        }
    }

    public void movePawn(Label origin, Label target) {
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
            movePawn(Move.getSelected(), LabelObj); //Move the pawn to the new field
            Move.resetMove(); //Clear the move variables and color up
            turn = (turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK; //Other player's turn
            return true;
        }

        Move.resetMove(); //Clear the move variables and color up
        short targetRow = (LabelObj.getState() == State.BLACK) ? (short) (LabelObj.y + 1) : (short) (LabelObj.y - 1); //The row in which the pawn can potentially move
        // If the field has a pawn and the turn is right, allow moves
        if ((LabelObj.getState() == State.BLACK && turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && turn == Turn.WHITE)) {
            Move.setSelected(LabelObj); //Set the Label to selected
            if (targetRow > 2 || targetRow < 0) return false;
            if (Move.forwardPossible(Label.retrieveLabel(LabelObj.x, targetRow))) { //Only allow forward move if field is empty
                Move.setForward(Label.retrieveLabel(LabelObj.x, targetRow)); //Set the Label to be a potential new position
            }
            if (Move.rightPossible(LabelObj, Label.retrieveLabel((short)(LabelObj.x + 1), targetRow)))
            { //Only allow diagonal move if there is a pawn and the turn is right
                Move.setRight(Label.retrieveLabel((short)(LabelObj.x + 1), targetRow)); //Set the Label to be a potential new position
            }
            if (Move.leftPossible(LabelObj, Label.retrieveLabel((short)(LabelObj.x - 1), targetRow)))
            { //Only allow diagonal move if there is a pawn and the turn is right
                Move.setLeft(Label.retrieveLabel((short)(LabelObj.x - 1), targetRow)); //Set the Label to be a potential new position
            }

        }
        return false;
    }

    public Win checkWin() {
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

    private void ResetGame() {
        winnerLabel.setIcon(null);
        won = false;
        initializePictures();
        Move.resetMove();
        Label.ResetLabels();
        turn = Turn.WHITE;
    }

    private boolean initializePictures() {
        boolean works;
        for (short x = 0; x < labelList[0].length; x++) {
            works = loadImage(labelList[0][x], Picture.BLACK); //Load the image for every label in the row
            new Label(labelList[0][x], x, (short) 0, State.BLACK); //Instantiate the label class
            if (!works) return false; //Return false if an error occurred while loading the images.
        }
        for (short x = 0; x < labelList[1].length; x++) {
            works = loadImage(labelList[1][x], Picture.EMPTY); //Load the image for every label in the row
            new Label(labelList[1][x], x, (short) 1, State.EMPTY); //Instantiate the label class
            if (!works) return false; //Return false if an error occurred while loading the images.
        }
        for (short x = 0; x < labelList[2].length; x++) {
            works = loadImage(labelList[2][x], Picture.WHITE); //Load the image for every label in the row
            new Label(labelList[2][x], x, (short) 2, State.WHITE); //Instantiate the label class
            if (!works) return false; //Return false if an error occurred while loading the images.
        }
        return true;
    }

    private boolean loadImage(JLabel label, Picture picture) {
        //Method to simplify loading images
        String path = "";
        if (picture == Picture.BLACK) path = "pictures/pawn_black.png";
        else if (picture == Picture.WHITE) path = "pictures/pawn_white.png";
        else if (picture == Picture.EMPTY) path = "pictures/empty_field.png";
        else if (picture == Picture.BLACK_WIN) path = "pictures/black_win.png";
        else if (picture == Picture.WHITE_WIN) path = "pictures/white_win.png";
        return _loadPicture(label, path);
    }

    private boolean _loadPicture(JLabel label, String path) {
        //Catch the exception if something went wrong with loading the image
        try {
            BufferedImage image = ImageIO.read(new File(path)); //Read the image
            ImageIcon icon = new ImageIcon(image); //Convert the image to an icon
            label.setIcon(icon); //Set the icon to the label
        }
        catch (IOException ex) {
            return false;
        }
        return true;
    }
}