package tetris;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        Game g = new Game();
        JFrame game = new JFrame();
        game.setTitle("Tetris");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(460, 630);
        game.setResizable(true);

        game.setLocationRelativeTo(null);
        game.setVisible(true);
        game.setResizable(false);

        game.add(g);

    }

}
