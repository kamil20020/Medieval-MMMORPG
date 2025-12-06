package org.example.mesh;

public enum MeshType {

    CUBE("Cube"),
    PLANT("Plant");

    private final String name;

    private MeshType(String name){

        this.name = name;
    }

    @Override
    public String toString(){

        return name;
    }

//    public static Mesh getMesh(MeshType meshType){
//
//        if(meshType == null){
//
//            return Cube.getInstance();
//        }
//
//        return switch (meshType){
//
//            case CUBE -> Cube.getInstance();
//            case PLANT -> Plant.getInstance();
//            default -> Cube.getInstance();
//        };
//    }

}
