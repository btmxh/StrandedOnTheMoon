/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.codingarea;

import codinggame.CodingGame;
import codinggame.lang.JavaParser;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import com.jfoenix.controls.JFXButton;
import com.lwjglwrapper.utils.Utils;
import in.co.s13.syntaxtextareafx.SyntaxTextAreaFX;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * FXML Controller class
 *
 * @author Welcome
 */
public class CodingWindowController implements Initializable {

    private Map<String, Image> images = new HashMap<>();
    
    private Theme codingAreaTheme;
    @FXML
    private TextArea outputTextArea;

    private SyntaxTextAreaFX codingArea;
    @FXML
    private JFXButton run;
    @FXML
    private JFXButton compile;
    @FXML
    private JFXButton runAndCompile;
    @FXML
    private TabPane codeAreaPane;
    
    private JavaParser parser;
    private ObservableMap<Robot, Class> lastClasses = FXCollections.observableHashMap();
    @FXML
    private AnchorPane root;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            codingAreaTheme = Theme.load(CodingWindowController.class.getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
        } catch (IOException ex) {
            Logger.getLogger(CodingWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setIcon(run, "run");
        setIcon(compile, "compile");
        setIcon(runAndCompile, "compile_run");
        images.put("stop", new Image(CodingWindowController.class.getResource("stop.png").toString(), 24, 24, true, true, true));
        
        setTooltip(run, "Run your Compiled Program (Shift + F5)");
        setTooltip(compile, "Compile your Program (F6)");
        setTooltip(runAndCompile, "Compile and Run your Program (F5)");
        
        run.setOnAction((evt) -> run(evt));
        compile.setOnAction((evt) -> compile(evt));
        runAndCompile.setOnAction((evt) -> runAndCompile(evt));
        
        parser = new JavaParser(null);
        
        codeAreaPane.getSelectionModel().selectedItemProperty().addListener((ob, oldVal, newVal) -> {
            RobotTab newTab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
            run.setDisable(!lastClasses.containsKey(newTab.robot));
        });
        lastClasses.addListener((MapChangeListener.Change<? extends Robot, ? extends Class> change) -> {
            RobotTab newTab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
            run.setDisable(!lastClasses.containsKey(newTab.robot));
        });
        BooleanBinding closable = Bindings.size(codeAreaPane.getTabs()).greaterThan(1);
        codeAreaPane.tabClosingPolicyProperty().bind(Bindings.createObjectBinding(() -> closable.get()? TabPane.TabClosingPolicy.ALL_TABS:TabPane.TabClosingPolicy.UNAVAILABLE, closable));
    }    
    
    public void setIcon(JFXButton button, String iconName) {
        Image image = new Image(CodingWindowController.class.getResource(iconName + ".png").toString(), 24, 24, true, true, true);
        images.put(iconName, image);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        button.setGraphic(imageView);
        button.setMaxSize(32, 32);
    }
    
    public void setTooltip(Control node, String tooltip) {
        Tooltip t = new Tooltip(tooltip);
        node.setTooltip(t);
    }
    
    public void run(ActionEvent evt) {
        ImageView graphic = (ImageView) run.getGraphic();
        if(graphic.getImage() == images.get("run")) {
            clearOutput();
            Robot currentRobot = ((RobotTab) codeAreaPane.getSelectionModel().getSelectedItem()).robot;
            Runnable main = null;
            try {
                main = parser.getMainMethod(new Pair<>(currentRobot, lastClasses.get(currentRobot)));
            } catch (NoSuchMethodException ex) {
                println("No Main Method");
            }
            final Runnable _main = main;
            new Thread(){
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        ((ImageView) run.getGraphic()).setImage(images.get("stop"));
                        runAndCompile.setDisable(true);
                    });
                    _main.run();
                    Platform.runLater(() -> {
                        ((ImageView) run.getGraphic()).setImage(images.get("run"));
                        runAndCompile.setDisable(false);
                    });
                }
            }.start();
        } else {
            GameState game = CodingGame.getInstance().getGameState();
            game.getCommandHandler().stop(((RobotTab) codeAreaPane.getSelectionModel().getSelectedItem()).robot);
        }
    }
    
    public void compile(ActionEvent evt) {
        new Thread(() -> {
            parser.setGameState(CodingGame.getInstance().getGameState());
            RobotTab currentTab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
            Class clazz = parser.loadClass(currentTab.swingCodingArea.getText(), loggerWriter(), currentTab.robot);
            if(clazz != null)   lastClasses.put(currentTab.robot, clazz);
        }).start();
    }
    
    public void runAndCompile(ActionEvent evt) {
        new Thread(() -> {
            parser.setGameState(CodingGame.getInstance().getGameState());
            RobotTab currentTab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
            Robot robot = currentTab.robot;
            Class clazz = parser.loadClass(currentTab.swingCodingArea.getText(), loggerWriter(), currentTab.robot);
            lastClasses.put(currentTab.robot, clazz);
            clearOutput();
            Platform.runLater(() -> {
                ((ImageView) run.getGraphic()).setImage(images.get("stop"));
                runAndCompile.setDisable(true);
            });
            try {
                Runnable main = parser.getMainMethod(new Pair<>(robot, lastClasses.get(robot)));
                main.run();
            } catch (NoSuchMethodException ex) {
                println("No Main Method");
            }
            Platform.runLater(() -> {
                ((ImageView) run.getGraphic()).setImage(images.get("run"));
                runAndCompile.setDisable(false);
            });
        }).start();
    }

    private PrintWriter writer;
    private PrintWriter loggerWriter() {
        if(writer == null)  {
            writer = new PrintWriter(new LogWriter());
        }
        return writer;
    }
    
    public void print(String string) {
        Platform.runLater(() -> {
            outputTextArea.appendText(string);
        });
    }

    public void println(String string) {
        print(string + "\n");
    }
    
    public void addRobot(Robot robot) {
        Platform.runLater(() -> {
            for (Tab tab : codeAreaPane.getTabs()) {
                if(((RobotTab) tab).robot == robot) return;
            }
            Tab newTab = new RobotTab(robot);
            codeAreaPane.getTabs().add(newTab);
        });
        
    }

    private void clearOutput() {
        outputTextArea.clear();
    }

    void after_load_init() {
        CodingFX.currentScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination runAndCompile = new KeyCodeCombination(KeyCode.F5);
            final KeyCombination compile = new KeyCodeCombination(KeyCode.F6);
            final KeyCombination run = new KeyCodeCombination(KeyCode.F5, KeyCombination.SHIFT_DOWN);
            
            @Override
            public void handle(KeyEvent evt) {
                if(runAndCompile.match(evt))  runAndCompile(null);
                else if(compile.match(evt)) compile(null);
                else if(run.match(evt))   run(null);
            }
        });
    }

    private class LogWriter extends Writer{

        private LogWriter() {
            lock = new Object();
        }

        @Override
        public void write(int c) throws IOException {
            print((char) c + "");
        }
        
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            String append = new String(cbuf, off, len);
            print(append);
        }

        @Override
        public void write(String append) throws IOException {
            print(append);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            print(str.substring(off, off + len));
        }

        @Override
        public LogWriter append(CharSequence csq) throws IOException {
            write(csq == null? "null":csq.toString());
            return this;
        }

        @Override
        public LogWriter append(char c) throws IOException {
            write(c);
            return this;
        }

        @Override
        public LogWriter append(CharSequence csq, int start, int end) throws IOException {
            CharSequence cs = (csq == null ? "null" : csq);
            write(cs.subSequence(start, end).toString());
            return this;
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            super.write(cbuf);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
        
    }
    
    public class RobotTab extends Tab{
        private Robot robot;
        private RSyntaxTextArea swingCodingArea;

        public RobotTab(Robot robot) {
            this.robot = robot;
            AnchorPane content = new AnchorPane();
            setContent(content);
            setText(robot.getName());
            swingCodingArea = new RSyntaxTextArea();
            swingCodingArea.setText(robot.getSourceCode());
        
            RTextScrollPane swingScrollPane = new RTextScrollPane(swingCodingArea);
            codingAreaTheme.apply(swingCodingArea);
            swingCodingArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            SwingNode swingNode = new SwingNode();
            SwingUtilities.invokeLater(()->{
                swingNode.setContent(swingScrollPane);
            });
            content.getChildren().add(swingNode);

            AnchorPane.setBottomAnchor(swingNode, 0d);
            AnchorPane.setLeftAnchor(swingNode, 0d);
            AnchorPane.setRightAnchor(swingNode, 0d);
            AnchorPane.setTopAnchor(swingNode, 0d);
            swingCodingArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    save();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    save();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    save();
                }
            });
            final List<File> files = new ArrayList<>();
            swingNode.addEventFilter(EventType.ROOT, (event) -> {
                final String type = event.getEventType().getName();
                if(event instanceof DragEvent);else return;
                if (type.contains("DRAG") || type.contains("drag")) {
                    ((DragEvent) event).acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    if(((DragEvent) event).getEventType() != DragEvent.DRAG_DROPPED) {
                        event.consume();
                        List<File> fileList = ((DragEvent) event).getDragboard().getFiles();
                        if(!fileList.isEmpty())  {
                            files.clear();
                            files.addAll(((DragEvent) event).getDragboard().getFiles());
                        }
                        return;
                    }
//                    Task<String> task = new Task<String>() {
//                        @Override
//                        protected String call() throws Exception {
//                            File file = ((DragEvent) event).getDragboard().getFiles().get(0);
//                            String content = Utils.loadFileAsString(file.toString());
//                            return content;
//                        }
//
//                        @Override
//                        protected void updateValue(String value) {
//                            super.updateValue(value); //To change body of generated methods, choose Tools | Templates.
//                        }
//                    };
//                    
//                    task.setOnSucceeded(evt -> {
//                        String value = task.getValue();
//                        RobotTab tab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
//                        tab.swingCodingArea.setText(value);
//                        tab.robot.setSourceCode(value);
//                    });
//
//                    Service<String> service = new Service<String>() {
//                        @Override
//                        protected Task<String> createTask() {
//                            return task;
//                        }
//                    };
                    
                    Platform.runLater(() -> {
                        try {
                            System.out.println(((DragEvent) event).getDragboard().getContentTypes());
                            File file = files.get(0);
                            String code = Utils.loadFileAsString(file.toString());
                            RobotTab tab = (RobotTab) codeAreaPane.getSelectionModel().getSelectedItem();
                            tab.swingCodingArea.setText(code);
                            tab.robot.setSourceCode(code);
                        } catch (IOException ex) {
                            Logger.getLogger(CodingWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    
                    event.consume();
                }
            });
        }
        
        public void save() {
            robot.setSourceCode(swingCodingArea.getText());
        }
        
    }
    
}
