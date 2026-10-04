#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:globals.glsl>

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform HeatConfig {
    float Strength;
    float Mode;
    float StartDistance;
    float EndDistance;
    float DepthScale;
    float DepthBias;
    float DepthLinear;
    float SkyDepth;
};

layout(location = 0) in vec2 texCoord;

layout(location = 0) out vec4 fragColor;

float reach(vec2 uv) {
    float depth = texture(DepthSampler, uv).r;
    if (depth == SkyDepth) { return -1.0; }
    return smoothstep(StartDistance, EndDistance, DepthLinear / (depth * DepthScale + DepthBias));
}

void main() {
    float phase = fract(GameTime * 1200.0) * 6.2831853;
    float sway = sin(texCoord.y * 90.0 - phase * 3.0) * 0.6 + sin(texCoord.y * 37.0 + texCoord.x * 11.0 - phase * 2.0) * 0.4;
    float rise = sin(texCoord.x * 70.0 + phase * 2.0) * 0.5 + sin(texCoord.x * 23.0 - texCoord.y * 40.0 + phase * 3.0) * 0.5;
    vec2 offset = vec2(sway * 0.012, rise * 0.008) * Strength;
    if (Mode < 0.5) {
        offset *= 1.0 - smoothstep(0.35, 0.75, texCoord.y);
    }
    else {
        float here = max(reach(texCoord), 0.0);
        float there = reach(texCoord + offset * here);
        offset *= there < 0.0 ? here : min(here, there);
    }
    fragColor = vec4(texture(InSampler, texCoord + offset).rgb, 1.0);
}
