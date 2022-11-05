public class Map {
    public static State MapState(short y, short x) {
        /*
        boolean turn = true; // true = whites turn / false = blacks turn

        // create a Map of the current States for move logic

        State[][] stateMap = new State[3][3];

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stateMap[i][j] = Label.labelList.get(i*3+j).getState();

            }
        }

        return stateMap[y][x];
        /*stateMap = state_0_0, state_0_1, state_0_2
                     state_1_0, state_1_1, state_1_2
                     state_2_0, state_2_1, state_2_2
         */
        return Label.labelList.get(y * 3 + x).getState();
    }
}
