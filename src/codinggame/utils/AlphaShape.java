/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import com.lwjglwrapper.nanovg.paint.AdditionalPaint;
import com.lwjglwrapper.nanovg.paint.types.FillPaint;
import com.lwjglwrapper.nanovg.paint.types.StrokePaint;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;

/**
 *
 * @author Welcome
 * @param <S>
 */
public class AlphaShape<S extends Shape> extends PaintedShape<S>{
    
    public AlphaShape(S shape, FillPaint fill, StrokePaint stroke,
            AdditionalPaint afterPaint, AdditionalPaint beforePaint) {
        super(shape, fill, stroke, afterPaint, beforePaint);
    }
    
}
