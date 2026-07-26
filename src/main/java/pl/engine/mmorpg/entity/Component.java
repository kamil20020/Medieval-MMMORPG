package pl.engine.mmorpg.entity;

public interface Component {

    void update(double deltaTime);

    default void prepare(){}
    default void clear(){}
}
