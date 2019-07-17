package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;

import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    
    private TriangleMesh roadMesh;
    private Texture RoadTexture;

       
    private TriangleMesh roadw;
    private Terrain terrain;
    
    private List<Point3D> mySpine;
    private final double SLICES = 1000; //smooth road 
    
    private static final float ALTITUDE_OFFSET = 0.001f; //increase z (collosion)
    
    private List<Point2D> textCoords = new ArrayList<>();
	private List<Point3D> vertices = new ArrayList<>();
	private List<Integer> indices = new ArrayList<>();
	
	private List<Point3D> myCrossSection;
    
    
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine,Terrain t) {
        this.width = width;
        this.points = spine;
        terrain = t;
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    public Point2D tangent(float t) {
    	int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        
        // figure out my direction vector
        float x = bT(0, t) * (p1.getX() - p0.getX()) + bT(1, t) * (p2.getX() - p1.getX()) + bT(2, t) * (p3.getX() - p2.getX());
        float y = bT(0, t) * (p1.getY() - p0.getY()) + bT(1, t) * (p2.getY() - p1.getY()) + bT(2, t) * (p3.getY() - p2.getY());
       
    	return new Point2D(x,y);
    }
    
    private float bT(int i, float t) {
      	 switch(i) {
   	         case 0:
   	             return (float) Math.pow(1-t, 2);
   	
   	         case 1:
   	             return 2 * (1-t) * t;
   	             
   	         case 2:
   	             return (float) Math.pow(t, 2);
           }
           
           throw new IllegalArgumentException("" + i);
      }
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    private void makeCurvedSpine() {
    	mySpine = new ArrayList<Point3D>();
    	for(float i = 0; i <(float)this.size(); i+=0.002f) {
    		Point2D slice = point((float)(i));
    		System.out.println(slice.getX());
    		System.out.println(slice.getY());
    		System.out.println(terrain.altitude(slice.getX(),slice.getY()));
    		mySpine.add(new Point3D(slice.getX(),
    				(float)(terrain.altitude(slice.getX(),slice.getY())+0.001f),
    				slice.getY()));
    		//System.out.println(slice);
    	}
    }
    
    // Transform the points in the cross-section using the Frenet frame and add them to the vertex list.
    
    private void addPoints(List<Point3D> crossSection, List<Point3D> vertices,
    		Point3D pPrev, Point3D pCurr, Point3D pNext) {

        // compute the Frenet frame as an affine matrix
        double[][] m = new double[4][4];
        
        // phi = pCurr        
        m[0][3] = pCurr.getX();
        m[1][3] = pCurr.getY();
        m[2][3] = pCurr.getZ();
        m[3][3] = 1;
        
        // k = pNext - pPrev (approximates the tangent)
       
        m[0][2] = pNext.getX() - pPrev.getX();
        m[1][2] = pNext.getY() - pPrev.getY();
        m[2][2] = pNext.getZ() - pPrev.getZ();
        m[3][2] = 0;
      
        
        // normalise k
        double d = Math.sqrt(m[0][2] * m[0][2] + m[1][2] * m[1][2] + m[2][2] * m[2][2]);  
        m[0][2] /= d;
        m[1][2] /= d;
        m[2][2] /= d;
        
        // i = simple perpendicular to k
        m[0][0] = -m[2][2];
        m[1][0] =  0;
        m[2][0] =  m[0][2];
        m[3][0] =  0;
        
        // j = k x i
        m[0][1] = 0;
        m[1][1] = 1;
        m[2][1] = 0;
        m[3][1] = 0;
        
        float[]mm = new float [16];
        for (int i = 0;i<4;i++) {
        	for(int j=0;j<4;j++) {
        		mm[i*4+j] = (float) m[i][j];
        	}
        }
        
        //get ML1,ML2;
        Matrix4 ma = new Matrix4(mm);
        for (Point3D cp : crossSection) {
        	
        	Vector4 v = new Vector4(cp.getX(),cp.getY(),cp.getZ(),1);
        	
            Vector4 q1 = ma.multiply(v);
            Point3D q = q1.asPoint3D();
            
            vertices.add(q);
            textCoords.add(new Point2D(q.getX(),q.getZ()));
        }
    }
    
    private void computeMesh(GL3 gl) {
//    	mySpine = new ArrayList<Point3D>();
//    	//System.out.println(points.get(0).getX());
//    	//System.out.println(points.get(0).getY());
//    	//System.out.println(points.size());
//    	//System.out.println((float)this.size());
//    	//LineStrip3D curve = new LineStrip3D();
//    	for(float i = 0; i < (float)this.size(); i+=1/SLICES) {
//    		//System.out.println("BOOP");
//    		Point2D slice = point((float)(i));
//    		System.out.println(slice.getX() +" " + slice.getY());
//    		mySpine.add(new Point3D(slice.getX(),
//    				(float)(terrain.altitude(slice.getX(),slice.getY())+ALTITUDE_OFFSET),
//    				slice.getY()));
//    		//System.out.println("BOOP");
//    		}
    
    	vertices = new ArrayList<>();
    	//0,1
    	
    	Point2D pp = point((float) (0));
		float yp = terrain.altitude(pp.getX(), pp.getY()) + 0.002f;
		Point3D p = new Point3D(pp.getX(),yp,pp.getY());
		Point2D t = tangent((float) (0));
		float n = (float) Math.sqrt(t.getX() * t.getX() + t.getY() * t.getY());
		float ny = -t.getY()/n;
		float nx = t.getX()/n;
		ny *= this.width / 2;
		nx *= this.width / 2; 
		 
		vertices.add(new Point3D(p.getX() - ny, p.getY(), p.getZ() - nx));
		vertices.add(new Point3D(p.getX() + ny, p.getY(), p.getZ() + nx));
		textCoords.add(new Point2D(p.getX() - ny,p.getZ() - nx));
		textCoords.add(new Point2D(p.getX() + ny,p.getZ() + nx));
    	
    	
//    	
//    	Point3D pPrev;
//	    Point3D pCurr = mySpine.get(0);
//	    System.out.println(mySpine.get(0).getX());
//    	System.out.println(mySpine.get(0).getZ());
//	    Point3D pNext = mySpine.get(1);
//	    List<Point3D> cs = myCrossSection;
//	    addPoints(myCrossSection, vertices, pCurr, pCurr, pNext);
	    //1---n
    	for(int ii = 1;ii<this.size()*SLICES;ii++) {
    		
    		pp = point((float) (ii/SLICES));
    		yp = terrain.altitude(pp.getX(), pp.getY()) + 0.002f;
    		p = new Point3D(pp.getX(),yp,pp.getY());
    		t = tangent((float) (ii/SLICES));
    		n = (float) Math.sqrt(t.getX() * t.getX() + t.getY() * t.getY());
    		ny = -t.getY()/n;
    		nx = t.getX()/n;
    		ny *= this.width / 2;
    		nx *= this.width / 2; 
    		 
    		vertices.add(new Point3D(p.getX() - ny, p.getY(), p.getZ() - nx));
    		vertices.add(new Point3D(p.getX() + ny, p.getY(), p.getZ() + nx));
    		textCoords.add(new Point2D(p.getX() - ny,p.getZ() - nx));
    		textCoords.add(new Point2D(p.getX() + ny,p.getZ() + nx));
    		
    		indices.add( ii*2 - 1 );
    		indices.add(ii*2     );
    		indices.add( ii*2 - 2 );
    		
    		
    		
    		indices.add( ii*2 + 1 );
    		indices.add( ii*2     );
    		indices.add( ii*2 - 1 );
    		
    		
 /*   		 pPrev = pCurr;
             pCurr = pNext;
             pNext = mySpine.get(ii+1);
             addPoints(myCrossSection, vertices, pPrev, pCurr, pNext);
             
           //Top left triangle (1,2,0)
     		indices.add( ii*2 - 1 );
     		//indices.add( ii*2 - 2 );
     		indices.add( ii*2     );
     		indices.add( ii*2 - 2 );
     		
     		
     		//Bottom right triangle (3,2,1)
     		indices.add( ii *2 + 1 );
     		//indices.add( ii*2 - 1 );
     		indices.add( ii *2     );
     		indices.add( ii*2 - 1 );*/
    	}
    	//n-1,n
//    	 pPrev = pCurr;
//         pCurr = pNext;
//         addPoints(myCrossSection, vertices, pPrev, pCurr, pCurr);
    		
            
       
        
    	
//    	System.out.println(vertices.size() +"   " + indices.size() );
        roadw = new TriangleMesh(vertices,indices, true, textCoords);
     	roadw.init(gl);
    }

    public void init(GL3 gl) {
    	//System.out.println(this.points);
    	//makeCurvedSpine();

//    	
//    	List<Point3D> square = new ArrayList<>();
//        square.add(new Point3D(-width/2, 0,0));
//        square.add(new Point3D(width/2, 0,0));
//        myCrossSection= square;
        computeMesh(gl);
        //roadw = new TriangleMesh(vertices,indices, true, textCoords);
    	//roadw.init(gl);
    }
    
    public void display(GL3 gl,CoordFrame3D view) {
    	
    	//computeMesh();
    	
    	//roadw = new TriangleMesh(vertices,indices, true, textCoords);
    	//roadw.init(gl);
    	
//    	System.out.println(vertices.size() +"   " + indices.size() );
    	roadw.draw(gl,view);
    }
    


}
