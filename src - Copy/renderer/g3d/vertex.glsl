#version 460 core

struct Light {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};

layout (location = 0) in vec2 xzPosition;
layout (location = 1) in vec4 yAndNormals;
layout (location = 2) in vec2 texCoords;

out vec3 pass_normal;
out vec3 toLightVector;
out vec2 pass_texCoords;

uniform mat4 pvMatrix;
uniform vec3 translation;
uniform Light light;

void main() {
    vec3 position;
    position.xz = xzPosition;
    position.y = yAndNormals.x;
    position += translation;
    gl_Position = pvMatrix * vec4(position, 1.0);
    pass_texCoords = texCoords;
    toLightVector = light.position - position;
    pass_normal= yAndNormals.yzw;
}