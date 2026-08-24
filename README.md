# Snake Game

A modern desktop implementation of the classic **Snake** game developed in **Java** using **JavaFX**. 

The project combines classic gameplay mechanics with a graphical user interface, adjustable game speed, persistent high scores and custom graphics.

## Game Rules

The objective is to collect as many fruits as possible and achieve the highest score.

Each collected fruit increases the score and makes the snake longer.

The game ends when:

1. the snake hits the edge of the board, or
2. the snake collides with its own body.

The player cannot immediately change direction to the opposite direction, preventing the snake from turning directly into itself.

## Gameplay Preview

![Snake gameplay](docs/gameplay.gif)

## Screenshots

### Main Menu

![Main menu](docs/screenshots/main-menu.png)

### Gameplay

![Gameplay](docs/screenshots/gameplay.png)

### Best Scores

![Best scores](docs/screenshots/best-scores.png)

### Game Over

![Game Over](docs/screenshots/game-over.png)

## Features

- Classic Snake gameplay
- Four-directional movement using:
  - `W` / `↑` - move up
  - `S` / `↓` - move down
  - `A` / `←` - move left
  - `D` / `→` - move right
- Adjustable snake speed
- Collision detection with board boundaries and the snake's own body
- Randomly generated fruits
- Score tracking
- Persistent **Best Scores** ranking
- Saving player names and scores


## Interface

The game consists of several screens.

### Main Menu

The main menu provides access to:

- **NEW GAME** - starts a new game
- **BEST SCORES** - displays the high-score ranking
- **EXIT** - closes the application
- **SPEED** - allows the player to adjust the snake's speed

### Game

During gameplay, the screen displays the game board, snake, fruit, current score and best score.

The game ends when the snake collides with the board boundary or its own body.

### Game Over

After the game ends, the player can:

- save their score,
- start a new game,
- return to the main menu.

## Best Scores

The game stores up to **10 highest scores**.

After completing a game, the player can enter their name and save the result. Scores are sorted from highest to lowest and stored locally, allowing the ranking to persist between application launches.

## Project Architecture

The application follows the **MVC (Model-View-Controller)** design pattern.

### Model

The model contains the game logic and state, including:

- snake position and movement,
- board state,
- fruit generation,
- collision detection,
- score calculation.

### View

The view is responsible for the graphical interface and rendering the game using JavaFX.

It handles menus, the game board, snake and fruit graphics, score display, the game-over screen and the best-score screen.

### Controller

The controller manages the game loop and connects the model with the view.

## Technologies

| Technology | Version |
|---|---|
| Java | 13 |
| JavaFX | 13 |
| Maven | 3.x |

## Running the Application

### Requirements

Before running the project, make sure you have installed:

- **JDK 13** or a compatible Java environment
- **Apache Maven**

### Run with Maven

Clone the repository:

```bash
git clone https://github.com/mdabek2/Snake.git
```

Then in the SnakeGame folder run:

```bash
mvn javafx:run
```

The application will start with the main menu.

