package de.ashminh.bikes.application.view;

import de.ashminh.bikes.application.controller.ControllerImpl;
import de.ashminh.bikes.application.controller.IController;
import edu.cads.bai5.vsp.tron.view.ITronView;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Hält alles was für jeden Screen gleich ist:
 * Eigenen Button, Label, Scrollpane
 * Stylesheet, Topbox, Centerbox Methoden, Backbutton
 */
public class OverlayBase extends BorderPane {
    private final String title;
    private final String previous;

    private final static Map<String, String> previousOverlay = Map.of(
            "home", "",
            "open", "home",
            "join", "home",
            "wait", "home",
            "gameover", "",
            "pause", "",
            "lobby", "home");

    public ITronView view;
    public IController controller;

    public void setViewController(ITronView view) {
        this.view = view;
        controller = ControllerImpl.getInstance();
    }

    public OverlayBase(String title) {
        super();
        this.title = title;
        this.previous = previousOverlay.get(title.toLowerCase());

        setPrefSize(1000, 700);
        setMenuBackground();

        TopBox topBox = new TopBox();
        setTop(topBox);

        getStylesheets().add("overlay.css");
        getStyleClass().add("pane");
    }

    private class TopBox extends HBox {
        public TopBox() {
            getChildren().addAll(createTitle(), createBackButton());
            getStyleClass().add("topbox");
        }

        private Label createTitle() {
            Label text = new Label(title);
            text.getStyleClass().add("overlay-label");
            return text;
        }

        private MyButton createBackButton() {
            // BACK BUTTON
            String btnName = "Back";
            if (title.equals("Home") || title.equals("GameOver") || title.equals("Pause")) {
                btnName = "Escape";
            }

            MyButton btnBack = new MyButton(btnName);
            btnBack.setOnMouseReleased(e -> {
                if (previous.isBlank())
                    controller.handlePressedEscape();
                else
                    view.showOverlay(previous);
            });

            return btnBack;
        }
    }

    public static class MyButton extends Button {
        public MyButton(String name) {
            super(name);
            getStyleClass().add("button");
        }
    }

    public static class MyTextField extends TextField {
        public MyTextField(String prompt) {
            super();
            setPromptText(prompt);
            getStyleClass().add("textfield");
        }

        /**
         * Roomname/Playername prüfen auf:
         * leer oder null
         * Länge
         * Genutzte Zeichen
         */
        public boolean checkInput(String input) {
            MyAlert myAlert = new MyAlert(Alert.AlertType.ERROR);

            boolean ok = true;
            if (!(input == null)) {
                if (input.length() > 15) {
                    myAlert.setContentText("Der Input ist zu lang!");
                    ok = false;
                    myAlert.showAndWait();
                } else if (input.matches(".*?\\W.*")) {
                    // TODO: Was soll böse sein?
                    myAlert.setContentText("Der Input hat böse Zeichen!");
                    ok = false;
                    myAlert.showAndWait();
                } else if (input.length() == 0) {
                    myAlert.setContentText("Der Input ist leer!");
                    ok = false;
                    myAlert.showAndWait();
                }
            }
            return ok;
        }
    }

    public static class MyAlert extends Alert {
        public MyAlert(AlertType type) {
            super(type);
            setTitle("Error");
            setHeaderText("Fehler bei deiner Eingabe");

            DialogPane dialogPane = this.getDialogPane();
            dialogPane.getStylesheets().add("overlay.css");
            dialogPane.getStyleClass().add("dialog-pane");
        }
    }

    private void setMenuBackground() {
        // BACKGROUND FÜR JEDES MENÜ
        try (InputStream is = Files.newInputStream(Paths.get("src/main/resources/images/bg.png"))) {
            Image img = new Image(is);
            BackgroundImage bgi = new BackgroundImage(img,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(1.0, 1.0, true, true, false, false));
            Background bg = new Background(bgi);
            setBackground(bg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MyButton createLeaveButton() {
        MyButton btnLeave = new MyButton("Leave");
        btnLeave.setOnMouseReleased(e -> {
            // 1. Button input verarbeiten
            controller.handlePressedLeave();
            // 2. Zurück in HomeScreen
            view.showOverlay("home");
        });

        return btnLeave;
    }

}
