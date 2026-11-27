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

### Docker

The application can be built and run in a container, so no local JDK or Maven is needed. The `Dockerfile` is multi-stage: Maven 17 builds the executable JAR, and a slim JRE 17 image runs it.

`ThreeInARow` is a Swing desktop application: it has no HTTP server, so there is no web port to expose. The container therefore offers two ways to see the window:

| Target | How the window is shown | Port |
|--------|-------------------------|------|
| `vnc` (default) | Virtual display (Xvfb) served over VNC; works on Linux, macOS and Windows | `5900` |
| `x11` | The host's own X11 display (Linux, or macOS/Windows with an X server) | none |

Run all Docker commands from the project root, where the `Dockerfile` is located.

#### VNC mode (default)

Build the image:

```bash
docker build -t threeinarow .
```

Start the container (the game starts automatically; the VNC port is bound only to the local machine):

```bash
docker run --rm -p 127.0.0.1:5900:5900 --name threeinarow threeinarow
```

Then connect any VNC client to `localhost:5900` (no password; do not publish the port on other interfaces). On macOS you can use `open vnc://localhost:5900`.

To play Tic-Tac-Toe, pass the game name after the image name:

```bash
docker run --rm -p 127.0.0.1:5900:5900 threeinarow TicTacToe
```

#### X11 mode (Linux)

```bash
docker build --target x11 -t threeinarow:x11 .
xhost +local:docker
docker run --rm -e DISPLAY="$DISPLAY" -v /tmp/.X11-unix:/tmp/.X11-unix threeinarow:x11
xhost -local:docker
```

Append `TicTacToe` to the `docker run` command to select the other game.

#### Maven integration (docker-maven-plugin)

The image can also be built and run from Maven with the [fabric8 docker-maven-plugin](https://github.com/fabric8io/docker-maven-plugin). Unlike the root `Dockerfile`, this route does not compile anything inside Docker: the plugin takes the executable JAR produced by the Maven build and packages it using `src/main/docker/Dockerfile` (same VNC runtime as the default target). It requires a running Docker daemon and is enabled only with the `docker` profile, so `mvn verify` keeps working without Docker.

Build the JAR and the image `threeinarow:1.0.0-SNAPSHOT` (also tagged `latest`):

```bash
mvn clean package -Pdocker
```

Run the container (VNC on `127.0.0.1:5900`) and stop it:

```bash
mvn docker:start
mvn docker:stop
```

Other useful goals: `mvn docker:build` (rebuild the image from the existing JAR), `mvn docker:logs`, `mvn docker:remove`. The host port can be changed with `-Ddocker.vnc.host.port=5901`, and the image name with `-Ddocker.image.name=myname`.

To choose the game, run the image with Docker directly: `docker run --rm -p 127.0.0.1:5900:5900 threeinarow TicTacToe`.

#### Notes

- The standalone `docker build` skips the unit tests, because the GUI tests (`TestExample`) need a display. Run them with the local Maven commands above (`mvn test`; with `-Pdocker`, add `-DskipTests` if no display is available), or headlessly with `xvfb-run mvn test`.
- Stop the container with `docker stop threeinarow`.
- Build files are excluded from the Docker context through `.dockerignore`.

### How to clean up

Remove generated build files with:

```bash
mvn clean
``````