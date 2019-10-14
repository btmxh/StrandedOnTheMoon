/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.globjs.Camera;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
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
    private Camera camera;
    private HashMap<String, Robot> robots = new HashMap<>();
    private Robot selected;

    public RobotHandler(GameState game, Camera camera) {
        this.game = game;
        this.camera = camera;
    }

    public void update() {
        robots.values().forEach(Robot::update);
    }
    
    public void render(NVGGraphics g) {
        g.begin();
        game.getMapViewport().updateScissor(g);
        for (Robot robot : robots.values()) {
            robot.render(g, robot == selected);
        }
        g.end();
    }
    
    private void addRobot(Robot robot) {
        robots.put(robot.getName(), robot);
        if(selected == null) selected = robot;
    }
    
    public void selectRobot(Robot robot) {
        selected = robot;
        game.select(robot);
    }
    
    public void selectRobot(String name) {
        selectRobot(robots.get(name));
    }

    public void addRobot(Robot robot, boolean select) {
        addRobot(robot);
        if(select)  selectRobot(robot);
    }

    public Robot getCurrentRobot() {
        return selected;
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
                    robot.getInventory().refresh(game);
                }
            } catch (IOException | ClassNotFoundException ex) {
                return false;
            }
        }
        return true;
    }

    public boolean hoveringOnRobots() {
        return robots.values().stream().anyMatch((robot) -> {
            return robot.isBeingHovered();
        });
    }
}
