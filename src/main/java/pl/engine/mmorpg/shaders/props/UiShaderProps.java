package pl.engine.mmorpg.shaders.props;

public enum UiShaderProps implements ShaderProps{

    TEXTURE0("texture0", ShaderPropsTypes.INTEGER),

    IS_GIVEN_COLOR("isGivenColor", ShaderPropsTypes.BOOLEAN),
    COLOR("color", ShaderPropsTypes.VECTOR4F),

    BOTTOM_LEFT_CORNER("bottomLeftCorner", ShaderPropsTypes.VECTOR3F),
    WINDOW_ORTHOGONAL_MATRIX("windowOrthogonalMatrix", ShaderPropsTypes.MATRIX4f),
    IS_DRAWING_UI("isDrawingUI", ShaderPropsTypes.BOOLEAN);

    private final String key;
    private final ShaderPropsTypes type;

    private UiShaderProps(String key, ShaderPropsTypes type){

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
