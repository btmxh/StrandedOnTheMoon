#version 460 core

layout (location = 0) in vec3 position;

out vec3 texCoords;

uniform mat4 pvMatrix;

const float SIZE = 1000;

void main() {
    gl_Position = pvMatrix * vec4(position * SIZE, 1.0);
    texCoords = position * SIZE;
}