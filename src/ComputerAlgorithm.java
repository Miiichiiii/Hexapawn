import enums.State;
import enums.Turn;
import enums.Win;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ComputerAlgorithm {
    public static Node root = null;
    public static Node current = null;
    public static Implementation implementation;
    public static Thread thread;

    public static void startThread() {
        thread = new Thread(() -> findMove(root));
        thread.start();
    }

    public static State[][] createStateArray() {
        //Creates a State array of the current score
        State[][] res = new State[3][3];
        for(int y = 0; y < 3; y++) {
            State[] row = new State[3];
            for(int x = 0; x < 3; x++) {
                row[x] = Label.labelList.get(y * 3 + x).getState();
            }
            res[y] = row;
        }
        return res;
    }

    public static State[][] createStateArray(String value) {
        //Method used to create a StateArray by a String which is used to store the State[][] array in JSON
        State state = State.EMPTY;
        State[][] stateArray = new State[3][3];
        State[] row;
        for(int y = 0; y < 3; y++) {
            row = new State[3];
            for(int x = 0; x < 3; x++) {
                switch (value.charAt(y * 3 + x) - '0') {
                    case 1:
                        state = State.BLACK;
                        break;
                    case 2:
                        state = State.WHITE;
                        break;
                    case 0:
                        state = State.EMPTY;
                        break;
                }
                row[x] = state;
            }
            stateArray[y] = row;
        }
        return stateArray;
    }

    public static void onMove() {
        //Gets called when the white Player makes his move to notify the AI to make its move
        thread.interrupt();
    }
    public static ArrayList<Label[]> getMoves() {
        //Get all the possible moves of the current score
        Label LabelObj;
        ArrayList<Label[]> allMoves = new ArrayList<>(); //Arraylist<[origin, target]>
        //Loop over all pawns
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                LabelObj = Label.retrieveLabel((short) x, (short) y); //Get the label corresponding to the x and y coordinates
                short targetRow = (LabelObj.getState() == State.BLACK) ? (short) (LabelObj.y + 1) : (short) (LabelObj.y - 1); //Get the target row in which the pawn could move

                //Check if move is allowed
                if ((LabelObj.getState() == State.BLACK && Implementation.turn == Turn.BLACK) || (LabelObj.getState() == State.WHITE && Implementation.turn == Turn.WHITE)) {
                    if (targetRow > 2 || targetRow < 0) continue; //Avoid IndexOutOfBounds Error
                    if (Move.forwardPossible(Label.retrieveLabel(LabelObj.x, targetRow))) { //Check if the forward move is allowed
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel(LabelObj.x, targetRow)});
                    }
                    if (Move.rightPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x + 1), targetRow))) { //Check if the right move is allowed
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel((short) (LabelObj.x + 1), targetRow)});
                    }
                    if (Move.leftPossible(LabelObj, Label.retrieveLabel((short) (LabelObj.x - 1), targetRow))) { //Check if the left move is allowed
                        allMoves.add(new Label[] {Label.retrieveLabel(LabelObj.x, LabelObj.y), Label.retrieveLabel((short) (LabelObj.x - 1), targetRow)});
                    }
                }
            }
        }
        return allMoves;

    }

    public static boolean childExists(Node root, State[][] nextState) {
        //Returns true if there exists such a child with this score
        return getChild(root, nextState) != null;
    }

    public static Node getChild(Node root, State[][] nextState) {
        for(Node child: root.children) {
            if(compareStateArrays(child.value, nextState)) {
                return child; //Finds and returns the child corresponding to the score, if it exists
            }
        }
        return null;
    }

    public static boolean findMove(Node root) {
        current = root; //Set the current position of the game in the tree

        Win rv = implementation.checkWin(); //Check for wins (BaseCase)
        if(rv == Win.WHITEWIN) {
            implementation.onWin(rv);
            return true; //Returns true if the path corresponding to the loose needs to be deleted
        }
        else if(rv == Win.BLACKWIN) {
            implementation.onWin(rv);
            return false;
        }

        if(root == null || root.turn == Turn.WHITE) { //Check if it is white's move
            try {
                synchronized (thread) {
                    thread.wait(); //Wait for the white players move
                }
            } catch (InterruptedException ignored) {}
        }

        if(root == null) {
            root = ComputerAlgorithm.root; //Root is evaluated by createChildren method
        }

        if(root.turn == Turn.WHITE) {
            Node child = getChild(root, createStateArray()); //Get the child corresponding to the player's move
            Implementation.turn = Turn.BLACK; //Indicate that Black is now on turn
            return findMove(child); //Make the recursive call and return the value.
            //It is not checked here whether the path needs to be deleted, as only black moves can be changed
        }
        else {
            createChildren(root); //Creates children if they don't exist

            //Get all the children which are not deleted
            ArrayList<Node> notDeleted = new ArrayList<>();
            for(Node child: root.children) {
                if(!child.deleted) {
                    notDeleted.add(child);
                }
            }

            //Pick a random move which has not been deleted
            Random random = new Random();
            int rand = random.nextInt(notDeleted.size());
            Node child = getChild(root, notDeleted.get(rand).value);


            State[][] currentState;
            ArrayList<Label[]> possibleMoves = getMoves();
            for(Label[] currentMove : possibleMoves) {
                currentState = createStateArray();
                currentState[currentMove[0].y][currentMove[0].x] = State.EMPTY;
                currentState[currentMove[1].y][currentMove[1].x] = currentMove[0].getState();
                if(compareStateArrays(currentState, child.value)) { //Find the move corresponding to the randomly selected child
                    implementation.movePawn(currentMove[0], currentMove[1]); //Move the pawn
                    Implementation.turn = Turn.WHITE; //Indicate that white is now on turn
                    if(findMove(child)) { //Make the recursive call and check if current child needs to be deleted
                        child.deleted = true; //Delete the child
                        return checkTwoLevelChildren(root); //Check if all children are deleted then return true because this Node needs to be deleted too
                    }
                    break;
                }
            }
            return false;
        }
    }

    public static boolean checkTwoLevelChildren(Node root) {
        //Returns true if all two level children have been deleted
        for(int i = 0; i < root.children.size(); i++) {
            for(int j = 0; j < root.children.get(i).children.size(); i++) {
                if(!root.children.get(i).children.get(j).deleted) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void createChildren(Node current) {
        ArrayList<Label[]> possibleMoves = getMoves();
        if(current == null) {
            root = new Node(createStateArray(), Turn.WHITE); //If there is no tree, create one
            current = root;
        }
        State[][] currentState;
        //Create tree possibilities
        for (Label[] possibleMove : possibleMoves) {
            //Basically check if the child already exists and if not add it to tree
            currentState = createStateArray();
            currentState[possibleMove[0].y][possibleMove[0].x] = State.EMPTY;
            currentState[possibleMove[1].y][possibleMove[1].x] = possibleMove[0].getState();
            if (!childExists(current, currentState)) {
                current.addChildren(new Node(currentState, (current.turn == Turn.BLACK) ? Turn.WHITE : Turn.BLACK));
            }
        }
    }

    public static boolean compareStateArrays(State[][] a1, State[][] a2) {
        return Arrays.deepEquals(a1, a2);
    }

    public static String getStateString(Node current) {
        //Method is used to convert the value (State[][]) of the current Node to a String representation
        String stateString = "";
        int stateInt = 0;
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                switch (current.value[y][x]) { //Convert enum to int
                    case BLACK:
                        stateInt = 1;
                        break;
                    case EMPTY:
                        stateInt = 0;
                        break;
                    case WHITE:
                        stateInt = 2;
                }
                stateString += stateInt;
            }
        }
        return stateString;
    }

    public static String getJson(Node current) {
        //Convert the current Node to a JSON String
        return getJsonObject(current).toString();
    }

    private static JSONObject getJsonObject(Node current) {
        //Recursive method which will create a JSONObject of the current Node
        JSONObject main = new JSONObject();
        main.append("value", getStateString(current));
        main.append("deleted", current.deleted);
        main.append("turn", current.turn);

        for(int i = 0; i < current.children.size(); i++) {
            main.append("children", getJsonObject(current.children.get(i))); //For all children of the current Node, create a JSONObject
        }
        return main; //Return the JSONObject
    }

    public static void loadJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        root = getNode(jsonObject); //Create the tree by the String JSON representation
    }

    private static Node getNode(JSONObject obj) {
        //Method is used to construct the tree by the JSONObjects
        Turn turn = (obj.getJSONArray("turn").getString(0).equals("WHITE")) ? Turn.WHITE : Turn.BLACK;
        Node current = new Node(createStateArray(obj.getJSONArray("value").getString(0)), turn);
        current.deleted = obj.getJSONArray("deleted").getBoolean(0);

        if(obj.has("children")) {
            JSONArray jsonArray = obj.getJSONArray("children");
            for (int i = 0; i < jsonArray.length(); i++) {
                current.addChildren(getNode(jsonArray.getJSONObject(i)));
            }
        }
        return current;
    }

}