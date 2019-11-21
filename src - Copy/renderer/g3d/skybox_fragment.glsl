#version 460 core

in vec3 texCoords;

out vec4 out_color;

uniform samplerCube cubeTexture;

const float lowerLimit = 0.0, upperLimit = 5.0;

void main() {
    //float factor = (texCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    //factor = clamp(factor, 0.0, 1.0);
    out_color = texture(cubeTexture, texCoords);
    //out_color.rgb *= factor;
}