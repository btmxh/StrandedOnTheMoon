/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.helps;

import codinggame.ui.codingarea.CodingWindowController;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Welcome
 */
public class HelpWindowController implements Initializable {

    private static final String URL;
    
    private Map<String, Image> images = new HashMap<>();
    
    static {
        String _URL = null; 
        try {
            _URL = new File("docs/index.html").toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(HelpWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        URL = _URL;
    }
    
    @FXML
    private WebView webView;
    @FXML
    private JFXButton back;
    @FXML
    private JFXButton forward;
    @FXML
    private JFXButton home;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        home(null);
        setIcon(back, "back");
        setIcon(forward, "forward");
        setIcon(home, "home");
    }    

    void after_load_init() {
    }

    @FXML
    private void back(ActionEvent event) {
        webView.getEngine().executeScript("history.back()");
    }

    @FXML
    private void forward(ActionEvent event) {
        webView.getEngine().executeScript("history.forward()");
    }

    @FXML
    private void home(ActionEvent event) {
        webView.getEngine().load(URL);
    }
    
    
    public void setIcon(JFXButton button, String iconName) {
        Image image = new Image(HelpWindowController.class.getResource(iconName + ".png").toString(), 32, 32, true, true, true);
        images.put(iconName, image);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        button.setGraphic(imageView);
        button.setMaxSize(32, 32);
    }
}
