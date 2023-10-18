package de.ashminh.bikes.application.view;

import de.ashminh.bikes.common.PlayerDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.List;

public interface IView {

	/**
	 * Erstellt und registriert die ganzen Overlays
	 * Zeigt den Home Screen
	 */
	void initialize();

	/**
	 * Override damit: Automatisch alle hiden nach show
	 */
	void showOverlay(String overlay);

	/**
	 * Malt das jeweilige Bike in einer bestimmten Farbe
	 */
	void draw(List<Coordinate> bike, Color color);

	/**
	 * Bikefront für einen Player highlighten
	 */
	void highlightBikefront(PlayerDto playerDto);

    /**
     * Malt den Game Background/Spielfeld
     */
    void drawGame();

    /**
     * Setzt den Gewinner zum Anzeigen im Game Over Screen
     */
    void setWinner(String winner);

    void showAlert(String msg);

    void updateWaitScreen();

    void hideOverlays();

    Scene getScene();

    int getRows();

    int getCols();
}
