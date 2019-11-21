#version 460 core

in vec2 pass_texCoords;

out vec4 out_color;

uniform sampler2D tileAtlas;
uniform sampler2D chooseMap;
uniform float alpha;

void main() {
    float choose = texture(chooseMap, pass_texCoords).r;
    out_color = vec4(0.0, 0.0, 0.0, 1.0);
        
}