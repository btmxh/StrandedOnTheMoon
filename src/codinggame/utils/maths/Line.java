/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils.maths;

import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class Line {
    private final float a, b, c;  //ax+by+c=0

    public Line(float a, float b, float c) {
        this.a = a;
        this.b = b;
        this.c = c;
        if(a*a+b*b==0)  throw new IllegalArgumentException("a^2+b^2==0");
    }
    
    public float signedDistance(Vector2f pt) {
        return (float) ((a*pt.x+b*pt.y+c) / Math.sqrt(a*a+b*b));
    }
    
    public int sign(Vector2f pt) {
        return (int) Math.signum(a*pt.x+b*pt.y+c);
    }
    
    public static Line xEquals(float x0) {
        //Line: x = x0;
        return new Line(1, 0, -x0);
    }
    
    public static Line yEquals(float y0) {
        //Line: y = y0;
        return new Line(0, 1, -y0);
    }
    
    public static Line from(Vector2f from, float a, float b) {
        return new Line(a, b, - a * from.x - b * from.y);
    }
    
    public static Line perpendicular(Line line, Vector2f from) {
        return from(from, line.b, -line.a);
    }
}
