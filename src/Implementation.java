import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Implementation extends GUI {
    public Implementation() {
        super();
    }

    private void initializeListener() {
        //For every label in the label list:
        //Add a ActionListener and run the onClick function
        for(int y = 0; y < labelList.length; y++) {
            for (int x = 0; x < labelList[0].length; x++) {
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

    }
}
