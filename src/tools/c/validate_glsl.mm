#import <AppKit/AppKit.h>
//#include <OpenGL/gl.h>
#include <OpenGL/gl3.h>
//#include <GLUT/glut.h>
#include <stdio.h>


// printShaderInfoLog
// From OpenGL Shading Language 3rd Edition, p215-216
// Display (hopefully) useful error messages if shader fails to compile
void printShaderInfoLog( GLint shader ) {
	int infoLogLen   = 0;
	int charsWritten = 0;
	GLchar *infoLog;
	glGetShaderiv( shader, GL_INFO_LOG_LENGTH, &infoLogLen );
 	
	if( infoLogLen > 0 ) {
		infoLog = (GLchar*)calloc( infoLogLen, sizeof(GLchar) );
		// error check for fail to allocate memory omitted
		glGetShaderInfoLog( shader, infoLogLen, &charsWritten, infoLog );
		printf( "InfoLog:\n%s\n", infoLog );
		free( infoLog );
	}
}

char *readFile( FILE* file ) {
	#define BUF_SIZE 1024
	char buffer[BUF_SIZE];
	size_t contentSize = 1; // includes NULL
	/* Preallocate space.  We could just allocate one char here, 
	but that wouldn't be efficient. */
	char *content = (char*)malloc( BUF_SIZE * sizeof(char) );
	if( content == NULL ) {
    	perror( "Failed to allocate content" );
    	exit( 1 );
	}
	content[0] = '\0';
	while( fgets( buffer, BUF_SIZE, file ) ) {
	    char *old = content;
    	contentSize += strlen( buffer );
    	content = (char*)realloc( content, contentSize );
	    if( content == NULL ) {
            perror( "Failed to reallocate content" );
        	free( old );
	        exit( 2 );
    	}
    	strcat( content, buffer );
    }

	if( ferror( stdin ) ) {
    	free( content );
    	perror( "Error reading from stdin." );
    	exit( 3 );
	}

	return content;
}


void compile( int compileNum, const char *source, int type ) {
	int shader = glCreateShader( type );
	glShaderSource( shader, 1, &source, 0 );
	glCompileShader( shader );
	//glGetShaderiv( shader, GL_COMPILE_STATES, %err );
	printf( "#### BEGIN COMPILER %d INFO LOG ####\n", compileNum );
	printShaderInfoLog( shader );
	printf( "\n#### END COMPILER %d INFO LOG ####\n\n\n", compileNum );
}


void compileFile( int compileNum, const char *path ) {
	int type = GL_FRAGMENT_SHADER;
	int len = strlen( path );
	if( len >= 5 && !strcmp( path + len - 5, ".vert" ) ) {
		type = GL_VERTEX_SHADER;
	} else if( len >= 5 && !strcmp( path + len - 5, ".geom" ) ) {
		type = GL_GEOMETRY_SHADER;
	} else if( len >= 5 && !strcmp( path + len - 5, ".frag" ) ) {
		type = GL_FRAGMENT_SHADER;
	}
	
	FILE *file = fopen( path, "r" );
	if( file == NULL ) {
		perror( "Error opening file" );
		exit( 6 );
	}
	char *source = readFile( file );
	fclose( file );
	compile( compileNum, source, type );
	free( source );
}
 

int main( int argc, char* argv[] ) {
 	NSOpenGLPixelFormatAttribute atts[] = {
        NSOpenGLPFAOpenGLProfile, NSOpenGLProfileVersion3_2Core,
        NSOpenGLPFADepthSize, 0,
        NSOpenGLPFAStencilSize, 0,
        NSOpenGLPFAAccelerated,
        0
    };
    NSOpenGLPixelFormat *format = [[[NSOpenGLPixelFormat alloc] initWithAttributes:atts] autorelease];
	NSOpenGLContext *context = [[NSOpenGLContext alloc] initWithFormat:format shareContext:nil];
	[context makeCurrentContext];
	int err = glGetError();
	if( err != 0 ) {
		perror( "Error creating OpenGL Context." );
		return 5;
	}

	int fileNum = 0;
	for( int i = 1; i < argc; i++ ) {
		if( argv[i][0] == '-' ) {
			continue;
		}
		compileFile( fileNum++, argv[i] );
	}
			
	return 0;
}
