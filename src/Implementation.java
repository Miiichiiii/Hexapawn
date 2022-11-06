import enums.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Implementation extends GUI {
    public static Turn turn = Turn.WHITE;
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


    }

    public void onClick(MouseEvent e) {
        move(e);
        if (checkWin() == Win.BLACKWIN) {
            System.out.println("Black won");
        }
        if (checkWin() == Win.WHITEWIN) {
            System.out.println("White won"); //This is an elementary solution. TODO: Implement a Win Screen and Score
        }
    }

    private void _movePawn(Label origin, Label target) {
        target.setState(origin.getState());
        loadImage(origin.getLabel(), Picture.EMPTY);

        if (origin.getState() == State.BLACK) loadImage(target.getLabel(), Picture.BLACK);
        else loadImage(target.getLabel(), Picture.WHITE);

        origin.setState(State.EMPTY);
    }

    public void move(MouseEvent e) {
        JLabel Current_Label = (JLabel) e.getComponent();
        Label LabelObj = Label.retrieveLabel(Current_Label);
        assert LabelObj != null; //IntelliJ cries without this

        if (LabelObj == Move.getForward() || LabelObj == Move.getLeft() || LabelObj == Move.getRight()) { //If Label can be the new position of the pawn
            _movePawn(Move.getSelected(), LabelObj); //Move the pawn to the new field
            Move.resetMove(); //Clear the move variables and color up
            turn = (turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK; //Other player's turn
            return;
        }

        Move.resetMove(); //Clear the move variables and color up
        short targetRow = (LabelObj.getState() == State.BLACK) ? (short) (LabelObj.y + 1) : (short) (LabelObj.y - 1); //The row in which the pawn can potentially move
        // If the field has a pawn and the turn is right, allow moves
        if ((LabelObj.getState() == State.BLACK && turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && turn == Turn.WHITE)) {
            Move.setSelected(LabelObj); //Set the Label to selected
            if (targetRow > 2 || targetRow < 0) return;
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
    }

    public Win checkWin() { //TODO: Implement this and find way to check if no pawn is movable anymore
        int AmountWhitePawns = 0, AmountBlackPawns = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label CheckLabel = Label.retrieveLabel(labelList[i][j]);
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
                // TODO: WIN CONDITION: if every pawn has an opponent pawn directly infront of it and no pawn diagonal to it, declare a win
                // This below sucks. WIP for the TODO above
                // TODO: FIND A BETTER WAY THAN THIS TO CHECK THE WIN CONDITION
                if ((CheckLabel.getState() == State.BLACK) && ((Label.retrieveLabel(labelList[CheckLabel.y + 1][CheckLabel.x]).getState() != State.EMPTY))) {
                    if ((CheckLabel.x < 3) && (Label.retrieveLabel(labelList[CheckLabel.y + 1][CheckLabel.x + 1]).getState() != State.EMPTY)) {
                        CheckLabel.setMovable(true);
                    }
                    else if((CheckLabel.x > 0) && (Label.retrieveLabel(labelList[CheckLabel.y + 1][CheckLabel.x - 1]).getState() != State.EMPTY)) {
                        CheckLabel.setMovable(true);
                    }
                    else {
                        CheckLabel.setMovable(false);
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

        return Win.UNDECIDED;
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
        String path;
        if (picture == Picture.BLACK) path = "pictures/pawn_black.png";
        else if (picture == Picture.WHITE) path = "pictures/pawn_white.png";
        else path = "pictures/empty_field.png";
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