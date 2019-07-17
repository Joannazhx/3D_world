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

public class Avatar {
    private Point3D position;
    
    private float[] po;
    
    private TriangleMesh avatar;
    
    private Shader shader;
    private Texture AvatarTexture;
    
    public Avatar(float x,float y,float z) {
        position = new Point3D(x, y, z);
        po = new float [3];
        po[0] = x;
        po[1] = y;
        po [2] = z;
        try {
			avatar = new TriangleMesh("res/models/bunny.ply", true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void changepo(float x,float y,float z) {
    	
    }
    
    public float[] getpo() {
    	return po;
    }
    public void init(GL3 gl) {
    
        avatar.init(gl);
        
    }
    
    public void display(GL3 gl,CoordFrame3D frame,Shader shader) {
        

        // Compute the view transform
        //get the location(x,y,z) y+half height of the tree(appro 0.5)
    	//frame = CoordFrame3D.identity()
        		//.translate(po[0], po[1]-0.2f, po[2])
                // Uncomment the line below to rotate the camera
                // .rotateY(rotateY)
        		//.scale(3, 3, 3);
        //Shader.setViewMatrix(gl, view.getMatrix());
    	
    	Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
    	Shader.setColor(gl,  "lightIntensity", new Color(0.6f, 0.6f, 0.6f));
        avatar.draw(gl, frame);

  
    }
    
public void display(GL3 gl,CoordFrame3D frame,Shader shader,int i) {
        

        // Compute the view transform
        //get the location(x,y,z) y+half height of the tree(appro 0.5)
    	//frame = CoordFrame3D.identity()
        		//.translate(po[0], po[1]-0.2f, po[2])
                // Uncomment the line below to rotate the camera
                // .rotateY(rotateY)
        		//.scale(3, 3, 3);
        //Shader.setViewMatrix(gl, view.getMatrix());
    	
    	//Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
    	//Shader.setColor(gl,  "lightIntensity", new Color(0.6f, 0.6f, 0.6f));
        avatar.draw(gl, frame);

  
    }
    
    public void destroy(GL3 gl) {
        
        avatar.destroy(gl);
        
    }
}
