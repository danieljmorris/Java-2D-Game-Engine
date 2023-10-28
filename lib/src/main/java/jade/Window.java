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

public class Window {
	
	private int width, height;
	private String title;
	private long glfwWindow;
	private float r, g, b, a;
	private boolean fadeToBlack = false;
	
	private static Window window = null;
	
	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "Mario";
		r = 1;
		b = 1;
		g = 1;
		a = 1;
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
		
	}
	
	public void loop() {
		while(!glfwWindowShouldClose(glfwWindow)) {
			//poll events
			glfwPollEvents();
			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);
			if(fadeToBlack) {
				r = Math.max(r -= 0.01f, 0);
				g = Math.max(g -= 0.01f, 0);
				b = Math.max(b -= 0.01f, 0);
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
				fadeToBlack = true;
			}
			glfwSwapBuffers(glfwWindow);
		}
	}
	
}
