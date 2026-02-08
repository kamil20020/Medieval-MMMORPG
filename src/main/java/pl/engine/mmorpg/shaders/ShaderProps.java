package pl.engine.mmorpg.shaders;

public enum ShaderProps {

    PERSPECTIVE("projection", ShaderPropsTypes.MATRIX4f),
    MODEL("model", ShaderPropsTypes.MATRIX4f),
    CAMERA("view", ShaderPropsTypes.MATRIX4f),

    TEXTURE0("texture0", ShaderPropsTypes.INTEGER),

    IS_ANIMATED("isAnimated", ShaderPropsTypes.BOOLEAN),
    FINAL_BONE_MATRICES("finalBoneMatrices", ShaderPropsTypes.MATRIX4f_ARRAY),

    IS_GIVEN_COLOR("isGivenColor", ShaderPropsTypes.BOOLEAN),
    COLOR("color", ShaderPropsTypes.VECTOR4F);

    private final String key;
    private final ShaderPropsTypes type;

    private ShaderProps(String key, ShaderPropsTypes type){

        this.key = key;
        this.type = type;
    }

    public String getKey(){

        return key;
    }

    public ShaderPropsTypes getType() {

        return type;
    }

    public<T> void setValue(int locationId, T value){

        type.setValue(locationId, value);
    }
}
