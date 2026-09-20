#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec4 color;

uniform int isDrawingUI;
uniform vec3 bottomLeftCorner;
uniform vec2 size;
uniform mat4 windowOrthogonalMatrix;

out vec2 vTexCoord;

void main() {

    gl_Position = windowOrthogonalMatrix * vec4(position, 1.0);
    //        vTexCoord = texCoord;
}
