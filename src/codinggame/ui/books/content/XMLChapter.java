/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content;

import static codinggame.ui.books.content.pages.Constants.CONTENT_TEXT_COLOR;
import codinggame.ui.books.content.pages.XMLCenteredPage;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class XMLChapter {
    private String index, title;
    private XMLCenteredPage firstPage;
    private List<XMLMission> missions;

    public XMLChapter(String index, String title, List<XMLMission> missions) {
        this.index = index;
        this.title = title;
        this.missions = missions;
        firstPage = new XMLCenteredPage(index + ". " + title, CONTENT_TEXT_COLOR);
    }

    public List<XMLMission> getMissions() {
        return missions;
    }

    /**
     * @return the firstPage
     */
    public XMLCenteredPage getFirstPage() {
        return firstPage;
    }

    
    
    
    
}
