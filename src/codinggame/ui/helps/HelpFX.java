/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.helps;

import codinggame.CodingGame;
import codinggame.handlers.ObjectChooseHandler;
import codinggame.objs.robots.Robot;
import codinggame.ui.codingarea.CodingFX;
import codinggame.ui.codingarea.CodingWindowController;
import com.bulenkov.darcula.DarculaLaf;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.UIManager;

/**
 *
 * @author Welcome
 */
public class HelpFX extends Application {

    public static HelpFX currentApp;
    public static Stage currentStage;
    public static HelpWindowController currentController;
    public static Scene currentScene;

    @Override
    public void start(Stage stage) throws Exception {
        //Set Swing LAF
        UIManager.setLookAndFeel(new DarculaLaf());
        Platform.setImplicitExit(false);

        FXMLLoader loader = new FXMLLoader(HelpFX.class.getResource("WindowFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        root.getStylesheets().add("/codinggame/ui/codingarea/windowfxml.css");  //using the CodingWindow's css
        stage.setScene(scene);

        currentApp = this;
        currentStage = stage;
        currentController = loader.getController();
        currentScene = scene;

        currentController.after_load_init();

        stage.setTitle("Code Editor");
        stage.setAlwaysOnTop(true);
    }

    public static void initApp() throws Exception {
        new HelpFX().start(new Stage());
    }

    public static void show() {
        Platform.runLater(() -> {
            currentStage.show();
        });
    }

    public static boolean isShowing() {
        return currentStage.isShowing();
    }

}
