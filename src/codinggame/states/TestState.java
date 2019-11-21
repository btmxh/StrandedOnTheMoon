/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import codinggame.map.MapTile;
import codinggame.map.MapTileset;
import codinggame.map.proceduralmap.ProcMap;
import codinggame.map.proceduralmap.ProcMapLoader;
import codinggame.objs.Clock;
import codinggame.utils.textures.TexturePacker;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.utils.colors.StaticColor;
import com.lwjglwrapper.utils.models.ModelGenerator;
import com.lwjglwrapper.utils.states.State;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class TestState extends State<CodingGame> {

    VAO quad;
    Shader shader;
    Texture2D texture;
    ProcMap map;
    
    public TestState(CodingGame game) {
        super(game);
    }

    @Override
    public void show() {
        quad = new ModelGenerator().rect(-0.5f, -0.5f, 1, 1);
        shader = new Shader(
            ShaderFile.fromResource(TestState.class, "/codinggame/states/test/vertex.glsl", GL20.GL_VERTEX_SHADER),
            ShaderFile.fromResource(TestState.class, "/codinggame/states/test/fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        TextureData rSlashShichimiyaMemes = TextureData.fromResource(TestState.class, "/sadness intensifies.png");
        texture = new Texture2D(new TextureData(rSlashShichimiyaMemes.getWidth() * 3, rSlashShichimiyaMemes.getHeight() * 3, null));
        texture.bind();
        texture.modifyTexture(rSlashShichimiyaMemes, 0, 0);
        texture.modifyTexture(rSlashShichimiyaMemes, rSlashShichimiyaMemes.getWidth(), rSlashShichimiyaMemes.getHeight());
        texture.modifyTexture(rSlashShichimiyaMemes, rSlashShichimiyaMemes.getWidth() * 2, rSlashShichimiyaMemes.getHeight() * 2);
        texture.unbind();
        try {
            map = ProcMapLoader.loadMap("saves/procmap", "", new Clock());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MapTileset tileset = map.getTileset();
        Map<Integer, MapTile> tiles = tileset.getTiles();
        TexturePacker packer = new TexturePacker(32, 32);
        int size = (int) Math.ceil(Math.sqrt(tiles.size()));
        packer.create(size, size);
        packer.getTexture().bind();
        int x = 0, y = 0;
        for (Map.Entry<Integer, MapTile> entry : tiles.entrySet()) {
            Integer id = entry.getKey();
            MapTile tile = entry.getValue();
            packer.addTexture(id, tile.getTexture().getTextureData(), x, y);
            x++;
            if(x >= size) {
                x = 0;
                y++;
            }
        }
        texture = packer.getTexture();
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render() {
        GLCalls.setClearColor(StaticColor.BLUE);
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT);
        GLCalls.enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        shader.bind();
        quad.bindAll();
        new UInt(shader, "tex").load(0);
        texture.bind(0);
        quad.renderElement(GL11.GL_TRIANGLES, 0, -1);
        quad.unbindByLastBind();
        texture.unbind();
        shader.unbind();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
    
}
