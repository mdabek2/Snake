/*
 * The View component of Snake game in the MVC (Model-View-Controller) design pattern.
 */
package snake;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import snake.model.Directions;
import snake.model.Fields;
import snake.score.ScoreEntry;
import snake.score.ScoreManager;

import java.awt.Point;
import java.util.List;

public class SnakeView {
    private static final int WINDOW_WIDTH = 680;
    private static final int WINDOW_HEIGHT = 720;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private SnakeModel snakeModel;
    private SnakeController snakeControler;
    private ScoreManager scoreManager;

    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private Scene saveScoreScene;
    private Scene scoresScene;
    private VBox gameOverPanel;
    private Canvas gameCanvas;
    private StackPane gameRoot;

    private Label scoreLabel;
    private Label bestScoreLabel;
    
    private Image iconImage; 
    private Image logoImage; 
    private Image appleImage; 
    private Image snakeHeadImage;

    public SnakeView(SnakeModel snakeModel) 
    {
        this.snakeModel = snakeModel;
        this.scoreManager = new ScoreManager();
        iconImage = new Image(getClass().getResourceAsStream("/images/snake_icon.png"));
        logoImage = new Image(getClass().getResourceAsStream("/images/snake_logo.png"));
        appleImage = new Image(getClass().getResourceAsStream("/images/apple.png"));
        snakeHeadImage = new Image(getClass().getResourceAsStream("/images/head.png"));
    }

	// Start JavaFX View
    public void start(Stage stage) 
    {
        this.stage = stage;

        stage.setTitle("Snake Game");
        stage.setResizable(false);
        
        stage.getIcons().add(iconImage);
        prepareMenu();

        stage.setScene(menuScene);
        stage.show();
    }

    // Prepares menu scene
    private void prepareMenu() 
    {  
        // Logo
        ImageView logo = new ImageView(logoImage);

        logo.setFitWidth(600);
        logo.setFitHeight(190);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);

        // Speed slider
        Label speedText = new Label("SPEED");
        speedText.getStyleClass().add("speed-title");

        Slider speedSlider = new Slider(1, 9, 5);
        speedSlider.setShowTickMarks(false);
        speedSlider.setShowTickLabels(false);
        speedSlider.getStyleClass().add("speed-slider");

        Label fastLabel = new Label("FAST");
        Label slowLabel = new Label("SLOW");

        fastLabel.getStyleClass().add("speed-label");
        slowLabel.getStyleClass().add("speed-label");

        HBox sliderLabels = new HBox();
        sliderLabels.setPrefWidth(575);
        sliderLabels.setMaxWidth(575);

        sliderLabels.getChildren().addAll(
            slowLabel,
            fastLabel
        );

        slowLabel.setMaxWidth(Double.MAX_VALUE);
        fastLabel.setMaxWidth(Double.MAX_VALUE);
        
