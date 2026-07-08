package com.nc.mdtable.tools;

import com.nc.mdtable.Globals;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelperFunctions {

    private static final Logger _log = LogManager.getLogger(HelperFunctions.class);
    public static double SF = 180.0 / Math.PI;

    public static void centerWindow(Window window) {
        window.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent event) -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        });
    }

    public Node loadFxml(ResourceBundle bundle, String path, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path), bundle);
            loader.setController(controller);
            Node node = loader.load();
            return node;
        } catch (IOException ex) {
            _log.error(ex.getMessage());
        }
        return null;
    }

    public static Node loadFxmlNode(ResourceBundle bundle, String path, Object controller) {
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        return node;
    }

    public static Tab addTab(ResourceBundle bundle, TabPane tabPane, String path, Object controller, String tabName) {
        long start = System.currentTimeMillis();
        Tab tab = new Tab(tabName);
        tabPane.getTabs().add(tab);
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        tab.setContent(node);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return tab;
    }

    public static BorderPane createTab(ResourceBundle bundle, String path, Object controller) {
        long start = System.currentTimeMillis();
        BorderPane borderPane = new BorderPane();
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        borderPane.setCenter(node);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return borderPane;
    }

    public static void showAlertDialog(ResourceBundle bundle, Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);//cleaner look
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Globals.CSS_PATH);

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        try {
            stage.getIcons().add(new Image(HelperFunctions.class.getResourceAsStream(Globals.APP_LOGO_PATH)));
        } catch (Exception ex) {
            _log.error("Failed to load app icon: " + Globals.APP_LOGO_PATH, ex);
        }

        HelperFunctions.centerWindow(stage);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

        styleDialogButtons(alert);

        alert.showAndWait();
    }

    private static Image scaleImage(Image original, double ratio) {
        int newHeight = (int) (original.getHeight());
        int newWidth = (int) (newHeight * ratio);

        WritableImage scaled = new WritableImage(newWidth, newHeight);
        PixelWriter writer = scaled.getPixelWriter();

        // Read pixels from original and write to scaled
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                // Get pixel from scaled position in original
                double srcX = x / ratio;
                double srcY = y;
                writer.setArgb(x, y, original.getPixelReader().getArgb((int) srcX, (int) srcY));
            }
        }

        return scaled;
    }

    public static void styleDialogButtons(Dialog<?> dialog) {
        dialog.setOnShown(e
                -> Platform.runLater(()
                        -> styleButtons(dialog)
                )
        );
    }

    private static void styleButtons(Dialog<?> dialog) {
        for (ButtonType type : dialog.getDialogPane().getButtonTypes()) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(type);
            if (type == ButtonType.CANCEL) {
                btn.getStyleClass().add("btn-indigo");
            } else {
                btn.getStyleClass().add("btn-blue");
            }
        }
    }
}
