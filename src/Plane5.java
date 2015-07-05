import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Plane5 extends Object3D{
    //黄金比*0.4
    float k = 0.4f*((1.0f+(float)Math.sqrt(5.0))/2.0f);
    
  private final float[] VertexData = new float[]{
      0f, -0.4f,    -k,     0f, -0.4f,   - k,   1f,0.078f,0.576f,1f,   0f,1f, //#0 
       0f,  0.4f,    -k,     0f,  0.4f,    -k,   1f,0.078f,0.576f,1f,   1f,1f, //#1
       0f, -0.4f,     k,     0f, -0.4f,     k,   1f,0.078f,0.576f,1f,   1f,0f, //#2
       0f,  0.4f,     k,     0f,  0.4f,     k,   1f,0.078f,0.576f,1f,  0f,0f, //#3
       -k,    0f, -0.4f,    -k,     0f, -0.4f,   1f,0.078f,0.576f,1f,  0f,1f, //#4
       -k,    0f,  0.4f,    -k,     0f,  0.4f,   1f,0.078f,0.576f,1f,  1f,0f, //#5
        k,    0f, -0.4f,     k,     0f, -0.4f,   1f,0.078f,0.576f,1f,  1f,1f, //#6
        k,    0f,  0.4f,     k,     0f,  0.4f,    1f,0.078f,0.576f,1f, 0f,0f, //#7
    -0.4f,    -k,    0f, -0.4f,     -k,    0f,    1f,0.078f,0.576f,1f, 0f,1f, //#8 
     0.4f,    -k,    0f,  0.4f,     -k,    0f,    1f,0.078f,0.576f,1f, 1f,1f, //#9
    -0.4f,     k,    0f, -0.4f,      k,    0f,    1f,0.078f,0.576f,1f, 1f,0f, //#10
     0.4f,     k,    0f,  0.4f,      k,    0f,    1f,0.078f,0.576f,1f, 0f,0f, //#11
    
 
  };//position            normal              color          texcoord
  private final int NormalOffset = Float.SIZE/8*3;
  private final int ColorOffset = Float.SIZE/8*6;
  private final int TexCoordOffset = Float.SIZE/8*10;
  private final int VertexCount = VertexData.length/12;
  private final int VertexSize = VertexData.length*Float.SIZE/8;
  private final FloatBuffer FBVertexData = FloatBuffer.wrap(VertexData);

  private final int[] ElementData = new int[]{
      0,1,6,
      1,0,4,
      2,3,5,
      3,2,7,
      
      4,5,10,
      5,4,8,
      6,7,9,
      7,6,11,

      8,9,2,
      9,8,0,
      10,11,1,
      11,10,3,

      0,6,9,
      0,8,4,
      1,4,10,
      1,11,6,

      2,5,8,
      2,9,7,
      3,7,11,
      3,10,5
  };
  private final int PolygonCount = ElementData.length/3;
  private final int ElementCount = ElementData.length;
  private final int ElementSize = ElementCount*Integer.SIZE/8;
  private final IntBuffer IBElementData = IntBuffer.wrap(ElementData);
  private int ElementBufferName;
  private int ArrayBufferName;
  private int TextureName;
  private int uniformTexture;
  
  private TextureImage img;

  public void init(GL2 gl, PMVMatrix mat, int programID){
    initCommon(mat, programID);
    int[] tmp = new int[1]; 
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize, FBVertexData, 
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

    gl.glGenBuffers(1, tmp, 0);
    ElementBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ElementSize, IBElementData, 
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

    //img = new ImageLoader("circles.png");
    img = new DotImage(512, 512);
    gl.glGenTextures(1, tmp, 0);
    TextureName = tmp[0];
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                       GL.GL_NEAREST);
    //                 GL.GL_LINEAR);

    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
                       GL.GL_NEAREST);
    //                       GL.GL_LINEAR);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, 
                       GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                       GL2.GL_CLAMP);

    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA8, img.getWidth(),
                    img.getHeight(), 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
		    img.getByteBuffer());


    uniformTexture = gl.glGetUniformLocation(programID, "texture0");
    //    gl.glUniform1i(uniformTexture, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
  }

  public void display(GL2 gl, PMVMatrix mats){
    // If this object has own special shader, bind it 
    //    bindProgram(gl, ProgramName);

    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glUniform1i(uniformTexture, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, 
                             true, 48, 0);
    gl.glVertexAttribPointer(VERTEXNORMAL, 3, GL.GL_FLOAT, 
			     true, 48, NormalOffset);
    gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT,
			     true, 48, ColorOffset);
    gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT,
                             true, 48, TexCoordOffset);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);

    gl.glEnableVertexAttribArray(VERTEXPOSITION);
    gl.glEnableVertexAttribArray(VERTEXCOLOR);
    gl.glEnableVertexAttribArray(VERTEXNORMAL);
    gl.glEnableVertexAttribArray(VERTEXTEXCOORD0);

    gl.glDrawElements(GL.GL_TRIANGLES, ElementCount, GL.GL_UNSIGNED_INT, 0);

    gl.glDisableVertexAttribArray(VERTEXPOSITION);
    gl.glDisableVertexAttribArray(VERTEXNORMAL);
    gl.glDisableVertexAttribArray(VERTEXCOLOR);
    gl.glDisableVertexAttribArray(VERTEXTEXCOORD0);

    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
 
  }
}