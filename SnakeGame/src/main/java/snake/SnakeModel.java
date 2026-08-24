/*
 * The Model component of Snake game in the MVC (Model-View-Controller) design pattern.
 */
package snake;

import java.awt.*;
import java.util.*;

import snake.model.*;

public class SnakeModel
{
    private Fields[][] board;
    private int size;
    private Snake snake;
    private Fruits fruits;
    private Directions move;
    private int score;

    public SnakeModel(int size)
    {
        this.board = new Fields[size][size];

        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                this.board[i][j] = Fields.EMPTY;

        this.size = size;
        this.snake = new Snake(size);
        this.fruits = new Fruits();
        this.move = Directions.RIGHT;
        this.setScore(0);

        this.refreshBoard();
    }

    // Refreshes the board content
    public void refreshBoard()
    {
        for (int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                if (snake.isMyBody(i, j))
                    board[i][j] = Fields.SNAKE;
                else if (!fruits.isFree(i, j))
                    board[i][j] = Fields.FRUIT;
                else
                    board[i][j] = Fields.EMPTY;
            }
        }
    }

    // Resets the board & adds new fruit
    public void resetBoard()
    {
        this.snake = new Snake(this.size);
        this.fruits = new Fruits();
        this.score = 0;
        this.move = Directions.RIGHT;

        refreshBoard();
        addFruit();
        refreshBoard();
    }

    // Moves snake in chosen direction 
    public boolean moveSnake()
    {
        switch (this.move) {
            case UP:
                return move(0, -1);
            case DOWN:
                return move(0, 1);
            case LEFT:
                return move(-1, 0);
            case RIGHT:
                return move(1, 0);
            default:
                return false;
        }
    }

    // Moves snake's body
    private boolean move(int dx, int dy)
    {
        Point head = this.snake.getHead();
        Point oldTail = snake.getTail();

        int newHeadX = head.x + dx;
        int newHeadY = head.y + dy;
        int newTongueX = head.x + 2 * dx;
        int newTongueY = head.y + 2 * dy;

        boolean headEatsFruit, tongueEatsFruit, eatingFruit = false;

        // Check for collision with the board boundaries
        if (newHeadX < 0 || newHeadX >= size || newHeadY < 0 || newHeadY >= size)
            return false;
        
        // Check if the head/tongue hits the fruit
        headEatsFruit = !fruits.isFree(newHeadX, newHeadY);
        tongueEatsFruit = false;

        if (newTongueX >= 0 && newTongueX < size && newTongueY >= 0 && newTongueY < size)
            tongueEatsFruit = !fruits.isFree(newTongueX, newTongueY);
        
       eatingFruit = headEatsFruit || tongueEatsFruit;

        // Check for collision with own body
        if (snake.willCollide(newHeadX, newHeadY, eatingFruit))
            return false;

        // Move snake
        snake.move(dx, dy);

        if (eatingFruit)
        {
            snake.grow(oldTail);

            if (headEatsFruit)
                fruits.removeFruit(newHeadX, newHeadY);

            if (tongueEatsFruit)
                fruits.removeFruit(newTongueX, newTongueY);

            score++;
            refreshBoard();
            addFruit();
        }

        refreshBoard();

        return true;
    }

    // Adds fruit in random place
    public void addFruit()
    {
        Random rand = new Random();

        while (true)
        {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);

            if (isFree(x, y))
            {
                fruits.addFruit(x, y);
                break;
            }
        }
    }

    public void setMove(Directions direction)
    {
        if (direction == null)
            return;

        if (move == Directions.UP && direction == Directions.DOWN)
            return;

        if (move == Directions.DOWN && direction == Directions.UP)
            return;

        if (move == Directions.LEFT && direction == Directions.RIGHT)
            return;

        if (move == Directions.RIGHT && direction == Directions.LEFT)
            return;

        this.move = direction;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Directions getMove() {
        return move;
    }

    public int getSize() {
        return size;
    }

    public Fields[][] getBoard() {
        return board;
    }

    public Point getTonguePos() {
        return snake.getTongue();
    }

    public Point getHeadPos() {
        return snake.getHead();
    }

    public boolean isTongueOnBody() {
		return snake.willCollide(snake.getTongue().x, snake.getTongue().y, true);
	}
   
    private boolean isFree(int x, int y){
        return board[x][y] == Fields.EMPTY;
    }
}