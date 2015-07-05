//#version 120
//
// simple.frag
//
uniform mat4 matn;
uniform sampler2D texture0;
uniform vec3 lightdir;
varying vec3 normal;
varying vec4 color;
varying vec2 texcoord;

void main (void)
{
//   gl_FragColor = max(texture2D(texture0, texcoord),color.xyzw);
  float a[9];
  a[0] = texture2D(texture0, texcoord+vec2(-0.001,-0.001)).x;
  a[1] = texture2D(texture0, texcoord+vec2( 0.000,-0.001)).x;
  a[2] = texture2D(texture0, texcoord+vec2( 0.001,-0.001)).x;
  a[3] = texture2D(texture0, texcoord+vec2(-0.001, 0.000)).x;
  a[4] = texture2D(texture0, texcoord+vec2( 0.000, 0.000)).x;
  a[5] = texture2D(texture0, texcoord+vec2( 0.001, 0.000)).x;
  a[6] = texture2D(texture0, texcoord+vec2(-0.001, 0.001)).x;
  a[7] = texture2D(texture0, texcoord+vec2( 0.000, 0.001)).x;
  a[8] = texture2D(texture0, texcoord+vec2( 0.001, 0.001)).x;

  float x = (-a[0]-a[3]-a[6]+a[2]+a[5]+a[8])/2.0;
  float y = (-a[0]-a[1]-a[2]+a[6]+a[7]+a[8])/2.0;
  /*
  vec2 dfdx = dFdx(texcoord);
  vec2 dfdy = dFdy(texcoord);
  x = dfdx.x;
  y = dfdy.x;
  */
  float z = (1.0-sqrt(x*x+y*y));
  vec3 bnormal = vec3(x,y,z);
  bnormal = (matn*vec4(bnormal,1.0)).xyz;
  bnormal = normalize(bnormal);
  gl_FragColor = 
     vec4((0.9*max(dot(bnormal, -normalize(lightdir)),0.0)+0.1)*color.xyz,1.0);
//  gl_FragColor = texture2D(texture0, texcoord);
//  gl_FragColor = vec4(0.3, 0.8, 0.2, 1.0);
//  vec2 dfdx = abs(dFdx(texcoord));
//  vec2 dfdy = abs(dFdy(texcoord));
//  float d = max(max(dfdx.x,dfdx.y),max(dfdy.x,dfdy.y));
//  gl_FragColor = vec4(d*20,0,0,1);
//  gl_FragColor = vec4((0.9*max(dot(normal, normalize(lightdir)),0.0)+0.1)*color.xyz,1.0);
//  gl_FragColor = vec4(1.0,0.8,0.3,1.0);
}
