/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import codinggame.CodingGame;
import codinggame.handlers.MapHandler.ChooseTile;
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.cells.FacingCell;
import codinggame.map.cells.FacingCell.Facing;
import codinggame.map.cells.InventoryCell;
import codinggame.map.cells.SheetCell;
import codinggame.objs.Battery;
import codinggame.objs.Inventory;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.modules.Module;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.states.InputProcessor;
import codinggame.ui.IButton;
import codinggame.ui.IComboBox;
import codinggame.ui.Tab;
import codinggame.ui.TabbedPanel;
import codinggame.ui.codingarea.CodingFX;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.nanovg.NVGFont;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.nanovg.paint.ImagePaint;
import com.lwjglwrapper.nanovg.paint.Paint;
import com.lwjglwrapper.nanovg.paint.types.FillPaint;
import com.lwjglwrapper.utils.colors.StaticColor;
import com.lwjglwrapper.utils.Utils;
import com.lwjglwrapper.utils.floats.GLFloats;
import com.lwjglwrapper.utils.floats.GLFloat;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;
import com.lwjglwrapper.utils.geom.shapes.GLRect;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.geom.shapes.RoundRect;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.Panel;
import com.lwjglwrapper.utils.ui.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class GameUIHandler {
    private Stage stage;
    
    private NVGImage closeIcon;
    public static NVGFont textFont;
    
    private final IButton openCodeButton;
    private final Button closeButton;
    private final TabbedPanel infoBar;
    private final Tab tileInventory;
    
    private GameState game;
    
    private IComboBox facingBox;

    public GameUIHandler(Window window, GameState game) {
        Tween.registerAccessor(TabbedPanel.class, new TabbedPanel.Accessor());
        initStaticVariables();
        textFont = LWJGL.graphics.createFont("res/ProggyClean.ttf", "Proggy Clean");
        stage = window.getStage();
        this.game = game;
        
        CodingFX.initApp();
        
        try {
            closeIcon = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(GameUIHandler.class.getResourceAsStream("/close1.png"), 8 * 1024), NanoVG.NVG_IMAGE_GENERATE_MIPMAPS);
        } catch (IOException ex) {
            Logger.getLogger(GameUIHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        infoBar = new TabbedPanel(stage, true);
        
        Panel robotInfoBar = new Panel(stage, false);
        robotInfoBar.getShapeStates().reset()
                .setAll(new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(StaticColor.BLACK)
                .setAllFills(new StaticColor(0.1f))
                .construct(false);
        
        Component robotPanelInfo = new Component(stage, false, 1);
        robotPanelInfo.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints((g) -> {
            g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get());
            Robot robot = (Robot) game.getSelectedObject();
            g.setUpText(textFont, StaticColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
            g.translate(0, 60);
            g.text(robot.getTypeName(), 10, 0);
            g.translate(0, 30);
            g.text("Name: " + robot.getName(), 10, 0);
            g.translate(0, 30);
            Battery battery = robot.getBattery();
            g.text("Battery: " + (int) battery.getEnergy() + "/" + (int) battery.getMaxEnergyCapacity(), 10, 0);
            g.translate(0, 30);
            g.text("Position: x=" + robot.getTileX() + ", y=" + robot.getTileY(), 10, 0);
            g.translate(0, 30);
            g.text("Capacity: " + String.format(Locale.US, "%.1f", robot.getInventory().getCurrentCapacity())
                    + "/" + robot.getInventory().getMaxCapacity() + " (kg)", 10, 0);
            g.translate(0, 30);
        }).construct(false);
        
        Tab robotInv = new Tab(stage, "Inventory", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                Robot robot = (Robot) game.getSelectedObject();
                for (Map.Entry<EquipmentSlot, Item> entry : robot.getInventory().getEquipments().entrySet()) {
                    EquipmentSlot slot = entry.getKey();
                    Item item = entry.getValue();

                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(slot.getColor());
                    g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
                for (Pair<ItemType, Item> entry : robot.getInventory().getItems().entrySet()) {
                    Item item = entry.getValue();

                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new StaticColor(0.2f));
                    g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
            }
        };
        Tab modules = new Tab(stage, "Modules", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                Robot robot = (Robot) game.getSelectedObject();
                for (Module module : robot.getInventory().getModules()) {
                    if(module == null)  continue;
                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new StaticColor(0.2f));
                    g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(module.getName(), 10, 15);
                    g.image(module.getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
            }
        };
        
        Panel tileInfoBar = new Panel(stage, false);
        facingBox = new IComboBox(stage, 40, new GLRect(()->90, ()->0, ()->100, ()->30), textFont);
        tileInfoBar.getShapeStates().reset()
                .setAll(new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(StaticColor.BLACK)
                .setAllFills(new StaticColor(0.1f))
                .setAllBeforePaints((g) -> g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get()))
                .setAllAfterPaints((g) -> {
                    ChooseTile tile = (ChooseTile) game.getSelectedObject();
                    MapCell cell = tile.getCell();
                    if(cell == null)    return;
                    if(cell.getTileType() == null)  return;
                    g.translate(0, 60);
                    g.setUpText(textFont, StaticColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
                    g.text("Tile: " + cell.getTileType().getName(), 10, 0);
                    g.translate(0, 30);
                    g.text("Position: x=" + tile.x + " y=" + tile.y, 10, 0);
                    g.translate(0, 25);
                    if(cell instanceof FacingCell) {
                        g.text("Facing: ", 10, 5);
                        facingBox.setVisible(true);
                    } else facingBox.setVisible(false);
                    if(cell instanceof SheetCell) {
                        if(cell.getTileType().getID() == MapTile.POTATO_CROPS) {
                            int idx = ((SheetCell) cell).getIndex();
                            String representation = idx == 3? "Mature":String.valueOf(idx + 1);
                            g.text("Growth Stage: " + representation, 10, 5);
                            g.translate(0, 30);
                        }
                    }
                    //g.translate(0, 30);
                }).construct(false);
        new IComboBox.IComboBoxCell(facingBox, "Left");
        new IComboBox.IComboBoxCell(facingBox, "Up");
        new IComboBox.IComboBoxCell(facingBox, "Right");
        new IComboBox.IComboBoxCell(facingBox, "Down");
        facingBox.setOnChangeListener((idx, cell)->{
            Object selected = game.getSelectedObject();
            if(selected instanceof ChooseTile) {
                ChooseTile tile = (ChooseTile) selected;
                ((FacingCell) tile.getCell()).setFacing(Facing.values()[idx]);
            }
        });
        
        tileInfoBar.addChild(facingBox);
        
        tileInventory = new Tab(stage, "Inventory", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                ChooseTile tile = (ChooseTile) game.getSelectedObject();
                InventoryCell cell = (InventoryCell) tile.getCell();
                Inventory inv = cell.getInventory();
                for (Pair<ItemType, Item> entry : inv.getItems().entrySet()) {
                    Item item = entry.getValue();
                    
                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new StaticColor(0.2f));
                    g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
            }
        };
        openCodeButton = new IButton(stage, false, new GLRect(
                GLFloat.memValue(10), GLFloats.w_fromFarXAxis(GLFloat.memValue(260)), GLFloat.memValue(200), GLFloat.memValue(40)
        ), new Paint[]{new StaticColor(0.5f), new StaticColor(0.6f), new StaticColor(0.2f)}, StaticColor.WHITE, StaticColor.WHITE);
        openCodeButton.setText("Open Code");
        openCodeButton.setOnClickListener(new Button.OnClickListener() {
            private boolean opened = false;
            @Override
            public void action(Stage stage, Button comp, int mode) {
                CodingFX.show();
            }
        });
        
        closeButton = new Button(stage, false);
        GLRect imageBounds = new GLRect(GLFloat.add(INFO_BAR_X, GLFloat.memValue(10)), GLFloat.add(INFO_BAR_Y, GLFloat.memValue(10)), GLFloat.memValue(32), GLFloat.memValue(32));
        GLRect bounds = new GLRect(GLFloat.add(INFO_BAR_X, GLFloat.memValue(5)), GLFloat.add(INFO_BAR_Y, GLFloat.memValue(5)), GLFloat.memValue(42), GLFloat.memValue(42));
        final RoundRect rect = new RoundRect(bounds, GLFloat.memValue(4));
        
        closeButton.getShapeStates().setAll(new Rect(imageBounds, bounds))
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.1f), null).render(g), Button.NORMAL)
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.3f), null).render(g), Button.HOVER)
                .setBeforePaint(g -> new PaintedShape(rect, StaticColor.BLACK, null).render(g), Button.CLICKED)
                .setAllFills(new FillPaint() {
                    @Override
                    public void fill(long nvgID) {
                        ImagePaint paint = LWJGL.graphics.imagePaint(closeIcon, imageBounds.getJOMLRect(), 0, 1f);
                        paint.fill(nvgID);
                    }

                    @Override
                    public FillPaint fmulAlpha(float alpha) {
                        return this;
                    }
                })
