package com.danzabarr.messager.client;

import com.danzabarr.messager.core.Connection;
import com.danzabarr.messager.core.ConnectionListener;
import com.danzabarr.messager.core.Request;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.StringCharacterIterator;

public class OLDClientGUI extends Application implements ConnectionListener
{


    public static void main(String[] args)
    {

        //String host = args[0];
        //int port = Integer.parseInt(args[1]);
        //System.out.println("hmm2");

        //new Client(host, port);

        launch(args);

    }

    private static void toString(Document newDoc) throws Exception
    {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(newDoc);
        Result dest = new StreamResult(System.out);
        aTransformer.transform(src, dest);
    }

    private double dragX;
    private double dragY;
    private Client client;
    private WebView webView;
    private WebEngine engine;
    private TextField inputTextBox;
    private StackPane dropShadow;

    public static final int RESIZE_INSET = 7;
    public static final int TITLE_BAR_HEIGHT = 30;

    public static final Color BACKGROUND_COLOR =                Color.web("#2d2f34");
    public static final Color TITLE_BAR_COLOR =                 Color.web("#25262c99");
    public static final Color TITLE_BAR_BUTTON_HOVER_COLOR =    Color.web("#1e2127");
    public static final Color TITLE_BAR_CLOSE_HOVER_COLOR =     Color.web("#ff0000cc");
    public static final Color TOP_BAR_COLOR =                   Color.web("#2f3239");
    public static final Color INPUT_TEXTBOX_COLOR =             Color.web("#363a42cc");

