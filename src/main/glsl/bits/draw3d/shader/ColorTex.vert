#version 330

uniform mat4 projViewMat;

in vec4 inColor;
in vec4 inTex0;
in vec4 inVert;

smooth out vec4 color;
smooth out vec4 tex0;

void main() {
	color = inColor;
	tex0 = inTex0;
	gl_Position = projViewMat * inVert;
}

