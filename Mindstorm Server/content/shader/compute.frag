varying vec4 ex_Color;
varying vec2 ex_uv;

uniform sampler2D tex;

uniform vec4 v4_obstacle;
uniform vec4 v4_ball;
uniform vec4 v4_goal;
uniform vec4 v4_robot;

bool isRelativeEqual(vec4 area_color, vec4 v4){
	vec3 min = v4.rgb - v4.a;
	vec3 max = v4.rgb + v4.a;
	return (all(greaterThanEqual(area_color.rgb, min)) && all(lessThanEqual(area_color.rgb, max)));
}

void main(void)
{ 
	vec4 v4_obstacle_final = vec4(1.0,0.25,0.0,1.0);
	vec4 v4_ball_final = vec4(1.0,1.0,0.0,1.0);
	vec4 v4_goal_final = vec4(0.25,1.0,0.0,1.0);
	vec4 v4_robot_final = vec4(1.0,1.0,1.0,1.0);

	vec4 area_color = texture2D(tex,ex_uv);

	// Obstacle Check
	if(isRelativeEqual(area_color, v4_obstacle)){
		gl_FragColor = v4_obstacle_final;
		return;
	}

	// Ball Check
	if(isRelativeEqual(area_color, v4_ball)){
		gl_FragColor = v4_ball_final;
		return;
	}

	// Goal Check
	if(isRelativeEqual(area_color, v4_goal)){
		gl_FragColor = v4_goal_final;
		return;
	}

	// Robot Check
	if(isRelativeEqual(area_color, v4_robot)){
		gl_FragColor = v4_robot_final;
		return;
	}
	
	// if we are here we had nothig detected
	float m = (area_color.r + area_color.g + area_color.b) / 3.0;
	gl_FragColor = vec4(m,m,m,1.0);
	
}