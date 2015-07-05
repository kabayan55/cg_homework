import javax.swing.*;
import javax.swing.event.*;
import java.lang.reflect.InvocationTargetException;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import javax.sound.sampled.*;
//import com.sun.opengl.util.*;

public abstract class SimpleExampleBase implements GLEventListener {
    private JFrame frame;
    private GLCanvas canvas;
    
    //ウィンドウの最初の大きさ
    protected final int SCREENW=800, SCREENH=600;
    
    //音楽
    public AudioClip ac = null ;
    public SimpleExampleBase(){
	//ここで題名つける
	frame = new JFrame("（っ・ω・）つ≡☆≡☆≡☆");
	GLProfile profile = GLProfile.get(GLProfile.GL2);
	GLCapabilities capabilities = new GLCapabilities(profile); 
	capabilities.setSampleBuffers(true); //This is effective if supported
	capabilities.setNumSamples(4);
	canvas = new GLCanvas(capabilities);
	canvas.addGLEventListener(this);
	//閉じるボタンをクリックしたらウィンドウを隠してプログラムを終了する
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Container container = frame.getContentPane();
	//    container.setLayout(new GroupLayout
	frame.add(canvas,BorderLayout.NORTH);
	
	canvas.setPreferredSize(new Dimension(SCREENW, SCREENH));
       	
	       
	       
	//音楽入れたい
	//ac = java.applet.Applet.newAudioClip(SimpleExampleBase.class.getResource("020warmafternoon.aiff"));
	// ac = getAudioClip(SimpleExampleBase.class.getResource("020warmafternoon.aiff"));
	// ac =getAudioClip(getDocumentBase(),"020warmafternoon.aiff");
	//ac = open(SimpleExampleBase.class.getResource("020warmafternoon.aiff"));
	// ac = open(getDocumentBase(),"020warmafternoon.aiff");
	 if(ac != null){
	   ac.stop();
	   ac =  java.applet.Applet.newAudioClip(SimpleExampleBase.class.getResource("020warmafternoon.aiff"));
	   ac.loop();
	   }
	  
	
	
	//ac =  java.applet.Applet.newAudioClip(SimpleExampleBase.class.getResource("020warmafternoon.aiff"));
	//wavだとファイルタイプはあってるって言われるaiff,mp3,mp4はたぶんだめ
	/* try{
	   File audioFile = new File("020warmafternoon.wav");
	   AudioFormat format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
	   DataLine.Info info = new DataLine.Info(Clip.class, format);
	   Clip clip = (Clip)AudioSystem.getLine(info);
	   
	   clip.open(AudioSystem.getAudioInputStream(audioFile));
	   clip.start();
	   clip.drain();
	   clip.close();
	   }
	   catch(Exception e){
	   e.printStackTrace();
	   }
	*/  

	
    }
    
    
    
    

    
    //ウィンドウ操作の開始時に呼び出されるんだと思う
    public void start(){
	try {
	    java.awt.EventQueue.invokeAndWait(new Runnable(){
		    public void run(){
			initGLUI();
		    }
		}
		);
	    java.awt.EventQueue.invokeAndWait(new Runnable(){
		    public void run(){
			Animator animator = new Animator(canvas);
			animator.start();
		    }
		}
		);
	} catch(InvocationTargetException e){
	    System.out.println(e);
	    e.printStackTrace();
	} catch(InterruptedException e){
	    System.out.println(e);
	}
    }

    private void initGLUI(){
    canvas.setVisible(true);
    frame.pack();
    //フレームのサイズを変更可
    frame.setResizable(true);
    frame.setVisible(true);
    //ウィンドウを画面の中央に表示させる
    frame.setLocationRelativeTo(null);
    }
    
    
    //表示領域が変更されたときにコールされるメソッド
    //reshapeメソッドには描画する範囲や、どこから見るかということなどを記述
    public void reshape(GLAutoDrawable drawable,
                        int x, int y, 
                        int width, int height){
	
	  //ここからコピペ
	  //float ratio = (float)height / (float)width;
	
	  //glViewport(0, 0, width, height);
	
	  //gl.glMatrixMode(GL2.GL_PROJECTION);
	  //gl.glLoadIdentity();
	  //gl.glFrustum(-1.0f, 1.0f, -ratio, ratio, 5.0f, 40.0f);	
	
	  //gl.glMatrixMode(GL2.GL_MODELVIEW);
	  //gl.glLoadIdentity();	
	  //gl.glTranslatef(0.0f, 0.0f, -20.0f);		
	  //ここまでコピペ
	
	
	  
    }

    public void dispose(GLAutoDrawable drawable){
    }
    
    protected void addKeyListener(KeyListener l){
	canvas.addKeyListener(l);
    }

    protected void addMouseListener(MouseListener l){
	canvas.addMouseListener(l);
    }
    
    protected void addMouseMotionListener(MouseMotionListener l){
    canvas.addMouseMotionListener(l);
    }
    //キャンバスには有効になってるけど物体にも有効にした方がいいの？？？
}