#version 330

layout (lines) in;
layout (triangle_strip, max_vertices = 4) out;

uniform float LINE_WIDTH;
uniform vec4 VIEWPORT;

in VertData {
	vec4 color;
} data[];

smooth out vec4 color;

void main() {
	 vec4 na = gl_in[0].gl_Position;
	 vec4 nb = gl_in[1].gl_Position;
	// Note that multiplying the NDU coordinates (-1 to 1) by viewport 
	// will give us domain of (-w, -h, w, h ), NOT ( 0, 0, w, h ).
	vec4 scaleA = vec4( VIEWPORT.zw / na.w, 1.0, 1.0 );
	vec4 scaleB = vec4( VIEWPORT.zw / nb.w, 1.0, 1.0 );
	vec4 a = na * scaleA;
	vec4 b = nb * scaleB;
	// Because our domain is twice is big, we're going to offset the line
	// by 'lineWidth' instead of '0.5 * lineWidth'.
	vec4 dy = normalize( vec4( b.y - a.y, a.x - b.x, 0.0, 0.0 ) ) * LINE_WIDTH;

	color = data[0].color;
	gl_Position = ( a - dy ) / scaleA;
	EmitVertex();
	color = data[0].color;
	gl_Position = ( a + dy ) / scaleA;
	EmitVertex();
	color = data[0].color;
	gl_Position = ( b - dy ) / scaleB;
	EmitVertex();
	color = data[0].color;
	gl_Position = ( b + dy ) / scaleB;
	EmitVertex();
	EndPrimitive();
}
