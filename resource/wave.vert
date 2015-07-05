//#version 120
//
// wave.vert
//
//invariant gl_Position; // invariant out gl_Position; //for #version 130
attribute vec3 inposition;//in vec3 position;          //for #version 130
attribute vec4 incolor;
attribute vec3 innormal;
attribute vec2 intexcoord0;
varying vec3 normal;
varying vec4 color;
varying vec2 texcoord;
varying vec3 pos;
uniform mat4 mat[4];
uniform float phase;
uniform float wavelength;
uniform float amplitude;

void main(void)
{
  float pi=3.141592653589793238462643383279;
  vec3 tpos = inposition;
  tpos.z = amplitude*sin(2.0*pi*inposition.y/wavelength+phase); 
  float c = cos(2.0*pi*inposition.y/wavelength+phase);
  float s = c/(abs(c)+0.000001); 
  c = abs(c);
  vec3 nml = vec3(0,-s,1.0/(amplitude*2.0*pi/wavelength*c+0.000001));
  nml = normalize(nml);
  normal = (mat[3]*vec4(nml,1.0)).xyz;
  color = incolor;
  texcoord = intexcoord0;
  gl_Position = mat[0]*mat[1]*vec4(tpos, 1.0);
//  gl_Position = vec4(inposition, 1.0);
  pos = gl_Position.xyz;
}

