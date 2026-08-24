/*
 * The Controler component of Snake game in the MVC (Model-View-Controller) design pattern.
 */
package snake;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.stage.Stage;

import snake.model.*;

public final class SnakeController extends Application
{
	private SnakeModel snakeModel;
	private SnakeView snakeView;
	private SnakeController thisSnakeController = this;
	
	private boolean failed = false;
	
	private static final int MIN_INTERVAL = 70;
	private static final int SPEED_INCREASE = 20;
	private static final int FRUITS_PER_LEVEL = 5;
	
	private int initialInterval = 220;
	private int interval = 220;
	
	// Executor for SnakeModel refresh()
	private ScheduledExecutorService executor;

	public static void main(String[] args) {
		launch(args);
	}

	// Game constructor
	@Override
	public void start(Stage stage)
	{
		this.snakeModel = new SnakeModel(30);
		this.snakeView = new SnakeView(this.snakeModel);
		this.snakeView.setSnakeController(this);
		this.snakeView.start(stage);
	}
	
	// Starts game
	public void startGame()
	{
		this.snakeModel.resetBoard();
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Run(), 0, this.interval, TimeUnit.MILLISECONDS);
	}

	// Main loop game
	class Run implements Runnable
	{
		public Run() {}

		// Handles all changes made to the Model during gameplay. If any error occurs, the game will pause and require access to the Menu.
		public void run() 
		{
			thisSnakeController.setFailed(!snakeModel.moveSnake());
			
			if (!thisSnakeController.failed)
				updateSpeed();
			
			snakeModel.refreshBoard();
			snakeView.refreshBoard();
			
			if(thisSnakeController.failed)
			{
				thisSnakeController.setFailed(false);
    			executor.shutdown();
    			snakeView.showGameOver();
			}
		}
	}

	private void updateSpeed()
	{
    	int score = snakeModel.getScore();
		int speedLevel = score / FRUITS_PER_LEVEL;
		int newInterval = initialInterval - speedLevel * SPEED_INCREASE;
		
		if (newInterval < MIN_INTERVAL) 
			newInterval = MIN_INTERVAL;
		
		if (newInterval != this.interval)
    	{
        	this.interval = newInterval;
        	executor.shutdownNow();

        	executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleAtFixedRate(new Run(), this.interval, this.interval, TimeUnit.MILLISECONDS);
    	}
	}

	public void abort() {
		System.exit(0);
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public boolean getFailed() {
		return this.failed;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getInterval() {
		return interval;
	}

	public void setSpeed(int i) 
	{
		this.initialInterval = 220 - i * 20;
		this.interval = this.initialInterval;
	}
}