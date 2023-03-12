package com.danzabarr.messager.client;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class WindowResizer implements EventHandler<MouseEvent>
{
    public int resizeInset;
    public int titleBarHeight;
    private Stage stage;
    private Scene scene;
    private double startX, startY;
    public final int minWidth = 128;
    public final int minHeight = 128;

    private WindowResizer(int resizeInset, int titleBarHeight)
    {
        this.resizeInset = resizeInset;
        this.titleBarHeight = titleBarHeight;
    }

    public static void addListener(Stage stage, Scene scene, int resizeInset, int titleBarHeight)
    {
        WindowResizer controller = new WindowResizer(resizeInset, titleBarHeight);
        controller.stage = stage;
        controller.scene = scene;

        controller.scene.addEventFilter(MouseEvent.MOUSE_MOVED, controller);
        controller.scene.addEventFilter(MouseEvent.MOUSE_PRESSED, controller);
        controller.scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, controller);
        controller.scene.addEventFilter(MouseEvent.MOUSE_EXITED, controller);
        controller.scene.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, controller);
    }

    @Override
    public void handle(MouseEvent mouseEvent)
    {
        double x = mouseEvent.getSceneX();
        double y = mouseEvent.getSceneY();

        double w = scene.getWidth();
        double h = scene.getHeight();

        EventType eventType = mouseEvent.getEventType();

        if (eventType.equals(MouseEvent.MOUSE_MOVED))
        {
            if (stage.isMaximized())
            {
                scene.setCursor(Cursor.DEFAULT);
            }
            else if (x < resizeInset && y < resizeInset)
            {
                scene.setCursor(Cursor.NW_RESIZE);
            }
            else if (x < resizeInset && y > h - resizeInset)
            {
                scene.setCursor(Cursor.SW_RESIZE);
            }
            else if (x > w - resizeInset && y < resizeInset)
            {
                scene.setCursor(Cursor.NE_RESIZE);
            }
            else if (x > w - resizeInset && y > h - resizeInset)
            {
                scene.setCursor(Cursor.SE_RESIZE);
            }
            else if (x < resizeInset)
            {
                scene.setCursor(Cursor.W_RESIZE);
            }
            else if (x > w - resizeInset)
            {
                scene.setCursor(Cursor.E_RESIZE);
            }
            else if (y < resizeInset)
            {
                scene.setCursor(Cursor.N_RESIZE);
            }
            else if (y > h - resizeInset)
            {
                scene.setCursor(Cursor.S_RESIZE);
            }
            
            /*
            else if (y < titleBarHeight)
            {
                scene.setCursor(Cursor.MOVE);
            }
            */
            else
            {
                scene.setCursor(Cursor.DEFAULT);
            }
        }
        else if (eventType.equals(MouseEvent.MOUSE_RELEASED))
        {

        }
        else if (eventType.equals(MouseEvent.MOUSE_EXITED) || eventType.equals(MouseEvent.MOUSE_EXITED_TARGET))
        {
            //scene.setCursor(Cursor.DEFAULT);
        }
        else if (eventType.equals(MouseEvent.MOUSE_PRESSED))
        {
            startX = stage.getWidth() - x;
            startY = stage.getHeight() - y;
        }
        else if (eventType.equals(MouseEvent.MOUSE_DRAGGED))
        {
            Cursor cursor = scene.getCursor();

            if (cursor == Cursor.S_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.SW_RESIZE)
                setStageHeight(y + startY);

            if (cursor == Cursor.E_RESIZE || cursor == Cursor.SE_RESIZE || cursor == Cursor.NE_RESIZE)
                setStageWidth(x + startX);

            if (cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE)
            {
                double x0 = mouseEvent.getScreenX();
                double x1 = stage.getX() - mouseEvent.getScreenX() + stage.getWidth();
                stage.setX(x0);
                setStageWidth(x1);
            }

            if (cursor == Cursor.N_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.NE_RESIZE)
            {
                double y0 = mouseEvent.getScreenY();
                double y1 = stage.getY() - mouseEvent.getScreenY() + stage.getHeight();
                stage.setY(y0);
                setStageHeight(y1);
            }
        }
    }

    private void setStageSize(double width, double height)
    {
        setStageWidth(width);
        setStageHeight(height);
    }

    private void setStageWidth(double width)
    {
        width = Math.max(width, minWidth);
        stage.setWidth(width);
    }

    private void setStageHeight(double height)
    {
        //height = Math.min(height, maxHeight);
        height = Math.max(height, minHeight);
        stage.setHeight(height);
    }
}
