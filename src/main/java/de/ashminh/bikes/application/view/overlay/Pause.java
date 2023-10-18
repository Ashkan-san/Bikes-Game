package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.view.OverlayBase;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

public class Pause extends OverlayBase {

    public Pause() {
        // BIKES LABEL
        super("Pause");

        // CENTER HBOX
        HBox centerBox = new HBox();
        // BUTTONS
        MyButton btnContinue = new MyButton("Continue");
        btnContinue.setOnMouseReleased(e -> {
            // 1. Menü schließen/hiden
            // 2. Game für beide Spieler fortsetzen (Countdown?)
            controller.resumeGame();
        });

        MyButton btnLeave = createLeaveButton();

        MyTextField field = new MyTextField("Input your name:");
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String playerName = field.getText();
                if (field.checkInput(playerName)) {
                    controller.handleInputName(playerName);
                }
            }
        });

        // ADD AS CHILDREN
        centerBox.getChildren().addAll(btnContinue, btnLeave);
        setCenter(centerBox);
        setBottom(field);

        // ALIGNMENT
        centerBox.setAlignment(Pos.CENTER);
    }
}
