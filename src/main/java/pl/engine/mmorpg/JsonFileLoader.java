package pl.engine.mmorpg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public interface JsonFileLoader {

    static final ObjectMapper objectMapper = new ObjectMapper();

    static <T> T loadList(String filePath, TypeReference<T> typeReference) throws IllegalStateException{

        InputStream inputStream = JsonFileLoader.class.getClassLoader().getResourceAsStream(filePath);

        T gotList = null;

        try {
            gotList = objectMapper.readValue(inputStream, typeReference);
        }
        catch (IOException e) {

            throw new IllegalStateException(e.getMessage());
        }

        return gotList;
    }
}
