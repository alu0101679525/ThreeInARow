[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=alu0101679525_ThreeInARow)

## ThreeInARow

This is a basic Java implementation of the Three in a Row and Tic-Tac-Toe games.

### Requirements

- Java Development Kit (JDK) 17 or later.
- Apache Maven.

Run all Maven commands from the project root directory, where `pom.xml` is located.

### How to build and test

Compile the project:

```bash
mvn clean compile
```

Run the unit tests:

```bash
mvn test
```

The JaCoCo coverage report is generated at `target/site/jacoco/index.html` when the tests run.

Run the full verification, including tests, Checkstyle, and the JaCoCo coverage check:

```bash
mvn verify
```

The verification requires at least 70% line coverage. Checkstyle uses the rules in `checkstyle.xml`; violations fail the verification build.

### Maven Site & Checkstyle Report

Generate the Maven project site with:

```bash
mvn clean site
```

Open `target/site/index.html` for the full Maven site or `target/site/checkstyle.html` directly for the Checkstyle report.

### Javadoc

Generate the Javadoc documentation with:

```bash
mvn javadoc:javadoc
```

Open `target/site/apidocs/index.html` to view the generated documentation.

### How to run

Build the executable JAR first:

```bash
mvn package
```

Run the default game (Three in a Row):

```bash
java -jar target/threeinarow-1.0.0-SNAPSHOT.jar
```

`RowGameApp` accepts an optional argument to select the game:

- `ThreeInARow` — Three in a Row (the default).
- `TicTacToe` — Tic-Tac-Toe.

For example, to run Tic-Tac-Toe:

```bash
java -jar target/threeinarow-1.0.0-SNAPSHOT.jar TicTacToe
```

### Model-View-Controller (MVC) architecture

The application follows the **Model-View-Controller (MVC)** architectural pattern to separate the game state, the user interface, and the logic that coordinates them.

- **Model (`model` package):** `RowGameModel` stores the overall state of a match, including the 3x3 board, the current player, the number of remaining moves, and the game result. Each cell is represented by a `RowBlockModel`, which stores its mark and whether a move is allowed in that cell. The model is independent of Swing components and publishes property-change events when relevant state changes.
- **View (`view` package):** `RowGameGUI` builds the main Swing window and brings together `RowGameBoardView` and `RowGameStatusView`. `RowGameBoardView` displays the board and captures cell clicks, while `RowGameStatusView` displays whose turn it is and the final result. Both views listen for property changes from the model and update the interface when the state changes.
- **Controller (`controller` package):** `RowGameController` receives user actions from the views and coordinates updates to the model. It processes moves, checks whether the game has been won or drawn, advances the turn when play continues, and disables further moves when the game ends. It also handles resets.

A typical move follows this sequence:

1. The player clicks a cell in `RowGameBoardView`.
2. The view passes the selected row and column to `RowGameController`.
3. The controller applies the move to `RowGameModel` and checks the game outcome.
4. The model publishes property-change events through `PropertyChangeSupport`.
5. `RowGameBoardView` and `RowGameStatusView`, which registered `PropertyChangeListener`s, receive those events and refresh the relevant parts of the interface.

This event-based communication uses the **Observer** pattern for model-to-view notifications. The views are therefore updated in response to model changes rather than having to poll the game state.

### Strategy pattern

The rules for each game mode are separated from the controller using the **Strategy** design pattern:

- `RowGameRulesStrategy` defines the common operations for resetting the board and applying a move.
- `RowGameRulesThreeInARow` and `RowGameRulesTicTacToe` implement the rules for their respective game modes.
- `RowGameController` delegates rule-specific operations to the active strategy. `RowGameApp` selects the strategy based on the optional command-line argument.

### How to clean up

Remove generated build files with:

```bash
mvn clean
```