#version 330 core

uniform sampler2D texture0;
uniform int isGivenColor;
uniform vec4 color;

in vec2 vTexCoord;
out vec4 fragColor;

void main() {

    if(isGivenColor == 1){
        //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
        fragColor = color;
    }
    else{
        fragColor = texture(texture0, vTexCoord);
    }
}