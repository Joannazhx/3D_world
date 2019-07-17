package unsw.graphics.world;



import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain{

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private Avatar avatar;
    
    private float rotateX = 0;
    private float rotateY = 0;
    private Point2D myMousePoint = null;
    private static final int ROTATION_SCALE = 1;
    private List<TriangleMesh> meshes =  new ArrayList<>();
    private List<Point2D> TexCoords;
    
    private Texture texture1;
    private TriangleMesh triangleMesh;
    private TriangleMesh waterMesh;
    private int count = 0;
    
    private Texture texture2;
    private Texture texture3;
    private List<Texture> texture4;
    
    private Shader shader;
    private float av_x =0;
    private float av_z =0;
    private float av_y =0;
    
    private float[]po;
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }
 
    
    public float getx() {
    	return (float) width;
    }
    public float getz() {
    	return (float) depth;
    }
    
    public float getx_v() {
    	return (float) av_x;
    }
    public float getz_v() {
    	return (float) av_z;
    }
    public float gety_v() {
    	return (float) av_y;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }
    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }
    


    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
    	// TODO: Implement this
        float altitude = 0;
        int xL = (int) Math.floor(x);
        int xR = xL + 1;
        int zL = (int) Math.floor(z);
        int zH = zL + 1;
        
        //if x,z not in map list -- al=0 flat;
        if( x < 0 || x > width -1 || z < 0 || z > depth - 1  ) {
        	return altitude;
        }
        
        if(x == xL && z == zL) {
        	 //at vertice -- x&&z = int (vertice A,C)
        	altitude = (float) getGridAltitude(xL,zL);
        }else if(x == xL && z != zL){
        	//x == int(edge AC)
        	int y1 = (int) getGridAltitude(xL,zL);
        	int y2 = (int) getGridAltitude(xL,zH);
        	altitude = (float) ((y2 - y1)*(z - zL) + y1);        	
        }else if(z == zL && x!= zL) {
        	//z == int(edge AB)
        	int y1 = (int) getGridAltitude(xL,zL);
        	int y2 = (int) getGridAltitude(xR,zL);
        	altitude = (float) ((y2 - y1)*(x - xL) + y1);
        }else if(Math.abs(xR - x) - Math.abs(z - zL) > 0) {
        	//left tran
        	/*
        	xL			xR
        	zL y0  +-----+ y1
        		   |    /
        		   |. /  
        		   |/ 
        zH	C  y2  +
        	*/

        	int y0 = (int) getGridAltitude(xL,zL);
        	int y1 = (int) getGridAltitude(xR,zL);
        	int y2 = (int) getGridAltitude(xL,zH);
        	
        	float y01 = (y2 - y0)*(z -zL) + y0;
        	float y02 = (y1 - y2)*(zH - z) + y2;
        	
        	altitude = ((y02 - y01)/(zH - z)) * (x - xL) + y01;
        }else {
        	//right tran
        	/*
        	xL			xR
        	zL        + y0
		   		     /|
		   	 	   /  |
		   		 /  . |
	zH	  	y2  +-----+ y1
        		(0,0,1)  (1,0.3,1)
           */
    
        	int y0 = (int) getGridAltitude(xR,zL);
        	int y1 = (int) getGridAltitude(xR,zH);
        	int y2 = (int) getGridAltitude(xL,zH);
        	
        	float y01 = (y0 - y2)*(zH -z) + y2;
        	float y02 = (y0 - y1)*(zH - z) + y1;
        	
        	altitude = ((y01 - y02)/(z - zL)) * (xR - x) + y02;
        }
        
        
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     * @throws IOException 
     */
    public void addTree(float x, float z){
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine,this);
        roads.add(road);        
    }
    
    public void init(GL3 gl) {
    
    	//avatar = new Avatar(av_x,av_y,av_z);    	
        
        texture3 = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
        texture4 = new ArrayList<>();
        for(int kk = 1;kk <= 250;kk++) {
        	String ss = "";
        	if(kk<=9) {
        		ss = "res/textures/color/water_055_c_000" + kk+ ".jpg";
        	}else if(kk <= 99) {
        		ss = "res/textures/color/water_055_c_00" + kk+ ".jpg";
        	}else if(kk <= 999 ) {
        		ss = "res/textures/color/water_055_c_0" + kk+ ".jpg";
        	}
        	texture4.add(new Texture(gl, ss, "jpg", true));
        //texture3 = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
        	}
        float av_x = (width -1)/2;
        float av_z = (depth-1)/2;
        float av_y = altitude(av_x,av_z);
        
    	
    	for(Tree tree:trees) {
      	  tree.init(gl);
        }
        for(Road road:roads) {
        	road.init(gl);
        }
      
        makeExtrusion(gl);
        
        List<Point3D> verticesw = new ArrayList<>();
        List<Integer> indicesw = new ArrayList<>();
        List<Point2D> texCoordsw = new ArrayList<>();
        verticesw.add(new Point3D(-width/2, -0.0015f, width/2));
        verticesw.add(new Point3D(width/2, -0.0015f, width/2));
        verticesw.add(new Point3D(width/2, -0.0015f, -width/2));
        verticesw.add(new Point3D(-width/2, -0.0015f, -width/2));
        texCoordsw.add(new Point2D(0,0));
        texCoordsw.add(new Point2D(1,0));
        texCoordsw.add(new Point2D(1,1));
        texCoordsw.add(new Point2D(0,1));
        indicesw.add(1);
        indicesw.add(2);
        indicesw.add(0);

        indicesw.add(0);
        indicesw.add(2);
        indicesw.add(3);
        
        waterMesh = new TriangleMesh(verticesw,indicesw,true,texCoordsw);
        waterMesh.init(gl);
        
        count = 0;
        
        
    }   
    
    private void makeExtrusion(GL3 gl) { 
    	
    	 /**
    	  * public TriangleMesh(List<Point3D> vertices, List<Integer> indices, 
                boolean vertexNormals, List<Point2D> texCoords)
         * Create a triangle mesh with the given list of vertices, indices and 
         * texture coordinates. The third argument indicates whether to generate 
         * vertex normals. If false, no normals are generated.
         * @param vertices
         * @param indices
         * @param vertexNormals
         * @param texCoords
         */
       
        //To add textures to surfaces in on our model, we set texture coordinates for each vertex.
    	texture1 = new Texture(gl, "res/textures/grass.bmp",
                "bmp", true);
    	texture2 = new Texture(gl, "res/textures/rock.bmp",
                "bmp", true);
    	texture3 = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
    	
        List<Point3D> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Point2D> texCoords = new ArrayList<>();
        
            for (int i = 0;i < width;i++) {
           	 for(int j = 0;j < depth;j++) {
           		vertices.add(new Point3D(i, (float) getGridAltitude(i,j), j));
           		 
           		//sideTexCoords.add(new Point2D(s,0));
                //sideTexCoords.add(new Point2D(s,1));
           		texCoords.add(new Point2D(i,j));
           		 
        
           		 
           		 if(i!= width -1 && j != depth -1) {
           		 /*			 j
   	        		  *     -----
   	        		  *  i  -+*--
   	        		  *     -*---
           		  */
           		 //top-left,bottom-left,top-right
           			 indices.add(depth * i + j);
           			 indices.add(depth * i + j + 1 );
           			 indices.add(depth * (i + 1) + j);
           			 //indices.add(depth * i + j + 1 );
   	        		

   	       		 /*			 j
   		        		  *     -----
   		        		  *  i  -+*--
   		        		  *     -**--
   	       		  */
   	       		 //top-right,bottom-left,bottom-right	
   		        	indices.add(depth * i + j + 1);
   		        	indices.add(depth * (i + 1) + j + 1 );
   		        	indices.add(depth * (i + 1) + j);
   		        	//indices.add(depth * (i + 1) + j + 1 );
           		 }
           	 }
            }  
            
            //triangleMesh = new TriangleMesh(vertices,indices, true);
            //use vertices nomals
            triangleMesh = new TriangleMesh(vertices,indices,true,texCoords);
            triangleMesh.init(gl);
            //meshes.add(triangleMesh); 
            
            
        
    }
    
   
    public void display(GL3 gl,float rotateX,float rotateY) {
    	
    	//Shader.setPenColor(gl, Color.WHITE);
    	
    	//Shader.setInt(gl, "tex", 0);
        //gl.glActiveTexture(GL.GL_TEXTURE0);
        //gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
 
        
        //set camera left-right side -- (y+1,z+1)to see the whole view
        CoordFrame3D frame = CoordFrame3D.identity();
                //.translate(-x,-y,-z);
                //.rotateX(rotateX)
                //camera left/right
                //.rotateY(rotateY);
        //Shader.setViewMatrix(gl, frame.getMatrix());
        //Shader.setPenColor(gl, Color.GRAY);]
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture1.getId());
        
        triangleMesh.draw(gl,frame);
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE);
        //for (TriangleMesh mesh : meshes)
            //mesh.draw(gl, frame);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture2.getId());
        for(Tree tree:trees) {
        	tree.display(gl,frame);
        }
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture2.getId());
        for(Road road:roads) {
        	road.display(gl,frame);
        }
        if(count > 250) {
        	count -=250;
        }
        count ++;
        int num = count / 7;
        Texture tt = texture4.get(num);
        CoordFrame3D frame2 = CoordFrame3D.identity()
        		.scale(40, 40, 40);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tt.getId());
        //CoordFrame3D frame2 = CoordFrame3D.identity()
        		//.scale(40, 40, 40);
        waterMesh.draw(gl,frame2);
        //avatar.display(gl, frame);
        
    }

    



}
