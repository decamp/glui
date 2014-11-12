#version 330

uniform mat4 PROJ_VIEW_MAT;
in vec4 vert;

void main() {
	gl_Position = PROJ_VIEW_MAT * vert;
}

