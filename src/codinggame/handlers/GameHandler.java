package codinggame.handlers;

import codinggame.globjs.Light;
import codinggame.map.GameMap;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ProcMap;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.proceduralmap.entities.rendering.EntityBatch;
import codinggame.map.proceduralmap.entities.rendering.EntityHandler;
import codinggame.map.proceduralmap.entities.rendering.ForEachable;
import codinggame.map.proceduralmap.entities.rendering.ModelManager;
import codinggame.map.renderer.g3d.Camera3D;
import codinggame.map.renderer.g3d.DebugCamera3D;
import codinggame.map.renderer.g3d.SkyboxRenderer;
import codinggame.map.renderer.g3d.TerrainPicker;
import codinggame.map.renderer.g3d.entities.AABBUtils.AABBBuilder;
import codinggame.map.renderer.g3d.entities.Entity;
import codinggame.map.renderer.g3d.entities.EntityModel;
import codinggame.map.renderer.g3d.entities.renderer.EntityRenderer;
import codinggame.map.renderer.g3d.terrain.RenderableChunk;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.INoise;
import codinggame.utils.textures.PackedTexture;
import codinggame.utils.textures.TexturePacker;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UMat4;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec3;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec4;
import com.lwjglwrapper.utils.Logger;
import com.lwjglwrapper.utils.input.KeyBindings;
import com.lwjglwrapper.utils.models.ModelGenerator;
import com.lwjglwrapper.utils.models.OBJLoader;
import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class GameHandler {
    private static final int RENDER_DISTANCE = 8;   //chunks
    public static final float NOISE_SCALE = 0.2f;
    private GameState game;
    private ProcMap map;
    private Shader shader, highlightShader;
    private Camera3D camera;
    private UMat4 pvMatrix, h_pvMatrix;
    private KeyBindings keyBindings;
    private PackedTexture tileAtlas;
    private TerrainPicker picker;
    private MapTilesets tileset;
    
    private Map<Point, RenderableChunk> renders;
    private Set<Point> circlePoints = generateCirclePoints(RENDER_DISTANCE);
    
    private final INoise noise;
    
    private Light.Struct lightStruct;
    private Light light;
    
    private ProcMapLayer turfLayer;
    
    //private Map<Point, ProcMapChunk> loadedChunks = new HashMap<>();
    private SkyboxRenderer skyboxRenderer;
    private Texture2D chooseMap;
    
    private Map<TexturedVAO, Iterable<? extends Entity>> entities = new HashMap<>();
    
    private AABBBuilder tileAABB = new AABBBuilder().xCenter(0f, 1f).yCenter(0f, 1f).zCenter(0f, 1f);
    
    private EntityHandler entityHandler;

    public GameHandler(GameState game) {
        this.game = game;
        map = (ProcMap) game.getMapHandler().getMap();
        turfLayer = map.getMapLayer(GameMap.TURF_LAYER);
        this.tileset = map.getTilesets();
        
        chooseMap = new Texture2D(TextureData.fromResource(GameHandler.class, "/chooseMap.png"));
        
        shader = new Shader(
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        
        highlightShader = new Shader(
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/h_vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/h_fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        
        picker = new TerrainPicker(LWJGL.window){
            @Override
            public float height(float x, float z) {
                return noise.getTriangleHeight(x, z);
            }
        };
        pvMatrix = new UMat4(shader, "pvMatrix");
        h_pvMatrix = new UMat4(highlightShader, "pvMatrix");
//        camera = new FirstPersonCamera(70.0f, LWJGL.window, 0.1f, 1000f);
        //camera.setSpeed(16f, 16f, 5f);
        keyBindings = new KeyBindings().defaultKeyBindings().debugKeyBindings();
        entityHandler = new EntityHandler(this);
        
        camera = new DebugCamera3D(new Vector3f(), 70.0f, LWJGL.window, 0.1f, 1000f){
            @Override
            public float getWorldHeight(float x, float z) {
                return noise.getTriangleHeight(x, z);
            }
        
        };
        
        Map<Integer, MapTile> tiles = tileset.getTiles();
        TexturePacker packer = new TexturePacker(32, 32);
        int size = (int) Math.ceil(Math.sqrt(tiles.size()));
        packer.create(size, size);
        packer.getTexture().bind();
        int x = 0, y = 0;
        for (Map.Entry<Integer, MapTile> entry : tiles.entrySet()) {
            Integer id = entry.getKey();
            MapTile tile = entry.getValue();
            if(id == MapTile.POTATO_CROPS)  continue;
            packer.addTexture(id, tile.getTexture().getTextureData(), x, y);
            x++;
            if(x >= size) {
                x = 0;
                y++;
            }
        }
        tileAtlas = packer.getTexture();
        
        noise = new INoise();
        noise.setXscl(NOISE_SCALE);
        noise.setYscl(NOISE_SCALE);
        
        renders = new HashMap<>();
        
        lightStruct = new Light.Struct(shader, "light");
        light = new Light(new Vector3f(0, 10000000f, 0), new Vector3f(1, 1, 1));
        skyboxRenderer = new SkyboxRenderer();
        
        Iterable<? extends Entity> robots = game.getRobotHandler().getRobotList().values();
        entities.put(ModelManager.ROBOT.getMesh(), robots);
        
        entityHandler.getEntities().put(ModelManager.ROBOT, game.getRobotHandler().getRobotList().values());
        entityHandler.getEntities().put((Consumer<EntityBatch> consumer) -> {
            Map<EntityModel, List> sortMap = new HashMap<>();
            for (RenderableChunk chunk : renders.values()) {
                for (EntityData entity : chunk.getEntities().values()) {
                    if(entity.isTileEntity()) {
                        EntityModel model = ModelManager.tileEntityModel(entity.getTileID());
                        List batch = sortMap.get(model);
                        if(batch == null) {
                            sortMap.put(model, batch = new LinkedList<>());
                        }
                        batch.add(entity);
                    }
                }
            }
            for (Map.Entry<EntityModel, List> entry : sortMap.entrySet()) {
                EntityModel model = entry.getKey();
                List batch = entry.getValue();
                consumer.accept(new EntityBatch(model, (c) -> batch.forEach(c)));
            }
        });
    }
    
    private Point lastCameraChunkPos;
    public void update(float delta) {
        camera.move(keyBindings, delta);
        if(keyBindings.anyReleased("polygon")) {
            wireframe = !wireframe;
        }
        picker.update(camera);
        game.getChooseHandler().beginFrame(picker.getRay(camera));
        if(picker.currentTerrainPoint != null) {
            int x = (int) Math.floor(picker.currentTerrainPoint.x);
            int z = (int) Math.floor(picker.currentTerrainPoint.z);
            if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                game.getChooseHandler().mousePressed(ObjectChooseHandler.ChoosingObject.tile(x, z, turfLayer), picker.intersectionRayT);
            }
            if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                game.getChooseHandler().mouseReleased(ObjectChooseHandler.ChoosingObject.tile(x, z, turfLayer), picker.intersectionRayT);
            }
            if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                turfLayer.setTileAt(x, z, MapTile.IRON_ORE);
            }
        }
        
        Point cameraChunk = getChunk(camera);
        Set<Point> usingChunkPositions = new HashSet<>();
        Set<RenderableChunk> usingChunks = new HashSet<>();
        
        if(lastCameraChunkPos == null)  lastCameraChunkPos = new Point(cameraChunk);
        for (Point pt : circlePoints) {
            RenderableChunk chunk = renders.get(pt);
            if(chunk == null) {
                chunk = new RenderableChunk(new Vector3f(), new Vector3f(1), ProcMapChunk.CHUNK_SIZE, ProcMapChunk.CHUNK_SIZE);
                renders.put(pt, chunk);
            }
            usingChunks.add(chunk);
            final Point translated = new Point(pt.x + cameraChunk.x, pt.y + cameraChunk.y);
            chunk.setHeights(noise, translated.x, translated.y);
            chunk.setChunk(turfLayer.getChunk(translated.x, translated.y), tileAtlas);
            
            if(chunk.isEmpty()) {
                turfLayer.registerChunk(translated.x, translated.y);
                chunk.mark();
            }
            usingChunkPositions.add(translated);
        }
        for (RenderableChunk chunk : renders.values()) {
            Map<Point, EntityData> chunkEntities = chunk.getEntities();
            for (EntityData entity : chunkEntities.values()) {
                if(entity.isTileEntity()) {
                    Vector3f translation = entity.get3DPosition().add(0.5f, 0.25f, 0.5f);
                    game.getChooseHandler().test(tileAABB, translation, ObjectChooseHandler.ChoosingObject.tileEntity(entity, turfLayer));
                }
            }
            if(usingChunks.contains(chunk)) continue;
            chunk.unmark();
        }
        lastCameraChunkPos = new Point(cameraChunk);
        
        turfLayer.saveChunksNotIn(usingChunkPositions);
        for (Robot robot : game.getRobotHandler().getRobotList().values()) {
            game.getChooseHandler().test(robot.getBoundBox(), new Vector3f(), ObjectChooseHandler.ChoosingObject.robot(robot));
        }
        game.getChooseHandler().endFrame();
        for (Iterable<? extends Entity> batch : entities.values()) for (Entity entity : batch) {
            entity.update(noise, delta);
        }
        ProcMapCell cell = (ProcMapCell) turfLayer.getTileAt(random.nextInt(16), random.nextInt(16));
        TileUpdateHandler.updateCell(cell);
        EntityData eCell = turfLayer.getEntityAt(random.nextInt(16), random.nextInt(16));
        if(eCell != null)   TileUpdateHandler.updateEntityCell(eCell);
    }
    
    public static final Random random = new Random();
    
    boolean wireframe = false;
    private Map<Integer, TexturedVAO> tileModels = new HashMap<>();
    private ModelGenerator mg = new ModelGenerator(new OBJLoader(0, 2, 1));
    public void render() {
        GLCalls.enable(GL11.GL_BLEND, GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_FRONT);
        skyboxRenderer.render(camera);
        GL11.glCullFace(GL11.GL_BACK);
        shader.bind();
        pvMatrix.load(camera.getCombinedMatrix());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframe? GL11.GL_LINE : GL11.GL_FILL);
        tileAtlas.bind(0);
        new UInt(shader, "tileAtlas").load(0);
        new UVec4(shader, "mixColor").load(0f, 0f, 0f, 0f);
        lightStruct.set(light);
        
        Point cameraChunk = getChunk(camera);
        
        for (Map.Entry<Point, RenderableChunk> entry : renders.entrySet()) {
            Point pt = entry.getKey();
            pt = new Point(pt.x + cameraChunk.x, pt.y + cameraChunk.y);
            RenderableChunk chunk = entry.getValue();
            new UVec3(shader, "translation").load(pt.x * ProcMapChunk.CHUNK_SIZE, 0, pt.y * ProcMapChunk.CHUNK_SIZE);
            chunk.render();
        }

        ObjectChooseHandler.ChoosingObject choosingObject = game.getChooseHandler().getChoosingObject();
        ObjectChooseHandler.Choosable choosing = choosingObject.getObject();
        if(choosing instanceof ProcMapCell) {
            int tileX = choosingObject.getX();
            int tileY = choosingObject.getY();
            
            int inChunkX = tileX % ProcMapChunk.CHUNK_SIZE;
            if(inChunkX < 0)   inChunkX += ProcMapChunk.CHUNK_SIZE;
            int inChunkY = tileY % ProcMapChunk.CHUNK_SIZE;
            if(inChunkY < 0)   inChunkY += ProcMapChunk.CHUNK_SIZE;
            
            int chunkX = (int) Math.floor((double) tileX / ProcMapChunk.CHUNK_SIZE);
            int chunkY = (int) Math.floor((double) tileY / ProcMapChunk.CHUNK_SIZE);
            
            new UVec3(shader, "translation").load(chunkX * ProcMapChunk.CHUNK_SIZE, 0, chunkY * ProcMapChunk.CHUNK_SIZE);
            
            chunkX -= cameraChunk.x;
            chunkY -= cameraChunk.y;
            
            new UVec4(shader, "mixColor").load(1f, 1f, 1f, 1f);
            RenderableChunk chunk = renders.get(new Point(chunkX, chunkY));
            
            GLCalls.disable(GL11.GL_DEPTH_TEST);
            chunk.renderTileAt(inChunkX, inChunkY);
            GLCalls.enable(GL11.GL_DEPTH_TEST);
            Logger.logln(chunkX, chunkY, inChunkX, inChunkY);
        }
        
        tileAtlas.unbind();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        shader.unbind();
        
        //renderer.render(camera, entities);
        //renderer.render(camera, tileEntities);
        
        entityHandler.render(camera);
    }
    
    private static Set<Point> generateCirclePoints(int radius) {
        Set<Point> set = new HashSet<>();
        for (int x = 0; x <= radius; x++) {
            int yRegion = (int) Math.floor(Math.sqrt(radius * radius - x * x));
            for (int y = 0; y <= yRegion; y++) {
                set.add(new Point(x, y));
                if(y != 0)  set.add(new Point(x, -y));
                if(x != 0) {
                    set.add(new Point(-x, y));
                    if(y != 0)  set.add(new Point(-x, -y));
                }
            }
        }
        return set;
    }
    
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{0, 0, 0, 4});
        System.out.println(buf.getFloat());
    }
    
    public static int sort(Point p1, Point p2) {
        return Integer.compare(p1.x, p1.y);
    }
    
    public static Point getChunk(Camera3D camera) {
        int x = (int) Math.floor(camera.getRotatePoint().x / ProcMapChunk.CHUNK_SIZE);
        int z = (int) Math.floor(camera.getRotatePoint().z / ProcMapChunk.CHUNK_SIZE);
        return new Point(x, z);
    }

    private <K, V> K keyOf(V v, Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if(v == value)  return key;
        }
        return null;
    }

    public INoise getNoise() {
        return noise;
    }
    
}
