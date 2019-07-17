package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.scene.MathUtil;

import com.jogamp.opengl.glu.GLU;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements MouseListener,KeyListener{

    private Terrain terrain;
    
    private float rotateX = 0;
    private float rotateY = 0;
    private float rotateXP = 0;
    private float rotateYP = 0;
    private float rotateXL = 0;
    private float rotateYL = 0;
    private float rotateYA = 0;
    private float rotateYV = 0;
    
    private Point2D myMousePoint = null;
    private static final int ROTATION_SCALE = 1;

	private static final String GL_MODELVIEW = null;
    
    private float x;
    private float y;
    private float z;
    
    private float xp;
    private float yp;
    private float zp;
    
    private float xl;
    private float yl;
    private float zl;
    
    private float xv;
    private float yv;
    private float zv;
    
    private float av_x;
    private float av_z;
    private float av_y;
    
    private float up = 0;
    private float down = 0;
    
    private int mode  = 0;
    
    private int t = 0;
    private float r = 0;
    
    private float []po;
    private float []pol;
    private float yvv;
    
    private  CoordFrame3D framep =  CoordFrame3D.identity();
    private CoordFrame3D frame =  CoordFrame3D.identity();
    private CoordFrame3D framepp = CoordFrame3D.identity();

    
    private float view_radius = 0.01f;
    
    private Matrix4 matrixpast;
    private Matrix4 matrixp;
    private Matrix4 matrixpp;
    private float[] mpast = new float[] {
            1, 0, 0, 0, // i
            0, 1, 0, 0, // j
            0, 0, 1, 0, // k
            0, 0, 0, 1  // phi
        };
    
    private Shader shader;
    private Texture texture3;
    
    private Avatar avatar;
    
    private CoordFrame3D framepast;

	private float[] poo;
	
	private int light = 0;

	private int night = 0;

	private int change = 0;
	
	private float sunr;
	
	private float sunx;
	private float suny;
	private float sunz;
	private float angle;
	private float initangle;
	private float color;
	private float intense;
	private int coi;
	private int coich;
    
    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
    	
        this.terrain = terrain;
        
   
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File("res/worlds/test1.json"));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		if(night == 1) {
			if(change == 1) {
			color = 0.6f;
			//this.setBackground(new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "lightIntensity", new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
			
			Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
			
			change = 0;}
		System.out.println(angle);
		if(angle% 360 >= 180 && coich == 0) {			
			//angle -= 1;
			coi = 1;
			coich = 1;
		}else if (angle% 360 <= 0 && coich == 1) {
			coi = 0;
			coich = 0;
			//angle += 1;
			}
		if(coi == 1) {
			angle -= 1;
		}else {
			angle += 1;
		}
		if(angle<=90) {
			if(intense < 0.995f) {
			intense += 0.005f;}
			if(color < 0.99f && coich == 0) {
				color += 0.01f;
			}else if(color > 0.01f && coich == 1 ) {
				color -= 0.01f;
			}
			
		}else {
			if(intense >0.005f) {
			intense -= 0.005f;}
			if(color > 0.01f && coich == 0) {
				color -= 0.01f;
			}else if(color < 0.99f && coich == 1) {
				color += 0.01f;
			}
		}
			float [] poo = getposition(angle,sunr,1,sunx);
					//getposition(int an,float r,int ni,float x)
			//255,255,255
			//this.setBackground(new Color(color, color, color));
			Shader.setPoint3D(gl, "lightPos", new Point3D(poo[0], poo[1], poo[2]));
			//Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
	        Shader.setColor(gl, "lightIntensity", new Color(intense, intense, intense));
	      //increase intensity
	        Shader.setColor(gl, "ambientIntensity", new Color(intense, intense, intense));
	        
	        // Set the material properties
	        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
	        
	        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
	        Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
	        Shader.setFloat(gl, "phongExp", 16f);
	        change = 0;
		}else if(night == 0) {
			if(change == 1) {
				this.setBackground(Color.WHITE);
				Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
				
				change = 0;}
				
			
			//gray      = new Color(128, 128, 128);
			//black     = new Color(0, 0, 0);
			// darkGray  = new Color(64, 64, 64);
			//float [] poo = getposition(angle,sunr,1,sunx);
			//getposition(int an,float r,int ni,float x)
	//255,255,255
			//Shader.setPoint3D(gl, "lightPos", new Point3D(poo[0], poo[1], poo[2]));
			//Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
			this.setBackground(Color.WHITE);
	        Shader.setColor(gl, "lightIntensity", Color.WHITE);
	      //increase intensity
	        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
	        
	        // Set the material properties
	        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
	        
	        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
	        Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
	        Shader.setFloat(gl, "phongExp", 16f);
	        change = 0;
		}
		Shader.setPenColor(gl, Color.WHITE);
    	
    	Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);

        
        Matrix4 matrix;
	    CoordFrame3D frame = CoordFrame3D.identity()
	    		//.translate(0,up,down)
                .rotateY(-rotateY)
                .translate(-x,-y,-z)//;
	    		.translate(0,-0.2f,-0.8f);	
	    matrix = frame.getMatrix();

	    Shader.setViewMatrix(gl, matrix);
	    if(mode == 1) {
	    	frame = CoordFrame3D.identity()
	    			//.translate(0,-0.6f,-1.5f)
	    			.rotateY(-rotateYA)
	    			.translate(-poo[0],-poo[1],-poo[2]);
	    			//.rotateY(-rotateYA);
	    	matrix = frame.getMatrix();
	    	matrixp = matrix.multiply(framepast.getMatrix());
	    	CoordFrame3D framemm = CoordFrame3D.identity().translate(0,-0.5f,-1.2f);
	    	Matrix4  matrix1 = framemm.getMatrix();
	    	matrix1 = matrix1.multiply(matrixp);
	    	//matrix = frame.getMatrix();
	    	//matrixp = matrix.multiply(framepast.getMatrix());
	    	//framepast = new CoordFrame3D(matrixp);
	    	//gl.glBindTexture(GL.GL_TEXTURE_2D, texture3.getId());
 	       	//avatar.display(gl,frame.translate(0,0.6f,1.5f));
	    	
 	       	
	        Shader.setViewMatrix(gl, matrix1);
	        
	    	 frame = CoordFrame3D.identity()
	    	       		//translate(av_x, av_y, av_z)
	    	       		//.rotateY(rotateYA)
	    	       		.translate(poo[0],poo[1],poo[2])
	    	 			.rotateY(rotateYA);
	    	       		//.translate(0,0.6f,1.5f)
	    	       		//.rotateY(-90);
	    	 matrixpp = frame.getMatrix();
	    	 //matrixpast = matrixpast.multiply(frame.getMatrix());
	    	 
	    	 frame = new CoordFrame3D(matrixpast);
	    	 //terrain.display(gl,rotateX,rotateY);
	    	 //avatar.init(gl);
	    	       gl.glBindTexture(GL.GL_TEXTURE_2D, texture3.getId());
	    	       if(night == 1) {
	    	       avatar.display(gl,frame.rotateY(-90).scale(3, 3, 3),shader,0);}else {
	    	    	   avatar.display(gl,frame.rotateY(-90).scale(3, 3, 3),shader);
	    	       }

	    }
	    

	   //Matrix4 matrix = frame.getMatrix();

       //Shader.setViewMatrix(gl, matrix);
		if(night == 1) {
			
			if(change == 1) {
			color = 0.6f;
			this.setBackground(new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "lightIntensity", new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
			
			Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
			
			change = 0;}

			float [] poo = getposition(angle,sunr,1,sunx);
					//getposition(int an,float r,int ni,float x)
			//255,255,255
			this.setBackground(new Color(color, color, color));
			Shader.setPoint3D(gl, "lightPos", new Point3D(poo[0], poo[1], poo[2]));
			//Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
	        Shader.setColor(gl, "lightIntensity", new Color(intense, intense, intense));
	      //increase intensity
	        Shader.setColor(gl, "ambientIntensity", new Color(intense, intense, intense));
	        
	        // Set the material properties
	        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
	        
	        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
	        Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
	        Shader.setFloat(gl, "phongExp", 16f);
	        change = 0;
	       
		}else if(night == 0) {
			this.setBackground(Color.WHITE);
			Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
	       
	        Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
		}else if(night == 2) {
			this.setBackground(Color.WHITE);
			Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
	       
	        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
		}
		
       terrain.display(gl,rotateX,rotateY);
       //frame = CoordFrame3D.identity().
       		//translate(av_x, av_y, av_z)
       		//.translate(pol[0],pol[1],pol[2])
       		//.rotateY(rotateYA)
       		//.scale(3, 3, 3)
       		//.translate(pol[0],pol[1],pol[2]);

       //gl.glBindTexture(GL.GL_TEXTURE_2D, texture3.getId());
       //avatar.display(gl,frame);
	}


	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		texture3 = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
		shader = new Shader(gl, "shaders/vertex_tex_phong.glsl","shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        
        // Set the lighting properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(terrain.getSunlight().getX(), terrain.getSunlight().getY(), terrain.getSunlight().getZ()));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
      //increase intensity
        Shader.setColor(gl, "ambientIntensity", new Color(0.6f, 0.6f, 0.6f));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
        Shader.setFloat(gl, "phongExp", 16f);
        
        
		CoordFrame3D frame = CoordFrame3D.identity();
		matrixpast =  frame.getMatrix();
		matrixpp = frame.getMatrix();
		CoordFrame3D framep = frame;
		framepp = frame;
		 float[] mpast = new float[16];
	        mpast = new float[] {
	                1, 0, 0, 0, // i
	                0, 1, 0, 0, // j
	                0, 0, 1, 0, // k
	                0, 0, 0, 1  // phi
	            };
	    
	        x = (terrain.getx() -1)/2;
	        z = terrain.getz() -1;
	        y = terrain.altitude((int)x,(int)z) + 1;
	        
	        xl = x;
	        zl = z;
	        yl = y;
	        
	        av_x = 0;
	        av_z = (terrain.getz() - 1);
	        av_y = terrain.altitude(av_x,av_z);
	        
		    xv = av_x + 1.2f;
		    yv = av_y + 0.6f;
		    zv = av_z;
		    
		    rotateYA = 0;
	        
		getWindow().addMouseListener(this);
		
		getWindow().addKeyListener(this);
		
		//avatar = new Avatar(x,y,z);
		avatar = new Avatar(xv,yv,zv); 
		avatar.init(gl);
		pol = new float [3];
		poo = new float [3];
		pol[0]=av_x;
		pol[1]=av_y;
		pol[2]=av_z;
		poo[0]=0;
		poo[1]=0;
		poo[2]=0;
		xl=av_x;
		yl=av_y;
		zl=av_z;
		xp=0;
		poo[1]=0;
		poo[2]=0;
		rotateYV = 0;
		yvv = av_y;

		
		framepast = CoordFrame3D.identity()
    			//.rotateY(90)
    			//.translate(0,-0.6f,-1.5f)
    			.translate(-poo[0],-poo[1],-poo[2])
    			//.rotateY(-90)
    			.rotateY(-rotateYA)
    			.translate(-av_x,-av_y,-av_z);
    			//.translate(0,-0.6f,-1.5f);
    			//.rotateY(-90);
		
		CoordFrame3D framet = CoordFrame3D.identity().
 	       		translate(av_x, av_y, av_z)
 	       		.rotateY(rotateYA)
 	       		.translate(poo[0],poo[1],poo[2]);
 	       		//.translate(0,0.6f,1.5f)
 	       		//.rotateY(-90);
		matrixpast = matrixpast.multiply(framet.getMatrix());
    	
		terrain.init(gl);
		sunx = terrain.getSunlight().getX();
		suny = terrain.getSunlight().getY();
		sunz = terrain.getSunlight().getZ();
		float rx = Math.abs(sunx) + terrain.getx()/2;
		float ry = terrain.getx()/2;
		sunr = (float) Math.sqrt(rx * rx + ry * ry);
		angle = (float) Math.acos(suny/sunr);
		intense = 0.6f;
		color = 0.6f;
		coi = 0;
		coich = 0;
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        //((GLMatrixFunc) gl).glMatrixMode(GL2.GL_MODELVIEW);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	   
		@Override
		public void mouseDragged(MouseEvent e) {
			Point2D p = new Point2D(e.getX(), e.getY());

	        if (myMousePoint != null) {
	            float dx = p.getX() - myMousePoint.getX();
	            float dy = p.getY() - myMousePoint.getY();

	            // Note: dragging in the x dir rotates about y
	            //       dragging in the y dir rotates about x
	            rotateY += dx * ROTATION_SCALE;
	            rotateX += dy * ROTATION_SCALE;

	        }
	        myMousePoint = p;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			 myMousePoint = new Point2D(e.getX(), e.getY());
		}

	    @Override
	    public void mouseClicked(MouseEvent e) { }

	    @Override
	    public void mouseEntered(MouseEvent e) { }

	    @Override
	    public void mouseExited(MouseEvent e) { }

	    @Override
	    public void mousePressed(MouseEvent e) { }

	    @Override
	    public void mouseReleased(MouseEvent e) { }

	    @Override
	    public void mouseWheelMoved(MouseEvent e) { }

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE:			
				if(mode == 0) {
					mode = 1;
					change = 1;
							
				}else {
					mode = 0;
					change = 1;
				}
				break;
			case KeyEvent.VK_LEFT:
				if(mode == 0) {
				rotateY += 2;}else {
					rotateYA = 2;
					rotateYV += 2;
					poo[0] = 0;
					poo[2] = 0;
					poo[1] = 0;
					framepast = new CoordFrame3D(matrixp);
					//System.out.println(poo[1]);
					//rotateYA = 0;
					matrixpast = matrixpast.multiply(matrixpp);
				}
				break;

			case KeyEvent.VK_RIGHT:
				if(mode == 0) {
				rotateY -= 2;}else {
					rotateYA =  -2;
					rotateYV -= 2;
					poo[0] = 0;
					poo[2] = 0;
					poo[1] = 0;
					framepast = new CoordFrame3D(matrixp);
					//System.out.println(poo[1]);
					//rotateYA = 0;
					matrixpast = matrixpast.multiply(matrixpp);
				}
			   				
				break;

			case KeyEvent.VK_DOWN:
				if(mode == 0) {
				x +=  Math.sin(Math.toRadians(rotateY))*0.1;
				z +=  Math.cos(Math.toRadians(rotateY))*0.1;
				y = terrain.altitude(x,z) + 1.5f;}
				if(mode == 1) {
					//poo[1] = terrain.altitude(pol[0],pol[2]);
					//System.out.println(pol[0] + pol[2]);
					poo[0] =  (float) (Math.sin(Math.toRadians(rotateYV))*0.1);
					poo[2] =  (float) (Math.cos(Math.toRadians(rotateYV))*0.1);
					pol[0]+=  (float) (Math.sin(Math.toRadians(rotateYV))*0.1);;
					pol[2]+=  (float) (Math.cos(Math.toRadians(rotateYV))*0.1);
					pol[1] = terrain.altitude(pol[0],pol[2]);
					poo[1] = (pol[1] - yvv);
					yvv = pol[1];
					System.out.println(pol[0] +" " + pol[1]+" " + pol[2]);
					framepast = new CoordFrame3D(matrixp);
					rotateYA = 0;
					matrixpast = matrixpast.multiply(matrixpp);
				}
				
				break;

			case KeyEvent.VK_UP:
				if(mode == 0) {
				x -=  Math.sin(Math.toRadians(rotateY))*0.1;
				z -=  Math.cos(Math.toRadians(rotateY))*0.1;
				//x -= Math.sin(rotateY)* 0.1f;
				//z -= Math.cos(rotateY)* 0.1f;
				y = terrain.altitude(x,z) + 1.5f;}
				if(mode == 1) {
					poo[1] = terrain.altitude(pol[0],pol[2]);
					//System.out.println(pol[0] +" " + pol[1]+" " + pol[2]);
					poo[0] =  -(float) (Math.sin(Math.toRadians(rotateYV))*0.1);
					poo[2] =  -(float) (Math.cos(Math.toRadians(rotateYV))*0.1);
					//System.out.println(pol[0] +" " + pol[2]);					
					pol[0]+= poo[0];
					pol[2]+= poo[2];
					pol[1] = terrain.altitude(pol[0],pol[2]);
					System.out.println(pol[0] +" " + pol[1]+" " + pol[2]);
					//System.out.println(pol[1]+" " +poo[1]);
					poo[1] = (pol[1] - poo[1]);
					framepast = new CoordFrame3D(matrixp);
					//System.out.println(poo[1]);
					rotateYA = 0;
					matrixpast = matrixpast.multiply(matrixpp);
				}
				
			
			
				break;
			case KeyEvent.VK_1:	
		        night = 1;
		        change = 1;
				break;
				
			case KeyEvent.VK_2:			
				night = 0;
				change = 1;
				break;
			case KeyEvent.VK_3:			
				night = 2;
				change = 1;
				break;

			default:
				break;
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		
		public float[] multiply(float [] p, float[] q) {

	        float[] m = new float[16];

	        for (int i = 0; i < 4; i++) {
	            for (int j = 0; j < 4; j++) {
	                m[i*4 + j] = 0;
	                for (int k = 0; k < 4; k++) {
	                   m[i*4 + j] += p[i*4 + k] * q[k*4 +j]; 
	                }
	            }
	        }

	        return m;
	    }

	private float[] getposition(float an,float r,int ni,float x) {
		float y = (float) (Math.sin(Math.toRadians(an))*r);
		float l =0;
		if(ni == 0) {
			l = (float) (r - Math.sin(Math.toRadians(an))*r);
			
		}else {
			l = (float) (r - Math.sin(Math.toRadians(an))*r);
		}
		float z = ((terrain.getx()/2)/r)*l;
		x = x + (((terrain.getx() + Math.abs(x))/2)/r)*l;
		//x = x + ((terrain.getx()/2)/r)*l;
		float []posi = new float[3];
		posi[0]= x;
		posi[1] = y;
		posi[2] = z;
		return posi;
		
	}
	
	
}
