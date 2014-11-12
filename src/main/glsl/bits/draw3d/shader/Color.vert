#version 330

uniform mat4 PROJ_VIEW_MAT;

in vec4 inColor;
in vec4 inVert;

out vec4 color;

void main() {
	color = inColor;
	gl_Position = PROJ_VIEW_MAT * inVert;
}

