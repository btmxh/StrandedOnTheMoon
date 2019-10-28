#version 330 core

in vec2 texCoords;

out vec4 out_color;

uniform sampler2D tileTexture;
uniform sampler2D breakMap;
uniform int hoveringType;
uniform int breakStage;
uniform vec4 tint;

const float t = 0.05;
const float s = 0.2;
const int breakMapWidth = 2, breakMapHeight = 2;

void main(){
    out_color = texture(tileTexture, texCoords);
    float cx = 0.5 - abs(texCoords.x - 0.5);
    float cy = 0.5 - abs(texCoords.y - 0.5);
    if(out_color.a < 0.3) {
        out_color = vec4(0.75, 0.75, 0.75, out_color.a);
    }
    if((cx < s && cy < t) || (cx < t && cy < s)) {
        if(hoveringType == 1) {
            out_color.rgb += 0.1;    
            out_color.a = 1;
        } else if(hoveringType == 2) {
            out_color.rgb += 0.2;
            out_color.a = 1;   
        }
    }
    
    if(breakStage >= 0) {
        int row = breakStage / breakMapHeight;
        int col = breakStage - row * breakMapHeight;
        vec2 breakMapTexCoords = (texCoords + vec2(col, row)) / vec2(breakMapWidth, breakMapHeight);
        float breakFactor = texture(breakMap, breakMapTexCoords).a;
        if(breakFactor > 0) out_color = vec4(0.0, 0.0, 0.0, 1.0);
    }
    
    if(out_color.a < 0.3)   out_color.rgb = vec3(0);
    
    out_color.rgb += tint.rgb;
    out_color.a = tint.a;
}
