package com.danzabarr.messager.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientApplication extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        //Style the window.
        stage.setTitle("Messager");
        stage.initStyle(StageStyle.TRANSPARENT);

        //Load the UI from FXML.
        VBox connectionScreen = FXMLLoader.load(getClass().getResource("/fxml/connection-screen.fxml"));

        //Create a new scene/
        Scene scene = new Scene(connectionScreen, 300, 275);
        scene.setFill(Color.TRANSPARENT);

        //Add the scene to the window.
        stage.setScene(scene);

        //Add a window resize listener.
        WindowResizer.addListener(stage, scene, 7, 30);

        //Show the window.
        stage.show();
    }
}
