#version 330 core

uniform sampler2D texture0;
uniform int isGivenColor;
uniform vec4 color;
uniform int isDisabledLight;

in vec2 vTexCoord;
in vec3 modelPosition;
in vec3 normal;
out vec4 fragColor;

vec4 lightColor = vec4(1.0);
vec3 lightPosition = vec3(500, 1000, 500);
float ambient = 0.2f;

vec4 getLight(){

    vec3 lightDirection = normalize(lightPosition - modelPosition);
    float diffuse = max(dot(lightDirection, normal), 0.0);

    return lightColor * (ambient + diffuse);
}

void main() {

    if(isGivenColor == 1){

        fragColor = color;
        return;
    }

    vec4 textureColor = texture(texture0, vTexCoord);

    if(isDisabledLight == 1){

        fragColor = textureColor;
        return;
    }

    fragColor = textureColor * getLight();
}