//ここまで動いている状態。
//太陽と月いれた

import javax.media.opengl.*;
import com.jogamp.opengl.util.PMVMatrix; 
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;
import java.io.*;
import javax.sound.sampled.*;
import java.util.*;

public class Kabayan extends SimpleExampleBase{
    Object3D[] objs;
    PMVMatrix mats;
    Shader shader;
    Shader shader2;
    Shader shader3;
    int uniformMat;
    int uniformMat2;
    int uniformMat3;
    int uniformLight;
    int uniformLight2;
    int uniformLight3;
    float t=0;
	//vaが遠い近い。
    float va=0f, vb=0f, vc=0f, vd=0f;
    float skyr, skyg, skyb;
    float sun;
    public Kabayan(){
		objs = new Object3D[7];
		objs[0] = new Cylinder2(8,0.4f,1.5f,false, new Vec4(0.6f, 0.6f, 0.4f,1.0f));
		objs[1] = new Grass(2,2,100f,100f); 
		objs[2] = new Cylinder2(3,0.8f,1.5f,false, new Vec4(1.0f, 0.412f, 0.706f,1.0f));
		objs[3] = new Tree();
		
		objs[4]= new House(1.7f, 1f, 3f, 2f, new Vec4(1f, 0.965f, 0.561f,1.0f), new Vec4(1f, 0f, 0f,1.0f), new Vec4(1f, 0.965f, 0.561f,1.0f));
		objs[5] = new Plane3();
		objs[6] = new Plane4();
		
		mats = new PMVMatrix();
		shader = new Shader("resource/simple.vert", "resource/simple.frag");
		shader2 = new Shader("resource/simple.vert", "resource/texture.frag");
		addKeyListener(new simpleExampleKeyListener());
		addMouseMotionListener(new simpleExampleMouseMotionListener());
		addMouseListener(new simpleExampleMouseListener());
	}
    //描画処理のための初期化
    public void init(GLAutoDrawable drawable){
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		final GL2 gl = drawable.getGL().getGL2();
		//drawable.getGL().getGL2()
		
		//スクリーン上のどの位置に描画するか　左, 下, 幅, 高さ
		//ビューポーとを設定する
		gl.glViewport(0, 0, SCREENW, SCREENH);
		
		
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		//6:00〜15:59まで水色
		if(5<hour && hour<16){
		    skyr=0.529f;
		    skyg=0.808f;
		    skyb=0.980f;
		}
		//16:00〜17:59までオレンジ
		else if(hour==16||hour==17){
		    skyr=1f;
		    skyg=0.647f;
		    skyb=0.310f;
		}
		//残り（夜）は黒
		else{
		    skyr=0f;
		    skyg=0f;
		    skyb=0.545f;
		}
		//背景
		gl.glClearColor(skyr, skyg, skyb, 1f);
		
		
		gl.glClearDepth(1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT,1);
		gl.glFrontFace(GL.GL_CCW); //反時計まわり
		gl.glEnable(GL.GL_CULL_FACE); //片面表示を有効
		gl.glCullFace(GL.GL_BACK); //裏面を表示しない
		
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
		objs[3].init(gl, mats, programName);
		objs[4].init(gl, mats, programName);
		objs[5].init(gl, mats, programName);
		objs[6].init(gl, mats, programName);
		
    }
    
    
    
    
    //物体を描画するときに呼び出されるメソッド
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
		mats.glRotatef(va, vb, vc, vd);
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
		mats.glRotatef(90,0f,0f,1f); //回転軸
		mats.glRotatef(-90,0f,1f,0f);
		mats.update();
		gl.glUseProgram(shader.getID());
		gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
		objs[2].display(gl, mats);
		
