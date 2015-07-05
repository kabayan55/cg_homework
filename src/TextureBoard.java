import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class TextureBoard extends GridPlane{
  TextureImage tex;  

  public TextureBoard(int numx, int numy, float w, float h, Vec4 color, 
               TextureImage tex){
    super(numx, numy, w, h, color);
    this.tex = tex;
  }

  public void init(GL2 gl, PMVMatrix mat, int programID){
    initCommon(mat, programID);
    initVertex(gl);
    initTexture(gl, tex, programID);
  }
}