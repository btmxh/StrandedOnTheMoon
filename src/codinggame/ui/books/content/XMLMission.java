/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content;

import codinggame.ui.books.content.pages.XMLMissionPage;
import codinggame.ui.books.content.pages.XMLMissionAdditionalPage;
import codinggame.ui.books.content.pages.XMLNullPage;
import codinggame.ui.books.content.pages.XMLPage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class XMLMission {
    private XMLMissionPage page;
    private XMLMissionAdditionalPage additionalPage;

    public XMLMission(XMLMissionPage page,
            XMLMissionAdditionalPage additionalPage) {
        this.page = page;
        this.additionalPage = additionalPage;
    }

    public XMLMission(XMLMissionPage page) {
        this.page = page;
    }

    public int pageCount() {
        return additionalPage == null? 1:2;
    }

    public List<XMLPage> getPages() {
        return new ArrayList<>(Arrays.asList(page, additionalPage == null? XMLNullPage.NULL_PAGE:additionalPage));
    }

    
    
}
