/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d.terrain;

import codinggame.handlers.GameHandler;
import codinggame.map.MapCell;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.utils.INoise;
import codinggame.utils.textures.PackedTexture;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.objects.VBO;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.joml.Rectanglef;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Welcome
 */
public class RenderableChunk {

    private int width, height;
    private int vertices, quads;
    private ProcMapChunk chunk;

    private VAO vao;
    private VBO yAndNormalsVBO;
    private VBO texCoordsVBO;
    private Vector3f offset;
    private Vector3f scale;
    
    private float[] arr;
    private float[][] heightMap;
    private int chunkX = Integer.MIN_VALUE, chunkZ = Integer.MIN_VALUE;

    public RenderableChunk(Vector3f offset, Vector3f scale, int width,
            int height) {
        createVAO(offset, scale, width, height);
        this.width = width;
        this.height = height;
        this.offset = offset;
        this.scale = scale;
        
        heightMap = new float[width + 1][height + 1];
        arr = new float[width * height * 4 * 4];
    }

    private void createVAO(Vector3f offset, Vector3f scale, int width,
            int height) {
        //Render with glDrawArray (or VAO.renderArray), no need to enable GL_PRIMITIVE_RESTART_FIXED_INDEX

        vertices = (width + 1) * (height + 1);
        quads = width * height;

        vao = new VAO();
        vao.bind();

//        FloatArrayStack xzCoords = new FloatArrayStack(quads * 4 * 2);
        float[] xzCoords = new float[quads * 4 * 2];
//        IntArrayStack indices = new IntArrayStack(quads * 6);
        int[] indices = new int[quads * 6];
        genXZ(xzCoords, width, height);
        genIndices(indices, width, height);

        vao.createAttribute(0, xzCoords, 2);
        vao.createIndexBuffer(indices);
        yAndNormalsVBO = vao.createEmptyVBO(quads * 4 * 4 * Float.BYTES);
        yAndNormalsVBO.bind();
        vao.createAttribute(1, 4, 0, 0);
        yAndNormalsVBO.unbind();
        texCoordsVBO = vao.createEmptyVBO(quads * 2 * 4 * Float.BYTES);
        texCoordsVBO.bind();
        vao.createAttribute(2, 2, 0, 0);
        texCoordsVBO.unbind();
    }

