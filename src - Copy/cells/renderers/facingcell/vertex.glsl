#version 330 core

layout (location = 0) in vec2 position;

out vec2 texCoords;
out vec2 oldTexCoords;

uniform vec2 translation;
uniform vec2 scale;
uniform vec4 rotationMatrix;

void main(){
    gl_Position = vec4(translation + position * scale, 0.0, 1.0);
    texCoords = vec2(position.x, 1 - position.y);
    oldTexCoords = texCoords;
    float newTexCoordsX = dot(rotationMatrix.xy, texCoords);
    float newTexCoordsY = dot(rotationMatrix.zw, texCoords);
    texCoords = vec2(newTexCoordsX, newTexCoordsY);
}
