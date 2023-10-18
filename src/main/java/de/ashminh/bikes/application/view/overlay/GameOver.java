package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.view.OverlayBase;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class GameOver extends OverlayBase {
    Label label = new Label();

    public GameOver() {
        super("GameOver");
        // CENTER HBOX
        HBox hBox = new HBox();

        // BUTTONS
        MyButton btnRestart = new MyButton("Restart");
        btnRestart.setOnMouseReleased(e -> {
            controller.startGame(true);
        });

        MyButton btnLeave = createLeaveButton();

        // ADD AS CHILDREN
        hBox.getChildren().addAll(btnRestart, btnLeave);
        setCenter(hBox);
        setBottom(label);

        // ALIGNMENT
        hBox.setAlignment(Pos.CENTER);
    }

    public void setWinner(String winner) {
        label.setText("Winner is: " + winner);
    }
}
