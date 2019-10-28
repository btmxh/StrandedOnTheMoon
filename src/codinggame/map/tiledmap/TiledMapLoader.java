/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.tiledmap;

import codinggame.map.GameMap;
import codinggame.map.MapLayers;
import codinggame.map.MapTile;
import codinggame.map.MapTileset;
import codinggame.map.MapTilesets;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Welcome
 */
public class TiledMapLoader {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    public static GameMap loadMap(String directory, String filename) throws IOException, SAXException {
        directory += "/";
        try {
            //Tileset must be embed in map file
            Document document = dbf.newDocumentBuilder().parse(TiledMapLoader.class.getResourceAsStream(directory + filename));
            
            Element mapNode = (Element) document.getElementsByTagName("map").item(0);
            int width = Integer.parseInt(mapNode.getAttribute("width"));
            int height = Integer.parseInt(mapNode.getAttribute("height"));
            
            NodeList tilesetNodes = mapNode.getElementsByTagName("tileset");
            MapTileset[] tilesetArray = new MapTileset[tilesetNodes.getLength()];
            for (int i = 0; i < tilesetArray.length; i++) {
                tilesetArray[i] = parseTileset(directory, (Element) tilesetNodes.item(i));
            }
            MapTilesets tilesets = new MapTilesets(tilesetArray);
            
            NodeList layerNodes = mapNode.getElementsByTagName("layer");
            MapLayers layers = new MapLayers();
            for (int i = 0; i < layerNodes.getLength(); i++) {
                TiledMapLayer layer = new TiledMapLayer(width, height);
                int id = parseLayer((Element) layerNodes.item(i), layer, tilesets);
                layers.add(layer);
            }
            return new GameMap(tilesets, layers);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TiledMapLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static MapTileset parseTileset(String directory, Element node) {
        int firstItem = Integer.parseInt(node.getAttribute("firstgid"));
        NodeList tileNodes = node.getElementsByTagName("tile");
        MapTileset tileset = new MapTileset();
        for (int i = 0; i < tileNodes.getLength(); i++) {
            Element tileNode = (Element) tileNodes.item(i);
            int id = Integer.parseInt(tileNode.getAttribute("id"));
            Element imageNode = (Element) tileNode.getElementsByTagName("image").item(0);
            String texturePath = directory + imageNode.getAttribute("source");
            Texture2D texture = new Texture2D(TextureData.fromResource(TiledMapLoader.class, texturePath)){
                @Override
                public void configTexture(int id) {
                    GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                    GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                }
            };
//            Texture2D texture = null;
            
            tileset.addTile(new MapTile(id + firstItem, texture));
        }
        return tileset;
    }

    private static int parseLayer(Element node, TiledMapLayer layer, MapTilesets tilesets) {
        int layerID = Integer.parseInt(node.getAttribute("id"));
        String csv = node.getElementsByTagName("data").item(0).getTextContent().trim();
        String[] lines = csv.split("\n");
        for (int y = 0; y < lines.length; y++) {
            String[] ids = lines[y].split(",");
            for (int x = 0; x < ids.length; x++) {
                if(ids[x].isEmpty())    continue;
                int id = Integer.parseInt(ids[x]);
                layer.setTileAt(x, y, tilesets.<MapTile>getTileByID(id));
            }
        }
        layer.setID(layerID);
        return layerID;
    }
}
