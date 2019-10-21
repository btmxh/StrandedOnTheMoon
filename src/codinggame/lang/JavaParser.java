/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    
    public Runnable parse(String block, Robot robot) {
        try {
            String className = robot.getName();
            block = formatCode(block, className);
            System.out.println(block);
            Class clazz = CompilerUtils.CACHED_COMPILER.loadFromJava(
                    new URLClassLoader(new URL[0]), "codinggame.lang." + className, block,
                    game.getUIHandler().loggerWriter());
            return () -> {
                try {
                    Method main = clazz.getMethod("main");
                    if(main != null) {
                        main.setAccessible(true);
                        Object inst = clazz.getConstructor(GameState.class, Robot.class).newInstance(game, game.getRobotHandler().getCurrentRobot());
                        robot.beginMoving();
                        main.invoke(inst);
                        robot.endMoving();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
        } catch (ClassNotFoundException ex) {
            System.out.println("Class loaded unsuccessfully");
        }
        
        return ()->{};
    }
    
    public String formatCode(String code, String className) {
        System.out.println(code);
        System.out.println("____________________");
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
        
        System.out.println(code);
        return code;
    }
    
    public static void main(String[] args) {
        new JavaParser(null).formatCode("import printlnutils.println.*;;;;import runnable;public void run() {println(123);}", "Main");
    }
}
