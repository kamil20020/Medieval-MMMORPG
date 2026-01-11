package pl.engine.mmorpg.mesh.libraries.jgltf;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import pl.engine.mmorpg.mesh.ComplexMesh;
import pl.engine.mmorpg.mesh.Meshable;
import pl.engine.mmorpg.texture.JgltfTexture;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ComplexJgltfMesh extends ComplexMesh {

    public ComplexJgltfMesh(String complexModelFilePath) {

        super(complexModelFilePath);
    }

    protected ComplexJgltfMesh(){


    }

    @Override
    protected void loadModel(String complexModelFilePath) {

        GltfModel model = load(complexModelFilePath);

        List<MeshModel> rawMeshes =  model.getMeshModels();

        for(int i = 0; i < rawMeshes.size(); i++){

            MeshModel rawMesh = rawMeshes.get(i);

            JgltfTexture texture = new JgltfTexture(rawMesh);
            Meshable mesh = new JgltfGlbMesh(rawMesh, texture);

            meshes.add(mesh);
        }
    }

    public static GltfModel load(String modelPath) throws IllegalStateException{

        File file = new File(modelPath);

        GltfModelReader reader = new GltfModelReader();
        GltfModel model = null;

        try {

            model = reader.read(file.toPath());
        }
        catch (IOException e) {

            e.printStackTrace();
        }

        return model;
    }
}
