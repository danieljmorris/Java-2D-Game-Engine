package jade;

import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import components.FontRenderer;
import components.SpriteRenderer;
import renderer.Shader;
import renderer.Texture;
import util.Time;

public class LevelEditorScene extends Scene {

	private float[] vertexArray = {
		//position					//color						//UV coords
		100f,   0f, 0.0f,		1.0f, 0.0f, 0.0f, 1.0f,			1,1,			//bottom right 0
		  0f, 100f, 0.0f, 		0.0f, 1.0f, 0.0f, 1.0f,			0,0,			//top left 1
		100f, 100f, 0.0f, 		1.0f, 0.0f, 1.0f, 1.0f,			1,0,			//top right 2
		  0f,   0f, 0.0f, 		1.0f, 1.0f, 0.0f, 1.0f,			0,1,			//bottom left 3
	};
	
	//IMPORTANT: must be in COUNTER-CLOCKWISE order
	private int[] elementArray = {
			
			/* 
			 	1x		x2
			 	  
			 	  
			 	3x		x0
			 */
			
		2, 1, 0,//top right triangle
		0, 1, 3,//bottom left triangle
	};
	
	private int vaoID, vboID, eboID;
	private Shader defaultShader;
	private Texture testTexture;
	private GameObject testObj;
	private boolean firstTime = false;

	public LevelEditorScene() {
		
	}

	@Override
	public void init() {
		System.out.println("Creating test object");
		this.testObj = new GameObject("test object");
		this.testObj.addComponent(new SpriteRenderer());
		this.testObj.addComponent(new FontRenderer());
		this.addGameObjectToScene(this.testObj);
		
		this.camera = new Camera(new Vector2f(-200, -300));
		defaultShader = new Shader("assets/shaders/default.glsl");
		defaultShader.compile();
		this.testTexture = new Texture("assets/images/testImage.png");
		//Generate VAO, VBO and EBO buffer objects and send to GPU
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		//create a float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();
		
		//create VBO upload the vertex buffer
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		//create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();
		
		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
		
		//add vertex attribute pointers
		int positionsSize = 3;
		int colorSize = 4;
		int uvSize = 2;
		int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
		glEnableVertexAttribArray(2);
	}

	@Override
	public void update(float dt) {
//		camera.position.x -= dt * 5.0f;
//		camera.position.y -= dt * 5.0f;
		defaultShader.use();
		
		// upload texture to shader
		defaultShader.uploadTexture("TEXT_SAMPLER", 0);
		glActiveTexture(GL_TEXTURE0);
		testTexture.bind();
		
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());
		defaultShader.uploadFloat("uTime", Time.getTime());
		//bind the vao
		glBindVertexArray(vaoID);
		//enable vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
		
		//unbind
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glBindVertexArray(0);
		defaultShader.detach();
		if(!firstTime) {
			System.out.println("creating game object");
			GameObject go = new GameObject("game test 2");
			go.addComponent(new SpriteRenderer());
			this.addGameObjectToScene(go);
			firstTime = true;
		}
		
		for(GameObject go : this.gameObjects) {
			go.update(dt);
		}
	}

}
