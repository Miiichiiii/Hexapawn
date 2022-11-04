import javax.swing.*;

public class Map {
    public void MapState() {
        State currentState = State.EMPTY;
        boolean turn = true; // true = whites turn / false = blacks turn

        State[][] stateMap = new State[3][3];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stateMap[i][j] = Label.labelList.get(i*3+j).getState();

            }
        }
    }
}
