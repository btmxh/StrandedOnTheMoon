#version 460 core

in vec2 pass_texCoords;

out vec4 out_color;

uniform sampler2D tex;

void main() {
    out_color = texture(tex, pass_texCoords);
}