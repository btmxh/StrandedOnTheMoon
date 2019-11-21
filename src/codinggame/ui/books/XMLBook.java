/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books;

import codinggame.ui.books.content.pages.XMLCenteredPage;
import codinggame.ui.books.content.XMLChapter;
import codinggame.ui.books.content.XMLMission;
import static codinggame.ui.books.content.pages.Constants.PAGE_HEIGHT;
import static codinggame.ui.books.content.pages.Constants.PAGE_WIDTH;
import codinggame.ui.books.content.pages.XMLNullPage;
import codinggame.ui.books.content.pages.XMLPage;
import com.lwjglwrapper.LWJGL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public class XMLBook implements Book{

    private XMLCenteredPage cover;
    private List<XMLChapter> bookContent;
    private List<XMLPage> bookPages;
    private int pageCount;

    public XMLBook(XMLCenteredPage cover, List<XMLChapter> bookContent) {
        this.cover = cover;
        this.bookContent = bookContent;
        
        bookPages = bookPages(bookContent);
        bookPages.add(0, XMLNullPage.NULL_PAGE);
        pageCount = bookPages.size();
        LAST_COVER = pageCount;
    }

    private final int COVER = -2, LAST_COVER;
    private int pageIndex = COVER;
    
    
    private static List<XMLPage> bookPages(List<XMLChapter> bookContent) {
        return bookContent.stream().map(XMLBook::bookPages).flatMap(List::stream).collect(Collectors.toList());
    }
    
    private static List<XMLPage> bookPages(XMLChapter chapter) {
        List<XMLPage> list = new ArrayList<>();
        list.add(chapter.getFirstPage());
        chapter.getMissions().stream().map(XMLBook::bookPages).forEach(list::addAll);
        return list;
    }
    
    private static List<XMLPage> bookPages(XMLMission mission) {
        return mission.getPages();
    }

    @Override
    public void goToPage(int page) {
        pageIndex = page;
    }

    @Override
    public void close() {
        pageIndex = COVER;
    }

    @Override
    public void turnRight() {
        pageIndex+=2;
        renders = null;
    }

    @Override
    public void turnLeft() {
        pageIndex-=2;
        renders = null;
    }

    private XMLPage[] renders;
    
    
    public void render() {
        if(renders == null)     renders = getCurrentPages();
        Rectanglef bounds = new Rectanglef(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        System.out.println(Arrays.toString(renders));
        for (int i = 0; i < renders.length; i++) {
            XMLPage page = renders[i];
            page.render(LWJGL.graphics, bounds, i);
            float width = bounds.maxX - bounds.minX;
            bounds.minX = bounds.maxX;
            bounds.maxX += width;
        }
        
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_RIGHT)) {
            turnRight();
        } else if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_LEFT)) {
            turnLeft();
        }
        
        System.out.println(pageIndex);
    }
    
    private XMLPage[] getCurrentPages() {
        return new XMLPage[]{
            getPage(pageIndex),
            getPage(pageIndex + 1)
        };
    }

    private XMLPage getPage(int idx) {
        if(idx - 1 == COVER)    return cover;
        else if(idx == LAST_COVER)  return XMLNullPage.NULL_PAGE;
        else try {
            return bookPages.get(idx);
        } catch (IndexOutOfBoundsException e) {
            return XMLNullPage.NULL_PAGE;
        }
    }
    
}
