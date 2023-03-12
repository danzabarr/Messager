package com.danzabarr.messager.client;

import com.danzabarr.messager.core.Connection;
import com.danzabarr.messager.core.ConnectionListener;
import com.danzabarr.messager.core.Request;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.StringCharacterIterator;

public class ChatController implements ConnectionListener
{
    @FXML
    public HBox titleBar;
    @FXML
    public WebView webView;
    @FXML
    public TextField input;

    public String readFileAsString(String fileName) throws Exception
    {
        String data = "";

        InputStream in = null;
        BufferedReader reader = null;

        try {

            in = getClass().getResourceAsStream(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
        }
        catch (NullPointerException e)
        {
            System.out.println(in);
            throw e;
        }

        String line = null;
        while ((line = reader.readLine()) != null)
        {
            data += line;
        }

        return data;


        //new File(getClass().getResource("zipcode_data.csv").toURI())

        //data = new String(Files.readAllBytes(Paths.get(fileName)));
        //return data;
    }


    @FXML
    public void initialize()
    {
        try
        {
            WebEngine engine = webView.getEngine();
            String html = null;
            html = readFileAsString("/html/chat.html");
            engine.loadContent(html);

            engine.getLoadWorker().stateProperty().addListener(
                    (ObservableValue<? extends Worker.State> ov, Worker.State oldState,
                     Worker.State newState) ->
                    {
                        if (newState == Worker.State.SUCCEEDED)
                        {
                            JSObject win = (JSObject) engine.executeScript("window");
                            win.setMember("app", new WebViewInterface());
                        }
                    });

            Client.instance().addListener(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void enter()
    {
        String text = input.getText();
        if (text != null && !text.isEmpty())
        {
            try
            {
                if (text.startsWith("/say "))
                    text = text.substring(5);
                Client.instance().sendInput(text);
            }
            catch (Exception e)
            {
                String error = "<div class=\"message\"><div class=\"error\">" + e.getMessage() + "</div></div>";
                appendContent(error);
            }
            input.setText("");
        }
    }

    public void appendContent(String html)
    {
        Platform.runLater(() ->
        {
            webView.getEngine().executeScript("appendHTML('" + addSlashes(html) + "')");
        });
    }

    public void appendContent(String timestamp, String sender, String text)
    {
        String html = "<div class=\"message\"><div class=\"timestamp\">" + timestamp + "</div><a href=\"\" class=\"sender\"> [" + sender + "] </a><div class=\"text\">" + text + "</div></div>";
        appendContent(html);
    }

    protected static String addSlashes(String text)
    {
        final StringBuffer sb = new StringBuffer(text.length() * 2);
        final StringCharacterIterator iterator = new StringCharacterIterator(text);

        char character = iterator.current();

        while (character != StringCharacterIterator.DONE)
        {
            if (character == '"')
                sb.append("\\\"");
            else if (character == '\'')
                sb.append("\\\'");
            else if (character == '\\')
                sb.append("\\\\");
            else if (character == '\n')
                sb.append("\\n");
            else if (character == '{')
                sb.append("\\{");
            else if (character == '}')
                sb.append("\\}");
            else/*from www  . ja  v a2 s.  c  o m*/
                sb.append(character);

            character = iterator.next();
        }

        return sb.toString();
    }

    @Override
    public void onReceiveObject(Connection connection, Object obj)
    {

    }

    @Override
    public void onReceiveRequest(Connection connection, Request request)
    {
        String text = request.getTextString();
        if (text.startsWith("/say "))
            text = text.substring(5);

        appendContent(request.getTimestampString(), request.getSenderString(), text);
    }

    @Override
    public void onConnect(Connection connection)
    {

    }

    @Override
    public void onDisconnect(Connection connection)
    {

    }

    private void centerStage(Stage stage, double width, double height) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
}
