package de.ashminh.bikes.application.controller;

import de.ashminh.bikes.application.BikesGame;
import de.ashminh.bikes.application.model.IModel;
import de.ashminh.bikes.application.model.ModelImpl;
import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.application.view.IView;
import de.ashminh.bikes.application.view.ViewImpl;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.Collection;

public class ControllerImpl implements IController {
    private static final IController instance = new ControllerImpl();

    private IView view;
    private IModel model;

    private boolean GAME_OVER = false;
    private boolean PLAYING = false;
    private Timeline timeline;

    // die Situation, dass das Model ein Room Object hat (entweder durch open oder join Room)
    private boolean HAS_ROOM = false;

    private PlayerNumber playerNumber = null;

    private ControllerImpl() {
        // GAME LOOP
        // Duration: wie "schnell" das Spiel verläuft, bzw. wie schnell die Frames verarbeitet werden
        // Cycle Count: wie oft die Duration cycled, hier unendlich lange
        // Sollte nur 1x aufgerufen werden und pausiert wenn nötig
        timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 30), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public static IController getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        model = ModelImpl.getInstance();
        view = ViewImpl.getInstance();
    }

    @Override
    public void tick() {
        if (GAME_OVER) {
            view.showOverlay("gameover");
        }

        if (!PLAYING) {
            if (model.isGameStarted()) {
                startGame(false);
            }
        } else if (PLAYING) {
            // FALL WAND WIRD BERÜHRT
            // FALL GEGNER WIRD BERÜHRT
            // FALL MAN BERÜHRT SICH SELBER
//            if (model.droveIntoWall(playerNumber, view.getRows(), view.getCols())
//                    || model.isSuicidal(playerNumber)
//                    || model.hitOpponent()) {
//                endGame();
//                return;
//            }
//
            if (model.isGameOver()) {
                endGame();
            }

//            // Player 1 wird hier gezeichnet
            var playerInfo = model.getPlayerDto(PlayerNumber.PLAYER_1);
            view.draw(playerInfo.trail(), playerInfo.trailColor());
            view.highlightBikefront(playerInfo);

            // Player 2 wird hier gezeichnet
            playerInfo = model.getPlayerDto(PlayerNumber.PLAYER_2);
            view.draw(playerInfo.trail(), playerInfo.trailColor());
            view.highlightBikefront(playerInfo);
        }
    }

    // METHODEN
    @Override
    public void startGame(boolean isRestart) {
        if (!HAS_ROOM) {
            return;
        }

        model.startGameLoop();

        GAME_OVER = false;
        PLAYING = true;

        if (!isRestart) {
            registerGameKeyboardEvents(playerNumber);
        }

        model.restartAt();

        // Hintergrund neu malen + Screens hiden
        view.hideOverlays();
        view.drawGame();

        // Bike malen
        view.draw(model.getTrail(playerNumber), model.getTrailColor(playerNumber));
        view.highlightBikefront(model.getPlayerDto(playerNumber));
    }

    @Override
    public void pauseGame() {
        model.stopGameLoop();
        timeline.stop();
        PLAYING = false;
        view.showOverlay("pause");
    }

    @Override
    public void resumeGame() {
        model.startGameLoop();
        timeline.play();
        PLAYING = true;
        view.hideOverlays();
    }

    @Override
    public void endGame() {
        GAME_OVER = true;
        PLAYING = false;
        view.setWinner(getWinnerName());
    }

    private boolean checkDirection(PlayerNumber playerNumber, Direction newDirection) {
        Direction direction = model.getDirection(playerNumber);

        if (newDirection == Direction.UP && direction == Direction.DOWN) {
            return false;
        } else if (newDirection == Direction.DOWN && direction == Direction.UP) {
            return false;
        } else if (newDirection == Direction.RIGHT && direction == Direction.LEFT) {
            return false;
        } else if (newDirection == Direction.LEFT && direction == Direction.RIGHT) {
            return false;
        }
        return true;
    }

    // HANDLE STUFF / USER INPUT
    @Override
    public void registerKeyboardEvents() {
        view.getScene().addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.ESCAPE) {
                handlePressedEscape();
            }
        });
    }

    @Override
    public void registerGameKeyboardEvents(PlayerNumber playerNumber) {
        view.getScene().addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            BikesGame.getThreadPool().submit(() -> {
                if (key.getCode() == KeyCode.W) {
                    setDirection(playerNumber, Direction.UP);
                }
                if (key.getCode() == KeyCode.A) {
                    setDirection(playerNumber, Direction.LEFT);
                }
                if (key.getCode() == KeyCode.S) {
                    setDirection(playerNumber, Direction.DOWN);
                }
                if (key.getCode() == KeyCode.D) {
                    setDirection(playerNumber, Direction.RIGHT);
                }
            });
        });
    }

    @Override
    public void handleInputName(String playerName) {
        System.out.printf("Controller: Trying to change playername to %s%n", playerName);
        model.setPlayerName(playerNumber, playerName);
        System.out.printf("Controller: Changed playername: %s successfully%n", playerName);
    }

    @Override
    public void handlePressedJoin(String roomName) {
        System.out.printf("Controller: Trying to join to %s%n", roomName);

        BikesGame.getThreadPool().submit(
                () -> model.joinRoom(roomName)
        );
    }

    @Override
    public void handlePressedOpen(String roomName) {
        System.out.printf("Controller: Trying to open room: %s%n", roomName);

        BikesGame.getThreadPool().submit(() -> {
            model.openRoom(roomName);
        });
    }

    @Override
    public void handlePressedEscape() {
        if (PLAYING) {
            pauseGame();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void handleOpenRoomSuccessful(String roomName, PlayerNumber playerNumber) {
        try {
            this.playerNumber = playerNumber;
            HAS_ROOM = true;
            System.out.println("vor update");
            Platform.runLater(() -> {
                view.updateWaitScreen();
            });
            System.out.println("nach update");
            view.showOverlay("wait");
            System.out.printf("Controller: Opened room: %s successfully%n", roomName);
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleOpenRoomFailed(String roomName) {
        view.showAlert("Raum konnte nicht geöffnet werden!");
    }

    @Override
    public void handleJoinRoomSuccessful(String roomName, PlayerNumber playerNumber) {
        this.playerNumber = playerNumber;
        HAS_ROOM = true;
        Platform.runLater(() -> {
            view.updateWaitScreen();
        });
        view.showOverlay("wait");
        System.out.printf("Controller: Joined room: %s successfully%n", roomName);
        timeline.play();
    }

    @Override
    public void handleJoinRoomFailed(String roomName) {
        view.showAlert("Raum konnte nicht betreten werden!");
    }

    @Override
    public void handlePressedLeave() {
        System.out.printf("Controller: Trying to leave room: %n");
        model.closeRoom();
        System.out.printf("Controller: Left room: successfully%n");
    }

    // GETTER & SETTER
    @Override
    public Collection<RoomDto> getRoomDtos() {
        return model.getRoomDtos();
    }

    @Override
    public PlayerDto getPlayerDto(PlayerNumber playerNumber) {
        //return ModelImpl.getInstance().getPlayerDto(playerNumber);
        System.out.println("in controllerdto hey");
        return model.getPlayerDto(playerNumber);
    }

    @Override
    public String getWinnerName() {
        return model.getWinnerName();
    }

    @Override
    public boolean isGAME_OVER() {
        return GAME_OVER;
    }

    @Override
    public boolean isPLAYING() {
        return PLAYING;
    }

    @Override
    public void setDirection(PlayerNumber playerNumber, Direction direction) {
        if (checkDirection(playerNumber, direction)) {
            model.setDirection(playerNumber, direction);
        }
    }
}
