import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Cylinder extends Object3D{
  private float[] VertexData; 
  //example for one vertex
  //{ -1.0f,  1.0f,  0f,  0.0f, 0.0f,-1.0f,   0f,0f,1f,1f,   0f,1f,} 
  //  position            normal              color          texcoord
  private final int NormalOffset = Float.SIZE/8*3;
  private final int ColorOffset = Float.SIZE/8*6;
  private final int TexCoordOffset = Float.SIZE/8*10;
  private final int VertexCount;
  private final int VertexSize;
  private final FloatBuffer FBVertexData;

  private int[] ElementData; // { 0,1,2 } example for one polygon

  private final int PolygonCount;
  private final int ElementCount;
  private final int ElementSize;
  private final IntBuffer IBElementData;
  private int ElementBufferName;
  private int ArrayBufferName;
  private int TextureName;
  private int uniformTexture;
  
  private TextureImage img;

  public Cylinder(int num, float radius, float height, boolean smooth){
    int offset=0;// center of bottom
    if(smooth){
      VertexData = new float[12*((num+1)*4+2)];
    }else{
      VertexData = new float[12*((num+1)*2+2+num*4)];
    }
    VertexData[0] = 0.0f;       VertexData[1] = 0.0f;
    VertexData[2] = -height/2f; VertexData[3] = 0.0f;
    VertexData[4] = 0.0f;       VertexData[5] = -1.0f;
    VertexData[6] = 1.0f;       VertexData[7] = 1.0f;
    VertexData[8] = 1.0f;       VertexData[9] = 1.0f;       
    VertexData[10] = 0.5f;      VertexData[11] = 0.5f;
    offset=1;
    for(int i=0;i<num+1;i++){ // bottom
      int j = i+offset;
      VertexData[j*12+0] = (float)(radius*Math.cos(i*2*Math.PI/num));
      VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
      VertexData[j*12+2] = -height/2f; 
      VertexData[j*12+3] = 0.0f;
      VertexData[j*12+4] = 0.0f;
      VertexData[j*12+5] = -1.0f;
      VertexData[j*12+6] = 1.0f;
      VertexData[j*12+7] = 1.0f;
      VertexData[j*12+8] = 1.0f;
      VertexData[j*12+9] = 1.0f;
      VertexData[j*12+10] = (float)(0.5*Math.cos(i*2*Math.PI/num) +0.5f);
      VertexData[j*12+11] = (float)(0.5*Math.sin(i*2*Math.PI/num) +0.5f);
    }

    offset=(num+1)+1;
    for(int i=0;i<num+1;i++){ //top
      int j = i+offset;
      VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
      VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
      VertexData[j*12+2] = height/2f; 
      VertexData[j*12+3] = 0.0f;
      VertexData[j*12+4] = 0.0f;
      VertexData[j*12+5] = 1.0f;
      VertexData[j*12+6] = 1.0f;
      VertexData[j*12+7] = 1.0f;
      VertexData[j*12+8] = 1.0f;       
      VertexData[j*12+9] = 1.0f;
      VertexData[j*12+10] = (float)(0.5*Math.cos(i*2*Math.PI/num) +0.5f);
      VertexData[j*12+11] = (float)(0.5*Math.sin(i*2*Math.PI/num) +0.5f);
    }

    offset = (num+1)*2+1; // center of top
    int j = offset;
    VertexData[j*12  ] = 0.0f;
    VertexData[j*12+1] = 0.0f;
    VertexData[j*12+2] = height/2f; 
    VertexData[j*12+3] = 0.0f;
    VertexData[j*12+4] = 0.0f;
    VertexData[j*12+5] = 1.0f;
    VertexData[j*12+6] = 1.0f;
    VertexData[j*12+7] = 1.0f;
    VertexData[j*12+8] = 1.0f;
    VertexData[j*12+9] = 1.0f;
    VertexData[j*12+10] = 0.5f;
    VertexData[j*12+11] = 0.5f;

    if(smooth){
      offset = (num+1)*2+2;
      for(int i=0;i<num+1;i++){  // lower side
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = -height/2f; 
        VertexData[j*12+3] = (float)Math.cos(i*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin(i*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f;       
        VertexData[j*12+9] = 1.0f;       
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 1.0f;
      }
      offset = (num+1)*3+2;
      for(int i=0;i<num+1;i++){ // upper side
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = height/2f; 
        VertexData[j*12+3] = (float)Math.cos(i*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin(i*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f; 
        VertexData[j*12+9] = 1.0f;       
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 0.0f;
      }
    }else{ //non smooth
      offset = (num+1)*2+2;
      for(int i=0;i<num;i++){  // lower side
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = -height/2f; 
        VertexData[j*12+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f;
        VertexData[j*12+9] = 1.0f;
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 1.0f;
      }
      offset = (num+1)*2+2+num;
      for(int i=0;i<num;i++){  // lower side 2
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = -height/2f; 
        VertexData[j*12+3] = (float)Math.cos((i-0.5)*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin((i-0.5)*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f;       
        VertexData[j*12+9] = 1.0f;
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 1.0f;
      }
      offset = (num+1)*2+2+num*2;
      for(int i=0;i<num;i++){  // upper side
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = height/2f; 
        VertexData[j*12+3] = (float)Math.cos((i+0.5)*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin((i+0.5)*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f;
        VertexData[j*12+9] = 1.0f;
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 1.0f;
      }
      offset = (num+1)*2+2+num*3;
      for(int i=0;i<num;i++){  // upper side 2
        j = i+offset;
        VertexData[j*12  ] = (float)(radius*Math.cos(i*2*Math.PI/num));
        VertexData[j*12+1] = (float)(radius*Math.sin(i*2*Math.PI/num));
        VertexData[j*12+2] = height/2f; 
        VertexData[j*12+3] = (float)Math.cos((i-0.5)*2*Math.PI/num);
        VertexData[j*12+4] = (float)Math.sin((i-0.5)*2*Math.PI/num);
        VertexData[j*12+5] = 0.0f;
        VertexData[j*12+6] = 1.0f;
        VertexData[j*12+7] = 1.0f;
        VertexData[j*12+8] = 1.0f;       
        VertexData[j*12+9] = 1.0f;
        VertexData[j*12+10] = (i*1.0f/num);
        VertexData[j*12+11] = 1.0f;
      }
    }
    ElementData = new int[num*4*3];
    for(int i=0;i<num;i++){ //bottom
      ElementData[i*3]   = 0;
      ElementData[i*3+1] = i+2;
      ElementData[i*3+2] = i+1;
    }
    offset = num;
    for(int i=0;i<num;i++){ //top
      j = offset+i;
      ElementData[j*3]   = (num+1)*2+1;
      ElementData[j*3+1] = num+2+i;
      ElementData[j*3+2] = num+3+i;
    }
    offset = num*2;
    if(smooth){
      for(int i=0;i<num;i++){ //side
        j = offset+i*2;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*3+2+i+1;
        ElementData[j*3+2] = (num+1)*3+2+i;
        j = offset+i*2+1;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*2+2+i+1;
        ElementData[j*3+2] = (num+1)*3+2+i+1;
      }
    }else{
      for(int i=0;i<num;i++){ //side
        j = offset+i*2;
        ElementData[j*3]   = (num+1)*2+2+i;
        ElementData[j*3+1] = (num+1)*2+2+num+(i+1)%num;
        ElementData[j*3+2] = (num+1)*2+2+num*2+i;
        j = offset+i*2+1;
        ElementData[j*3]   = (num+1)*2+2+num+(i+1)%num;
        ElementData[j*3+1] = (num+1)*2+2+num*3+(i+1)%num;
        ElementData[j*3+2] = (num+1)*2+2+num*2+i;
      }
    }
    VertexCount = VertexData.length/12;
    VertexSize = VertexData.length*Float.SIZE/8;
    FBVertexData = FloatBuffer.wrap(VertexData);
    PolygonCount = ElementData.length/3;
    ElementCount = ElementData.length;
    ElementSize = ElementCount*Integer.SIZE/8;
    IBElementData = IntBuffer.wrap(ElementData);
  }


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

    /* // this configuration is for mip mapping 
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, 
                       GL2.GL_LINEAR_MIPMAP_LINEAR);
    */

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
    bindProgram(gl,programID);
    uniformTexture = gl.glGetUniformLocation(programID, "texture0");
    gl.glUniform1i(uniformTexture, 0);//set activetexture number
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    unbindProgram(gl);
  }

  public void display(GL2 gl, PMVMatrix mats){
    bindProgram(gl, programID);

    gl.glBindTexture(GL2.GL_TEXTURE_2D, TextureName);
    gl.glUniform1i(uniformTexture, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ArrayBufferName);
    gl.glVertexAttribPointer(VERTEXPOSITION, 3, GL.GL_FLOAT, 
                             false, 48, 0);
    gl.glVertexAttribPointer(VERTEXNORMAL, 3, GL.GL_FLOAT, 
			     false, 48, NormalOffset);
    gl.glVertexAttribPointer(VERTEXCOLOR, 4, GL.GL_FLOAT,
			     false, 48, ColorOffset);
    gl.glVertexAttribPointer(VERTEXTEXCOORD0, 2, GL.GL_FLOAT,
                             false, 48, TexCoordOffset);
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
}
