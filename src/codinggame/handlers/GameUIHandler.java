/*
 * To change this license header, mouseReleased License Headers in Project Properties.
 * To change this template file, mouseReleased Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import codinggame.CodingGame;
import codinggame.map.MapCell;
import codinggame.map.proceduralmap.CellUtils;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.tiles.MapTiles;
import codinggame.objs.Battery;
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
import codinggame.ui.books.Book;
import codinggame.ui.books.XMLBookParser;
import codinggame.ui.books.content.pages.XMLPage;
import codinggame.ui.codingarea.CodingFX;
import codinggame.ui.helps.HelpFX;
import codinggame.utils.Utils;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.nanovg.NVGFont;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.nanovg.paint.ImagePaint;
import com.lwjglwrapper.nanovg.paint.Paint;
import com.lwjglwrapper.utils.colors.StaticColor;
//import com.lwjglwrapper.utils.Utils;
import com.lwjglwrapper.utils.floats.GLFloats;
import com.lwjglwrapper.utils.floats.GLFloat;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;
import com.lwjglwrapper.utils.geom.shapes.GLRect;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.Panel;
import com.lwjglwrapper.utils.ui.Stage;
import java.io.IOException;
import java.nio.ByteBuffer;
import static java.text.MessageFormat.format;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class GameUIHandler {
    private Stage stage;
    
    public static NVGFont textFont;
    
    private final IButton openCodeButton;
    private final Button closeButton;
    private final TabbedPanel infoBar;
    private final Tab tileInventory;
    
    private final Panel optionBar;
    
    
    private GameState game;
    
    private IComboBox facingBox;
    
    private Book guideBook;

    public GameUIHandler(Window window, GameState game) {
        Tween.registerAccessor(TabbedPanel.class, new TabbedPanel.Accessor());
        initStaticVariables();
        textFont = LWJGL.graphics.createFont("res/fonts/OpenSans-Regular.ttf", "Proggy Clean");
        stage = window.getStage();
        this.game = game;
        
        CodingFX.initApp();
//        HelpFX.initApp();
        XMLPage.init();
        
        try {
            guideBook = XMLBookParser.parse("/books/guidebook/content.xml", "");
        } catch (Exception ex) {
            Logger.getLogger(GameUIHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        optionBar = new Panel(stage, true);
        optionBar.getShapeStates().reset()
                .setAll(new Rect(new GLRect(GLFloats.ZERO, GLFloats.ZERO, OPTION_BAR_WIDTH, OPTION_BAR_HEIGHT)))
                .setAllFills(new StaticColor(0.1f))
                .setAllBeforePaints(g -> {
                    g.push();
                    g.translate(OPTION_BAR_X.get(), OPTION_BAR_Y.get());
                })
                .construct(false);
        
        infoBar = new TabbedPanel(stage, true);
        
        GLRect temp;
        
        temp = new GLRect(INFO_BAR_X, INFO_BAR_Y, GLFloat.memValue(32), GLFloat.memValue(32));
        closeButton = new IconButton(stage, temp, 4f, IconManager.icon("close"));
        closeButton.setOnClickListener((s, c, m) -> {
            closeInfoBar();
        });
        
        temp = new GLRect(GLFloats.ZERO, GLFloats.ZERO, OPTION_BAR_HEIGHT, OPTION_BAR_HEIGHT);
        Button pauseButton = new IconButton(stage, temp, 4f, IconManager.icon("pause"), OPTION_BAR_HEIGHT, GLFloats.ZERO);
        pauseButton.setOnClickListener((s, c, m) -> {
            CodingGame.getInstance().setState(CodingGame.getInstance().os);
        });
        
        
        
        Component timeInfo = new Component(stage, false, 1);
        timeInfo.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints(g -> {
            final float TIME_WIDTH = 150;
            g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_BASELINE | NanoVG.NVG_ALIGN_CENTER);
            g.text(game.getClock().getDisplayDay() + "( Day " + game.getClock().getDay() + " )", TIME_WIDTH / 2, 12);
            g.text(game.getClock().getDisplayTime(), TIME_WIDTH / 2, 26);
            g.translate(TIME_WIDTH, 0);
        }).construct(false);
        
        Component supplyInfo = new Component(stage, false, 1);
        supplyInfo.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints(g -> {
            final float BAR_WIDTH = 120, BAR_HEIGHT = 10, X_OFFSET = 4, Y_OFFSET1 = 4, Y_OFFSET2 = Y_OFFSET1 * 2 + BAR_HEIGHT;
            final float IN_BAR_X_OFF = 2, IN_BAR_Y_OFF = 2, IN_BAR_WIDTH = BAR_WIDTH - IN_BAR_X_OFF * 2, IN_BAR_HEIGHT = BAR_HEIGHT - IN_BAR_Y_OFF * 2;
            final SurvivalHandler sHandler = game.getSurvivalHandler();
            g.strokeWeight(2);
            g.translate(X_OFFSET, 0);
            g.roundRect(0, Y_OFFSET1, BAR_WIDTH, BAR_HEIGHT, 2);
            g.stroke(StaticColor.ORANGE);
            g.rect(IN_BAR_X_OFF, Y_OFFSET1 + IN_BAR_Y_OFF, IN_BAR_WIDTH * sHandler.getFoodPercentage(), IN_BAR_HEIGHT);
            g.fill(StaticColor.ORANGE);
            g.roundRect(0, Y_OFFSET2, BAR_WIDTH, BAR_HEIGHT, 2);
            g.stroke(StaticColor.AQUA);
            g.rect(IN_BAR_X_OFF, Y_OFFSET2 + IN_BAR_Y_OFF, IN_BAR_WIDTH * sHandler.getWaterPercentage(), IN_BAR_HEIGHT);
            g.fill(StaticColor.AQUA);
            g.translate(X_OFFSET + BAR_WIDTH, 0);
            g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_LEFT);
            g.text(format("Food: {0, number, integer} / 100", sHandler.getFood()), 0, Y_OFFSET1 + BAR_HEIGHT / 2 + 4);
            g.text(format("Water: {0, number, integer} / 100", sHandler.getWater()), 0, Y_OFFSET2 + BAR_HEIGHT / 2 + 4);
            g.translate(100, 0);
            
            g.translate(X_OFFSET, 0);
            g.roundRect(0, Y_OFFSET1, BAR_WIDTH, BAR_HEIGHT, 2);
            g.stroke(StaticColor.LIGHTSKYBLUE);
            g.rect(IN_BAR_X_OFF, Y_OFFSET1 + IN_BAR_Y_OFF, IN_BAR_WIDTH * sHandler.getOxygenPercentage(), IN_BAR_HEIGHT);
            g.fill(StaticColor.LIGHTSKYBLUE);
            g.roundRect(0, Y_OFFSET2, BAR_WIDTH, BAR_HEIGHT, 2);
            g.stroke(StaticColor.GOLD);
            g.rect(IN_BAR_X_OFF, Y_OFFSET2 + IN_BAR_Y_OFF, IN_BAR_WIDTH * 0f, IN_BAR_HEIGHT);
            g.fill(StaticColor.GOLD);
            g.translate(X_OFFSET + BAR_WIDTH, 0);
            g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_LEFT);
            g.text(format("Oxygen: {0, number, integer} / 100", sHandler.getOxygen()), 0, Y_OFFSET1 + BAR_HEIGHT / 2 + 4);
            g.text("Energy: 0 / 100", 0, Y_OFFSET2 + BAR_HEIGHT / 2 + 4);
            
            g.translate(300, 0);
            
        }).construct(false);
        
        Button robots = new IconButton(stage, temp, 4f, IconManager.icon("robot"), OPTION_BAR_HEIGHT, GLFloats.ZERO);
        robots.setOnClickListener((s, b, m) -> {
            infoBar.setVisible(true);
            showInfoBar(2);
        });
        
        Component pop = new Component(stage, false, 1);
        pop.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints(NVGGraphics::pop).construct(false);
        
        Component align = new Component(stage, false, 1);
        align.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints(g -> {
            g.translate(LWJGL.window.getWidth() - 32, LWJGL.window.getHeight() - 32);
        }).construct(false);
        
        Button helpButton = new IconButton(stage, temp, 4f, IconManager.icon("help"), OPTION_BAR_HEIGHT.neg(), GLFloats.ZERO);
        helpButton.setOnClickListener((s, b, m) -> {
            HelpFX.show();
        });
        
        Button musicButton = new IconButton(stage, temp, 4f, IconManager.icon("music-on"), OPTION_BAR_HEIGHT.neg(), GLFloats.ZERO);
        musicButton.setOnClickListener((s, b, m) -> {
            boolean enabled = CodingGame.getInstance().audioHandler.invert();
            ((IconButton) b).setIcon(IconManager.icon(enabled? "music-on":"music-off"));
        });
        
        
        optionBar.addChild(pauseButton);
        optionBar.addChild(timeInfo);
        optionBar.addChild(supplyInfo);
        optionBar.addChild(robots);
        optionBar.addChild(pop);
        optionBar.addChild(align);
        optionBar.addChild(helpButton);
        optionBar.addChild(musicButton);
        
        
        Panel robotListInfoBar = new Panel(stage, false){
            @Override
            public void tick() {
                super.tick();
            }
        };
        robotListInfoBar.getShapeStates().reset()
                .setAll(new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(StaticColor.BLACK)
                .setAllFills(new StaticColor(0.1f))
                .setAllAfterPaints(g -> {
                    g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get());
                    g.setUpText(textFont, StaticColor.WHITE, 28, NanoVG.NVG_ALIGN_BOTTOM | NanoVG.NVG_ALIGN_LEFT);
                    g.text("Robots", 40, 32);
                }).construct(false);
        
        Panel robotInfoBar = new Panel(stage, false);
        robotInfoBar.getShapeStates().reset()
                .setAll(new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(StaticColor.BLACK)
                .setAllFills(new StaticColor(0.1f))
                .construct(false);
        
        Component robotPanelInfo = new Component(stage, false, 1);
        robotPanelInfo.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints((g) -> {
            g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get());
            Robot robot = game.getChooseHandler().getChoosingObject().getObject();
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
                Robot robot = game.getChooseHandler().getChoosingObject().getObject();
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
                Robot robot = game.getChooseHandler().getChoosingObject().getObject();
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
                    ObjectChooseHandler.ChoosingObject choosingObject = game.getChooseHandler().getChoosingObject();
                    ObjectChooseHandler.Choosable choosing = choosingObject.getObject();
                    if(choosing instanceof ProcMapCell) {
                        ProcMapCell cell = (ProcMapCell) choosing;
                        if(!MapCell.nullCheck(cell))    return;
                        byte tileType = cell.getTileType();
                        g.translate(0, 60);
                        g.setUpText(textFont, StaticColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
                        g.text("Tile: " + MapTiles.get(cell.getTileID()).getName(), 10, 0);
                        g.translate(0, 30);
                        g.text("Position: x=" + choosingObject.getX() + " y=" + choosingObject.getY(), 10, 0);
                        g.translate(0, 50);
                        g.text("Tile type: " + CellUtils.name(tileType), 10, 0);
                        switch(tileType) {
                            case CellUtils.SOIL_CELL:
                                g.translate(0, 30);
                                g.text("Hydration: " + Utils.percentage(CellUtils.soil_getHydration(cell), 0, 1, 1), 10, 0);
                                g.translate(0, 30);
                                g.text("pH: " + CellUtils.soil_getPH(cell), 10, 0);
                                break;
                        }
                        g.translate(0, 25);
//                        if(cell instanceof FacingCell) {
//                            g.text("Facing: ", 10, 5);
//                            facingBox.setVisible(true);
//                        } else facingBox.setVisible(false);
//                        if(cell instanceof SheetCell) {
//                            if(cell.getTileID() == MapTile.POTATO_CROPS) {
//                                int idx = ((SheetCell) cell).getIndex();
//                                String representation = idx == 3? "Mature":String.valueOf(idx + 1);
//                                g.text("Growth Stage: " + representation, 10, 5);
//                                g.translate(0, 30);
//                            }
//                        }
                    } else if(choosing instanceof EntityData) {
                        EntityData data = (EntityData) choosing;
                        int tileID = data.getTileID();
                        g.translate(0, 60);
                        g.setUpText(textFont, StaticColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
                        g.text("Tile: " + MapTiles.get(tileID).getName(), 10, 0);
                        g.translate(0, 30);
                        g.text("Position: x=" + choosingObject.getX() + " y=" + choosingObject.getY(), 10, 0);
                        g.translate(0, 25);
                    }
                }).construct(false);
        new IComboBox.IComboBoxCell(facingBox, "Left");
        new IComboBox.IComboBoxCell(facingBox, "Up");
        new IComboBox.IComboBoxCell(facingBox, "Right");
        new IComboBox.IComboBoxCell(facingBox, "Down");
        facingBox.setOnChangeListener((idx, cell)->{
//            Object selected = game.getSelectedObject();
//            if(selected instanceof ChooseTile) {
//                ChooseTile tile = (ChooseTile) selected;
//                ((FacingCell) tile.getCell()).setFacing(Facing.values()[idx]);
//            }
        });
        
        tileInfoBar.addChild(facingBox);
        
        tileInventory = new Tab(stage, "Inventory", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                ObjectChooseHandler.ChoosingObject choosingObject = game.getChooseHandler().getChoosingObject();
                ObjectChooseHandler.Choosable choosing = choosingObject.getObject();
//                InventoryCell cell = (InventoryCell) choosing;
//                Inventory inv = cell.getInventory();
//                for (Pair<ItemType, Item> entry : inv.getItems().entrySet()) {
//                    Item item = entry.getValue();
//                    
//                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
//                    g.fill(new StaticColor(0.2f));
//                    g.setUpText(textFont, StaticColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
//                    g.text(item.toString(), 10, 15);
//                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
//                    g.translate(0, 32);
//                }
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
        
        
        
        robotInfoBar.addChild(robotPanelInfo);
        robotInfoBar.addChild(openCodeButton);
        robotInfoBar.addChild(robotInv);
        robotInfoBar.addChild(modules);
        
        tileInfoBar.addChild(tileInventory);
        
        infoBar.getComponents().add(closeButton);
        
        infoBar.getPanels().addAll(Arrays.asList(robotInfoBar, tileInfoBar, robotListInfoBar));
        
        closeInfoBar();
    }
    
    public void tick(InputProcessor processor) {
        stage.tick();
    }
    
    public void render(NVGGraphics g) {
        ObjectChooseHandler.Choosable selected = game.getChooseHandler().getChoosingObject().getObject();
        if(selected != null) {
            if(selected instanceof MapCell) {
                MapCell cell = (MapCell) selected;
//                tileInventory.setVisible(cell instanceof InventoryCell);
//                if(cell instanceof FacingCell) {
//                    Facing facing = ((FacingCell) cell).getFacing();
//                    facingBox.set(facing.ordinal());
//                }
                tileInventory.setVisible(false);
                facingBox.setVisible(false);
            } else if(selected instanceof EntityData) {
                tileInventory.setVisible(false);
                facingBox.setVisible(false);
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
        //guideBook.render();
    }
    
    private static GLFloat INFO_BAR_WIDTH, INFO_BAR_HEIGHT, INFO_BAR_X, INFO_BAR_Y;
    private static GLFloat OPTION_BAR_WIDTH, OPTION_BAR_HEIGHT, OPTION_BAR_X, OPTION_BAR_Y;
    
    private void initStaticVariables() {
        INFO_BAR_WIDTH = GLFloat.memValue(300);
        INFO_BAR_HEIGHT = () -> LWJGL.window.getHeight() - OPTION_BAR_HEIGHT.get();
        INFO_BAR_X = GLFloats.w_fromFarYAxis(INFO_BAR_WIDTH);
        INFO_BAR_Y = GLFloats.ZERO;
        
        OPTION_BAR_WIDTH = () -> LWJGL.window.getWidth();
        OPTION_BAR_HEIGHT = GLFloat.memValue(32);
        OPTION_BAR_X = GLFloats.ZERO;
        OPTION_BAR_Y = GLFloats.w_fromFarXAxis(OPTION_BAR_HEIGHT);
        
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
        Rect optionBarBounds = new Rect(OPTION_BAR_X, OPTION_BAR_Y, OPTION_BAR_WIDTH, OPTION_BAR_HEIGHT);
        if(optionBarBounds.contains(LWJGL.mouse.getCursorPosition()))   return true;
        if(infoBar.getSelectedIndex() == -1)    return false;
        Rect infoBarBounds = new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT);
        return infoBarBounds.contains(LWJGL.mouse.getCursorPosition());
    }
    
    public void dispose() {
    }

    public boolean infoBarVisible() {
        return infoBar.getSelectedIndex() >= 0;
    }
    
    public void showInfoBar(int idx) {
        infoBar.select(idx);
        Tween.to(infoBar, TabbedPanel.Accessor.FADE, 0.5f).target(1).ease(Sine.OUT).start(CodingGame.getInstance().tweenManager);
        Tween.to(infoBar, TabbedPanel.Accessor.MOVE, 0.25f).target(0).ease(Sine.IN).start(CodingGame.getInstance().tweenManager);
    }
    
    public void closeInfoBar() {
        Tween.to(infoBar, TabbedPanel.Accessor.FADE, 0.25f).target(0).ease(Sine.OUT).start(CodingGame.getInstance().tweenManager);
        Tween.to(infoBar, TabbedPanel.Accessor.MOVE, 0.5f).target(INFO_BAR_WIDTH.get()).ease(Sine.IN).start(CodingGame.getInstance().tweenManager).setCallback((type, source) -> {
            infoBar.select(-1);
            game.getChooseHandler().unchoose();
            game.setProcessor(InputProcessor.GAME);
        });
    }
    
    private static class VaryingImagePaint extends Paint {

        private GLRect bounds;
        private NVGImage image;
        private float alpha;
        private float offset;

        public VaryingImagePaint(GLRect bounds, NVGImage image, float alpha) {
            this.bounds = bounds;
            this.image = image;
            this.alpha = alpha;
            this.offset = 0;
        }

        public VaryingImagePaint(GLRect bounds, NVGImage image, float alpha, float offset) {
            this.bounds = bounds;
            this.image = image;
            this.alpha = alpha;
            this.offset = offset;
        }
        
        @Override
        public Paint mulAlpha(float alpha) {
            return new VaryingImagePaint(bounds, image, alpha * this.alpha, offset);
        }

        @Override
        public void fill(long nvgID) {
            get().fill(nvgID);
        }

        @Override
        public void stroke(long nvgID) {
            get().stroke(nvgID);
        }

        @Override
        public void text(long nvgID) {
            get().text(nvgID);
        }
        
        private ImagePaint get() {
            Rectanglef jomlRect = bounds.getJOMLRect();
            
            jomlRect.minX += offset;
            jomlRect.minY += offset;
            jomlRect.maxX -= offset;
            jomlRect.maxY -= offset;
            
            return LWJGL.graphics.imagePaint(image, jomlRect, 0, alpha);
        }
        
    }
    
    private static class IconManager {
        
        private static Map<String, NVGImage> icons;
        public static NVGImage icon(String name) {
            NVGImage icon;
            if(icons == null) {
                icons = new HashMap<>();
                return loadIcon(name);
            }
            if((icon = icons.get(name)) == null) return loadIcon(name);
            else return icon;
        }
        
        private static NVGImage loadIcon(String name) {
            try {
                String pathName = "/ui/" + name + ".png";
                ByteBuffer buffer = com.lwjglwrapper.utils.Utils.ioResourceToByteBuffer(IconManager.class.getResourceAsStream(pathName), 8 * 1024);
                NVGImage image = LWJGL.graphics.createNanoVGImageFromResource(buffer, 0);
                icons.put(name, image);
                return image;
            } catch (IOException ex) {
                Logger.getLogger(GameUIHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        
    }
    
    private static class IconButton extends Button {
        
        public IconButton(Stage stage, GLRect bounds, float offset, NVGImage icon) {
            super(stage, false);
            
            Rect rect = new Rect(bounds);

            shapes.setAll(rect)
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.1f), null).render(g), Button.NORMAL)
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.3f), null).render(g), Button.HOVER)
                .setBeforePaint(g -> new PaintedShape(rect, StaticColor.BLACK, null).render(g), Button.CLICKED)
                .setAllFills(new VaryingImagePaint(bounds, icon, 1f, offset))
                .construct(false);
        }

        public IconButton(Stage stage, GLRect bounds, float offset, NVGImage icon, GLFloat translateX, GLFloat translateY) {
            super(stage, false);
            
            Rect rect = new Rect(bounds);

            shapes.setAll(rect)
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.1f), null).render(g), Button.NORMAL)
                .setBeforePaint(g -> new PaintedShape(rect, new StaticColor(0.3f), null).render(g), Button.HOVER)
                .setBeforePaint(g -> new PaintedShape(rect, StaticColor.BLACK, null).render(g), Button.CLICKED)
                .setAllFills(new VaryingImagePaint(bounds, icon, 1f, offset))
                .setAllAfterPaints(g -> g.translate(translateX.get(), translateY.get()))
                .construct(false);
        }

        private void setIcon(NVGImage icon) {
            VaryingImagePaint paint = (VaryingImagePaint) shapes.get(0).getFill();
            paint.image = icon;
        }
        
    }
    
    
}
