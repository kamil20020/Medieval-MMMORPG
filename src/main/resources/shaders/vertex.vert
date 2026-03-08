#version 330 core

#define MAX_BONES 200
#define NUM_BONES_PER_VERTEX 4

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 rawNormal;
layout (location = 3) in ivec4 vertexBoneIndices;
layout (location = 4) in vec4 vertexBoneWeights;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform int isAnimated;
uniform mat4 finalBoneMatrices[MAX_BONES];
uniform int isGivenColor;

out vec2 vTexCoord;
out vec3 modelPosition;
out vec3 normal;

void main() {

    vec4 skinnedPos = vec4(position, 1.0);
    normal = vec3(0, 0, 0);

    if(isAnimated == 1){

        skinnedPos = vec4(0.0);
        vec4 rawSkinnedNormal = vec4(0.0);

        for(int i = 0; i < NUM_BONES_PER_VERTEX; i++){

            int boneIndex = vertexBoneIndices[i];
            float weight = vertexBoneWeights[i];

            skinnedPos += weight * (finalBoneMatrices[boneIndex] * vec4(position, 1.0));
            rawSkinnedNormal += weight * (finalBoneMatrices[boneIndex] * vec4(rawNormal, 0.0));
        }

        normal = normalize(rawSkinnedNormal.xyz);
    }
    else{
        normal = rawNormal;
    }

    gl_Position = projection * view * model * skinnedPos;
    vTexCoord = texCoord;

    vec4 rawPosition = model * skinnedPos;
    modelPosition = rawPosition.xyz;
}
