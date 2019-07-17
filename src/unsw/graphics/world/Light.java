package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

/**
 * Sets up the Ambient, Diffuse, Specular and Global ambient as well as the
 * position and angle for the main directional light of the game.
 * @author BrandonSandoval
 *
 */
public class Light {
	Shader shader;
    
    // Ambient, Diffuse, Specular properties of light
    private float a, d, s;
    
    private Color c;
    // Position of light
    private float x, y, z;
    // Local viewpoint?
    private int localViewer = 0;

    public void setProperties(float a, float d, float s, Color c) {
        this.a = a;
        this.d = d;
        this.s = s;
        this.c = c;
    }
    
    public void setAngle(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setLighting(GL3 gl , boolean Night) {
        
        // Light property
        
        // Set the lighting properties (sunlight as the light source?)
        Shader.setPoint3D(gl, "lightPos", new Point3D(x, y, z));
        Shader.setColor(gl, "lightIntensity", c);
        Shader.setColor(gl, "ambientIntensity", new Color(a, a, a));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", c);
        Shader.setColor(gl, "diffuseCoeff", new Color(d, d, d));
        Shader.setColor(gl, "specularCoeff", new Color(s, s, s));
        Shader.setFloat(gl, "phongExp", 16f);
        
    }

    
}
