package jade;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import util.Time;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
	
	private int width, height;
	private String title;
	private long glfwWindow;
	public float r, g, b, a;
	private boolean fadeToBlack = false;
	
	private static Window window = null;
	
	private static Scene currentScene = null;
	
	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "Mario";
		r = 1;
		b = 1;
		g = 1;
		a = 1;
	}
	
	public static void changeScene(int newScene) {
		switch(newScene) {
		case 0:
			currentScene = new LevelEditorScene();
			currentScene.init();
			break;
		case 1:
			currentScene = new LevelScene();
			currentScene.init();
			break;
		default:
			assert false : "unknown scene " + newScene;
			break;
		}
	}

	public static Window get() {
		if(Window.window == null) {
			Window.window = new Window();
		}
		return Window.window;
	}
	
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion());
		
		init();
		loop();
		
		//free the memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		//terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		
	}
	
	public void init() {
		//error callback
		GLFWErrorCallback.createPrint(System.err).set();
		//initialize GLFW
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		} 
		//configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		//create window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if(glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create glfw window");
		}
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		//make OpenGl context current
		glfwMakeContextCurrent(glfwWindow);
		//enable v-sync
		glfwSwapInterval(1);
		//show window
		glfwShowWindow(glfwWindow);
		
		GL.createCapabilities();
		
		Window.changeScene(0);
		
	}
	
	public void loop() {
		float beginTime = Time.getTime();
		float endTime;
		float dt = -1.0f;
		
		while(!glfwWindowShouldClose(glfwWindow)) {
			//poll events
			glfwPollEvents();
			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);
			
			if(dt>=0) {
				currentScene.update(dt);
			}
			
			glfwSwapBuffers(glfwWindow);
			
			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
	
}
