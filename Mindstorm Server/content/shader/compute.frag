varying vec4 ex_Color;
varying vec2 ex_uv;

uniform sampler2D tex;

uniform vec4 v4_obstacle;
uniform vec4 v4_ball;
uniform vec4 v4_goal;
uniform vec4 v4_robot;

void main(void)
{ 
	vec4 color = texture2D(tex,ex_uv);

	if(color.r > 0.25 && color.g < 0.75){
		color = vec4(0.5,1.0,0.0,1.0);
	}
	gl_FragColor = color;
}