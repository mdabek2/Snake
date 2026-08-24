/**
 * Snake class implementation.
 * body[0] - tongue
 * body[1] - head
 * body[2...] - body
 */
package snake.model;

import java.awt.Point;
import java.util.ArrayList;

public class Snake
{
    private ArrayList<Point> body;
    private int length;

    // Constructor
    public Snake(int boardSize)
    {
        this.length = 5;
        this.body = new ArrayList<Point>();

        int center = boardSize / 2;

        for (int i = 0; i < 5; i++)
        {
            body.add(new Point(center - i, center));
        }
    }

    // Moves snake according to given direction
    public void move(int dx, int dy)
    {
        Point oldHead = new Point(body.get(1));
        Point previous = oldHead;
        Point newHead = new Point(oldHead.x + dx, oldHead.y + dy);
        Point newTongue = new Point(oldHead.x + 2 * dx, oldHead.y + 2 * dy);
        
        body.set(0, newTongue);
        body.set(1, newHead);

        for (int i = 2; i < length; i++)
        {
            Point current = body.get(i);
            body.set(i, previous);
            previous = current;
        }
    }

    // Checks if given point is part of snake's body.
    public boolean isMyBody(int x, int y)
    {
        for (int i = 0; i < length; i++)
        {
            Point point = body.get(i);

            if (point.x == x && point.y == y)
                return true;
        }

        return false;
    }

    // Checks whether the new head position will cause a collision with the snake's body.
    // The tongue is skipped because it moves with the head.
    // The tail is skipped during normal movement because it will leave its current position in the same movement.
    public boolean willCollide(int x, int y, boolean growing)
	{
		for (int i = 1; i < length; i++)
		{
			if (!growing && i == length - 1)
				continue;

			Point point = body.get(i);

			if (point.x == x && point.y == y)
				return true;
		}
		return false;
	}
    
    // Lengthens the snake.
    public void grow(Point newTail)
    {
        body.add(new Point(newTail));
        length++;
    }

    public Point getTongue() {
        return body.get(0);
    }

    public Point getHead() {
        return body.get(1);
    }

    public Point getTail() {
        return body.get(length - 1);
    }

    public int getLength() {
        return length;
    }
}