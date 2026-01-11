package pl.engine.mmorpg.entity.animation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class Skeleton {

    protected final Map<String, Integer> bonesNamesIndices = new LinkedHashMap<>();

    protected boolean addBone(String boneName, int boneIndex){

        if(bonesNamesIndices.containsKey(boneName)){
            return false;
        }

        bonesNamesIndices.put(boneName, boneIndex);

        return true;
    }

    public int getNumberOfBones(){

        return bonesNamesIndices.size();
    }

    public int getBoneIndex(String boneName){

        return bonesNamesIndices.get(boneName);
    }

    public boolean containsBone(String boneName){

        return bonesNamesIndices.containsKey(boneName);
    }

    public Set<Map.Entry<String, Integer>> getEntrySet(){

        return bonesNamesIndices.entrySet();
    }

    protected abstract void loadBonesNamesIndices();
}
