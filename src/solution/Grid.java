package solution;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.sun.javafx.geom.Rectangle;

import problem.Box;
import problem.MovingObstacle;
import problem.ProblemSpec;
import problem.StaticObstacle;

public class Grid {
	
	//run problemspec to access lists of moving boxes, moving obstacles etc.
	public void load() {
        ProblemSpec ps = new ProblemSpec();
        this.ps = ps;
        try {
            ps.loadProblem("input1.txt");
            ps.loadSolution("output1.txt");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");
        w = ps.getRobotWidth();

    }
	
	//fields
	private ProblemSpec ps;
	private double d; //distance between each node
	private int k; //The k last nodes you need to search for neighborhood
	private ArrayList<Node> vertices = new ArrayList<>(); //Array to keep nodes
	private static double w; //length of robot arm
	
	//constructor
	public Grid(double distance, int k) {
		this.d = distance;
		this.k = k; 
	}
	
	
	//Methods
	
	//Method to sample vertices
	public void sampleGrid() {
		int a = (int) Math.floor((1-w)/d); //How many samples for each row/columns
		double x = w/2, y = w/2; 
		//loop to generate nodes in grid
		for(int i = 0; i<a; i++) {
			for(int j = 0; j<a; j++) {
				String g = assignGroundType(x,y); //g equals ground type
				if(!g.equals("SO")) { //do not add nodes if they are situated within a static obstacle
					Node n = new Node(x,y,g); //calls constructor in Node class
					vertices.add(n); //is added to the collection of nodes
				}
				x+=d;
			}
			x = w/2; //reset x-value
			y += d;
			
		}
	}
		
	//Assign ground type for node
	private String assignGroundType(double x, double y) {
		String result = "";
		//make a rectangle area around the node with heigh w/2 and width w/2 and x,y as center point
		Rectangle2D.Double r = new Rectangle2D.Double(x-w/2,y-w/2,w, w); 
		for(Box MO : ps.getMovingObstacles()) { //loop through moving obstacles
			if(r.intersects(MO.getRect())) {
				result += "MO";
			}
		}
		for(Box MB : ps.getMovingBoxes()) { //loop through moving boxes
			if(r.intersects(MB.getRect())) {
				result += "MB";
			}
		}
		for(StaticObstacle SO : ps.getStaticObstacles()) { //loop through static obstacles
			if(r.intersects(SO.getRect())) {
				result += "SO";
			}
		}
		if(result.equals("")) { //did not intersect -> the node is in free space
			result+= "FS";
		}

		return result;
	}
		
	//getters
	
	//get number of nodes in Vertices
	private int getNumberOfSamples() {
		return vertices.size();

	}
	
	//get length of robot arm
	private double getLength() {
		return w;
	}
	
	//get distance between nodes
	private double getDistance() {
		return d;
	}
	
	//get k


	
	
	//main for testing and debugging
	public static void main(String[] args) {
		Grid g = new Grid(0.1,15);
		g.load();
		g.sampleGrid();
		System.out.println(g.vertices);
		System.out.println(g.vertices.get(1).getNeighbours());

		
	}
	
	

}
