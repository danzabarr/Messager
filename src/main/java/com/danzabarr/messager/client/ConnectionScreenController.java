package com.danzabarr.messager.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectionScreenController
{
    @FXML
    public TextField host;
    @FXML
    public TextField port;
    @FXML
    public Label error;

    @FXML
    public void connect() throws IOException
    {
        try
        {
            tryConnect();
            VBox chatScreen = FXMLLoader.load(getClass().getResource("/fxml/chat.fxml"));
            Scene scene = new Scene(chatScreen);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = (Stage) host.getScene().getWindow();
            stage.setScene(scene);

            WindowResizer.addListener(stage, scene, 7, 30);

            centerStage(stage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            error.setText(e.getMessage());
        }
    }


    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void tryConnect() throws Exception
    {
        try
        {
            Client.createConnection(host.getText(), Integer.parseInt(port.getText()));
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("Invalid port number.");
        }
    }
}