        HBox.setHgrow( slowLabel, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow( fastLabel, javafx.scene.layout.Priority.ALWAYS);
       
        slowLabel.setAlignment(Pos.CENTER_LEFT);
        fastLabel.setAlignment(Pos.CENTER_RIGHT);
        
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (snakeControler != null)
                snakeControler.setSpeed(newValue.intValue());
        });

        // Buttons
        Button newGameButton = createMenuButton("NEW GAME");
        Button scoresButton = createMenuButton("BEST SCORES");
        Button exitButton = createMenuButton("EXIT");

	    newGameButton.setOnAction(event -> startGame());
        scoresButton.setOnAction(event -> showScores());
        exitButton.setOnAction(event -> {
            if (snakeControler != null)
                snakeControler.abort();
        });

        // Menu
        VBox menu = new VBox();

        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(20);
        menu.setPadding(new Insets(40));

        menu.getStyleClass().add("menu");
        menu.getChildren().addAll( 
            logo, 
            speedText, 
            speedSlider, 
            sliderLabels, 
            newGameButton, 
            scoresButton, 
            exitButton);

        menuScene = new Scene(menu, WINDOW_WIDTH, WINDOW_HEIGHT);

        menuScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        menuScene.setOnKeyPressed(event -> {
			if (event.getCode().toString().equals("ESCAPE"))
                Platform.exit();
        });
    }

    // Prepares game scene
    private void prepareGameScene() {
        
        gameCanvas = new Canvas(WIDTH + 40, HEIGHT + 80);
        scoreLabel = new Label("Score: 0");
        scoreLabel.getStyleClass().add("game-score");

        bestScoreLabel = new Label("Best: 0");
        bestScoreLabel.getStyleClass().add("game-score");

        // Scores
        HBox scoreBox = new HBox();

        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setSpacing(40);

        scoreBox.getChildren().addAll(
            scoreLabel,
            bestScoreLabel
        );

        // Board
        VBox boardBox = new VBox();

        boardBox.setAlignment(Pos.CENTER);
        boardBox.setSpacing(5);

        boardBox.getChildren().addAll(
            scoreBox,
            gameCanvas
        );

        gameRoot = new StackPane();
        gameRoot.setAlignment(Pos.CENTER);
        gameRoot.getChildren().add(boardBox);

        gameScene = new Scene( gameRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Keys actions
        gameScene.setOnKeyPressed(event -> {

            if (gameOverPanel != null)
                return;

            switch (event.getCode()) {
                case UP:
                case W:
                    snakeModel.setMove(Directions.UP);
                    break;
                case DOWN:
                case S:
                    snakeModel.setMove(Directions.DOWN);
                    break;
                case LEFT:
                case A:
                    snakeModel.setMove(Directions.LEFT);
                    break;
                case RIGHT:
                case D:
                    snakeModel.setMove(Directions.RIGHT);
                    break;
                case ESCAPE:
                    showMenu();
                    break;
                default:
                    break;
            }
        });

        drawBoard();
    }

    // Starts game
    private void startGame() 
    {
        gameOverPanel = null;
        prepareGameScene();
        stage.setScene(gameScene);
        gameScene.getRoot().requestFocus();
        snakeControler.startGame();
    }

    // Draws board
    private void drawBoard()
    {
        if (gameCanvas == null)
            return;

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        Fields[][] board = snakeModel.getBoard();
        int boardSize = snakeModel.getSize();
        Point tongue = snakeModel.getTonguePos();
        Point head = snakeModel.getHeadPos();

        double cellWidth = WIDTH / (double) boardSize;
        double cellHeight = HEIGHT / (double) boardSize;
        double drawX, drawY;

        // background
        gc.setFill(Color.web("#071918"));
        gc.fillRect(0, 0, WIDTH + 40, HEIGHT + 40);

        // board
        gc.setFill(Color.web("#0b2927"));
        gc.fillRect(20, 20, WIDTH, HEIGHT);

        // grid
        gc.setStroke(Color.web("#16443d"));
        gc.setLineWidth(0.5);

        for (int i = 0; i <= boardSize; i++)
        {
            drawX = 20 + i * cellWidth;
            gc.strokeLine(drawX, 20, drawX, 20 + HEIGHT);
        }

        for (int i = 0; i <= boardSize; i++)
        {
            drawY = 20 + i * cellHeight;
            gc.strokeLine(20, drawY, 20 + WIDTH, drawY);
        }     

        // Body & fruits
        for (int x = 0; x < boardSize; x++)
        {
            for (int y = 0; y < boardSize; y++)
            {
                drawX = 20 + x * cellWidth;
                drawY = 20 + y * cellHeight;

                if (board[x][y] == Fields.SNAKE)
                {
                    boolean isHead = head.equals(new Point(x, y));
                    boolean isTongue = tongue.equals(new Point(x, y));
                    
                    // If there is a collision between the body and the tounge, draw both the body and the tongue 
                    if (snakeModel.isTongueOnBody() && !isHead)
                    {
                        gc.setFill(Color.web("#2fcf83"));
                        gc.fillRoundRect(drawX + 2, drawY + 2, cellWidth - 4, cellHeight - 4, 8, 8);
                    }
                    if (!snakeModel.isTongueOnBody() && !isHead && !isTongue)
                    {
                        gc.setFill(Color.web("#2fcf83"));
                        gc.fillRoundRect(drawX + 2, drawY + 2, cellWidth - 4, cellHeight - 4, 8, 8);
                    }
                }
                else if (board[x][y] == Fields.FRUIT)
                    gc.drawImage(appleImage, drawX + cellWidth * 0.05, drawY + cellHeight * 0.05, cellWidth * 0.9, cellHeight * 0.9);
            }
        }

        // Head
        drawSnakeHead(gc, cellWidth, cellHeight);

        // Score
        if (scoreLabel != null && bestScoreLabel != null) 
        {
            int currentScore = snakeModel.getScore();
            int bestScore = Math.max(currentScore, scoreManager.getBestScore());

            scoreLabel.setText("Score: " + currentScore);
            bestScoreLabel.setText("Best: " + bestScore);
        }
    }

    // Draws snake's head
    private void drawSnakeHead(GraphicsContext gc, double cellWidth, double cellHeight)
    {
        Point head = snakeModel.getHeadPos();
        Directions direction = snakeModel.getMove();

        double headWidth = cellWidth;
        double headHeight = cellHeight * 1.8;

        double centerX = 20 + head.x * cellWidth + cellWidth / 2.0;
        double centerY = 20 + head.y * cellHeight + cellHeight / 2.0;

        double rotation = 0;

        switch (direction) {
            case DOWN:
                rotation = -180;
                break;
            case RIGHT:
                rotation = 90;
                break;
            case UP:
                rotation = 0;
                break;

            case LEFT:
                rotation = 270;
                break;
        }

        gc.save();
        gc.translate(centerX, centerY);
        gc.rotate(rotation);
        gc.setImageSmoothing(true);
        gc.drawImage(snakeHeadImage, -headWidth/2, -3*headHeight/4, headWidth, headHeight);
        gc.restore();
    }
    
    // Shows game over panel 
    public void showGameOver() 
    {
        Platform.runLater(() -> {
            if (gameRoot == null)
                return;

            if (gameOverPanel != null)
                return;

            int currentScore = snakeModel.getScore();
            int bestScore = Math.max(currentScore, scoreManager.getBestScore());
            Label gameOverLabel = new Label("GAME OVER!");
            Label scoreLabel = new Label("Your score: " + currentScore);
            Label maxScoreLabel = new Label("Best Score: " + bestScore);

            gameOverLabel.getStyleClass().add("title");
            scoreLabel.getStyleClass().add("game-over-score");      
            maxScoreLabel.getStyleClass().add("game-over-score"); 

            Button saveScoreButton = createMenuButton("SAVE SCORE");
            Button newGameButton = createMenuButton("NEW GAME");
            Button menuButton = createMenuButton("MENU");
            
            saveScoreButton.setOnAction(event -> {showSaveScore(currentScore);});
            newGameButton.setOnAction(event -> {startGame();});
            menuButton.setOnAction(event -> {showMenu();});

            VBox buttons = new VBox();

            buttons.setAlignment(Pos.CENTER);
            buttons.setSpacing(10);

            buttons.getChildren().addAll(
                saveScoreButton,
                newGameButton,
                menuButton
            );

            gameOverPanel = new VBox();
            gameOverPanel.setAlignment(Pos.CENTER);
            gameOverPanel.setSpacing(15);
            gameOverPanel.getStyleClass().add("game-over-panel");

            gameOverPanel.getChildren().addAll(
                gameOverLabel,
                scoreLabel,
                maxScoreLabel,
                buttons
            );

            gameRoot.getChildren().add(gameOverPanel);
        });
    }

    // Shows "Save Score" scene
    private void showSaveScore(int score) 
    {
        Label title = new Label("SAVE YOUR SCORE");
        Label scoreLabel = new Label("Your score: " + score);
        Label nameLabel = new Label("Enter your name:");
        Label errorLabel = new Label();

        title.getStyleClass().add("add-score-title");   
        scoreLabel.getStyleClass().add("game-over-score");
        nameLabel.getStyleClass().add("info-label");
        errorLabel.getStyleClass().add("info-label");

        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
        nameField.setPromptText("Your name");
        nameField.setMaxWidth(250);      

        Button saveButton = createMenuButton("SAVE");
        Button cancelButton = createMenuButton("CANCEL");

        saveButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            
            if (name.isEmpty())
            {
                errorLabel.setText("Please enter your name.");
                return;
            }

            scoreManager.addScore(name, score);
            showScores();
        });


        cancelButton.setOnAction(event -> {showMenu();});

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(15);
        box.setPadding(new Insets(40));
        box.getStyleClass().add("menu");

        box.getChildren().addAll(
            title,
            scoreLabel,
            nameLabel,
            nameField,
            errorLabel,
            saveButton,
            cancelButton
        );

        saveScoreScene = new Scene(box, WINDOW_WIDTH, WINDOW_HEIGHT);
        saveScoreScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        stage.setScene(saveScoreScene);
        nameField.requestFocus();
    }

    // Return to menu
    public void showMenu() 
    {
        Platform.runLater(() -> {
			if (stage != null && menuScene != null)
            {
				stage.setScene(menuScene);
                menuScene.getRoot().requestFocus();
            }
        });
    }

    // Show scores
    private void showScores() 
    {
        Label title = new Label("BEST SCORES");
        title.getStyleClass().add("title");

        VBox scoresList = new VBox();
        scoresList.setAlignment(Pos.CENTER);
        scoresList.setSpacing(10);

        List<ScoreEntry> scores = scoreManager.getScores();
        if (scores.isEmpty()) 
        {
            Label emptyLabel = new Label("No scores yet.");

            emptyLabel.getStyleClass().add("info-label");
            scoresList.getChildren().add(emptyLabel);

        } 
        else 
        {
            for (int i = 0; i < scores.size(); i++) 
            {
                ScoreEntry entry = scores.get(i);
                Label scoreLabel = new Label((i + 1) + ".   " + entry.getName() + "   -   " + entry.getScore());
                
                scoreLabel.getStyleClass().add("info-label");
                scoresList.getChildren().add(scoreLabel);
            }
        }

        Button backButton = createMenuButton("BACK");
        backButton.setOnAction(event -> showMenu());
        
        VBox scoresBox = new VBox();
        scoresBox.setAlignment(Pos.CENTER);
        scoresBox.setSpacing(25);
        scoresBox.setPadding(new Insets(40));
        scoresBox.getStyleClass().add("menu");
        scoresBox.getChildren().addAll(title, scoresList,backButton);

        scoresScene = new Scene(scoresBox, WINDOW_WIDTH, WINDOW_HEIGHT);
        scoresScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        stage.setScene(scoresScene);
    }
    
    // Creates menu button
    private Button createMenuButton(String text) 
    {
        Button button = new Button(text);

        button.getStyleClass().add("menu-button");
        button.setPrefWidth(250);
        button.setPrefHeight(50);

        return button;
    }

    // Refreshes board using JavaFX Aplication Thread
    public void refreshBoard() 
    {
        Platform.runLater(() -> {
            if (gameCanvas != null)
                drawBoard();
        });
    }
  
    public void setSnakeControler(SnakeController snakeControler) {
        this.snakeControler = snakeControler;
    }
}