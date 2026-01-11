package pl.engine.mmorpg.shaders;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public enum ShaderPropsTypes {

    BOOLEAN{

        @Override
        public void setValue(int locationId, Object value) {

            validateShaderPropertyValue(Boolean.class, value);

            Boolean convertedValue = (Boolean) value;

            glUniform1i(locationId, convertedValue ? 1 : 0);
        }
    },
    MATRIX4f{

        @Override
        public void setValue(int locationId, Object value) {

            validateShaderPropertyValue(Matrix4f.class, value);

            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

            Matrix4f convertedValue = (Matrix4f) value;
            convertedValue.get(floatBuffer);

            glUniformMatrix4fv(locationId, false, floatBuffer);
        }
    },
    MATRIX4f_ARRAY{

        @Override
        public void setValue(int locationId, Object value) {

            validateShaderPropertyValue(Matrix4f[].class, value);

            Matrix4f[] data = (Matrix4f[]) value;

            float[] matricesData = new float[16 * data.length];

            for (int i = 0; i < data.length; i++) {

                Matrix4f matrix = data[i];

                matrix.get(matricesData, i * 16);
            }

            glUniformMatrix4fv(locationId, false, matricesData);
        }
    },
    INTEGER{

        @Override
        public void setValue(int locationId, Object value) {

            validateShaderPropertyValue(Integer.class, value);

            glUniform1i(locationId, 0); // GL_TEXTURE0
        }
    };

    public abstract void setValue(int locationId, Object value);

    private static void validateShaderPropertyValue(Class<?> shaderPropertyType, Object value){

        if(shaderPropertyType == null){

            throw new IllegalArgumentException("Property type class was not given");
        }

        if(value == null){

            throw new IllegalArgumentException("Property type value was not given");
        }

        Class<?> valueType = value.getClass();

        if(!(shaderPropertyType.isInstance(value))){

            throw new IllegalArgumentException(
                "Shader property value got invalid type " + valueType.getSimpleName() +
                " type " + shaderPropertyType.getSimpleName() + "was expected"
            );
        }

        if(shaderPropertyType != valueType){

            throw new IllegalStateException(
                "Invalid shader property value was sent " + valueType.getSimpleName() + " " +
                shaderPropertyType.getSimpleName() + " + was expected"
            );
        }
    }
}
