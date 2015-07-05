import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Tree extends Object3D{
  private Cylinder2 trunk; 
  private Cylinder2 leaf;
  private float heighttrunk;
  private float heightleaf;
  private float scaleh;
  private float toptrunk;
  private int uniformMat;

  public Tree(){
    trunk = new Cylinder2(8,0.4f,1.5f,false, new Vec4(0.6f, 0.6f, 0.4f,1.0f));
    //leaf = new Cylinder2(3,0.6f,1.0392f,false, new Vec4(0.2f, 0.8f, 0.2f,1.0f));
	leaf = new Cylinder2(3,0.6f,1.0392f,false, new Vec4(1.0f, 0.412f, 0.706f,1.0f));	
	heightleaf = 0.7f;
    toptrunk = 1.5f/2f;
    heighttrunk = 1.5f+heightleaf/3f;
    scaleh = heightleaf/(0.6f*1.5f);
  }
  
  public Tree(float heighttrunk, float heightleaf, float widthleaf,
              Vec4 colortrunk, Vec4 colorleaf){
    trunk = new Cylinder2(8, 0.4f, heighttrunk, false, colortrunk);
    leaf = new Cylinder2(3, (float)(widthleaf/Math.sqrt(3)),
                         widthleaf, false, colorleaf);
    this.heightleaf = heightleaf;
    toptrunk = heighttrunk/2f;
    this.heighttrunk = heighttrunk+heightleaf/3.0f;
    scaleh =heightleaf/(float)(widthleaf*(Math.sqrt(3)/2.0f));
  }

  public void init(GL2 gl, PMVMatrix mat, int programID){
    initCommon(mat, programID);
    trunk.init(gl, mat, programID);
    leaf.init(gl, mat, programID);
    bindProgram(gl,programID);
    uniformMat = gl.glGetUniformLocation(programID, "mat");
    unbindProgram(gl);
  }

  public void display(GL2 gl, PMVMatrix mats){
    mats.glMatrixMode(GL2.GL_MODELVIEW);
    mats.glPushMatrix();

    bindProgram(gl,programID);

    mats.glTranslatef(0.0f,0.0f,toptrunk);
    trunk.display(gl, mats);

    mats.glTranslatef(0.0f,0.0f,heighttrunk-toptrunk*2);
    mats.glScalef(1.0f,1.0f,scaleh);
    mats.glRotatef(90,0f,0f,1f);
    mats.glRotatef(-90,0f,1f,0f);
    mats.update();
    gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
    leaf.display(gl, mats);
    mats.glPopMatrix();
    unbindProgram(gl);
  }
}