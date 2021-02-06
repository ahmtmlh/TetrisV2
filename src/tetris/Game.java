package tetris;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game extends JPanel {

    private static final Color[] colors = {
            Color.ORANGE,
            Color.BLUE,
            Color.RED,
            Color.green,
            Color.YELLOW,
            Color.cyan
    };

    private static final Color BG_COLOR = new Color(0x363636);
    private final AtomicBoolean keyPressed;
    private final AtomicBoolean gameOver;

    private static final int GRID_Y = 20;
    private static final int GRID_X = 10;

    private static final int TETROMINO_SPAWN_X = 4;
    private static final int TETROMINO_SPAWN_Y = 0;

    // Game variables
    private final Timer mainTimer;
    private TimerTask moveDownTask;
    private int moveDownPeriod;
    private final Block[][] grid;
    private Tetromino tetromino;
    private Tetromino nextTetromino;
    private final Random random;
    private int score;

    private class MoveDownTask extends TimerTask {
        @Override
        public void run() {
            if (tetromino != null)
                moveTetrominoDown();
        }
    }

    public Game() {
        grid = new Block[GRID_Y][GRID_X];
        random = new Random();
        keyPressed = new AtomicBoolean(false);
        gameOver = new AtomicBoolean(false);
        nextTetromino = new Tetromino(getRandomShape(), getRandomColor(), TETROMINO_SPAWN_X, TETROMINO_SPAWN_Y);
        score = 0;
        mainTimer = new Timer();
        moveDownTask = new MoveDownTask();
        moveDownPeriod = 1000;

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (keyPressed.get()) {
                    return;
                }
                keyPressed.set(true);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        if (tetromino != null)
                            moveTetrominoDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveTetrominoLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveTetrominoRight();
                        break;
                    case KeyEvent.VK_UP:
                        Block[] rotatedBlocks = tetromino.getRotatedBlocks();
                        if (checkValidity(rotatedBlocks, 0, 0))
                            tetromino.validateRotation(rotatedBlocks);
                        break;
                    case KeyEvent.VK_SPACE:
                        while (tetromino != null)
                            moveTetrominoDown();
                        break;
                    default:
                        keyPressed.set(false);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        mainTimer.schedule(moveDownTask, 1000, moveDownPeriod);
        // Rendering task, fixed at 25fps
        mainTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
                if (gameOver.get())
                    cancel();
                keyPressed.set(false);
                // Not the best solution, but works
                if (!hasFocus()) {
                    requestFocusInWindow();
                }
            }
        }, 0, 40);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(BG_COLOR);
        //Game logic
        logic();

        // Game over
        if (gameOver.get()) {
            //g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", getWidth() / 2 - 27, getHeight() / 2);
            g.drawString("Total Score: " + score, getWidth() / 2 - (30 + ((int)Math.floor(Math.log10(score)))*4), getHeight() / 2 + 30);
            return;
        }

        //Render
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        int xOff = 0;
        int yOff = 0;
        // Draw grid
        for (Block[] blocks : grid) {
            for (int j = 0; j < grid[0].length; j++) {
                Block current = blocks[j];
                g.setColor(current == null ? Color.white : current.getColor());
                g.fillRect(xOff, yOff, 29, 29);
                xOff += 30;
            }
            yOff += 30;
            xOff = 0;
        }

        // Draw tetromino on grid
        if (tetromino != null){
            g.setColor(tetromino.getBlocks()[0].getColor());
            for (Block block : tetromino.getBlocks()) {
                int x = (tetromino.x + block.getX()) * 30;
                int y = (tetromino.y + block.getY()) * 30;
                g.fillRect(x, y, 29, 29);
            }
        }

        //Draw score
        g.setColor(Color.white);
        g.drawString("Score: " + score, 320, 50);

        // Draw next piece
        g.drawString("Next Piece", 320, 120);
        g.drawRect(320, 140, 120, 150);
        for (Block block : nextTetromino.getBlocks()) {
            int x = (11 + block.getX()) * 30;
            int y = (5 + block.getY()) * 30;
            g.setColor(block.getColor());
            g.fillRect(x, y, 29, 29);
        }
    }

    private void logic() {
        // Check current tetromino
        if (tetromino == null) {
            tetromino = nextTetromino;
            int i = 0;
            while (i > -3){
                if (!checkValidity(tetromino.getBlocks(), 0, 0)){
                    i--;
                    tetromino.x--;
                }
                else
                    break;
            }
            if (i <= -3)
                gameOver.set(true);
            nextTetromino = new Tetromino(getRandomShape(), getRandomColor(), TETROMINO_SPAWN_X, TETROMINO_SPAWN_Y);
        }
        // Check rows
        for (int i = grid.length - 1; i > 0; i--) {
            Block[] row = grid[i];
            boolean complete = true;
            for (Block block : row) {
                if (block == null) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                shiftDown(i);
                // Check for the same row
                i++;
                score += 100;
            }
        }
        // Speed up tetromino falling if score is high
        if (moveDownPeriod > 500 && score != 0 && score % 500 == 0) {
            moveDownTask.cancel();
            moveDownTask = new MoveDownTask();
            moveDownPeriod -= 150;
            mainTimer.schedule(moveDownTask, 0, moveDownPeriod);
        }
    }

    private TetrominoShape getRandomShape() {
        return TetrominoShape.values()[random.nextInt(TetrominoShape.values().length)];
    }

    private Color getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

    private void shiftDown(int index) {
        //Shift all one down.
        for (int i = index; i > 0; i--) {
            System.arraycopy(grid[i - 1], 0, grid[i], 0, grid[0].length);
        }
        //Fill the upper line with zeros.
        Arrays.fill(grid[0], null);

    }

    private boolean checkValidity(Block[] blocks, int xOff, int yOff) {
        for (Block block : blocks) {
            int gridX = block.getX() + tetromino.x + xOff;
            int gridY = block.getY() + tetromino.y + yOff;
            if (gridY >= GRID_Y || gridX >= GRID_X || gridY < 0 || gridX < 0 || grid[gridY][gridX] != null)
                return false;
        }
        return true;
    }

    private void moveTetrominoRight() {
        if (checkValidity(tetromino.getBlocks(), 1, 0))
            tetromino.moveRight();
    }

    private void moveTetrominoLeft() {
        if (checkValidity(tetromino.getBlocks(), -1, 0))
            tetromino.moveLeft();
    }

    private void moveTetrominoDown() {
        if (!moveTetrominoDownAction())
            storeTetrominoBlocks();
    }

    private boolean moveTetrominoDownAction() {
        // Check validity with y+1, which is moving down
        if (checkValidity(tetromino.getBlocks(), 0, 1)) {
            tetromino.moveDown();
            return true;
        }
        return false;
    }

    private void storeTetrominoBlocks() {
        for (Block block : tetromino.getBlocks()) {
            int gridX = block.getX() + tetromino.x;
            int gridY = block.getY() + tetromino.y;
            grid[gridY][gridX] = Block.fromBlock(block);
        }
        tetromino = null;
    }
}