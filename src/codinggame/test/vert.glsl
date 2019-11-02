#version 460 core

layout (location = 0) in vec2 position;

out vec4 color;

void main() {
    gl_Position = vec4(position * 2, 0.0, 1.0);
    color = vec4(0.0, 0.0, 1.0, 1.0);    
}