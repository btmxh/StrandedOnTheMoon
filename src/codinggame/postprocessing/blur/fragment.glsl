#version 330 core

in vec2 blurTextureCoords[11];

out vec4 out_color;

uniform sampler2D tex;

void main() {
    out_color = vec4(0.0);
    out_color += texture(tex, blurTextureCoords[0]) * 0.0093;
    out_color += texture(tex, blurTextureCoords[1]) * 0.028002;
    out_color += texture(tex, blurTextureCoords[2]) * 0.065984;
    out_color += texture(tex, blurTextureCoords[3]) * 0.121703;
    out_color += texture(tex, blurTextureCoords[4]) * 0.175713;
    out_color += texture(tex, blurTextureCoords[5]) * 0.198596;
    out_color += texture(tex, blurTextureCoords[6]) * 0.175713;
    out_color += texture(tex, blurTextureCoords[7]) * 0.121703;
    out_color += texture(tex, blurTextureCoords[8]) * 0.065984;
    out_color += texture(tex, blurTextureCoords[9]) * 0.028002;
    out_color += texture(tex, blurTextureCoords[10]) * 0.0093;
    out_color = texture(tex, blurTextureCoords[5]);
}