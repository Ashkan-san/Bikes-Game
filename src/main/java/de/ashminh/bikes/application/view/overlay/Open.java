package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.view.OverlayBase;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class Open extends OverlayBase {

    public Open() {
        super("Open");

        // CENTER HBOX
        HBox centerBox = new HBox();
        // BUTTON, TEXTFIELD
        MyTextField field = new MyTextField("Input Roomname:");

        MyButton btnOpen = new MyButton("Open");
        btnOpen.setOnMouseReleased(e -> {
            String roomName = field.getText();
            if (field.checkInput(roomName)) {
                controller.handlePressedOpen(roomName);
            }

        });

        // ADD AS CHILDREN
        centerBox.getChildren().addAll(field, btnOpen);
        setCenter(centerBox);

        // ALIGNMENT
        centerBox.setAlignment(Pos.CENTER);
    }
}
