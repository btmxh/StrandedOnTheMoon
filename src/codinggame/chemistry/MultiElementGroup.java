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
public class MultiElementGroup implements ElementGroup{
    private Map<ElementGroup, Float> composition;

    public MultiElementGroup(Map<ElementGroup, Float> composition) {
        this.composition = composition;
    }

    public MultiElementGroup() {
        composition = new HashMap<>();
    }
    
    public MultiElementGroup put(ElementGroup e, float amt) {
        composition.put(e, amt);
        return this;
    }
    
    public MultiElementGroup put(Element e, float amt) {
        return put(new SingleElementGroup(e), amt);
    }

    @Override
    public double averageMass() {
        return composition.entrySet().stream().mapToDouble((e) -> e.getKey().averageMass() * e.getValue()).sum();
    }

    @Override
    public void calculateElementMass(Map<Element, Float> massMap, float mol) {
        for (Map.Entry<ElementGroup, Float> entry : composition.entrySet()) {
            ElementGroup group = entry.getKey();
            float groupMol = (float) entry.getValue();
            group.calculateElementMass(massMap, groupMol);
        }
    }
}
