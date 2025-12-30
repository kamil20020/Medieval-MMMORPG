#version 330 core

#define MAX_BONES 200
#define NUM_BONES_PER_VERTEX 4

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in ivec4 vertexBoneIndices;
layout (location = 3) in vec4 vertexBoneWeights;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform int isAnimated;
uniform mat4 finalBoneMatrices[MAX_BONES];

out vec2 vTexCoord;

void main() {

    vec4 skinnedPos = vec4(position, 1.0);

    if(isAnimated == 1){

        skinnedPos = vec4(0.0);

        for(int i = 0; i < NUM_BONES_PER_VERTEX; i++){

            int boneIndex = vertexBoneIndices[i];
            float weight = vertexBoneWeights[i];

            skinnedPos += weight * (finalBoneMatrices[boneIndex] * vec4(position, 1.0));
        }
    }

    gl_Position = projection * view * model * skinnedPos;
    vTexCoord = texCoord;
}