    private static void genXZ(float[] xzCoords, int width, int height) {
        int ptr = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                xzCoords[ptr++] = x;
                xzCoords[ptr++] = z;
                xzCoords[ptr++] = x + 1;
                xzCoords[ptr++] = z;
                xzCoords[ptr++] = x;
                xzCoords[ptr++] = z + 1;
                xzCoords[ptr++] = x + 1;
                xzCoords[ptr++] = z + 1;
            }
        }
    }

    private static void genIndices(int[] indices, int width, int height) {
        int ptr = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                int quadIndex = (x + z * width) * 4;
                indices[ptr++] = quadIndex;
                indices[ptr++] = quadIndex + 2;
                indices[ptr++] = quadIndex + 3;
                indices[ptr++] = quadIndex + 3;
                indices[ptr++] = quadIndex + 1;
                indices[ptr++] = quadIndex;
            }
        }
    }

    private static float[][] mapToArray(BiFunction<Integer, Integer, Float> func,
            int width, int height) {
        float[][] arr = new float[width][height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                arr[x][z] = func.apply(x, z);
            }
        }
        return arr;
    }
    
    private void mapHeights(BiFunction<Integer, Integer, Float> func) {
        for (int x = 0; x < width + 1; x++) {
            for (int z = 0; z < height + 1; z++) {
                heightMap[x][z] = func.apply(x, z);
            }
        }
    }

    public void setChunk(ProcMapChunk turfChunk, PackedTexture tilemap) {
        if (this.chunk == turfChunk) {
            return;
        }
        if (this.chunk != null) {
            this.chunk.setListener(null);
        }
        this.chunk = turfChunk;
        if (turfChunk == null) {
            return;
        }
        float[] arr = new float[quads * 4 * 2];
        int ptr = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                ProcMapCell cell = turfChunk.getTileAt(x, z);
                if (MapCell.nullCheck(cell)) {
                    Rectanglef bounds = tilemap.getTextureBounds(cell.getTileID());
                    arr[ptr++] = bounds.minX;
                    arr[ptr++] = bounds.minY;
                    arr[ptr++] = bounds.maxX;
                    arr[ptr++] = bounds.minY;
                    arr[ptr++] = bounds.minX;
                    arr[ptr++] = bounds.maxY;
                    arr[ptr++] = bounds.maxX;
                    arr[ptr++] = bounds.maxY;
                }
            }
        }
        vao.bind();
        texCoordsVBO.updateVBO(arr);
        vao.unbind();
        turfChunk.setListener((x, y, oldVal, newVal) -> updateTile(x, y, tilemap.getTextureBounds(newVal.getTileID())));
    }

    public void setHeights(INoise noise, int chunkX, int chunkZ) {
        if(this.chunkX == chunkX && this.chunkZ == chunkZ)  return;
        
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        
        noise.setXoff(chunkX * ProcMapChunk.CHUNK_SIZE * GameHandler.NOISE_SCALE);
        noise.setYoff(chunkZ * ProcMapChunk.CHUNK_SIZE * GameHandler.NOISE_SCALE);
        BiFunction<Integer, Integer, Float> heightFunc = noise.floatFunc();
        mapHeights(heightFunc);
        int ptr = 0;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                Vector3f normal;

                normal = calculateNormal(x, z, heightFunc, heightMap);
                arr[ptr++] = heightMap[x][z];
                arr[ptr++] = normal.x;
                arr[ptr++] = normal.y;
                arr[ptr++] = normal.z;

                normal = calculateNormal(x + 1, z, heightFunc, heightMap);
                arr[ptr++] = heightMap[x + 1][z];
                arr[ptr++] = normal.x;
                arr[ptr++] = normal.y;
                arr[ptr++] = normal.z;

                normal = calculateNormal(x, z + 1, heightFunc, heightMap);
                arr[ptr++] = heightMap[x][z + 1];
                arr[ptr++] = normal.x;
                arr[ptr++] = normal.y;
                arr[ptr++] = normal.z;


                normal = calculateNormal(x + 1, z + 1, heightFunc, heightMap);
                arr[ptr++] = heightMap[x + 1][z + 1];
                arr[ptr++] = normal.x;
                arr[ptr++] = normal.y;
                arr[ptr++] = normal.z;
            }
        }
        vao.bind();
        yAndNormalsVBO.updateVBO(arr);
        vao.unbind();
    }

    public void render() {
        if (chunk == null) {
            return;
        }
        vao.bindAll();
        vao.renderElement(GL11.GL_TRIANGLES, -1);
        vao.unbindByLastBind();
    }

    public void renderTileAt(int x, int z) {
        if (chunk == null) {
            return;
        }
        vao.bindAll();
        vao.renderElement(GL11.GL_TRIANGLES, (x + z * width) * 6, 6);
        vao.unbindByLastBind();
    }

    public void setTile(int x, int z, int tileID, PackedTexture tilemap) {
        chunk.setTileAt(x, z, tileID);
    }

    private void updateTile(int x, int z, Rectanglef texBounds) {
        float[] data = new float[]{
            texBounds.minX, texBounds.minY,
            texBounds.maxX, texBounds.minY,
            texBounds.minX, texBounds.maxY,
            texBounds.maxX, texBounds.maxY,};
        int offset = (z + x * height) * data.length * Float.BYTES;
        texCoordsVBO.updateVBO(offset, data);
    }

    public boolean isEmpty() {
        return chunk == null && !mark;
    }

    private boolean mark;

    public void mark() {
        mark = true;
    }

    public void unmark() {
        mark = false;
    }

    public Vector3f calculateNormal(int x, int z,
            BiFunction<Integer, Integer, Float> heightFunc, float[][] heightMap) {
        float heightL = getHeight(x - 1, z, heightFunc, heightMap);
        float heightR = getHeight(x + 1, z, heightFunc, heightMap);
        float heightU = getHeight(x, z + 1, heightFunc, heightMap);
        float heightD = getHeight(x, z - 1, heightFunc, heightMap);
        return new Vector3f(heightL - heightR, 2f, heightD - heightU).normalize();
    }

    private float getHeight(int x, int z,
            BiFunction<Integer, Integer, Float> heightFunc, float[][] heightMap) {
        if (x < 0 | x >= ProcMapChunk.CHUNK_SIZE | z < 0 | z >= ProcMapChunk.CHUNK_SIZE) {
            return heightFunc.apply(x, z);
        } else {
            return heightMap[x][z];
        }
    }

    public Map<Point, EntityData> getEntities() {
        return chunk == null ? new HashMap<>() : chunk.getEntities();
    }

}