		//objs[3]木１本目
		mats.glTranslatef(-0.5f,1.0f, 0f);
		mats.update();
		//一番左が高さ、真ん中が左右、右が奥行き
		mats.glScalef(0.5f,0.5f,0.8f);
		//ここで向きかえられる
		mats.glRotatef(90,0f,1f,0f);
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木2本目
		//一番左が奥行き、真ん中が左右、右が高さ	
		mats.glTranslatef(0.95f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木3本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.9f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木4本目
		//一番左が奥行き、真ん中が左右、右が高さ	
		mats.glTranslatef(0.85f,1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木5本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.8f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木6本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.75f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木7本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.7f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木8本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.65f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木9本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.6f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木10本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.55f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木11本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.5f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木12本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.4f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木13本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.3f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木14本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.2f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木15本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.1f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木16本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(0.02f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木17本目	
		mats.glTranslatef(0.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木18本目	
		mats.glTranslatef(0.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木19本目	
		mats.glTranslatef(-0.02f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木20本目	
		mats.glTranslatef(-0.04f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木21本目	
		mats.glTranslatef(-0.08f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木22本目	
		mats.glTranslatef(-0.16f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木23本目	
		mats.glTranslatef(-0.32f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木24本目	
		mats.glTranslatef(-0.64f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木25本目	
		mats.glTranslatef(-1.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木26本目	
		mats.glTranslatef(-1.0f, 0.9f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木27本目	
		mats.glTranslatef(-1.0f, 0.8f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木28本目	
		mats.glTranslatef(-1.0f, 0.7f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//木29本目	
		mats.glTranslatef(-1.0f, 0.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木30本目
		mats.glTranslatef(-1.0f, 0.5f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木31本目
		mats.glTranslatef(-2.0f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木32本目
		mats.glTranslatef(-1.8f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木33本目
		mats.glTranslatef(-1.6f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木34本目
		mats.glTranslatef(-1.4f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木35本目
		mats.glTranslatef(-1.2f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木36本目
		mats.glTranslatef(-1.0f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木37本目
		mats.glTranslatef(-1.0f, -1.2f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木38本目
		mats.glTranslatef(-1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木39本目
		mats.glTranslatef(-1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//木40本目
		mats.glTranslatef(-1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//木41本目
		mats.glTranslatef(-1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木42本目
		mats.glTranslatef(-1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木43本目
		mats.glTranslatef(-1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木44本目
		mats.glTranslatef(-1.0f, -1.8f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木45本目
		mats.glTranslatef(-1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木46本目
		mats.glTranslatef(-1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木47本目
		mats.glTranslatef(-1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木48本目
		mats.glTranslatef(-1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//49木本目
		mats.glTranslatef(-1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//50木本目、一番したの木
		mats.glTranslatef(-0.5f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//51木本目
		mats.glTranslatef(0.5f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//52木本目
		mats.glTranslatef(1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木53本目
		mats.glTranslatef(1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木54本目
		mats.glTranslatef(1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木55本目
		mats.glTranslatef(1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木56本目
		mats.glTranslatef(1.0f, -2.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木57本目
		mats.glTranslatef(1.0f, -1.8f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木58本目
		mats.glTranslatef(1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木59本目
		mats.glTranslatef(1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//木60本目
		mats.glTranslatef(1.0f, -1.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木61本目
		mats.glTranslatef(1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木62本目
		mats.glTranslatef(1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木63本目
		mats.glTranslatef(1.0f, -1.4f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木64本目
		mats.glTranslatef(1.0f, -1.2f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木65本目
		mats.glTranslatef(1.0f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木66本目
		mats.glTranslatef(1.2f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木67本目
		mats.glTranslatef(1.4f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木68本目
		mats.glTranslatef(1.6f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木69本目
		mats.glTranslatef(1.8f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木70本目
		mats.glTranslatef(2.0f, -1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木71本目
		mats.glTranslatef(1.0f, 0.5f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木72本目	
		mats.glTranslatef(1.0f, 0.6f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木73本目	
		mats.glTranslatef(1.0f, 0.7f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木74本目	
		mats.glTranslatef(1.0f, 0.8f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木75本目	
		mats.glTranslatef(1.0f, 0.9f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木76本目	
		mats.glTranslatef(1.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木77本目	
		mats.glTranslatef(0.64f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木78本目	
		mats.glTranslatef(0.32f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木79本目	
		mats.glTranslatef(0.16f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木80本目	
		mats.glTranslatef(0.08f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木81本目	
		mats.glTranslatef(0.04f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木82本目	
		mats.glTranslatef(0.02f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木83本目	
		mats.glTranslatef(0.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木84本目	
		mats.glTranslatef(0.0f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木85本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.02f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木86本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.1f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木87本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.2f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木88本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.3f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木89本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.4f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木90本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.5f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木91本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.55f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木92本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.6f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木93本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.65f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木94本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.7f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木95本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.75f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木96本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.8f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木97本目
		//一番左が奥行き、真ん中が左右、右が高さ	
		mats.glTranslatef(-0.85f,1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木98本目	
		//一番左が奥行き、真ん中が左右、右が高さ
		mats.glTranslatef(-0.9f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//木99本目
		//一番左が奥行き、真ん中が左右、右が高さ	
		mats.glTranslatef(-0.95f, 1.0f, 0.0f);
		mats.update();
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		//100本目
		mats.glTranslatef(-1.0f,1.0f, 0.0f);
		mats.update();
		//一番左が高さ、真ん中が左右、右が奥行き
		//mats.glScalef(0.5f,0.5f,0.8f);
		//ここで向きかえられる
		//mats.glRotatef(90,0f,1f,0f);
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[3].display(gl, mats);
		
		
		//家
		//左が前後、真ん中が左右
		mats.glTranslatef(-2.0f,-10.0f, 0.0f);
		mats.update();
		//一番左が高さ、真ん中が左右、右が奥行き
		//mats.glScalef(0.5f,0.5f,0.8f);
		//ここで向きかえられる
		//mats.glRotatef(90,0f,1f,0f);
		mats.glRotatef(45,0f,0f,1f); 
		gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
		objs[4].display(gl, mats);
		
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		
		if(5<hour && hour<18){
			//太陽
			//Calendar now = Calendar.getInstance();
			//int hour = now.get(Calendar.HOUR_OF_DAY);
			//t=30f*(hour);
			//mats.glLoadIdentity();
			//mats.glTranslatef(-30.0f,0.0f,60.0f);
			//左が値大きくしたら左上になったよ？、真ん中が奥行き？値が大きいほど画面の左下、上の方、右が高さ手前、上の方？
			//全部負で同じ値だと真下
			mats.glTranslatef(100f,-100f,10f);
			mats.glScalef(5f,5f,5f);
			//mats.glRotatef(-90,0f,0f,1f);*/
			//mats.glRotatef(-t,0f,0f,1f);
			//mats.glTranslatef(-3.5f,0.0f,0.0f);
			//mats.glRotatef(-2*t,0f,0f,1f);
			mats.update();
			gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
			objs[5].display(gl, mats);}
		
		//月
		else{
			mats.glTranslatef(100f,-100f,10f);
			mats.glScalef(5f,5f,5f);
			//mats.glRotatef(-90,0f,0f,1f);*/
			//mats.glRotatef(-t,0f,0f,1f);
			//mats.glTranslatef(-3.5f,0.0f,0.0f);
			//mats.glRotatef(-2*t,0f,0f,1f);
			mats.update();
			gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
			objs[6].display(gl, mats);
		}
		
		
		
		gl.glFlush();
		gl.glUseProgram(0);
		
		
    }
	
    
    public static void main(String[] args){
		SimpleExampleBase t = new Kabayan();
		t.start();
    }
    
    class simpleExampleKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e){
			int keycode = e.getKeyCode();
			//System.out.print(keycode);
			if(java.awt.event.KeyEvent.VK_Q == keycode){
				System.exit(0);
			}
			if(java.awt.event.KeyEvent.VK_Z == keycode){
				va-=1f;
				
			}
			else if(java.awt.event.KeyEvent.VK_W == keycode){
				va+=1f;
			}
			//zoom
			if(java.awt.event.KeyEvent.VK_RIGHT == keycode){
				vc-=1f;
			}
			//wide
			if(java.awt.event.KeyEvent.VK_LEFT == keycode){
				vc+=1f;
			}
			
		}
		public void keyReleased(KeyEvent e){
			int keycode = e.getKeyCode();
		}
		public void keyTyped(KeyEvent e){
			int keycode = e.getKeyCode();
			//System.out.print(keycode);
			//quit
			if(java.awt.event.KeyEvent.VK_Q == keycode){
				System.exit(0);
			}
			//zoom
			if(java.awt.event.KeyEvent.VK_Z == keycode){
				va-=1f;
				
			}
			//wide
			if(java.awt.event.KeyEvent.VK_W == keycode){
				va+=1f;
			}
			//zoom
			if(java.awt.event.KeyEvent.VK_RIGHT == keycode){
				vc-=1f;
			}
			//wide
			if(java.awt.event.KeyEvent.VK_LEFT == keycode){
				vc+=1f;
			}
			
		}
		
    }
    
    class simpleExampleMouseMotionListener implements MouseMotionListener{
		public void mouseDragged(MouseEvent e){
			//System.out.println("dragged:"+e.getX()+" "+e.getY());
		}
		public void mouseMoved(MouseEvent e){
			//System.out.println("moved:"+e.getX()+" "+e.getY());
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
			// System.out.println("pressed:"+e.getX()+" "+e.getY());
		}
		public void mouseReleased(MouseEvent e){
		}
    }
}