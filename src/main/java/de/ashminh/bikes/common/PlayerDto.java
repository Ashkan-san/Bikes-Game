package de.ashminh.bikes.common;

import de.ashminh.bikes.application.model.room.PlayerNumber;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.List;

public record PlayerDto(Coordinate coordinate, Color color, List<Coordinate> trail, Color trailColor, String name,
                        PlayerNumber playerNumber) {
}
