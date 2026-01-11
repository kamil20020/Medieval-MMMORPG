#version 330 core

uniform sampler2D texture0;

in vec2 vTexCoord;
out vec4 fragColor;

void main() {

    fragColor = texture(texture0, vTexCoord);
    //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}