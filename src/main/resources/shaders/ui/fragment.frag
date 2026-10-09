#version 330 core

uniform sampler2D texture0;
uniform int isGivenColor;
uniform vec4 color;

in vec2 vTexCoord;

out vec4 fragColor;

void main() {

    if(isGivenColor == 1){

        fragColor = color;
        return;
    }

    fragColor =  texture(texture0, vTexCoord);
}