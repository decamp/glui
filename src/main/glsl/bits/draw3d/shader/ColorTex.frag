#version 330

uniform sampler2D texUnit0;

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
	//fragColor = color * textureLod( texUnit0, tex0, mipmapLevel( tex0 ) );
	fragColor = color * clamp( texture( texUnit0, tex0.st ).a, 0.0, 1.0 );
	if( fragColor.a <= 0.0 ) {
		discard;
	}
}
