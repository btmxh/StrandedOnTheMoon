/*
 * To change this license header, mouseReleased License Headers in Project Properties.
 * To change this template file, mouseReleased Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.codingarea;

import codinggame.CodingGame;
import codinggame.handlers.ObjectChooseHandler;
import codinggame.objs.robots.Robot;
import codinggame.ui.helps.HelpFX;
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
    public static CodingWindowController currentController;
    public static Scene currentScene;
    
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
        currentScene = scene;
        
        currentController.after_load_init();
        
        stage.setTitle("Code Editor");
        stage.setAlwaysOnTop(true);
        
        HelpFX.initApp();
    }
    
    public static void initApp() {
        new Thread(()->Application.launch(CodingFX.class)).start();
    }
    
    public static void show() {
        Platform.runLater(()->{
            currentStage.show();
            ObjectChooseHandler.Choosable choosing = CodingGame.getInstance().getGameState().getChooseHandler().getChoosingObject().getObject();
            if(choosing instanceof Robot)   currentController.addRobot((Robot) choosing);
        });
    }

    public static boolean isShowing() {
        return currentStage.isShowing();
    }
}
