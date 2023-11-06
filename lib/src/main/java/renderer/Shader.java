package renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {

	private int shaderProgramID;
	private String vertexSource;
	private String fragmentSource;
	private String filepath;

	public Shader(String filepath) {
		this.filepath = filepath;
		try {
			String source = new String(Files.readAllBytes(Paths.get(filepath)));
			String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

			// Find the first pattern after #type 'pattern'
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\n", index);
			String firstPattern = source.substring(index, eol).trim();

			// Find the second pattern after #type 'pattern'
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\n", index);
			String secondPattern = source.substring(index, eol).trim();

			if (firstPattern.equals("vertex")) {
				vertexSource = splitString[1];
			} else if (firstPattern.equals("fragment")) {
				fragmentSource = splitString[1];
			} else {
				throw new IOException("Unexpected token '" + firstPattern + "'");
			}

			if (secondPattern.equals("vertex")) {
				vertexSource = splitString[2];
			} else if (secondPattern.equals("fragment")) {
				fragmentSource = splitString[2];
			} else {
				throw new IOException("Unexpected token '" + secondPattern + "'");
			}
		} catch (IOException e) {
			e.printStackTrace();
			assert false : "Error: Could not open file for shader: '" + filepath + "'";
		}

		System.out.println(vertexSource);
		System.out.println(fragmentSource);

	}

	public void compile() {
		int vertexID, fragmentID;
		// compile and link shaders

		// load and compile vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);

		// pass shader source to GPU
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);

		// check for errors in compilation process
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: " + filepath + "\n\tVertex shader compilation failed.");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}

		// compile and link shaders

		// load and compile fragment shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

		// pass shader source to GPU
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);

		// check for errors in compilation process
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: " + filepath + "\n\tFragment shader compilation failed.");
			System.out.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}

		// link shaders and check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);

		// check for linking errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: " + filepath + "\n\tLinking of shaders failed.");
			System.out.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}

	public void use() {
		//bind shader program
		glUseProgram(shaderProgramID);
	}

	public void detach() {
		glUseProgram(0);
	}

}
