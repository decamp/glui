#version 330

uniform mat4 projViewMat;
uniform mat3 normMat;

in vec3 inNorm;
in vec4 inTex;
in vec4 inVert;

smooth out vec4 tex;
smooth out vec3 norm;

void main() {
	tex = inTex;
	norm = normalize( normMat * inNorm );
	gl_Position = projViewMat * inVert;
}

