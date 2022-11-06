import java.awt.*;

public class Move {
    private static Label selected = null;
    private static Label left = null;
    private static Label forward = null;
    private static Label right = null;

    public static void clearColor() {
        if (selected != null) selected.getLabel().setBackground(Color.WHITE);
        if (forward != null) forward.getLabel().setBackground(Color.WHITE);
        if (left != null) left.getLabel().setBackground(Color.WHITE);
        if (right != null) right.getLabel().setBackground(Color.WHITE);
    }

    public static void resetMove() {
        clearColor();
        Move.selected = null;
        Move.forward = null;
        Move.right = null;
        Move.left = null;
    }

    public static Label getSelected() {
        return selected;
    }

    public static Label getLeft() {
        return left;
    }

    public static Label getForward() {
        return forward;
    }

    public static Label getRight() {
        return right;
    }

    public static void setSelected(Label selected) {
        resetMove();
        Move.selected = selected;
        selected.getLabel().setBackground(Color.GRAY);
    }

    public static void setLeft(Label left) {
        left.getLabel().setBackground(Color.GREEN);
        Move.left = left;
    }

    public static void setForward(Label forward) {
        forward.getLabel().setBackground(Color.GREEN);
        Move.forward = forward;
    }

    public static void setRight(Label right) {
        right.getLabel().setBackground(Color.GREEN);
        Move.right = right;
    }
}
