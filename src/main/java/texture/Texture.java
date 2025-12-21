package texture;

import org.lwjgl.assimp.AIVector3D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public abstract class Texture {

    protected int textureId;
    protected int width = 0;
    protected int height = 0;
    protected String textureFileUrl;

    public Texture(String textureFileUrl){
        this.textureFileUrl = textureFileUrl;
        textureId = createNonEmptyTexture();
    }

    public Texture(){
        textureId = createEmptyTexture();
    }

    protected int createNonEmptyTexture(){

        ByteBuffer buffer = loadTextureData();

        return createNonEmptyTexture(buffer);
    }

    protected ByteBuffer loadTextureData(){

        BufferedImage bufferedImage = loadImage(textureFileUrl);

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        for (int y = height - 1; y >= 0; y--) {

            for (int x = 0; x < width; x++) {

                int pixelARGB = bufferedImage.getRGB(x, y);

                int alpha = (pixelARGB >> 24) & 0xFF;
                int red   = (pixelARGB >> 16) & 0xFF;
                int green = (pixelARGB >> 8)  & 0xFF;
                int blue  = pixelARGB & 0xFF;

                buffer.put((byte) red);
                buffer.put((byte) green);
                buffer.put((byte) blue);
                buffer.put((byte) alpha);
            }
        }

        buffer.flip();

        return buffer;
    }

    protected int createNonEmptyTexture(ByteBuffer buffer){

        //generate a texture handle or unique ID for this texture
        int textureId = createEmptyTexture();

        //upload our ByteBuffer to GL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return textureId;
    }

    protected int createEmptyTexture() {

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        //use an alignment of 1 to be safe
        //this tells OpenGL how to unpack the RGBA bytes we will specify
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // set parameters for the texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // Use linear filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // Use linear filtering for magnification

        //Setup wrap mode, i.e. how OpenGL will handle pixels outside of the expected range
        //Note: GL_CLAMP_TO_EDGE is part of GL12
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        return textureId;
    }

    public int getId(){

        return textureId;
    }

    public abstract void appendUv(FloatBuffer buffer, int uvIndex);

    private static BufferedImage loadImage(String filePath){

        URL gotResourceUrl = Texture.class.getClassLoader().getResource(filePath);

        BufferedImage bufferedImage = null;

        try {
            File file = new File(gotResourceUrl.toURI());

            bufferedImage = ImageIO.read(file);
        }
        catch (URISyntaxException | IOException e) {

            e.printStackTrace();
        }

        return bufferedImage;
    }

    public static void useTexture(int textureId){

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

}
