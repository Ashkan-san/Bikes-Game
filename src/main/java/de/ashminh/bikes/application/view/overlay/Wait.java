package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.application.view.OverlayBase;
import de.ashminh.bikes.common.PlayerDto;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Wait extends OverlayBase {
    public Wait() {
        super("Wait");
        //ImageView iv = createVersus();

        // BOTTOM
        HBox hbox = new HBox();
        Label label = waitingLabelAnimation();
        MyButton btnStart = new MyButton("Start");
        btnStart.setOnMouseReleased(event -> {
            controller.startGame(false);
        });

        // ADD AS CHILDREN
        //setCenter(iv);
        hbox.getChildren().addAll(btnStart, label);
        setBottom(hbox);
    }

    public void updatePlayers() {
        PlayerDto p1 = controller.getPlayerDto(PlayerNumber.PLAYER_1);
        PlayerDto p2 = controller.getPlayerDto(PlayerNumber.PLAYER_2);

        if (p1 != null) {
            VBox vbox1 = createPlayerStuff(p1);
            setLeft(vbox1);
        }
        if (p2 != null) {
            VBox vbox2 = createPlayerStuff(p2);
            setRight(vbox2);
        }
    }

    private Label waitingLabelAnimation() {
        Label label = new Label("Waiting for opponent");
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        event -> {
                            label.setText("Waiting for opponent.");
                        }
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        event -> {
                            label.setText("Waiting for opponent..");
                        }
                ), new KeyFrame(
                Duration.seconds(3),
                event -> {
                    label.setText("Waiting for opponent...");
                }
        )
        );

        if (/*someone joined*/ false) {
            label.setText("Game can start!");
        }

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return label;
    }

    private VBox createPlayerStuff(PlayerDto player) {
        VBox vbox = new VBox();
        // PICTURE
        String src;
        ImageView iv = new ImageView();
        if (player.playerNumber().equals(PlayerNumber.PLAYER_1)) {
            src = "src/main/resources/images/player1.png";
        } else {
            src = "src/main/resources/images/player2.png";
        }
        try {
            iv.setImage(new Image(new FileInputStream(src), 300, 300, true, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // LABEL
        Label label = new Label(player.name());
        label.getStyleClass().add("player-label");

        // CHECKBOX
        /*CheckBox cb = new CheckBox("Ready?");
        if (cb.isSelected()){
            player.setReady(true);
        }*/

        vbox.getChildren().addAll(iv, label);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    private ImageView createVersus() {
        String src = "src/main/resources/images/vs.png";
        ImageView iv = new ImageView();
        try {
            iv.setImage(new Image(new FileInputStream(src), 100, 100, true, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return iv;
    }

}
