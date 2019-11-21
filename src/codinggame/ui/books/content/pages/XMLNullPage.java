/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content.pages;

import com.lwjglwrapper.nanovg.NVGGraphics;

/**
 *
 * @author Welcome
 */
public class XMLNullPage extends XMLPage{

    public static final XMLNullPage NULL_PAGE = new XMLNullPage();
    
    @Override
    public void render(NVGGraphics g, float pageWidth, float pageHeight) {
    }
}
