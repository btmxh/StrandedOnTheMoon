#version 330 core

layout (location = 0) in vec2 position;

out vec2 texCoords;

uniform vec2 translation, scale;
uniform bool flipY;

void main() {
    gl_Position = vec4(position * scale + translation, 0.0, 1.0);
    texCoords = position;
    if(!flipY) {
        texCoords.y = 1 - texCoords.y;
    }
}