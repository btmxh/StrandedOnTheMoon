#version 460 core

struct Light {
    vec3 position;
    vec3 color;
    vec3 attenuation;
};

in vec3 pass_normal;
in vec2 pass_texCoords;
in vec3 toLightVector;

out vec4 out_color;

uniform sampler2D tileAtlas;
uniform Light light;
uniform vec4 mixColor;

void main() {
    float ndc = dot(normalize(pass_normal), normalize(toLightVector));
    float brightness = max(ndc, 0.0);
    float distance = length(toLightVector);
    float attenutationFactor = dot(light.attenuation, vec3(1, distance, distance * distance));
    vec3 diffuse = brightness * light.color / attenutationFactor;
    
    out_color = texture(tileAtlas, pass_texCoords);    
    out_color.rgb *= diffuse;
    out_color.rgb = mix(out_color.rgb, mixColor.rgb, mixColor.a);
}