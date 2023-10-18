package de.ashminh.bikes.application.view;

import de.ashminh.bikes.application.controller.ControllerImpl;
import de.ashminh.bikes.application.controller.IViewController;
import de.ashminh.bikes.application.view.overlay.*;
import de.ashminh.bikes.common.PlayerDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import edu.cads.bai5.vsp.tron.view.TronView;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ViewImpl extends TronView implements IView {
    private static IView instance;
    static {
        try {
            instance = new ViewImpl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IViewController controller;

    // INGAME VARIABLEN
    private final int TILE_WIDTH = getWIDTH() / getCOLUMNS();
    private final int TILE_HEIGHT = getHEIGHT() / getROWS();

    private final Color field = Color.rgb(0, 0, 0, 0.9);
    private final Color border = Color.WHITE;

    private GameOver gameOver;
    private Wait wait;

    private ViewImpl() throws IOException {
        super("view.properties", Color.BLACK);
        setGameBackground();
    }

    public static IView getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        controller = ControllerImpl.getInstance();

        gameOver = new GameOver();
        wait = new Wait();

        // CREATE MENU SCREENS
        final Map<String, OverlayBase> overlays = Map.of(
                "home", new Home(),
                "open", new Open(),
                "wait", wait,
                "gameover", gameOver,
                "pause", new Pause(),
                "lobby", new Lobby());

        // SET VIEW AND CONTROLLER, REGISTER OVERLAYS
        for (Map.Entry<String, OverlayBase> screens : overlays.entrySet()) {
            screens.getValue().setViewController(this);
            registerOverlay(screens.getKey(), screens.getValue());
        }

        // SHOW HOME
        showOverlay("home");
    }

    public void showAlert(String msg) {
        OverlayBase.MyAlert alert = new OverlayBase.MyAlert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // DRAWING
    @Override
    public void drawGame() {
        GraphicsContext gc = gameBoard.getGraphicsContext2D();

        for (int i = 0; i < getROWS(); i++) {
            for (int j = 0; j < getCOLUMNS(); j++) {
                DropShadow borderGlow = new DropShadow();
                borderGlow.setColor(Color.CYAN);
                borderGlow.setWidth(10);
                borderGlow.setHeight(10);
                gc.setEffect(borderGlow);

                gc.setFill(field);

                gc.fillRect(i * TILE_WIDTH, j * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                gc.setStroke(border);
                gc.strokeRect(i * TILE_WIDTH, j * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }
    }

    private void setGameBackground() {
        // BACKGROUND FÜR GAME
        try (InputStream is = Files.newInputStream(Paths.get("src/main/resources/images/test.jpg"))) {
            Image img = new Image(is);
            BackgroundImage bgi = new BackgroundImage(img,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(1.0, 1.0, true, true, false, false));
            Background bg = new Background(bgi);
            base.setBackground(bg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void highlightBikefront(PlayerDto playerDto) {
        GraphicsContext gc = gameBoard.getGraphicsContext2D();

        gc.setFill(playerDto.color());
        gc.fillRect(playerDto.coordinate().x * TILE_WIDTH, playerDto.coordinate().y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
    }

    @Override
    public void draw(List<Coordinate> bike, Color color) {
        if (bike == null || color == null) {
            throw new NullPointerException();
        }
        for (Coordinate pos : bike) {
            if (pos.x < 0 || pos.x >= getCOLUMNS()) {
                throw new IllegalArgumentException("x value out of bounds: x is " + pos.x + ", but should be 0 <= x < " + getCOLUMNS());
            }
            if (pos.y < 0 || pos.y >= getROWS()) {
                throw new IllegalArgumentException("y value out of bounds: y is " + pos.y + ", but should be 0 <= y < " + getROWS());
            }

            // paint new bike position
            GraphicsContext g = gameBoard.getGraphicsContext2D();
            g.setFill(color); //Color.PAPAYAWHIP);
            g.fillRect(pos.x * TILE_WIDTH, pos.y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        }
    }

    @Override
    public void updateWaitScreen() {
        wait.updatePlayers();
    }

    // GETTER & SETTER

    @Override
    public int getRows() {
        return getROWS();
    }

    @Override
    public int getCols() {
        return getCOLUMNS();
    }

    @Override
    public void setWinner(String winner) {
        gameOver.setWinner(winner);
    }

    @Override
    public void showOverlay(String name) {
        hideOverlays();
        super.showOverlay(name);
    }

}
