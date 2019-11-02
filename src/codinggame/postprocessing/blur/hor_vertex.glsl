#version 330 core

const int samples = 11;
const int start = -samples/2;
const int end = samples/2;

layout (location = 0) in vec2 position;

out vec2 blurTextureCoords[samples];

uniform vec2 translation, scale;
uniform bool flipY;
uniform float fboWidth;

void main() {
    gl_Position = vec4(position * scale + translation, 0.0, 1.0);
    float pixelSize = 1 / fboWidth;
    for(int i = start; i <= end; i++) {
        blurTextureCoords[i+end] = position + vec2(pixelSize * i, 0.0);
    }
}