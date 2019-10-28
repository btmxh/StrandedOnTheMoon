/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import net.openhft.compiler.CompilerUtils;

/**
 *
 * @author Welcome
 */
public class JavaParser {

    private GameState game;
    
    public JavaParser(GameState game) {
        this.game = game;
    }
    
    public Runnable getMainMethod(Pair<Robot, Class> pair) throws NoSuchMethodException {
        Robot robot = pair.getKey();
        Class clazz = pair.getValue();
        Method _main = null;
        try {
            _main = clazz.getMethod("main");
            _main.setAccessible(true);
        } catch (SecurityException ex) {
            Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        final Method main = _main;
        return () -> {
            try {
                Object inst = clazz.getConstructor(GameState.class, Robot.class).newInstance(game, game.getRobotHandler().getCurrentRobot());
                robot.beginMoving();
                main.invoke(inst);
                robot.endMoving();
            } catch (Exception ex) {
                Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }
    
    public Class loadClass(String block, PrintWriter loggerWriter) {
        try {
            Robot robot = game.getRobotHandler().getCurrentRobot();
            String className = robot.getName();
            block = formatCode(block, className);
            return CompilerUtils.CACHED_COMPILER.loadFromJava(
                    new URLClassLoader(new URL[0]), "codinggame.lang." + className, block,
                    loggerWriter);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String formatCode(String code, String className) {
        String importRegex = "import.+?(?=;);+";
        Pattern pattern = Pattern.compile(importRegex);
        Matcher matcher = pattern.matcher(code);
        List<String> importStatements = new ArrayList<>();
        while (matcher.find()) {
            String importStatement = matcher.group();
            importStatements.add(importStatement);
        }
        code = code.replaceAll(importRegex, "");
        
        code = "\n\tpublic " + className + "(GameState game, Robot robot) {\n\t\tsuper(game, robot);\n\t}\n\n\t" + code;
        
        code = "public class " + className + " extends GameRobot {\n" + code + "\n\n}"; 
        
        importStatements.add("import codinggame.lang.interfaces.*;");
        importStatements.add("import codinggame.states.*;");
        importStatements.add("import codinggame.objs.*;");
        importStatements.add("import codinggame.objs.robots.*;");
        
        String importString = String.join("\n", importStatements);
        code = importString + "\n\n" + code;
        
        String packageDeclaration = "package codinggame.lang;";
        code = packageDeclaration + "\n\n" + code;
        
        return code;
    }
    
    public static void main(String[] args) {
        new JavaParser(null).formatCode("import printlnutils.println.*;;;;import runnable;public void run() {println(123);}", "Main");
    }

    public void setGameState(GameState game) {
        this.game = game;
    }
    
    
}
