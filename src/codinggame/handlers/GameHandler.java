package codinggame.handlers;

import codinggame.CodingGame;
import codinggame.globjs.Light;
import codinggame.map.GameMap;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ChunkGenerator;
import codinggame.map.proceduralmap.ProcMap;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.proceduralmap.entities.rendering.EntityBatch;
import codinggame.map.proceduralmap.entities.rendering.EntityHandler;
import codinggame.map.proceduralmap.entities.rendering.ModelManager;
import codinggame.map.renderer.g3d.Camera3D;
import codinggame.map.renderer.g3d.DebugCamera3D;
import codinggame.map.renderer.g3d.SkyboxRenderer;
import codinggame.map.renderer.g3d.TerrainPicker;
import codinggame.map.renderer.g3d.entities.AABBUtils.AABBBuilder;
import codinggame.map.renderer.g3d.entities.EntityModel;
import codinggame.map.renderer.g3d.terrain.RenderableChunk;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.ColoredLogger;
import codinggame.utils.INoise;
import codinggame.utils.Timer;
import codinggame.utils.textures.PackedTexture;
import codinggame.utils.textures.TexturePacker;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UMat4;
import com.lwjglwrapper.utils.input.KeyBindings;
import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.Collection;
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
import org.joml.Vector4f;
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
    private Shader shader;
    private Camera3D camera;
    private UMat4 pvMatrix;
    private KeyBindings keyBindings;
    private PackedTexture tileAtlas;
    private TerrainPicker picker;
    private MapTilesets tileset;
    
    private final Map<Point, RenderableChunk> renders;
    private Set<Point> circlePoints = generateCirclePoints(RENDER_DISTANCE);
    
    private final INoise noise;
    
    private Light.Struct lightStruct;
    private Light light;
    
    private ProcMapLayer turfLayer;
    
    //private Map<Point, ProcMapChunk> loadedChunks = new HashMap<>();
    private SkyboxRenderer skyboxRenderer;
    private Texture2D chooseMap;
    
    private AABBBuilder tileAABB = new AABBBuilder().xCenter(0f, 1f).yCenter(0f, 1f).zCenter(0f, 1f);
    
    private EntityHandler entityHandler;
    private EntityLoader entityLoader;
    
    private EntityUpdateHandler entityUpdateHandler;

    public GameHandler(GameState game) {
        this.game = game;
        map = (ProcMap) game.getMapHandler().getMap();
        turfLayer = map.getMapLayer(GameMap.TURF_LAYER);
        entityLoader = turfLayer.getEntityLoader();
        this.tileset = map.getTilesets();
        
        chooseMap = new Texture2D(TextureData.fromResource(GameHandler.class, "/chooseMap.png"));
        
        shader = new Shader(
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(GameHandler.class, "/codinggame/map/renderer/g3d/fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        
        picker = new TerrainPicker(LWJGL.window){
            @Override
            public float height(float x, float z) {
                return noise.getTriangleHeight(x, z);
            }
        };
        pvMatrix = new UMat4(shader, "pvMatrix");
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
        camera.getPosition().set(8, 8, 8);
        
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
        
        entityUpdateHandler = new EntityUpdateHandler(this, entityLoader);
        entityHandler.getEntities().put(ModelManager.ROBOT, game.getRobotHandler().getRobotList().values());
        entityHandler.getEntities().put((Consumer<EntityBatch> consumer) -> {
            Map<EntityModel, List> sortMap = new HashMap<>();
            for (RenderableChunk chunk : renders.values()) {
                Collection<Long> pointers = chunk.getEntities().values();
                for (long pointer : pointers) {
                    EntityData entity = entityLoader.getFromCache(pointer);
                    EntityModel model = null;
                    if(entity.isTileEntity()) {
                        model = ModelManager.tileEntityModel(entity.getSubTileID());
                    } else if(entity.isTreeEntity()) {
                        model = ModelManager.treeModel(entity.getSubTileID());
                    } 
                    if(entity.getType() == 4) {
                        model = ModelManager.TEST;
                        System.out.println("codinggame.handlers.GameHandler.<init>()");
                    }
                    List batch = sortMap.get(model);
                    if(batch == null) {
                        sortMap.put(model, batch = new LinkedList<>());
                    }
                    batch.add(entity);
                }
            }
            for (Map.Entry<EntityModel, List> entry : sortMap.entrySet()) {
                EntityModel model = entry.getKey();
                List batch = entry.getValue();
                consumer.accept(new EntityBatch(model, (c) -> batch.forEach(c)));
            }
        });
        
        Timer.logger = (s, c) -> CodingGame.debug(s);
        
        EntityData testEntityData = entityLoader.emptyEntity();
        testEntityData.setPosition(new Vector2f(8, 8));
        testEntityData.setHeight(Float.NaN);
        testEntityData.setType((byte) 4);
        testEntityData.setSubTileID(MapTile.IRON_ORE);
        
        entityLoader.writeData(testEntityData.getEntityID(), testEntityData);
        entityLoader.putToCache(testEntityData.getEntityID(), testEntityData);
        
        turfLayer.setEntityAt(8, 8, testEntityData.getEntityID());
    }
    
    public void update(float delta) {
        Timer.start();
        
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
            }
        }
        Point cameraChunk = getChunk(camera);
        
        Set<Point> usingChunkPositions = new HashSet<>();
        Set<RenderableChunk> usingChunks = new HashSet<>();
        
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
            Set<Long> pointers = new HashSet<>(chunk.getEntities().values());
            for (long pointer : pointers) {
                EntityData entity = entityLoader.getFromCache(pointer);
                if(entity == null)  continue;
                if(entity.isTileEntity()) {
                    Vector3f translation = entity.get3DPosition().add(0.5f, 0.25f, 0.5f);
                    game.getChooseHandler().test(tileAABB, translation, ObjectChooseHandler.ChoosingObject.tileEntity(entity, turfLayer));
                }
            }
            if(usingChunks.contains(chunk)) continue;
            chunk.unmark();
        }
        
        turfLayer.saveChunksNotIn(usingChunkPositions);
        for (Robot robot : game.getRobotHandler().getRobotList().values()) {
            game.getChooseHandler().test(robot.getBoundBox(), new Vector3f(), ObjectChooseHandler.ChoosingObject.robot(robot));
        }
        game.getChooseHandler().endFrame();
        entityUpdateHandler.update(delta, renders);
        int x = random.nextInt(16);
        int y = random.nextInt(16);
        TileUpdateHandler.updateCell(turfLayer, x, y);
        EntityData eCell = turfLayer.getEntityAt(random.nextInt(16), random.nextInt(16));
        if(eCell != null)   TileUpdateHandler.updateEntityCell(eCell);
        
        
        Timer.stop();
    }
    
    public static final Random random = new Random();
    
    boolean wireframe = false;
    public void render() {
        Timer.start();
        GLCalls.enable(GL11.GL_BLEND, GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_FRONT);
        skyboxRenderer.render(camera);
        GL11.glCullFace(GL11.GL_BACK);
        shader.bind();
        pvMatrix.load(camera.getCombinedMatrix());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframe? GL11.GL_LINE : GL11.GL_FILL);
        shader.loadUniformVariable("tileAtlas", tileAtlas.bind(0));
        shader.loadUniformVariable("chooseMap", chooseMap.bind(1));
        lightStruct.set(light);
        
        Point cameraChunk = getChunk(camera);
        
        int mixColor_location = shader.loadUniformVariable("mixColor", new Vector4f(1f, 1f, 1f, 0f)); 
        for (Map.Entry<Point, RenderableChunk> entry : renders.entrySet()) {
            Point pt = entry.getKey();
            pt = new Point(pt.x + cameraChunk.x, pt.y + cameraChunk.y);
            RenderableChunk chunk = entry.getValue();
            shader.loadUniformVariable("translation", new Vector3f(pt.x * ProcMapChunk.CHUNK_SIZE, 0, pt.y * ProcMapChunk.CHUNK_SIZE));
            chunk.render();
        }

        shader.loadUniformVariable(mixColor_location, new Vector4f(1f, 1f, 1f, 1f));
        ObjectChooseHandler.ChoosingObject choosingObject = game.getChooseHandler().getChoosingObject();
        ObjectChooseHandler.Choosable choosing = choosingObject.getObject();
        if(picker.currentTerrainPoint != null) {
            int tileX = (int) Math.floor(picker.currentTerrainPoint.x);
            int tileY = (int) Math.floor(picker.currentTerrainPoint.z);
            
            int inChunkX = tileX % ProcMapChunk.CHUNK_SIZE;
            if(inChunkX < 0)   inChunkX += ProcMapChunk.CHUNK_SIZE;
            int inChunkY = tileY % ProcMapChunk.CHUNK_SIZE;
            if(inChunkY < 0)   inChunkY += ProcMapChunk.CHUNK_SIZE;
            
            int chunkX = (int) Math.floor((double) tileX / ProcMapChunk.CHUNK_SIZE);
            int chunkY = (int) Math.floor((double) tileY / ProcMapChunk.CHUNK_SIZE);
            
            shader.loadUniformVariable("translation", new Vector3f(chunkX * ProcMapChunk.CHUNK_SIZE, 0, chunkY * ProcMapChunk.CHUNK_SIZE));
            
            chunkX -= cameraChunk.x;
            chunkY -= cameraChunk.y;
            
            shader.loadUniformVariable("mixColor", new Vector4f(1f, 1f, 1f, 0.25f));
            
            RenderableChunk chunk = renders.get(new Point(chunkX, chunkY));
            
            GLCalls.disable(GL11.GL_DEPTH_TEST);
            chunk.renderTileAt(inChunkX, inChunkY);
            GLCalls.enable(GL11.GL_DEPTH_TEST);
            CodingGame.debug(chunkX, chunkY, inChunkX, inChunkY);
        }
        
        if(choosing instanceof ProcMapCell) {
            int tileX = choosingObject.getX();
            int tileY = choosingObject.getY();
            
            int inChunkX = tileX % ProcMapChunk.CHUNK_SIZE;
            if(inChunkX < 0)   inChunkX += ProcMapChunk.CHUNK_SIZE;
            int inChunkY = tileY % ProcMapChunk.CHUNK_SIZE;
            if(inChunkY < 0)   inChunkY += ProcMapChunk.CHUNK_SIZE;
            
            int chunkX = (int) Math.floor((double) tileX / ProcMapChunk.CHUNK_SIZE);
            int chunkY = (int) Math.floor((double) tileY / ProcMapChunk.CHUNK_SIZE);
            
            shader.loadUniformVariable("translation", new Vector3f(chunkX * ProcMapChunk.CHUNK_SIZE, 0, chunkY * ProcMapChunk.CHUNK_SIZE));
            
            chunkX -= cameraChunk.x;
            chunkY -= cameraChunk.y;
            
            shader.loadUniformVariable("mixColor", new Vector4f(1f, 1f, 1f, 0.5f));
            
            RenderableChunk chunk = renders.get(new Point(chunkX, chunkY));
            
            GLCalls.disable(GL11.GL_DEPTH_TEST);
            chunk.renderTileAt(inChunkX, inChunkY);
            GLCalls.enable(GL11.GL_DEPTH_TEST);
            CodingGame.debug(chunkX, chunkY, inChunkX, inChunkY);
            game.getChooseHandler().debug();
        }
        
        tileAtlas.unbind();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        shader.unbind();
        
        entityHandler.render(camera);
        long time = Timer.stop(ColoredLogger.Color16.FG_RED);
        
        avg = (time + avg * frames) / (frames + 1);
        frames++;
        
        CodingGame.debug(time, avg, frames);
    }
    
    int frames;
    long avg;
    
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

    public ProcMapLayer getTurfLayer() {
        return turfLayer;
    }

    public ProcMap getMap() {
        return map;
    }
    
}
