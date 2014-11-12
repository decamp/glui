#!/bin/bash
clang -o ../../../buildtools/validate_glsl validate_glsl.mm -Wall -O3 -framework OpenGL -framework AppKit