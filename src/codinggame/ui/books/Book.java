/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books;

/**
 *
 * @author Welcome
 */
public interface Book {
    
    public void goToPage(int page);
    public void close();
    public void turnRight();
    public void turnLeft();
    public void render();
    
}
