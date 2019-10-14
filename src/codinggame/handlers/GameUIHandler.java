/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.lang.CommandBlock;
import codinggame.lang.Parser;
import codinggame.map.GameMap.ChooseTile;
import codinggame.map.MapCell;
import codinggame.map.cells.InventoryCell;
import codinggame.objs.Battery;
import codinggame.objs.Clock;
import codinggame.objs.Inventory;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.modules.Module;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.ui.CodingArea;
import codinggame.ui.IButton;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.paint.Paint;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Stage;
import com.lwjglwrapper.utils.ui.TextField;
import java.util.Locale;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class GameUIHandler {
    private Stage stage;
    private final CodingArea codingArea, loggerArea;
    private final IButton playButton;
    
    private final IButton openCodeButton;
    
    private Parser parser;
    private GameState game;

    public GameUIHandler(Window window, GameState game) {
        initStaticVariables();
        parser = new Parser(game);
        stage = window.getStage();
        this.game = game;
        int width = window.getWidth(), height = window.getHeight();
        
        codingArea = new CodingArea(stage, Rect.jomlRect(LWJGL.window.getWidth() - INFO_BAR_WIDTH * 3f - 2, LWJGL.window.getHeight() - 810, INFO_BAR_WIDTH * 2f, 600));
        codingArea.setVisible(false);
        loggerArea = new CodingArea(stage, Rect.jomlRect(LWJGL.window.getWidth() - INFO_BAR_WIDTH * 3f - 2, LWJGL.window.getHeight() - 200, INFO_BAR_WIDTH * 2f, 200));
        loggerArea.setEditable(false);
        loggerArea.setVisible(false);
        println("Output:");
        openCodeButton = new IButton(stage, Rect.jomlRect(INFO_BAR_X + 10, LWJGL.window.getHeight() - 100, 200, 40), new Paint[]{new IColor(0.5f), new IColor(0.6f), new IColor(0.2f)}, IColor.WHITE, IColor.WHITE);
        openCodeButton.setVisible(false);
        openCodeButton.setText("Open Code");
        openCodeButton.setOnClickListener(new Button.OnClickListener() {
            private boolean opened = false;
            @Override
            public void action(Stage stage, Button comp, int mode) {
                opened = !opened;
                openCodeButton.setText(opened? "Close Code":"Open Code");
                codingArea.setVisible(opened);
                loggerArea.setVisible(opened);
            }
        });
        playButton = new IButton(stage, Rect.jomlRect(INFO_BAR_X + 10, LWJGL.window.getHeight() - 50, 200, 40), new Paint[]{new IColor(0.5f), new IColor(0.6f), new IColor(0.2f)}, IColor.WHITE, IColor.WHITE);
        playButton.setText("Execute");
        playButton.setOnClickListener((Stage s, Button comp, int mode) -> {
            if(game.getRobotHandler().getCurrentRobot().isRunning()) {
                game.getCommandHandler().stop();
                playButton.setText("Execute");
            } else {
                String code = codingArea.getText();
                CommandBlock block = parser.parse(code);
                game.getCommandHandler().execute(block);
                playButton.setText("Stop");
            }
        });
    }
    
    public void tick() {
        stage.tick();
        Object selected = game.getSelectedObject();
        if(selected instanceof ChooseTile) {
            if(codingArea.isVisible()) {
                openCodeButton.getOnClickListener().action(null, null, 0);
            }
        }
        playButton.setText(game.getRobotHandler().getCurrentRobot().isRunning()? "Stop":"Execute");
        playButton.setVisible(selected instanceof Robot);
        openCodeButton.setVisible(selected instanceof Robot);
    }
    
    public boolean render(NVGGraphics g) {
        //stage.render();
        g.begin();
        CodingArea.textFont.use();
        g.beginPath();
        g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        g.textPaint(IColor.WHITE);
        g.textSize(24);
        Clock clock = game.getClock();
        g.text(clock.getDisplayTime(), 10, 10);
        g.end();
        renderInfoBar(g);
        g.begin();
        stage.render();
        g.end();
        return true;
    }

    private void renderBatteryUI(NVGGraphics g, Battery battery) {
        g.begin();
        CodingArea.textFont.use();
        g.beginPath();
        g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        g.textSize(24);
        g.textPaint(IColor.WHITE);
        g.text("Battery: " + battery.getEnergy() + "/" + battery.getMaxEnergyCapacity(), 10, 10);
        g.strokeWeight(2);
        g.end();
    }
    
    private static int INFO_BAR_WIDTH, INFO_BAR_HEIGHT, INFO_BAR_X, INFO_BAR_Y;
    
    private boolean renderInfoBar(NVGGraphics g) {
        g.begin();
        g.rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT);
        Rect rect = new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT);
        
        g.stroke(IColor.BLACK);
        g.fill(new IColor(0.1f));
        g.push();
        g.translate(INFO_BAR_X, INFO_BAR_Y + 20);
        
        Object selected = game.getSelectedObject();
        if(selected instanceof Robot) {
            renderRobotInfoBar(g, (Robot) selected);
        } else if(selected instanceof ChooseTile) {
            ChooseTile tile = (ChooseTile) selected;
            renderTileInfoBar(g, tile.x, tile.y, tile.layer.getTileAt(tile.x, tile.y));
        }
        g.pop();
        g.end();
        
        return rect.contains(LWJGL.mouse.getCursorPosition()) && LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private void renderRobotInfoBar(NVGGraphics g, Robot robot) {
        CodingArea.textFont.use();
        g.textAlign(NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
        g.textSize(20);
        g.textPaint(IColor.WHITE);
        g.beginPath();
        
        g.text(robot.getTypeName(), 10, 0);
        g.translate(0, 24);
        g.text("Name: " + robot.getName(), 10, 0);
        g.translate(0, 30);
        Battery battery = robot.getBattery();
        g.text("Battery: " + battery.getEnergy() + "/" + battery.getMaxEnergyCapacity(), 10, 0);
        g.translate(0, 30);
        g.text("Position: x=" + robot.getTileX() + ", y=" + robot.getTileY(), 10, 0);
        g.translate(0, 30);
        g.text("Capacity: " + String.format(Locale.US, "%.1f", robot.getInventory().getCurrentCapacity())
                + "/" + robot.getInventory().getMaxCapacity() + " (kg)", 10, 0);
        g.translate(0, 30);
        Rect invTab = new Rect(0, 0, INFO_BAR_WIDTH, 30);
        boolean hovering = invTab.contains(LWJGL.mouse.getCursorPosition().sub(g.getCurrentState().getTranslation()));
        new PaintedShape<>(invTab, hovering? IColor.LIGHTGRAY:IColor.GRAY, null).render(g);
        g.textPaint(IColor.WHITE);
        g.text("Inventory", 10, 5);
        if(hovering && LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            openRobotInv = !openRobotInv;
        }
        float[] ys;
        if(openRobotInv) {
            ys = new float[]{20, 20, 10};
        } else {
            ys = new float[]{10, 10, 20};
        }
        g.polygon(new float[]{
            INFO_BAR_WIDTH - 20, INFO_BAR_WIDTH - 10, INFO_BAR_WIDTH - 15
        }, ys, 3);
        g.fill(IColor.WHITE);
        
        g.translate(0, 32);
        if(openRobotInv) {
            for (Map.Entry<EquipmentSlot, Item> entry : robot.getInventory().getEquipments().entrySet()) {
                EquipmentSlot slot = entry.getKey();
                Item item = entry.getValue();

                g.rect(0, 0, INFO_BAR_WIDTH, 30);
                g.fill(new IColor(0.3f));
                g.textSize(16);
                g.textPaint(IColor.WHITE);
                g.text(item.toString(), 10, 10);
                g.translate(0, 32);
            }
            for (Map.Entry<ItemType, Item> entry : robot.getInventory().getItems().entrySet()) {
                ItemType type = entry.getKey();
                Item item = entry.getValue();

                g.rect(0, 0, INFO_BAR_WIDTH, 30);
                g.fill(new IColor(0.2f));
                g.textSize(16);
                g.textPaint(IColor.WHITE);
                g.text(item.toString(), 10, 10);
                g.translate(0, 32);
            }
        }
        
        Rect modTab = new Rect(0, 0, INFO_BAR_WIDTH, 30);
        hovering = modTab.contains(LWJGL.mouse.getCursorPosition().sub(g.getCurrentState().getTranslation()));
        new PaintedShape<>(modTab, hovering? IColor.LIGHTGRAY:IColor.GRAY, null).render(g);
        g.textPaint(IColor.WHITE);
        g.textSize(20);
        g.text("Modules", 10, 5);
        if(hovering && LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            openRobotMod = !openRobotMod;
        }
        if(openRobotMod) {
            ys = new float[]{20, 20, 10};
        } else {
            ys = new float[]{10, 10, 20};
        }
        g.polygon(new float[]{
            INFO_BAR_WIDTH - 20, INFO_BAR_WIDTH - 10, INFO_BAR_WIDTH - 15
        }, ys, 3);
        g.fill(IColor.WHITE);
        
        if(openRobotMod) {
            for (Module module : robot.getInventory().getModules()) {
                if(module == null)  continue;
                g.translate(0, 32);
                g.rect(0, 0, INFO_BAR_WIDTH, 30);
                g.fill(new IColor(0.2f));
                g.textSize(16);
                g.textPaint(IColor.WHITE);
                g.text(module.getName(), 10, 10);
            }
        }
    }
    
    private void renderTileInfoBar(NVGGraphics g, int x, int y, MapCell cell) {
        CodingArea.textFont.use();
        g.textAlign(NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
        g.textSize(20);
        g.textPaint(IColor.WHITE);
        g.beginPath();
        
        g.text("Tile: " + cell.getTileType().getID(), 10, 0);
        g.translate(0, 30);
        g.text("Position: x=" + x + " y=" + y, 10, 0);
        g.translate(0, 30);
        
        if(cell instanceof InventoryCell) {
            Inventory inv = ((InventoryCell) cell).getInventory();
            g.text("Capacity: " + String.format(Locale.US, "%.1f", inv.getCurrentCapacity())
                    + "/" + inv.getMaxCapacity() + " (kg)", 10, 0);
            g.translate(0, 30);
            Rect invTab = new Rect(0, 0, INFO_BAR_WIDTH, 30);
            boolean hovering = invTab.contains(LWJGL.mouse.getCursorPosition().sub(g.getCurrentState().getTranslation()));
            new PaintedShape<>(invTab, hovering? IColor.LIGHTGRAY:IColor.GRAY, null).render(g);
            g.textPaint(IColor.WHITE);
            g.textSize(20);
            g.text("Inventory", 10, 5);
            if(hovering && LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                openTileInv = !openTileInv;
            }
            float[] ys;
            if(openTileInv) {
                ys = new float[]{20, 20, 10};
            } else {
                ys = new float[]{10, 10, 20};
            }
            g.polygon(new float[]{
                INFO_BAR_WIDTH - 20, INFO_BAR_WIDTH - 10, INFO_BAR_WIDTH - 15
            }, ys, 3);
            g.fill(IColor.WHITE);

            g.translate(0, 32);
            if(openTileInv) {
                for (Map.Entry<ItemType, Item> entry : inv.getItems().entrySet()) {
                    ItemType type = entry.getKey();
                    Item item = entry.getValue();
                    
                    g.rect(0, 0, INFO_BAR_WIDTH, 30);
                    g.fill(new IColor(0.3f));
                    g.textSize(16);
                    g.textPaint(IColor.WHITE);
                    g.text(item.toString(), 10, 10);
                    g.translate(0, 32);
                }
            }
        }
        
        
    } 
    
    private void initStaticVariables() {
        INFO_BAR_WIDTH = 300;
        INFO_BAR_HEIGHT = LWJGL.window.getHeight();
        INFO_BAR_X = LWJGL.window.getWidth() - INFO_BAR_WIDTH;
        INFO_BAR_Y = 0;
    }
    
    private boolean openTileInv = false, openRobotInv = false, openRobotMod = false;
    public boolean clickedOn() {
        return new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT).contains(LWJGL.mouse.getCursorPosition()) || (codingArea.isVisible()? codingArea.clickedOn():false);
    }

    public void println(String string) {
        print(string + "\n");
    }
    
    public void print(String string) {
        loggerArea.getTextBuilder().append(string);
        loggerArea.getTextOnChangeListener().textOnChange(string, TextField.TextOnChangeListener.ADD);
    }
}
