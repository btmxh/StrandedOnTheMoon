#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normals;
layout (location = 2) in vec2 texCoords;

out vec2 pass_texCoords;

uniform mat4 tMatrix;
uniform mat4 pvMatrix;

void main() {
    gl_Position = pvMatrix * tMatrix * vec4(position, 1.0);
    pass_texCoords = texCoords;
}