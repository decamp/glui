#version 330

uniform mat4 PROJ_VIEW_MAT;
uniform mat3 NORM_MAT;

in vec3 inNorm;
in vec4 inTex;
in vec4 inVert;

smooth out vec4 tex;
smooth out vec3 norm;

void main() {
	tex = inTex;
	norm = normalize( NORM_MAT * inNorm );
	gl_Position = PROJ_VIEW_MAT * inVert;
}

