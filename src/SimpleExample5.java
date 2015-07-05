import javax.media.opengl.*;
import com.jogamp.opengl.util.PMVMatrix; 
import java.awt.event.*;

public class Kabayan extends SimpleExampleBase{
  Object3D[] objs;
  PMVMatrix mats;
  Shader shader;
  Shader shader2;
  int uniformMat;
  int uniformMat2;
  int uniformLight;
  int uniformLight2;
  float t=0;

  public Kabayan(){
    objs = new Object3D[3];
    objs[0] = new Cylinder2(8,0.4f,1.5f,false, new Vec4(0.6f, 0.6f, 0.4f,1.0f));
    objs[1] = new Grass(2,2,8f,6f); 
    objs[2] = new Cylinder2(3,0.8f,1.5f,false, new Vec4(0.2f, 0.8f, 0.2f,1.0f));
    mats = new PMVMatrix();
    shader = new Shader("resource/simple.vert", "resource/simple.frag");
    shader2 = new Shader("resource/simple.vert", "resource/texture.frag");
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
    shader2.init(gl);
    int programName =shader.getID();
    gl.glBindAttribLocation(programName,Object3D.VERTEXPOSITION, "inposition");
    gl.glBindAttribLocation(programName,Object3D.VERTEXCOLOR, "incolor");
    gl.glBindAttribLocation(programName,Object3D.VERTEXNORMAL, "innormal");
    gl.glBindAttribLocation(programName,Object3D.VERTEXTEXCOORD0,"intexcoord0");
    int programName2 =shader2.getID();
    gl.glBindAttribLocation(programName2,Object3D.VERTEXPOSITION, "inposition");
    gl.glBindAttribLocation(programName2,Object3D.VERTEXCOLOR, "incolor");
    gl.glBindAttribLocation(programName2,Object3D.VERTEXNORMAL, "innormal");
    gl.glBindAttribLocation(programName2,Object3D.VERTEXTEXCOORD0,"intexcoord0");
    shader.link(gl);
    shader2.link(gl);
    uniformMat = gl.glGetUniformLocation(programName, "mat");
    uniformLight = gl.glGetUniformLocation(programName, "lightdir");
    uniformMat2 = gl.glGetUniformLocation(programName2, "mat");
    uniformLight2 = gl.glGetUniformLocation(programName2, "lightdir");
    gl.glUseProgram(programName);
    gl.glUniform3f(uniformLight, -1f, -6f, -10f);
    gl.glUseProgram(programName2);
    gl.glUniform3f(uniformLight2, -1f, -6f, -10f);
    gl.glUseProgram(0);

    objs[0].init(gl, mats, programName);
    objs[1].init(gl, mats, programName2);
    objs[2].init(gl, mats, programName);
  }

  public void display(GLAutoDrawable drawable){
    final GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    mats.glMatrixMode(GL2.GL_MODELVIEW);
    mats.glLoadIdentity();
    mats.glTranslatef(0.0f,0.0f,-3.0f);
    if(t<360){
      t = t+0.1f;
    }else{
      t = 0f;
    }
    //    mats.glRotatef(t,0f,1f,0f);
    mats.glRotatef(-50,1f,0f,0f);
    mats.glMatrixMode(GL2.GL_PROJECTION);
    mats.glLoadIdentity();
    mats.glFrustumf(-1f,1f,-1f,1f,1f,100f);


    mats.update();
    gl.glUseProgram(shader.getID());
    gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
    gl.glUseProgram(shader2.getID());
    gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());

    objs[0].display(gl, mats);
    objs[1].display(gl, mats);

    mats.glMatrixMode(GL2.GL_MODELVIEW);

    mats.glTranslatef(0.0f,0.0f,1.0f);
    mats.glScalef(0.8f,0.8f,1.2f);
    mats.glRotatef(90,0f,0f,1f);
    mats.glRotatef(-90,0f,1f,0f);
    mats.update();
    gl.glUseProgram(shader.getID());
    gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
    objs[2].display(gl, mats);


    gl.glFlush();
    gl.glUseProgram(0);
  }

  public static void main(String[] args){
    SimpleExampleBase t = new SimpleExample5();
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