    public static final CornerRadii CORNERS = new CornerRadii(10);
    public static final Background SQUARE_CORNERS = new Background(new BackgroundFill(BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Background ROUND_CORNERS = new Background(new BackgroundFill(BACKGROUND_COLOR, CORNERS, Insets.EMPTY));

    public static final Background TITLE_BAR = new Background(new BackgroundFill(TITLE_BAR_COLOR, new CornerRadii(10, 10, 0, 0, false), Insets.EMPTY));
    public static final Background TITLE_BAR_BUTTON = Background.EMPTY;
    public static final Background TITLE_BAR_BUTTON_HOVER = new Background(new BackgroundFill(TITLE_BAR_BUTTON_HOVER_COLOR, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Background TITLE_BAR_CLOSE_HOVER = new Background(new BackgroundFill(TITLE_BAR_CLOSE_HOVER_COLOR, new CornerRadii(0, 10, 0, 0, false), Insets.EMPTY));

    public static final Background TOP_BAR = new Background(new BackgroundFill(TOP_BAR_COLOR, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Background BOTTOM_BAR = new Background(new BackgroundFill(BACKGROUND_COLOR, new CornerRadii(0, 0, 10, 10, false), Insets.EMPTY));
    public static final Background INPUT_TEXTBOX = new Background(new BackgroundFill(INPUT_TEXTBOX_COLOR, new CornerRadii(20), Insets.EMPTY));

    public static Button createTitleBarButton(String label, String imagePath, Background background, Background hover)
    {
        Button button = new Button();
        button.setTooltip(new Tooltip(label));
        button.setMinSize(TITLE_BAR_HEIGHT, TITLE_BAR_HEIGHT);
        button.setMaxSize(TITLE_BAR_HEIGHT, TITLE_BAR_HEIGHT);

        Image image = new Image(OLDClientGUI.class.getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(12);
        imageView.setFitWidth(12);
        button.setAlignment(Pos.CENTER);
        button.setGraphic(imageView);

        button.setBackground(background);
        button.setOnMouseEntered(mouseEvent -> {
            button.setBackground(hover);
        });
        button.setOnMouseExited(mouseEvent -> {
            button.setBackground(background);
        });

        return button;
    }

    private void applyTheme()
    {

    }

    @Override
    public void start(Stage stage) throws Exception
    {
        webView = new WebView();
        engine = webView.getEngine();
        String html = readFileAsString("/html/chat.html");
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



        inputTextBox = new TextField();

        inputTextBox.setBackground(INPUT_TEXTBOX);
        inputTextBox.setPadding(new Insets(10, 20, 10, 20));
        inputTextBox.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        inputTextBox.setPromptText("Write a message");

        inputTextBox.setOnAction((actionEvent ->
        {
            String text = inputTextBox.getText();
            if (text != null && !text.isEmpty())
            {
                try
                {
                    if (text.startsWith("/say "))
                        text = text.substring(5);
                    client.sendInput(text);
                }
                catch (Exception e)
                {
                    String error = "<div class=\"message\"><div class=\"error\">" + e.getMessage() + "</div></div>";
                    appendContent(error);
                }
                inputTextBox.setText("");
            }
        }));

        HBox titleBar = new HBox();
        HBox topBar = new HBox();
        HBox bottomBar = new HBox();

        titleBar.setMinHeight(TITLE_BAR_HEIGHT);
        titleBar.setMaxHeight(TITLE_BAR_HEIGHT);
        titleBar.setBackground(TITLE_BAR);

        topBar.setMinHeight(0);
        topBar.setMaxHeight(0);
        topBar.setBackground(TOP_BAR);

        bottomBar.setBackground(BOTTOM_BAR);

        StackPane wrapper = new StackPane();
        VBox content = new VBox();

        wrapper.setBackground(ROUND_CORNERS);

        Button close = createTitleBarButton("Close", "/graphics/titlebar_close.png", TITLE_BAR_BUTTON, TITLE_BAR_CLOSE_HOVER);
        close.setOnAction(actionEvent ->
        {
            stage.close();
            System.exit(0);
        });

        Button minimise = createTitleBarButton("Minimise", "/graphics/titlebar_minimise.png", TITLE_BAR_BUTTON, TITLE_BAR_BUTTON_HOVER);
        minimise.setOnAction(actionEvent ->
        {
            stage.setIconified(true);
        });

        Button maximise = createTitleBarButton("Maximise", "/graphics/titlebar_maximise.png", TITLE_BAR_BUTTON, TITLE_BAR_BUTTON_HOVER);
        maximise.setOnAction(actionEvent ->
        {
            if (stage.isMaximized())
            {
                stage.setMaximized(false);
                wrapper.setBackground(ROUND_CORNERS);
                //dropShadow.setPadding(new Insets(20, 20, 20, 20));
            }
            else
            {
                stage.setMaximized(true);
                wrapper.setBackground(SQUARE_CORNERS);
                //dropShadow.setPadding(null);
            }
        });

        Region titleBarFiller = new Region();
        HBox.setHgrow(titleBarFiller, Priority.ALWAYS);

        titleBar.getChildren().addAll(titleBarFiller, minimise, maximise, close);

        titleBar.setOnMousePressed(mouseEvent ->
        {
            dragX = mouseEvent.getSceneX();
            dragY = mouseEvent.getSceneY();
        });

        titleBar.setOnMouseDragged(mouseEvent ->
        {
            stage.setX(mouseEvent.getScreenX() - dragX);
            stage.setY(mouseEvent.getScreenY() - dragY);
        });

        StackPane webViewContainer = new StackPane(webView);
        //webViewContainer.setPadding(new Insets(0, 0, 0, 0));

        //box.getChildren().addAll(topBar, webViewContainer, textField, button);

        HBox.setHgrow(inputTextBox, Priority.ALWAYS);
        bottomBar.getChildren().addAll(inputTextBox);
        bottomBar.setPadding(new Insets(0, 10, 20, 10));
        bottomBar.setSpacing(5);

        content.getChildren().addAll(titleBar, topBar, webViewContainer, bottomBar);
        VBox.setVgrow(webViewContainer, Priority.ALWAYS);
        content.setPrefSize(500, 650);

        Pane topPane = new Pane();
        Pane leftPane = new Pane();
        Pane rightPane = new Pane();
        Pane bottomPane = new Pane();

        topPane.setMinHeight(RESIZE_INSET);
        topPane.setMaxHeight(RESIZE_INSET);

        leftPane.setMinWidth(RESIZE_INSET);
        leftPane.setMaxWidth(RESIZE_INSET);

        rightPane.setMinWidth(RESIZE_INSET);
        rightPane.setMaxWidth(RESIZE_INSET);

        bottomPane.setMinHeight(RESIZE_INSET);
        bottomPane.setMaxHeight(RESIZE_INSET);

        StackPane.setAlignment(topPane, Pos.TOP_LEFT);
        StackPane.setAlignment(leftPane, Pos.CENTER_LEFT);
        StackPane.setAlignment(rightPane, Pos.CENTER_RIGHT);
        StackPane.setAlignment(bottomPane, Pos.BOTTOM_RIGHT);

        wrapper.getChildren().addAll(content, leftPane, rightPane, topPane, bottomPane);

        //dropShadow = new StackPane(wrapper);
        //dropShadow.setEffect(new DropShadow());
        //dropShadow.setPadding(new Insets(20, 20, 20, 20));
        //dropShadow.setBackground(Background.EMPTY);

        Scene scene = new Scene(wrapper);
        scene.setFill(Color.TRANSPARENT);
        //scene.getRoot().setEffect(new DropShadow());

        //wrapper.setPadding(new Insets(20,20,20,20));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(true);
        //ResizeHelper.addResizeListener(stage);
        WindowResizer.addListener(stage, scene, RESIZE_INSET, TITLE_BAR_HEIGHT);


        TextField connectionHostInput = new TextField();
        TextField connectionPortInput = new TextField();
        Button connectionConnectButton = new Button("Connect");
        Label connectionErrorOutput = new Label();

        VBox connectionRoot = new VBox(connectionHostInput, connectionPortInput, connectionConnectButton, connectionErrorOutput);

        Scene connectionScreen = new Scene(connectionRoot);

        stage.setScene(connectionScreen);
        stage.show();

        connectionConnectButton.setOnAction(actionEvent ->
        {

            String host = connectionHostInput.getText();
            String port = connectionPortInput.getText();

            try
            {
                connect(host, port);
                stage.setScene(scene);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                connectionErrorOutput.setText(e.getMessage());
            }

        });
    }

    private void connect(String host, String port)
            throws Exception
    {
        //String host = "127.0.0.1";
        //String host = "2.27.89.164";
        //int port = 8888;
        client = Client.createConnection(host, Integer.parseInt(port));
//        client.connect(host, Integer.parseInt(port));
        client.addListener(this);
    }

    public void appendContent(String html)
    {
        Platform.runLater(() ->
        {
            engine.executeScript("appendHTML('" + addSlashes(html) + "')");
        });
    }

    public void appendContent(String timestamp, String sender, String text)
    {
        String senderString = "<a href=\"https://www.w3schools.com\"> ["+sender+"]</a>";

        String html = "<div class=\"message\"><div class=\"timestamp\">" + timestamp + "</div><div class=\"sender\"> " + senderString + ": </div><div class=\"text\">" + text + "</div></div>";
        appendContent(html);
    }

    private boolean documentLoaded = false;

    public void documentLoaded()
    {

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
}
