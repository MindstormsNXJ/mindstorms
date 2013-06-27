uniform vec4 color;

in vec3 position;
in vec2 uv;

varying vec4 ex_Color;
varying vec2 ex_uv;

void main(void)
{
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	ex_uv = uv;
    ex_Color = color;
}