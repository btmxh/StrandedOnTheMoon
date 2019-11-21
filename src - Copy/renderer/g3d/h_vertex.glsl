#version 460 core

layout (location = 0) in vec2 xzPosition;
layout (location = 1) in vec4 yAndNormals;
layout (location = 2) in vec2 texCoords;

uniform mat4 pvMatrix;
uniform vec3 translation;

out vec2 pass_texCoords;

void main() {
    vec3 position;
    position.xz = xzPosition;
    position.y = yAndNormals.x;
    position += translation;
    gl_Position = pvMatrix * vec4(position, 1.0);
    pass_texCoords = texCoords;
}