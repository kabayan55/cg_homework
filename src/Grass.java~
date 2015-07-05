import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Grass extends GridPlane{

  public Grass(int numx, int numy, float w, float h){
    this(numx, numy, w, h, new Vec4(1f,1f,1f,1f));
  }

  public Grass(int numx, int numy, float w, float h, Vec4 color){
    super(numx, numy, w, h, color);
  }

  public void init(GL2 gl, PMVMatrix mat, int programID){
    initCommon(mat, programID);
    initVertex(gl);
    initTexture(gl, new ImageLoader("grass512.png"), programID);
  }
}