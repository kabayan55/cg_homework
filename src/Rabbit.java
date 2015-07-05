import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.nio.*;

public class Rabbit extends Object3D{
    private Cylinder2 house; 
    private Cylinder2 roof;
    private Cylinder2 chimney;
    private float heighthouse;
    private float heightroof;
    private float heightchimney;
    private float scaleh;
    private float tophouse;
    private int uniformMat;
    
  
    public Rabbit(float heighthouse, float heightroof, float heightchimney, float widthroof,
		 Vec4 colorhouse, Vec4 colorroof, Vec4 colorchimney){
	//public Cylinder2(int num, float radius, float height, boolean smooth,Vec4 color)
	house = new Cylinder2(4, 1.2f, heighthouse, false, colorhouse);
	//roof = new Cylinder2(3, (float)(widthroof/Math.sqrt(3)),widthroof, false, colorroof);
	roof = new Cylinder2(3, 1.4f,widthroof, false, colorroof);
	chimney = new Cylinder2(4,0.2f,heightchimney, false, colorchimney);
	
	this.heightroof = heightroof;
	tophouse = heighthouse/2f;
	this.heighthouse = heighthouse+heightroof/3.0f;
	scaleh =heightroof/(float)(widthroof*(Math.sqrt(3)/2.0f));
	this.heightchimney = heightchimney;
    }

    public void init(GL2 gl, PMVMatrix mat, int programID){
	initCommon(mat, programID);
	house.init(gl, mat, programID);
	roof.init(gl, mat, programID);
	chimney.init(gl, mat, programID);
	bindProgram(gl,programID);
	uniformMat = gl.glGetUniformLocation(programID, "mat");
	unbindProgram(gl);
    }

    public void display(GL2 gl, PMVMatrix mats){
	mats.glMatrixMode(GL2.GL_MODELVIEW);
	mats.glPushMatrix();
	
	bindProgram(gl,programID);
	
	//家
	mats.glTranslatef(0.0f,0.0f,tophouse);
	mats.glRotatef(45,0f,0f,1f);
	house.display(gl, mats);
	
	//屋根
	/*mats.glTranslatef(0.0f,0.0f,heighthouse-tophouse*2);
	mats.glScalef(1.0f,1.0f,scaleh);
	mats.glRotatef(90,0f,0f,1f);
	mats.glRotatef(-90,0f,1f,0f);
	mats.update();
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	roof.display(gl, mats);
	*/

	//えんとつ
	mats.glTranslatef(0f,0.7f,0f);
	//mats.glTranslatef(0.0f,0.0f,tophouse-heightchimney*2);
	mats.glRotatef(-45,1f,0f,0f);
	mats.glRotatef(-90,0f,1f,0f);
	mats.update();
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	chimney.display(gl, mats);

	//mats.glRotatef(-45,1f,0f,0f);
	//mats.glRotatef(-90,0f,1f,0f);
	mats.glTranslatef(0f,0.7f,0f);
	mats.update();
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	chimney.display(gl, mats);

	

	mats.glPopMatrix();
	unbindProgram(gl);

    }
}