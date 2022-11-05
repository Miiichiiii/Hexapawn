import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Implementation extends GUI {
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

    private void clearColor() {
        for (JLabel[] jLabels : labelList) {
            for (JLabel label: jLabels) {
                label.setBackground(Color.WHITE);
            }
        }
    }
    public void onClick(MouseEvent e) {
        JLabel Current_Label = (JLabel) e.getComponent();
        Label LabelObj = Label.retrieveLabel(Current_Label);
        assert LabelObj != null; //IntelliJ cries without this

        if (Current_Label.getBackground() == Color.GREEN) { //Background is only green if the spot is eligible to move on
            LabelObj.setState(State.WHITE); // Set the state of an empty field to white, indicate move
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (State.SELECTED == Label.retrieveLabel(labelList[i][j]).getState()) { // Look for Selected Label
                        Label.retrieveLabel(labelList[i][j]).setState(State.EMPTY); // Replace with Empty Field
                        //NEED TO FIND WAY TO UPDATE PICTURES
                    }
                }
            }
        }
        clearColor();
        Current_Label.setBackground(Color.GRAY);
        // If the field has a white pawn, allow moves

        if (LabelObj.getState() == State.WHITE) {
            LabelObj.setState(State.SELECTED); // Select Current white Pawn, needed for moves
            short targetRow = (short) (LabelObj.y - 1); //The row in which the pawn can potentially move
            if (Map.MapState(targetRow, LabelObj.x) == State.EMPTY) { //Only allow forward move if field is empty
                labelList[targetRow][LabelObj.x].setBackground(Color.GREEN);
            }
            if (LabelObj.x + 1 < 3 && Map.MapState(targetRow, (short) (LabelObj.x + 1)) == State.BLACK) { //Only allow diagonal move if there is a black pawn
                labelList[targetRow][LabelObj.x].setBackground(Color.GREEN);
            }
            if (LabelObj.x + 1 > 0 && Map.MapState(targetRow, (short) (LabelObj.x - 1)) == State.BLACK) { //Only allow diagonal move if there is a black pawn
                labelList[targetRow][LabelObj.x].setBackground(Color.GREEN);
            }


            }

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