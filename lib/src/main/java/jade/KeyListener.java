package jade;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class KeyListener {
	
	private static KeyListener instance = null;
	private boolean keyPressed[] = new boolean[350];
	
	
	private  KeyListener() {
		
	}
	
	public static KeyListener get() {
		if(KeyListener.instance == null) {
			KeyListener.instance = new KeyListener();
		}
		return KeyListener.instance;
	}
	
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if(action == GLFW_PRESS) {
			get().keyPressed[key] = true;
		} else if(action == GLFW_RELEASE) {
			get().keyPressed[key] = false;
		}
	}
	
	public static boolean isKeyPressed(int keyCode) {
		return get().keyPressed[keyCode];
	}

}
