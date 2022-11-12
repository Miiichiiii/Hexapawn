import java.awt.*;

public class Run {
    public static void main(String[] args) {
        run();
    }


    private static void run() {
        //Start the Application
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Implementation m = new Implementation();
                ComputerAlgorithm.implementation = m;
                m.setVisible(true);
            }
        });
    }
}
