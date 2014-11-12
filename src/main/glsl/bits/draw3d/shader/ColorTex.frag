#version 330

uniform sampler2D TEX_UNIT0;

smooth in vec4 color;
smooth in vec4 tex0;

out vec4 fragColor;


float mipmapLevel( in vec2 tex ) {
	vec2 dx = dFdx( tex );
	vec2 dy = dFdy( tex );
	float deltaMaxSqr = max( dot( dx, dx ), dot( dy, dy ) );
	return 0.5 * log2( deltaMaxSqr );
}


void main() {
	fragColor = color * textureLod( TEX_UNIT0, tex0.st, mipmapLevel( tex0.st ) );
	if( fragColor.a <= 0.0 ) {
		discard;
	}
}
