/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.codingarea;

import codinggame.CodingGame;
import com.bulenkov.darcula.DarculaLaf;
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
public class CodingFX extends Application{

    public static CodingFX currentApp;
    public static Stage currentStage;
    public static WindowController currentController;
    
    @Override
    public void start(Stage stage) throws Exception {
        //Set Swing LAF
        UIManager.setLookAndFeel(new DarculaLaf());
        Platform.setImplicitExit(false);
        
        FXMLLoader loader = new FXMLLoader(CodingFX.class.getResource("WindowFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        root.getStylesheets().add("/codinggame/ui/codingarea/windowfxml.css");
        stage.setScene(scene);
        
        currentApp = this;
        currentStage = stage;
        currentController = loader.getController();
        
        stage.setTitle("Code Editor");
        stage.setAlwaysOnTop(true);
        stage.show();
        stage.hide();
    }
    
    public static void initApp() {
        new Thread(()->Application.launch(CodingFX.class)).start();
    }
    
    public static void show() {
        Platform.runLater(()->{
            currentStage.show();
            currentController.addRobot(CodingGame.getInstance().getGameState().getRobotHandler().getCurrentRobot());
        });
    }

    public static boolean isShowing() {
        return currentStage.isShowing();
    }
}