//                .setAllFills(StaticColor.BLUE)
                .construct(false);
        closeButton.setOnClickListener((Stage stage1, Button comp, int mode) -> {
            closeInfoBar();
        });
        
        robotInfoBar.addChild(robotPanelInfo);
        robotInfoBar.addChild(openCodeButton);
        robotInfoBar.addChild(robotInv);
        robotInfoBar.addChild(modules);
        
        tileInfoBar.addChild(tileInventory);
        
        infoBar.getComponents().add(closeButton);
        
        infoBar.getPanels().addAll(Arrays.asList(robotInfoBar, tileInfoBar));  
    }
    
    public void tick(InputProcessor processor) {
        stage.tick();
    }
    
    public void render(NVGGraphics g) {
        Object selected = game.getSelectedObject();
        if(selected == null)    infoBar.select(-1);
        else if(selected instanceof Robot)  infoBar.select(0);
        else if(selected instanceof ChooseTile) {
            infoBar.select(1);
            ChooseTile tile = (ChooseTile) selected;
            MapCell cell = tile.getCell();
            tileInventory.setVisible(cell instanceof InventoryCell);
            if(cell instanceof FacingCell) {
                Facing facing = ((FacingCell) cell).getFacing();
                facingBox.set(facing.ordinal());
            }
        }
        closeButton.setVisible(infoBar.getSelectedIndex() >= 0);
//        g.image(ItemTypes.COPPER_ORE.getNanoVGTexture(), 40, 40, 128, 128, StaticColor.RED);
        g.setUpText(textFont, StaticColor.WHITE, 24, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
        g.text(game.getClock().getDisplayTime(), 10, 10);
        g.text(game.getInputProcessor().toString(), 10, 40);
        g.text(infoBar.dx + "", 10, 70);
        g.text(infoBar.getSelectedIndex() + "", 10, 100);
        
        stage.render();
    }
    
    private static GLFloat INFO_BAR_WIDTH, INFO_BAR_HEIGHT, INFO_BAR_X, INFO_BAR_Y;
    
    private void initStaticVariables() {
        INFO_BAR_WIDTH = GLFloat.memValue(300);
        INFO_BAR_HEIGHT = () -> LWJGL.window.getHeight();
        INFO_BAR_X = GLFloats.w_fromFarYAxis(INFO_BAR_WIDTH);
        INFO_BAR_Y = GLFloats.ZERO;
        GLFloats.setWindow(LWJGL.window);
        GLFloats.setViewport(LWJGL.window.getViewport());
        
        LWJGL.window.getWindowCallbacks().getSizeCallback().add(1, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if((width == 0 && height == 0)) {
                    //User has just restore/minimize the window, not resizing it
                } else {
                    game.resize();
                }
            }
        });
    }
    
    public boolean clickedOn() {
        if(infoBar.getSelectedIndex() == -1)    return false;
        Rect infoBarBounds = new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT);
        return infoBarBounds.contains(LWJGL.mouse.getCursorPosition());
    }
    
    public void dispose() {
        closeIcon.dispose();
    }

    public boolean infoBarVisible() {
        return infoBar.getSelectedIndex() >= 0;
    }
    
    public void showInfoBar(int idx) {
        if(infoBarVisible())    return;
        infoBar.select(idx);
        Tween.to(infoBar, TabbedPanel.Accessor.FADE, 0.5f).target(1).ease(Sine.OUT).start(CodingGame.getInstance().tweenManager);
        Tween.to(infoBar, TabbedPanel.Accessor.MOVE, 0.25f).target(0).ease(Sine.IN).start(CodingGame.getInstance().tweenManager);
    }
    
    public void closeInfoBar() {
        Tween.to(infoBar, TabbedPanel.Accessor.FADE, 0.25f).target(0).ease(Sine.OUT).start(CodingGame.getInstance().tweenManager);
        Tween.to(infoBar, TabbedPanel.Accessor.MOVE, 0.5f).target(INFO_BAR_X.get()).ease(Sine.IN).start(CodingGame.getInstance().tweenManager).setCallback((type, source) -> {
            infoBar.select(-1);
            game.select(null);
            game.setProcessor(InputProcessor.GAME);
        });
    }
}
