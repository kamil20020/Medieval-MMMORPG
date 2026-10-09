package pl.engine.mmorpg.entity.ui.element.complex;

import org.joml.Vector2f;
import pl.engine.mmorpg.entity.ui.element.UiElement;
import pl.engine.mmorpg.texture.Texture;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ComplexUiElement implements UiElement {

    protected final Vector2f bottomLeftCorner = new Vector2f();
    private final Map<ComplexUiElementType, UiElement> uiElements;

    public ComplexUiElement(Vector2f bottomLeftCorner){

        this.bottomLeftCorner.set(bottomLeftCorner);
        this.uiElements = appendUiElements();

        setParentOffset(bottomLeftCorner);
    }

    protected void addUiElement(ComplexUiElementType complexUiElementType, UiElement uiElement) throws IllegalStateException{

        if(uiElements.containsKey(complexUiElementType)){
            throw new IllegalStateException("Complex ui element already contains ui element " + complexUiElementType);
        }

        uiElements.put(complexUiElementType, uiElement);
    }

    protected UiElement getUiElement(ComplexUiElementType complexUiElementType) throws IllegalStateException{

        if(!uiElements.containsKey(complexUiElementType)){
            throw new IllegalStateException("Complex ui element does not have ui element " + complexUiElementType);
        }

        return uiElements.get(complexUiElementType);
    }

    @Override
    public void setParentOffset(Vector2f offset) {

        this.bottomLeftCorner.set(offset);

        doForAllUiElements(element -> element.setParentOffset(offset));
    }

    @Override
    public void appendVertices(FloatBuffer verticesBuffer, Texture texture){

        doForAllUiElements(element -> element.appendVertices(verticesBuffer, texture));
    }

    private void doForAllUiElements(Consumer<UiElement> handleUiElement){

        for(UiElement uiElement : uiElements.values()){

            handleUiElement.accept(uiElement);
        }
    }

    public void update(double deltaTime){

        doForAllUiElements(element -> update(deltaTime));
    }

    protected abstract Map<ComplexUiElementType, UiElement> appendUiElements();
}
