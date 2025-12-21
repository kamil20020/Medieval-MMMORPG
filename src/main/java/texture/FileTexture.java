package texture;

import java.nio.FloatBuffer;

public class FileTexture extends Texture{

    private float[][] textureCoords;

    public FileTexture(String textureFileUrl, float[][] textureCoords){
        super(textureFileUrl);

        this.textureCoords = textureCoords;
    }

    @Override
    public void appendUv(FloatBuffer buffer, int uvIndex) {

        float[] uv = textureCoords[uvIndex];
        buffer.put(uv);
    }
}
