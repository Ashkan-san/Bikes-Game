package de.ashminh.bikes.application.view.overlay;

import de.ashminh.bikes.application.controller.IViewController;
import de.ashminh.bikes.application.view.OverlayBase;
import de.ashminh.bikes.common.RoomDto;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;


public class Lobby extends OverlayBase {
    public Lobby() {
        super("Lobby");

        VBox vbox = new VBox();

        // UPDATE BUTTON
        MyButton updateButton = new MyButton("Update");
        updateButton.setOnMouseReleased(
                event -> {
                    vbox.getChildren().clear();
					var roomList = controller.getRoomDtos();

                    // Controller muss von Modell eine Room Repraesentation bekommen, die man in unserem RPC parsen kann

                    if (roomList.isEmpty()) {
                        // todo
                        // Eine Anzeige wie "No one opened a room yet.."
                    } else {
                        for (var room : roomList) {
                            var item = new MyRoomItem(room, controller);
                            vbox.getChildren().add(item);
                        }
                    }

                }
        );
        setBottom(updateButton);

        // SCROLL PANE
        MyScrollPane sp = new MyScrollPane();
        sp.setContent(vbox);

        // ADD AS CHILDREN
        setCenter(sp);

        // ALIGNMENT
        vbox.setAlignment(Pos.CENTER);
    }

    private static class MyScrollPane extends ScrollPane {
        public MyScrollPane() {
            super();
            VBox.setVgrow(this, Priority.ALWAYS);
        }
    }

    private static class MyRoomItem extends HBox {
		public MyRoomItem(RoomDto room, IViewController controller) {
			// ROOMNAME, PLAYER COUNT, PLAYERS
			Text roomInfo = createRoomInfo(room.playerNames(), room);

			// IS STARTED/IS FULL
			HBox isStartedFullHbox = createIsStartedIsFull(room.isEveryoneReady(), room.isFull());

			// JOIN BUTTON
			MyButton btnJoin = new MyButton("Join");
			btnJoin.setOnMouseReleased(mouseEvent -> {
				controller.handlePressedJoin(room.roomName());
            });

            //btnJoin.getStyleClass().add("roomitem-button");

            // ADD AS CHILDREN
            getChildren().addAll(roomInfo, isStartedFullHbox, btnJoin);

            getStyleClass().add("roomitem");
        }

		private Text createRoomInfo(List<String> names, RoomDto room) {

			var namesString = String.join(", ", names);

			var string = String.format("%s (%d/%d): %s",
					room.roomName(), room.size(), room.max(), namesString);

			Text text = new Text(string);
			text.getStyleClass().add("roomitem-text");

			return text;
        }

        private HBox createIsStartedIsFull(boolean started, boolean full) {
            HBox hbox = new HBox();

            // STARTED
            Text text1 = new Text(started ? "Started" : "Not Started");
            text1.getStyleClass().add("roomitem-text");

            Circle circle1 = new Circle();
            circle1.setRadius(10);
            circle1.setFill(started ? Color.RED : Color.GREEN);

            // FULL
            Text text2 = new Text(full ? "Full" : "Vacant");
            text2.getStyleClass().add("roomitem-text");

            Circle circle2 = new Circle();
            circle2.setRadius(10);
            circle2.setFill(full ? Color.RED : Color.GREEN);

            hbox.getChildren().addAll(circle1, text1, circle2, text2);
            hbox.getStyleClass().add("roomitem-text");

            return hbox;
        }
    }
}
