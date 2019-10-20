/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.utils.Utils;
import com.lwjglwrapper.utils.IColor;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Welcome
 */
public enum EquipmentSlot {
    DRILL(IColor.ORANGE, MinerRobot.class);
    
    private List<Class<? extends Robot>> robotTypes;
    private IColor color;

    private EquipmentSlot(IColor color, Class<? extends Robot>... classes) {
        this.color = color;
        this.robotTypes = Arrays.asList(classes);
    }
    
    public boolean hasSlot(Robot robot) {
        return robotTypes.stream().anyMatch((cl) -> robot.instanceOf(cl));
    }
    
    public static EquipmentSlot getFromName(String name) {
        return valueOf(Utils.toVariableNameString(name));
    }

    public IColor getColor() {
        return color;
    }
}
