#version 330

uniform mat4 projViewMat;

in vec4 inColor;
in vec4 inVert;

smooth out vec4 color;


void main() {
	color = inColor;
	gl_Position = projViewMat * inVert;
}

