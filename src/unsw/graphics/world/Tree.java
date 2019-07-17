package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private Point3D position;
    
    private TriangleMesh tree;
    
    private Shader shader;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
        try {
			tree = new TriangleMesh("res/models/tree.ply", true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    public void init(GL3 gl) {
    
        tree.init(gl);
        
        //shader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                //"shaders/fragment_tex_phong.glsl");
     
       
        //shader.use(gl);
    }
    
    public void display(GL3 gl,CoordFrame3D frame) {
        
    	//Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        //Shader.setColor(gl, "lightIntensity", Color.WHITE);
        //Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
        
        // Set the material properties
        //Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        //Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        //Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
        //Shader.setFloat(gl, "phongExp", 16f);
        
        
        // Compute the view transform
        //get the location(x,y,z) y+half height of the tree(appro 0.5)
        CoordFrame3D treeframe = frame.translate(position.getX(), position.getY()+0.5f, position.getZ())
                // Uncomment the line below to rotate the camera
                // .rotateY(rotateY)
        		.scale(0.1f, 0.1f, 0.05f);
        //Shader.setViewMatrix(gl, view.getMatrix());

        
        tree.draw(gl, treeframe);

  
    }
    
    public void destroy(GL3 gl) {
        
        tree.destroy(gl);
        
    }

}
