import javax.media.opengl.*;
import com.jogamp.opengl.util.PMVMatrix; 
import java.awt.event.*;

public class SimpleExample8 extends SimpleExampleBase{
  Object3D obj;
  PMVMatrix mats;
  Shader shader;
  int uniformMat;
  int uniformLight;
  int uniformPhase;
  int uniformWavelength;
  int uniformAmplitude;
  float t=0;

  public SimpleExample8(){
    obj = new GridPlane(20,300,4.6f,10.8f,new Vec4(0.1f, 0.1f, 0.8f,1.0f));
    mats = new PMVMatrix();
    shader = new Shader("resource/wave.vert", "resource/spot.frag");
    addKeyListener(new simpleExampleKeyListener());
    addMouseMotionListener(new simpleExampleMouseMotionListener());
    addMouseListener(new simpleExampleMouseListener());
  }

  public void init(GLAutoDrawable drawable){
    drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
    final GL2 gl = drawable.getGL().getGL2();
    //drawable.getGL().getGL2();
    gl.glViewport(0, 0, SCREENW, SCREENH);

    // Clear color buffer with black
    gl.glClearColor(1.0f, 0.5f, 1.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT,1);
    gl.glFrontFace(GL.GL_CCW);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glCullFace(GL.GL_BACK);

    shader.init(gl);
    int programName =shader.getID();
    gl.glBindAttribLocation(programName,Object3D.VERTEXPOSITION, "inposition");
    gl.glBindAttribLocation(programName,Object3D.VERTEXCOLOR, "incolor");
    gl.glBindAttribLocation(programName,Object3D.VERTEXNORMAL, "innormal");
    gl.glBindAttribLocation(programName,Object3D.VERTEXTEXCOORD0,"intexcoord0");
    shader.link(gl);
    uniformMat = gl.glGetUniformLocation(programName, "mat");
    uniformLight = gl.glGetUniformLocation(programName, "lightdir");
    uniformPhase = gl.glGetUniformLocation(programName, "phase");
    uniformWavelength = gl.glGetUniformLocation(programName, "wavelength");
    uniformAmplitude = gl.glGetUniformLocation(programName, "amplitude");
    gl.glUseProgram(programName);
    gl.glUniform3f(uniformLight, 0f, 100f, -3f);
    gl.glUniform1f(uniformWavelength,1.2f);
    gl.glUniform1f(uniformAmplitude, 0.14f);
    obj.init(gl, mats, programName);
    gl.glUseProgram(0);
  }

  public void display(GLAutoDrawable drawable){
    final GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    mats.glMatrixMode(GL2.GL_MODELVIEW);
    mats.glLoadIdentity();
    mats.glTranslatef(0f,0f,-3.0f);
    if(t<360){
      t = t+0.1f;
    }else{
      t = 0f;
    }
    //    mats.glRotatef(t,0f,1f,0f);
    mats.glRotatef(-60,1f,0f,0f);
    mats.glMatrixMode(GL2.GL_PROJECTION);
    mats.glLoadIdentity();
    mats.glFrustumf(-1f,1f,-1f,1f,1f,100f);
    mats.update();
    gl.glUseProgram(shader.getID());
    gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
    gl.glUniform1f(uniformPhase, t*0.09f);

    obj.display(gl, mats);
    gl.glFlush();
    gl.glUseProgram(0);
  }

  public static void main(String[] args){
    SimpleExampleBase t = new SimpleExample8();
    t.start();
  }

  class simpleExampleKeyListener implements KeyListener{
    public void keyPressed(KeyEvent e){
      int keycode = e.getKeyCode();
      System.out.print(keycode);
      if(java.awt.event.KeyEvent.VK_LEFT == keycode){
        System.out.print("a");
      }
    }
    public void keyReleased(KeyEvent e){
    }
    public void keyTyped(KeyEvent e){
    }
  }

  class simpleExampleMouseMotionListener implements MouseMotionListener{
    public void mouseDragged(MouseEvent e){
      System.out.println("dragged:"+e.getX()+" "+e.getY());
    }
    public void mouseMoved(MouseEvent e){
      System.out.println("moved:"+e.getX()+" "+e.getY());
    }
  }

  class simpleExampleMouseListener implements MouseListener{
    public void mouseClicked(MouseEvent e){
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mousePressed(MouseEvent e){
      System.out.println("pressed:"+e.getX()+" "+e.getY());
    }
    public void mouseReleased(MouseEvent e){
    }
  }
}
