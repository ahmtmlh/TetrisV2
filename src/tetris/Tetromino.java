package tetris;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Tetromino {

    private final Block[] blocks;
    private final TetrominoShape shape;
    private final Color color;
    public int x;
    public int y;

    public Tetromino(TetrominoShape shape, Color color, int x, int y) {
        this.blocks = new Block[4];
        this.shape = shape;
        this.color = color;
        this.x = x;
        this.y = y;
        initTetromino();
    }

    private void initTetromino() {
        switch (shape) {
            case L_SHAPE:
                blocks[0] = new Block(0, 0, color);
                blocks[1] = new Block(0, 1, color);
                blocks[2] = new Block(0, 2, color);
                blocks[3] = new Block(1, 2, color);
                break;
            case BOX_SHAPE:
                blocks[0] = new Block(0, 0, color);
                blocks[1] = new Block(0, 1, color);
                blocks[2] = new Block(1, 0, color);
                blocks[3] = new Block(1, 1, color);
                break;
            case Z_SHAPE:
                blocks[0] = new Block(0, 0, color);
                blocks[1] = new Block(1, 0, color);
                blocks[2] = new Block(1, 1, color);
                blocks[3] = new Block(2, 1, color);
                break;
            case T_SHAPE:
                blocks[0] = new Block(0, 0, color);
                blocks[1] = new Block(1, 0, color);
                blocks[2] = new Block(2, 0, color);
                blocks[3] = new Block(1, 1, color);
                break;
            case STICK_SHAPE:
                blocks[0] = new Block(0, 0, color);
                blocks[1] = new Block(0, 1, color);
                blocks[2] = new Block(0, 2, color);
                blocks[3] = new Block(0, 3, color);
                break;
            case INVERSE_L_SHAPE:
                blocks[0] = new Block(1, 0, color);
                blocks[1] = new Block(1, 1, color);
                blocks[2] = new Block(1, 2, color);
                blocks[3] = new Block(0, 2, color);
                break;
            case INVERSE_Z_SHAPE:
                blocks[0] = new Block(1, 0, color);
                blocks[1] = new Block(2, 0, color);
                blocks[2] = new Block(0, 1, color);
                blocks[3] = new Block(1, 1, color);
                break;
            default:
                break;
        }
    }

    /*
    private Block leftMostBlock() {
        int min = Integer.MAX_VALUE;
        Block farLeftBlock = null;
        for (Block block : blocks) {
            if (block.getX() < min) {
                min = block.getX();
                farLeftBlock = block;
            }
        }
        return farLeftBlock;
    }

    private Block rightMostBlock() {
        int max = Integer.MIN_VALUE;
        Block farRightBlock = null;
        for (Block block : blocks) {
            if (block.getX() > max) {
                max = block.getX();
                farRightBlock = block;
            }
        }
        return farRightBlock;
    }

    private Block lowerMostBlock() {
        int max = Integer.MIN_VALUE;
        Block farDownBlock = null;
        for (Block block : blocks) {
            if (block.getY() > max) {
                max = block.getY();
                farDownBlock = block;
            }
        }
        return farDownBlock;
    }
    */

    // Function to rotate the matrix 90 degree clockwise
    private void rotate90Clockwise(int[][] a) {
        int N = 4;
        for (int i = 0; i < N / 2; i++) {
            for (int j = i; j < N - i - 1; j++) {
                int temp = a[i][j];
                a[i][j] = a[N - 1 - j][i];
                a[N - 1 - j][i] = a[N - 1 - i][N - 1 - j];
                a[N - 1 - i][N - 1 - j] = a[j][N - 1 - i];
                a[j][N - 1 - i] = temp;
            }
        }
    }

    public void moveDown() {
        y++;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public Block[] getRotatedBlocks() {
        int[][] matrix = new int[4][4];

        for (Block block : blocks) {
            matrix[block.getY()][block.getX()] = 1;
        }
        rotate90Clockwise(matrix);
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        List<Block> temp = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 1) {
                    temp.add(new Block(j, i, null));
                    minX = Integer.min(j, minX);
                    minY = Integer.min(i, minY);
                }
            }
        }
        int finalMinX = minX;
        int finalMinY = minY;
        temp.forEach(block -> {
            block.setX(block.getX() - finalMinX);
            block.setY(block.getY() - finalMinY);
        });
        Block[] newBlocks = new Block[4];
        temp.toArray(newBlocks);

        return newBlocks;
    }

    public void validateRotation(Block[] rotatedBlocks) {
        for (int i = 0; i < blocks.length; i++) {
            blocks[i].setX(rotatedBlocks[i].getX());
            blocks[i].setY(rotatedBlocks[i].getY());
        }
    }

    public Block[] getBlocks() {
        return blocks;
    }
}
