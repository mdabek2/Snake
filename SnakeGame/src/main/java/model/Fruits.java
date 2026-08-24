/**
 * Fruits class implementation.
 */
package snake.model;

import java.awt.*;
import java.util.*;

public class Fruits{
	private ArrayList<Point> snackList;
	
	// Constructor
	public Fruits()
	{
		snackList=new ArrayList<Point>();
	}

	// Adds a fruit to a given point on the board.
	public boolean addFruit(int x, int y)
	{
		Point fruit = new Point(x,y);

		if (snackList.contains(fruit))
        {
            System.err.println("This snack already exists");
            return false;
        }

		snackList.add(fruit);
		return true;
	}

	// Removes a fruit from a given point on the board.
	public boolean removeFruit(int x, int y)
	{
		Point fruit = new Point(x, y);

        return snackList.remove(fruit);
	}

	public boolean isFree(int x, int y)
	{
		Point point = new Point(x, y);
		
        return !snackList.contains(point);
	}
	
}
