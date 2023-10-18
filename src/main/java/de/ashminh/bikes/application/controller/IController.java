package de.ashminh.bikes.application.controller;

public interface IController extends IViewController, IModelController {
    void initialize();

    /**
     * Behandelt den Fall, wenn der Leave Button gedrückt wurde
     */
    void handlePressedLeave();

    String getWinnerName();

    boolean isGAME_OVER();

    boolean isPLAYING();
}
