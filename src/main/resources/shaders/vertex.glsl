#version 120

attribute vec3 position;
attribute vec2 texCoord;

varying vec2 vTexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 1.0);
    vTexCoord = texCoord;
}