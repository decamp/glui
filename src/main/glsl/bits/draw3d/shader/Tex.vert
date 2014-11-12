#version 330

uniform mat4 projViewMat;
in vec4 inTex0;
in vec4 inVert;
smooth out vec4 tex0;

void main() {
	tex0 = inTex0;
	gl_Position = projViewMat * inVert;
}

