/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.chemistry;

import java.util.HashMap;
import java.util.Map;

/**
 * A group of elements, which can have electric charges like (SO4)2- or not (H2O)
 * @author Welcome
 */
public interface ElementGroup {
    public double averageMass();
    public void calculateElementMass(Map<Element, Float> massMap, float mol);
}
