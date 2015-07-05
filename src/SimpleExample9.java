import javax.media.opengl.*;
import com.jogamp.opengl.util.PMVMatrix; 
import java.awt.event.*;
import com.jogamp.opengl.math.VectorUtil;
import java.nio.FloatBuffer;

public class SimpleExample9 extends SimpleExampleBase{
  Object3D obj;
  PMVMatrix mats;
  PMVMatrix matsl;
  Shader shader;
  int uniformMat;
  int uniformMatn;
  int uniformLight;
  float t=0;
  float[] lv;
  float[] tmpmat; 

  public SimpleExample9(){
    obj = new TextureBoard(4,4,10f,10f,new Vec4(0.8f, 0.8f, 0.8f,1.0f),
                           new DotImage(512,512));
    mats = new PMVMatrix();
    matsl = new PMVMatrix();
    shader = new Shader("resource/simple.vert", "resource/bump.frag");
    addKeyListener(new simpleExampleKeyListener());
    addMouseMotionListener(new simpleExampleMouseMotionListener());
    addMouseListener(new simpleExampleMouseListener());
    lv = new float[3];
    tmpmat = new float[16]; 
  }

  public void init(GLAutoDrawable drawable){
    drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
    final GL2 gl = drawable.getGL().getGL2();
    //drawable.getGL().getGL2();
    gl.glViewport(0, 0, SCREENW, SCREENH);

    // Clear color buffer with black
    gl.glClearColor(0.7f, 0.7f, 0.7f, 1.0f);
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
    uniformMatn = gl.glGetUniformLocation(programName, "matn");
    uniformLight = gl.glGetUniformLocation(programName, "lightdir");
    gl.glUseProgram(programName);
    obj.init(gl, mats, programName);
    gl.glUseProgram(0);
  }

  public void display(GLAutoDrawable drawable){
    final GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    mats.glMatrixMode(GL2.GL_PROJECTION);
    mats.glLoadIdentity();
    mats.glFrustumf(-1f,1f,-1f,1f,1f,100f);
    mats.glMatrixMode(GL2.GL_MODELVIEW);
    mats.glLoadIdentity();
    mats.glTranslatef(0f,0f,-3.0f);
    if(t<360){
      t = t+0.4f;
    }else{
      t = 0f;
    }
    mats.glRotatef(-30,1f,0f,0f);
    mats.update();
    matsl.glMatrixMode(GL2.GL_MODELVIEW);
    matsl.glLoadIdentity();
    matsl.glRotatef(-30,1f,0f,0f);
    matsl.update();
    FloatBuffer tmp = matsl.glGetMviMatrixf();
    tmp.mark();
    tmp.get(tmpmat,0,16);
    tmp.reset();

    lv[0] = 0.6f*(float)Math.cos(Math.PI/180*t);
    lv[1] = 0.6f*(float)Math.sin(Math.PI/180*t);
    lv[2] = -1;

    float[] mvlv =VectorUtil.rowMatrixVectorMult(tmpmat, lv);

    gl.glUseProgram(shader.getID());
    gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
    gl.glUniformMatrix4fv(uniformMatn, 1, false, mats.glGetMvitMatrixf());
    gl.glUniform3fv(uniformLight, 1, mvlv, 0);
    obj.display(gl, mats);
    gl.glFlush();
    gl.glUseProgram(0);
  }

  public static void main(String[] args){
    SimpleExampleBase t = new SimpleExample9();
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
