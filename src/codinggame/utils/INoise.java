/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import com.lwjglwrapper.utils.OpenSimplexNoise;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 *
 * @author Welcome
 */
public class INoise extends OpenSimplexNoise{
    
    private float xoff = 0, yoff = 0;
    private float xscl = 1, yscl = 1;

    /**
     * @param xoff the xoff to set
     */
    public void setXoff(float xoff) {
        this.xoff = xoff;
    }

    /**
     * @param yoff the yoff to set
     */
    public void setYoff(float yoff) {
        this.yoff = yoff;
    }

    /**
     * @param xscl the xscl to set
     */
    public void setXscl(float xscl) {
        this.xscl = xscl;
    }

    /**
     * @param yscl the yscl to set
     */
    public void setYscl(float yscl) {
        this.yscl = yscl;
    }

    @Override
    public double eval(double x, double y) {
        return super.eval(x * xscl + xoff, y * yscl + yoff);
    }
    
    public float getTriangleHeight(float x, float y) {
        if (isInteger(x) && isInteger(y)) {
            return evalFloat(x, y);
        } else {
            int tileX = (int) Math.floor(x);
            int tileY = (int) Math.floor(y);
            

            float xCoord = x - tileX;
            float yCoord = y - tileY;
            
//            System.out.println(tileX + " " + tileZ);
//            System.out.println(xCoord + " " + zCoord);

            Vector2f coords = new Vector2f(xCoord, yCoord);

            /*
                A _____ Bz
                 |___/|
                 |__/_|
                 |_/__|
               xD|/___| C
            
             */
            Vector2f A = new Vector2f(1, 1);
            Vector2f B = new Vector2f(0, 1);
            Vector2f C = new Vector2f(0, 0);
            Vector2f D = new Vector2f(1, 0);

            float Aheight = heightOf(A, tileX, tileY);
            float Bheight = heightOf(B, tileX, tileY);
            float Cheight = heightOf(C, tileX, tileY);
            float Dheight = heightOf(D, tileX, tileY);

            float height;
            Vector3f barryCentric;
            if (xCoord + yCoord < 1) {
                barryCentric = cartesianToBarryCentric(C, B, D, coords);
                height = barryCentric.dot(Cheight, Bheight, Dheight);
            } else {
                barryCentric = cartesianToBarryCentric(A, B, D, coords);
                height = barryCentric.dot(Aheight, Bheight, Dheight);
            }
            return height;
        }
    }

    private boolean isInteger(float f) {
        return f == Math.floor(f);
    }
    
    private Vector3f cartesianToBarryCentric(Vector2f A, Vector2f B, Vector2f C, Vector2f cartesianCoords) {
        float x1 = A.x, y1 = A.y, x2 = B.x, y2 = B.y, x3 = C.x, y3 = C.y;
        float x = cartesianCoords.x, y = cartesianCoords.y;

        float detT = (y1 - y3) * (x2 - x3) + (x1 - x3) * (y3 - y2);
        float a = ((y3 - y2) * (x - x3) + (x2 - x3) * (y - y3)) / detT;
        float b = (x - a * x1 - x3 + a * x3) / (x2 - x3);
        float c = 1 - a - b;

        return new Vector3f(a, b, c);
    }

    private float heightOf(Vector2f point, float xOff, float zOff) {
        float x = point.x + xOff;
        float y = point.y + zOff;
        return (float) super.eval(x * xscl, y * yscl);
    }
    
    public static void main(String[] args) {
        INoise noise = new INoise();
        noise.setXscl(0.2f);
        noise.setYscl(0.2f);
        
        //float h = noise.getTriangleHeight(0.5f, 0.5f);
        System.out.println(noise.getTriangleHeight(2.2f, 2f) + " " + noise.evalFloat(2f, 2f));
    }
}
