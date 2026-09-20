package pl.engine.mmorpg.shaders.props;

public interface ShaderProps {

    public String getKey();
    public ShaderPropsTypes getType();
    public<T> void setValue(int locationId, T value);
}
