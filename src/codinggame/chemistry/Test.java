/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.chemistry;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class Test {
    public static void main(String[] args) {
        ElementGroup g = new MultiElementGroup().
                            put(Element.O, 1.6f).
                            put(Element.Si, 0.2f).
                            put(Element.Fe, 0.1f).
                            put(Element.Al, 0.05f).
                            put(Element.Ca, 0.05f);
        Map<Element, Float> massMap = new HashMap<>();
        g.calculateElementMass(massMap, 1);
        System.out.println(massMap);
        System.out.println(massMap.values().stream().mapToDouble(Float::doubleValue).sum() + " " + g.averageMass());
    }
}
