/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer;

import codinggame.CodingGame;
import codinggame.globjs.Camera;
import codinggame.handlers.MapHandler;
import codinggame.map.MapLayer;
import codinggame.map.cells.renderers.CellRenderer;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.states.GameState;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class ProcMapRenderer extends MapRenderer{

    @Override
    public void renderLayer(MapHandler mapHandler, MapLayer layer, int width,
            int height, Vector2f cursorPosition, boolean hover, boolean topLayer,
            Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells) {
        super.renderLayer(mapHandler, layer, width, height, cursorPosition, hover, topLayer, specialCells);
        int startX = -(int) (defaultCellRenderer.x / defaultCellRenderer.tileWidth / ProcMapChunk.CHUNK_SIZE) - width / 2 - 1;
        int startY = -(int) (defaultCellRenderer.y / defaultCellRenderer.tileHeight / ProcMapChunk.CHUNK_SIZE) - height / 2 - 1;
        GameState game = CodingGame.getInstance().getGameState();
        Camera camera = game.getCamera();
        Point chunkPos = camera.getChunk();
        ProcMapChunk chunk = ((ProcMapLayer) layer).getChunk(chunkPos.x, chunkPos.y);
        renderChunk(mapHandler, layer, chunkPos, chunk, cursorPosition, hover, topLayer, specialCells);
    }

    private void renderChunk(MapHandler mapHandler, MapLayer layer,
            Point chunkPos, ProcMapChunk chunk, Vector2f cursorPosition,
            boolean hover, boolean topLayer,
            Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells) {
        for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                renderCell(mapHandler, layer, x + chunkPos.x * ProcMapChunk.CHUNK_SIZE, y + chunkPos.y * ProcMapChunk.CHUNK_SIZE, chunk.getTileAt(x, y), cursorPosition, hover, topLayer, specialCells);
            }
        }
    }
    
    
    
}
