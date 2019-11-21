#version 460 core

layout (location = 0) in vec2 position;

out vec2 texCoords;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    
    texCoords.x = position.x + 0.5;
    texCoords.y = 0.5 - position.y;
}