#version 120

attribute vec3 position;
attribute vec2 texCoord;

uniform mat4 model;

varying vec2 vTexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * model * vec4(position, 1.0);
    vTexCoord = texCoord;
}