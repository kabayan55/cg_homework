import java.applet.AudioClip;
import java.awt.*;
import java.applet.Applet;


public class MySound extends Applet{
    
    

    public void playing(String str){
	AudioClip ac = null;
	Thread th = new Thread();
	ac = java.applet.Applet.newAudioClip(CgMain.class.getResource(str));
	
	ac.play();
	try {
	    th.sleep(300);	// １秒間停止
	} catch (InterruptedException e){} 
    }
}