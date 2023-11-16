package renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Texture {
	
	private String filepath;
	private int texID;
	
	public Texture(String filepath) {
		
		this.filepath = filepath;
		
		//generate texture on GPU
		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		
		// Set texture parameters
		//repeat in both directions
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		//when stretching - pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		//when shrinking - pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
		
		if(image != null) {
			if(channels.get(0) == 3) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
			} else if(channels.get(0) == 4) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			} else {
				assert false: "Error: (Texture) unknown number of channels " + channels.get(0);
			}
		} else {
			assert false: "Error: (Texture) could not load image " + filepath;
		}
		
		stbi_image_free(image);
		
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texID);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

}
