uniform vec4 color;

varying vec4 ex_Color;

void main(void)
{
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    ex_Color = color;
}