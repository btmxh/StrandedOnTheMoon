#version 330 core

const int samples = 11;
const int start = -samples/2;
const int end = samples/2;

layout (location = 0) in vec2 position;

out vec2 blurTextureCoords[samples];

uniform vec2 translation, scale;
uniform bool flipY;
uniform float fboHeight;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    //vec2 texCoords = (position + 1.0) / 2;
    float pixelSize = 1 / fboHeight;
    for(int i = start; i <= end; i++) {
        blurTextureCoords[i+end] = position + vec2(0.0, pixelSize * i);
    }
}