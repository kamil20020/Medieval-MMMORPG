#version 120

uniform sampler2D texture0;
varying vec2 vTexCoord;

void main() {
    gl_FragColor = texture2D(texture0, vTexCoord);
}