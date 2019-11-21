#version 460 core

in vec2 pass_texCoords;
in vec3 color;
out vec4 out_color;

uniform sampler2D tileAtlas;

void main() {
    out_color = texture(tileAtlas, pass_texCoords);
}