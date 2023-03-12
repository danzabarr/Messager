package com.danzabarr.messager.client;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TitleBarController
{
    private double dragX, dragY;

    @FXML
    public HBox titleBar;

    public void minimise()
    {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    public void maximise()
    {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        if (stage.isMaximized())
        {
            stage.setMaximized(false);
            //wrapper.setBackground(ClientGUI.ROUND_CORNERS);
            //dropShadow.setPadding(new Insets(20, 20, 20, 20));
        }
        else
        {
            stage.setMaximized(true);
            //wrapper.setBackground(ClientGUI.SQUARE_CORNERS);
            //dropShadow.setPadding(null);
        }
    }

    public void close()
    {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    public void dragStart(MouseEvent mouseEvent) {
        dragX = mouseEvent.getSceneX();
        dragY = mouseEvent.getSceneY();
    }

    @FXML
    public void drag(MouseEvent mouseEvent)
    {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setX(mouseEvent.getScreenX() - dragX);
        stage.setY(mouseEvent.getScreenY() - dragY);
    }
}
