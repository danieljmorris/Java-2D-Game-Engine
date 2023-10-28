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
	
	private static Window window = null;
	
	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "Mario";
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
			glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			glfwSwapBuffers(glfwWindow);
		}
	}
	
}
