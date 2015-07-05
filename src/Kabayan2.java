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
    int uniformPhase;
    int uniformWavelength;
    int uniformAmplitude;
    float t=0;
    //vaが遠い近い。
    float va=0f, vb=0f, vc=0f, vd=0f;
    float skyr, skyg, skyb;
    float sun;

    public Kabayan(){
		objs = new Object3D[7];
		//大きな木の下
		//public Cylinder2(int num, float radius, float height, boolean smooth,Vec4 color)
		objs[0] = new Cylinder2(8,0.4f,1.5f,false, new Vec4(0.6f, 0.6f, 0.4f,1.0f));
		//草
		objs[1] = new Grass(2,2,100f,100f); 
		//大きな木の上
		objs[2] = new Cylinder2(3,0.8f,1.5f,false, new Vec4(1.0f, 0.412f, 0.706f,1.0f));
		//小さな木
		objs[3] = new Tree();
		//家
		//public House(float heighthouse, float heightroof, float heightchimney, float widthroof,
		 //Vec4 colorhouse, Vec4 colorroof, Vec4 colorchimney)
		//widthroofは奥行き
		objs[4] = new House(1.7f, 1f, 3f, 2f, new Vec4(1f, 0.965f, 0.561f,1.0f), new Vec4(1f, 0f, 0f,1.0f), new Vec4(1f, 0.965f, 0.561f,1.0f));

		//川
		//objs[5] = new GridPlane(20,300,4.6f,10.8f,new Vec4(0.1f, 0.1f, 0.8f,1.0f));
		

		//太陽
		//public Cylinder2(int num, float radius, float height, boolean smooth,Vec4 color)
		//objs[6] = new Cylinder2(32,0.3f,0.1f,false, new Vec4(1.0f, 0.0f, 0.0f,sun));


		mats = new PMVMatrix();
		//ここでシェーダーの選べるプログラムネイムでやってそっちを物体がよぶ
		//simple.vert
		//普通のとき
		shader = new Shader("resource/simple.vert", "resource/simple.frag");
		//テクスチャ使うときはこれ
		shader2 = new Shader("resource/simple.vert", "resource/texture.frag");//黒いうにゃにゃ
		//波
		shader3 = new Shader("resource/wave.vert", "resource/spot.frag");

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

		
		//時間によって背景色を
		Calendar now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		//6:00〜15:59まで水色
		if(5<hour && hour<16){
		    skyr=0.529f;
		    skyg=0.808f;
		    skyb=0.980f;
		    sun=1f;
		}
		//16:00〜17:59までオレンジ
		else if(hour==16||hour==17){
		    skyr=1f;
		    skyg=0.647f;
		    skyb=0.310f;
		    sun=0f;
		}
		//残り（夜）は黒
		else{
		    skyr=0.282f;
		    skyg=0.237f;
		    skyb=0.545f;
		    sun=0f;
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
		//shader3.init(gl);
		
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
		//int programName3 =shader3.getID();
		//gl.glBindAttribLocation(programName3,Object3D.VERTEXPOSITION, "inposition");
		//gl.glBindAttribLocation(programName3,Object3D.VERTEXCOLOR, "incolor");
		//gl.glBindAttribLocation(programName3,Object3D.VERTEXNORMAL, "innormal");
		//gl.glBindAttribLocation(programName3,Object3D.VERTEXTEXCOORD0,"intexcoord0");
		
	
		shader.link(gl);
		shader2.link(gl);
		//shader3.link(gl);
	

		uniformMat = gl.glGetUniformLocation(programName, "mat");
		uniformLight = gl.glGetUniformLocation(programName, "lightdir");
		
		uniformMat2 = gl.glGetUniformLocation(programName2, "mat");
		uniformLight2 = gl.glGetUniformLocation(programName2, "lightdir");

		//ここから波
		//uniformMat3 = gl.glGetUniformLocation(programName3, "mat");
		//uniformLight3 = gl.glGetUniformLocation(programName3, "lightdir");
		//uniformPhase = gl.glGetUniformLocation(programName3, "phase");
		//uniformWavelength = gl.glGetUniformLocation(programName3, "wavelength");
		//uniformAmplitude = gl.glGetUniformLocation(programName3, "amplitude");
		//ここまで波

		
		gl.glUseProgram(programName);
		gl.glUniform3f(uniformLight, 1f, 60f, 100f);
		gl.glUseProgram(programName2);
		gl.glUniform3f(uniformLight2, -1f, -6f, -10f);
		//これは増やしても意味ないの？
		//gl.glUseProgram(programName3);
		//gl.glUniform3f(uniformLight, -1f, -6f, -10f);
		//gl.glUniform1f(uniformWavelength,1.2f);
		//gl.glUniform1f(uniformAmplitude, 0.14f);
		
		gl.glUseProgram(0);
		
		objs[0].init(gl, mats, programName);
		objs[1].init(gl, mats, programName2);
		objs[2].init(gl, mats, programName);
		objs[3].init(gl, mats, programName);
		objs[4].init(gl, mats, programName);
		//objs[5].init(gl, mats, programName3);
		//objs[6].init(gl, mats, programName10);
		
		/*try{
		    Sound.singleton.loadSound(new File("1.au"));
		}
		catch(UnsupportedAudioFileException e){
		    e.printStackTrace();
		}
		catch(IOException e){
		    e.printStackTrace();
		}
		catch(LineUnavailableException e){
		    e.printStackTrace();
		    }*/
		

    }
    
    
    
    
    //物体を描画するときに呼び出されるメソッド
    public void display(GLAutoDrawable drawable){
	final GL2 gl = drawable.getGL().getGL2();
	//ウィンドウをクリアする
	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	//modelview matrixは、ある点のオブジェクト座標系における座標値を、カメラ座標値へ変換する際に、オブジェクト座標系における座標値に対して左から乗算される行列
	mats.glMatrixMode(GL2.GL_PROJECTION);
	//幾何変換のための行列を初期化する
	mats.glLoadIdentity();
	//とおく、ちかく。右の値を大きく（負）すると視界が広がる
	//mats.glTranslatef(0f,6f,-6f);
       	mats.glTranslatef(0.0f,0.0f,-4.5f);
	//向き。左が高さ。
	//mats.glRotatef(60.0f,0.0f,0.0f,0.0f);
	//mats.glRotatef(60.0f,0.0f,0.0f,0.0f);	
	//mats.glRotatef(80.0f,0.0f,0.0f,0.0f);
	//mats.glRotatef(80.0f,0.0f,0.0f,0.0f);の値のところをfloatでおいて、keyの中で値かえる
	mats.glRotatef(va, vb, vc, vd);	
	


	//時間によって向きを
	if(t<360){
	    t = t+0.1f;
	}else{
	    t = 0f;
	    }
	//mats.glRotatef(t,0f,1f,0f);


	//むき
	mats.glRotatef(-50,1f,0f,0f);


	//木下
	mats.update();
	gl.glUseProgram(shader.getID());
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	objs[0].display(gl, mats);
	//草
	mats.update();
	gl.glUseProgram(shader2.getID());
	gl.glUniformMatrix4fv(uniformMat2, 4, false, mats.glGetPMvMvitMatrixf());
	objs[1].display(gl, mats);
	mats.glMatrixMode(GL2.GL_MODELVIEW);//    デル変換
	
	mats.update();
	gl.glUseProgram(shader.getID());
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());



	//objs[2]木上
	//高さが１なのは、木の上の部分は木の下の部分の上にのるから
	//左が左右、真ん中が前後、右が高さ
	mats.glTranslatef(0.0f,0.0f,1.0f);
	mats.glScalef(0.8f,0.8f,1.2f);
	//ここで向きかえられる
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


	//太陽
	/*Calendar now = Calendar.getInstance();
	int hour = now.get(Calendar.HOUR_OF_DAY);
	t=30f*(hour+90);*/
	//mats.glLoadIdentity();
	//mats.glTranslatef(-30.0f,0.0f,60.0f);
	//左が値大きくしたら左上になったよ？、真ん中が奥行き？値が大きいほど画面の左下、上の方、右が高さ手前、上の方？
	//全部負で同じ値だと真下
	/*mats.glTranslatef(100f,100f,100f);
	mats.glScalef(50f,50f,50f);
	mats.glRotatef(-90,0f,0f,1f);*/
	//mats.glRotatef(-t,0f,0f,1f);
	//mats.glTranslatef(-3.5f,0.0f,0.0f);
	//mats.glRotatef(-2*t,0f,0f,1f);
	/*mats.update();
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	objs[6].display(gl, mats);*/

	
	//川
	/*if(t<360){
	    t = t+0.1f;
	}else{
	    t = 0f;
	    }*/
	//mats.glRotatef(-60,1f,0f,0f);
	//mats.glTranslatef(10f,10.0f,0.5f);
	//mats.glMatrixMode(GL2.GL_PROJECTION);
	//mats.glLoadIdentity();
	//mats.glFrustumf(-1f,1f,-1f,1f,1f,100f);
	/*mats.update();
	gl.glUseProgram(shader3.getID());
	gl.glUniformMatrix4fv(uniformMat, 4, false, mats.glGetPMvMvitMatrixf());
	gl.glUniform1f(uniformPhase, t*0.09f);
	
	objs[5].display(gl, mats);
	*/



	gl.glFlush();
	gl.glUseProgram(0);	
    }
    
    
    public static void main(String[] args){
	SimpleExampleBase t = new Kabayan();
	t.start();
    }

    class simpleExampleKeyListener implements KeyListener{
	//キーを推したとき（主に長押ししたとき）
	//keyPressedとkeyTypedに同じの入れておいた方がいいかも
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
	
	//キーから手を離したとき（主に長押しを終了したとき）
	public void keyReleased(KeyEvent e){
	    int keycode = e.getKeyCode();
	    //System.out.print(keycode);
	}
	//キーをタイプしたとき（主に一瞬だけキーが押されたとき）
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
	    // System.out.println("dragged:"+e.getX()+" "+e.getY());
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
	    System.out.println("pressed:"+e.getX()+" "+e.getY());
	}
	public void mouseReleased(MouseEvent e){
	}
    }
}