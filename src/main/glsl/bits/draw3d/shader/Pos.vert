#version 330

uniform mat4 projViewMat;
in vec4 vert;

void main() {
	gl_Position = projViewMat * vert;
}

