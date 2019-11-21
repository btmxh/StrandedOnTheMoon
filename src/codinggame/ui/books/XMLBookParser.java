/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books;

import codinggame.ui.books.content.BookImage;
import codinggame.ui.books.content.pages.XMLCenteredPage;
import codinggame.ui.books.content.XMLChapter;
import codinggame.ui.books.content.XMLMission;
import codinggame.ui.books.content.pages.XMLMissionAdditionalPage;
import codinggame.ui.books.content.pages.XMLMissionPage;
import codinggame.ui.books.content.XMLRequirement;
import static codinggame.ui.books.content.pages.Constants.COVER_TEXT_COLOR;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Welcome
 */
public class XMLBookParser {
    
    public static XMLBook parse(String xmlFile, String resourcePath) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(XMLBookParser.class.getResourceAsStream(xmlFile));
        Node book = document.getElementsByTagName("book").item(0);
        
        return parseBook((Element) book, resourcePath);
    }

    private static XMLBook parseBook(Element book, String resourcePath) {
        XMLCenteredPage cover = parseCover((Element) book.getElementsByTagName("cover").item(0));
        NodeList chapters = book.getElementsByTagName("chapter");
        List<XMLChapter> bookContent = new ArrayList<>();
        for (int i = 0; i < chapters.getLength(); i++) {
            bookContent.add(parseChapter((Element) chapters.item(i), resourcePath));
        }
        return new XMLBook(cover, bookContent);
    }
    
    private static XMLCenteredPage parseCover(Element cover) {
        return new XMLCenteredPage(cover.getTextContent(), COVER_TEXT_COLOR);
    }
    
    private static XMLChapter parseChapter(Element chapter, String resourcePath) {
        String title = chapter.getAttribute("title");
        String index = chapter.getAttribute("no");
        List<XMLMission> missions = new ArrayList<>();
        NodeList missionNodes = chapter.getElementsByTagName("mission");
        for (int i = 0; i < missionNodes.getLength(); i++) {
            missions.add(parseMission((Element) missionNodes.item(i), resourcePath));
        }
        return new XMLChapter(index, title, missions);
    }
    
    private static XMLMission parseMission(Element mission, String resourcePath) {
        String title = mission.getElementsByTagName("title").item(0).getTextContent();
        String content = mission.getElementsByTagName("content").item(0).getTextContent().replace("\\n", "\n");
        NodeList imagePaths = mission.getElementsByTagName("image");
        NodeList requirementNodes = mission.getElementsByTagName("requirements");
        List<BookImage> images = new ArrayList<>();
        for (int i = 0; i < imagePaths.getLength(); i++) {
            String fileName = imagePaths.item(i).getTextContent();
            images.add(new BookImage(resourcePath + "/" + fileName));
        }
        XMLMissionPage missionPage = new XMLMissionPage(title, content);
        if(imagePaths.getLength() == 0 && requirementNodes.getLength() == 0) {
            return new XMLMission(missionPage);
        }
        String requirementRepresentation = requirementNodes.item(0).getTextContent();
        String description = mission.getElementsByTagName("description").item(0).getTextContent();
        XMLRequirement requirement = new XMLRequirement(requirementRepresentation);
        XMLMissionAdditionalPage additionalPage = new XMLMissionAdditionalPage(description, images, requirement);
        return new XMLMission(missionPage, additionalPage);
    }
    
}
