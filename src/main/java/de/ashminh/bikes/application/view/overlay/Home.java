package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.view.OverlayBase;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * Unser Startmenü, aus welchem:
 * Lobby geöffnet
 * Lobby beigetreten
 */
public class Home extends OverlayBase {
    public Home() {
        super("Home");

        // CENTER HBOX
        HBox centerBox = new HBox();

        // BUTTONS
        /*MyButton btnTest = new MyButton("LobbyopenTest");
        btnTest.setOnMouseReleased(event -> {
            controller.handlePressedOpen("Test");
        });

        MyButton btnStart = new MyButton("Start");
        btnStart.setOnMouseReleased(event -> {
            controller.startGame();
        });*/

        MyButton btnOpen = new MyButton("Open");
        btnOpen.setOnMouseReleased(e -> {
            view.showOverlay("open");
        });

        MyButton btnJoin = new MyButton("Join");
        btnJoin.setOnMouseReleased(e -> {
            view.showOverlay("lobby");
        });

        // ADD AS CHILDREN
        centerBox.getChildren().addAll(btnOpen, btnJoin);
        setCenter(centerBox);

        // ALIGNMENT
        centerBox.setAlignment(Pos.CENTER);
    }

}
