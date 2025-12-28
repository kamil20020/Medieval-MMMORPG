#version 330 core

uniform sampler2D texture0;
uniform int useOutsideColor;
uniform vec4 outsideColor;

in vec2 vTexCoord;
out vec4 fragColor;

void main() {

    if(useOutsideColor == 1){
        fragColor = outsideColor;
    }
    else{
        fragColor = texture(texture0, vTexCoord);
        //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}