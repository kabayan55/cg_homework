import com.jogamp.opengl.util.PMVMatrix;

public class CameraController implements MouseMotionListener, MouseListener, 
                                         KeyListener{
  volatile PMVMatrix mat;
  CameraController(){
    mat = new PMVMatrix();
    //モデルビュー行列　GL_MODELVIEW
    mat.glMatrixMode(PMVMatrix.GL_MODELVIEW);
  }

  //synchronizedはインスタンスへのアクセスをブロックする
  synchronized FloatBuffer getFloatBuffer(){
    return mat.glGetMvMatrixf();
  }

  public void keyPressed(KeyEvent e){
    int keycode = e.getKeyCode();
    System.out.print(keycode);
    switch(keycode){
    case java.awt.event.KeyEvent.VK_LEFT:
	System.out.print("a");
	break;
    
	
    
    
    }
  }

  public void keyReleased(KeyEvent e){
  }

  public void keyTyped(KeyEvent e){
  }

  public void mouseDragged(MouseEvent e){
    System.out.println("dragged:"+e.getX()+" "+e.getY());
  }
    
    

  public void mouseMoved(MouseEvent e){
    System.out.println("moved:"+e.getX()+" "+e.getY());
  }

  public void mouseClicked(MouseEvent e){
  }
  public void mouseEntered(MouseEvent e){
  }
  public void mouseExited(MouseEvent e){
  }
  public void mousePressed(MouseEvent e){
  
  }
  public void mouseReleased(MouseEvent e){
  }
  
}