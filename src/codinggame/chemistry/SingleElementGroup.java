/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.chemistry;

import java.util.Map;

/**
 *
 * @author Welcome
 */
public class SingleElementGroup implements ElementGroup{
    private Element e;

    public SingleElementGroup(Element e) {
        this.e = e;
    }
    
    public final void setElement(Element e) {
        this.e = e;
    }

    @Override
    public double averageMass() {
        return e.getAtomicMass();
    }

    @Override
    public void calculateElementMass(Map<Element, Float> massMap, float mol) {
        float mass = (float) (mol * averageMass());
        Float amt = massMap.get(e);
        if(amt == null) massMap.put(e, mass);
        else massMap.put(e, amt + mass);
    }
}
