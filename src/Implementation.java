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
        //Add a ActionListener and run the onClick function
        for(int y = 0; y < labelList.length; y++) {
            for (int x = 0; x < labelList[0].length; x++) {
                labelList[y][x].setOpaque(true);
                labelList[y][x].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onClick(e);
                        super.mouseClicked(e);
                    }
                });
            }
        }


    }


    private void onClick(MouseEvent e) {
        JLabel Current_Label = (JLabel) e.getComponent();
        Current_Label.setBackground(Color.GREEN);
        // If the Field has a white pawn, allow move
        Label LabelObj = Label.retrieveLabel(Current_Label);
        if (LabelObj.state == State.WHITE) {


        }
    }

    private boolean initializePictures() {
        boolean works;
        for (short x = 0; x < labelList[0].length; x++) {
            works = load_image(labelList[0][x], "pictures/pawn_black.png"); //Load the Image for every Label in the row
            new Label(labelList[0][x], x, (short) 0, State.BLACK); //Instanciate the Label class
            if (!works) return false; //Return false if an error occured while loading the images.
        }
        for (short x = 0; x < labelList[1].length; x++) {
            works = load_image(labelList[1][x], "pictures/empty_field.png");
            new Label(labelList[1][x], x, (short) 1, State.EMPTY);
            if (!works) return false;
        }
        for (short x = 0; x < labelList[2].length; x++) {
            works = load_image(labelList[2][x], "pictures/pawn_white.png");
            new Label(labelList[2][x], x, (short) 2, State.WHITE);
            if (!works) return false;
        }
        return true;
    }

    private boolean load_image(JLabel label, String path) {
        //Catch the Exception if something went wrong with loading the image
        try {
            BufferedImage image = ImageIO.read(new File(path)); //Read the image
            ImageIcon icon = new ImageIcon(image); //Convert the image to an icon
            label.setIcon(icon); //Set the picture to the label
        }
        catch (IOException ex) {
            return false;
        }
        return true;
    }
}