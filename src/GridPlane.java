import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class GridPlane extends Object3D{
  private final float[] VertexData;
  /*
    = new float[]{
    -1.0f, -1.0f,  0f,  0.0f, 0.0f,-1.0f,   0f,0f, //#0
     1.0f, -1.0f,  0f,  0.0f, 0.0f,-1.0f,   1f,0f, //#1
     1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,   1f,1f, //#2
    -1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,   0f,1f, //#3 
  };//position            normal            texcoord
  */
  private final float[] VertexData2;
  private final int NormalOffset = Float.SIZE/8*3;
  private final int ColorOffset = 0;
  private final int TexCoordOffset = Float.SIZE/8*6;
  private final int VertexCount;// = VertexData.length/8;
  private final int VertexSize;// = VertexData.length*Float.SIZE/8;
  private final int VertexSize2;// = VertexData2.length*Float.SIZE/8;
  private final FloatBuffer FBVertexData;// = FloatBuffer.wrap(VertexData);
  private final FloatBuffer FBVertexData2;// = FloatBuffer.wrap(VertexData2);

  private final int[] ElementData;
  /*
    = new int[]{
    0,1,2, 
    0,2,3 
  };
  */
  private final int PolygonCount;// = ElementData.length/3;
  private final int ElementCount;// = ElementData.length;
  private final int ElementSize;// = ElementCount*Integer.SIZE/8;
  private final IntBuffer IBElementData;// = IntBuffer.wrap(ElementData);
  private int ElementBufferName;
  private int ArrayBufferName;
  private int ArrayBufferName2;
  private int TextureName;
  private int uniformTexture;
  
  private TextureImage img;

  public GridPlane(int numx, int numy, float w, float h){
    this(numx, numy, w, h, new Vec4(1f,1f,1f,1f));
  }

  public GridPlane(int numx, int numy, float w, float h, Vec4 color){
    VertexData = new float[8*numy*numx];
    VertexData2 = new float[4*numy*numx];
    for(int i=0;i<numy;i++){
      for(int j=0;j<numx;j++){
        int a =(i*numx+j)*8;
        int b =(i*numx+j)*4;
        VertexData[a]   = j*w/(numx-1)-w/2f;
        VertexData[a+1] = i*h/(numy-1)-h/2f;
        VertexData[a+2] = 0f;
        VertexData[a+3] = 0f;
        VertexData[a+4] = 0f;
        VertexData[a+5] = 1f;
        VertexData[a+6] = j/(float)(numx-1);
        VertexData[a+7] = i/(float)(numx-1);
        VertexData2[b  ] = color.data[0];
        VertexData2[b+1] = color.data[1];
        VertexData2[b+2] = color.data[2];
        VertexData2[b+3] = color.data[3];
      }
    }
    VertexCount = VertexData.length/8;
    VertexSize = VertexData.length*Float.SIZE/8;
    FBVertexData = FloatBuffer.wrap(VertexData);

    VertexSize2 = VertexData2.length*Float.SIZE/8;
    FBVertexData2 = FloatBuffer.wrap(VertexData2);
    
    ElementData = new int[(numx-1)*(numy-1)*6];
    for(int i=0;i<numy-1;i++){
      for(int j=0;j<numx-1;j++){
        ElementData[(i*(numx-1)+j)*6]  =(i*numx+j); 
        ElementData[(i*(numx-1)+j)*6+1]=(i*numx+j+1); 
        ElementData[(i*(numx-1)+j)*6+2]=((i+1)*numx+j); 
        ElementData[(i*(numx-1)+j)*6+3]=(i*numx+j+1); 
        ElementData[(i*(numx-1)+j)*6+4]=((i+1)*numx+j+1); 
        ElementData[(i*(numx-1)+j)*6+5]=((i+1)*numx+j); 
      }
    }
    PolygonCount = ElementData.length/3;
    ElementCount = ElementData.length;
    ElementSize = ElementCount*Integer.SIZE/8;
    IBElementData = IntBuffer.wrap(ElementData);
  }

  public void init(GL2 gl, PMVMatrix mat, int programID){
    initCommon(mat, programID);
    initVertex(gl);
    initTexture(gl, new DotImage(512, 512), programID);
    //    initTexture(new ImageLoader("circles.png"), programID);
  }

  public void display(GL2 gl, PMVMatrix mats){
    bindProgram(gl, programID);

    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glUniform1i(uniformTexture, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, 
                             false, 32, 0);
    gl.glVertexAttribPointer(VERTEXNORMAL, 3, GL.GL_FLOAT, 
			     false, 32, NormalOffset);
    gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT,
                             false, 32, TexCoordOffset);

    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName2);
    gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT,
			     false, 16, ColorOffset);

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
    unbindProgram(gl);

    /* // drawing this object without shader 
    gl.glUseProgram(0);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
    gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
    gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
    gl.glVertexPointer(3, GL.GL_FLOAT, 48, 0);
    gl.glNormalPointer(GL.GL_FLOAT, 48, NormalOffset);
    gl.glColorPointer(4, GL.GL_FLOAT, 48, ColorOffset);
    gl.glTexCoordPointer(2, GL.GL_FLOAT, 48, TexCoordOffset);
    gl.glDrawElements(GL.GL_TRIANGLES, ElementCount, GL.GL_UNSIGNED_INT, 0);
    //gl.glDrawArrays(GL.GL_TRIANGLES,0,3);
    */

    /* // drawing a polygon by the most traditional way
    gl.glUseProgram(0);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glBegin(GL2.GL_TRIANGLES);
    gl.glTexCoord2f(0,0);
    gl.glColor3f(1f,0f,1f);
    gl.glVertex3f(-0.5f,-0.5f,-1f);
    gl.glTexCoord2f(0,1);
    gl.glColor3f(1f,1f,0f);
    gl.glVertex3f(0.5f,-0.5f,-1f);
    gl.glTexCoord2f(1,1);
    gl.glColor3f(0f,1f,1f);
    gl.glVertex3f(0.5f,0.5f,-1f);
    gl.glEnd();
    */
  }

  protected void initVertex(GL2 gl){
    int[] tmp = new int[1]; 
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize, FBVertexData, 
                    GL.GL_STATIC_DRAW);
    gl.glGenBuffers(1, tmp, 0);
    ArrayBufferName2 = tmp[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName2);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, VertexSize2, FBVertexData2, 
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

    gl.glGenBuffers(1, tmp, 0);
    ElementBufferName = tmp[0];
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ElementBufferName);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, ElementSize, IBElementData, 
                    GL.GL_STATIC_DRAW);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
  }

  protected void initTexture(GL2 gl ,TextureImage img, int programID){
    int[] tmp = new int[1];
    gl.glGenTextures(1, tmp, 0);
    TextureName = tmp[0];
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
    //                 GL.GL_NEAREST);
                     GL.GL_LINEAR);

    /* // this configuration is for mip mapping 
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, 
                       GL2.GL_LINEAR_MIPMAP_LINEAR);
    */

    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
    //                 GL.GL_NEAREST);
                       GL.GL_LINEAR);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, 
                       GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
                       GL2.GL_CLAMP);

    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA8, img.getWidth(),
                    img.getHeight(), 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
		    img.getByteBuffer());

    /* // this configuration is for mip mapping 
    int level=0;
    for(int w=img.getWidth();0<w;w = w/2){
      gl.glTexImage2D(GL2.GL_TEXTURE_2D, level, GL.GL_RGBA8, 
                      img.getWidth()>>level, img.getHeight()>>level,
                      0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, 
                      img.getByteBufferOfLevel(level));
      level++;
    }
    */
    bindProgram(gl, programID);
    uniformTexture = gl.glGetUniformLocation(programID, "texture0");
    gl.glUniform1i(uniformTexture, 0);
    unbindProgram(gl);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
  }
}
