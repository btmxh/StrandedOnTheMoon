/*
 * To change this license header, mouseReleased License Headers in Project Properties.
 * To change this template file, mouseReleased Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.globjs.Camera;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.states.InputProcessor;
import codinggame.ui.codingarea.CodingFX;
import com.lwjglwrapper.nanovg.NVGGraphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author Welcome
 */
public class RobotHandler {
    private GameState game;
    private HashMap<String, Robot> robots = new HashMap<>();

    public RobotHandler(GameState game) {
        this.game = game;
    }

    public void update(InputProcessor inputProcessor) {
        robots.values().forEach(r -> r.update(inputProcessor));
    }
    
    private void addRobot(Robot robot) {
        robots.put(robot.getName(), robot);
    }
    
    public void selectRobot(Robot robot, float t) {
        game.getChooseHandler().mouseReleased(ObjectChooseHandler.ChoosingObject.robot(robot), t);
        if(CodingFX.currentController == null || !CodingFX.isShowing())   return;
        CodingFX.currentController.addRobot(robot);
    }
    
    public void selectRobot(String name, float t) {
        selectRobot(robots.get(name), t);
    }

    public void addRobot(Robot robot, boolean select) {
        addRobot(robot);
        if(select)  selectRobot(robot, Float.POSITIVE_INFINITY);
    }
    
    public Robot getRobot(String robotName) {
        return robots.get(robotName);
    }

    public void writeRobots() {
        try {
            for (String name : robots.keySet()) {
                Robot robot = robots.get(name);
                writeRobot(name, robot);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void writeRobot(String name, Robot robot) throws IOException {
        String robotPath = "saves/procmap/robots/r" + name + ".rbt";
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(robotPath));
        oos.writeObject(robot);
        oos.flush();
        oos.close();
    }
    
    public boolean loadRobots(GameState game) {
        String robotDirectory = "saves/procmap/robots";
        for (File file : new File(robotDirectory).listFiles()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Object obj = ois.readObject();
                if(obj instanceof Robot) {
                    Robot robot = (Robot) obj;
                    addRobot(robot);
                    robot.setGameState(game);
                    robot.createLock();
                    robot.getInventory().refresh(game);
                }
            } catch (IOException | ClassNotFoundException ex) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, Robot> getRobotList() {
        return robots;
    }
}
