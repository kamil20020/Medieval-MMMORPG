package pl.engine.mmorpg.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.engine.mmorpg.mesh.Rect;
import pl.engine.mmorpg.shaders.Shader;
import pl.engine.mmorpg.shaders.ShaderProps;
import pl.engine.mmorpg.terrain.TerrainMesh;
import pl.engine.mmorpg.texture.FileTexture;
import pl.engine.mmorpg.texture.Texture;

import static org.lwjgl.opengl.GL11.*;

public class ShadowComponent implements Component{

    private final TransformComponent transformComponent;

    private final Rect shadowRect;

    public ShadowComponent(TransformComponent transformComponent){

        this.transformComponent = transformComponent;

        Texture texture = new FileTexture("textures/simple-shadow.png", Rect.TEXTURE_COORDS);
        shadowRect = new Rect(texture);
    }

    @Override
    public void prepare() {

        shadowRect.uploadToGpu();
    }

    @Override
    public void update(double deltaTime) {

        Vector3f position = transformComponent.getPosition();

        TerrainMesh terrainMesh = TerrainMesh.getInstance();
        float terrainMaxY = (float) terrainMesh.getTerrainMaxY(position.x, position.z);

        Vector3f translate = new Vector3f(position.x - 0.25f, terrainMaxY, position.z - 0.25f);
        Matrix4f model = new Matrix4f().identity()
            .translate(translate);

        shadowRect.setModel(model);
    }

    @Override
    public void draw(){

        Shader shader = Shader.getInstance();

        shader.setPropertyValue(ShaderProps.IS_DISABLED_LIGHT, Boolean.TRUE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);

        shadowRect.draw();

        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
        glDepthMask(true);

        shader.setPropertyValue(ShaderProps.IS_DISABLED_LIGHT, Boolean.FALSE);
    }
}
