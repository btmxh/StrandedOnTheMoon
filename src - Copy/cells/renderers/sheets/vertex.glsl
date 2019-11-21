#version 330 core

layout (location = 0) in vec2 position;

out vec2 oldTexCoords;
out vec2 texCoords;

uniform vec2 translation;
uniform vec2 scale;

uniform int noOfRows;
uniform vec2 offset;

void main(){
    gl_Position = vec4(translation + position * scale, 0.0, 1.0);
    oldTexCoords = vec2(position.x, 1 - position.y);
    texCoords = oldTexCoords / noOfRows + offset;
}
