#version 330

uniform mat4 PROJ_VIEW_MAT;

in vec4 inColor;
in vec4 inVert;

out VertData {
	vec4 color;
} data;

void main() {
	data.color = inColor;
	gl_Position = PROJ_VIEW_MAT * inVert;
}

