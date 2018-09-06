package solution;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.VetoableChangeListener;
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
	private double distance; //distance between each node
	private int kLastNodes; //The k last nodes you need to search for neighborhood
	private int maxNodesEachRow;//How many samples for each row/columns
	private ArrayList<Node> vertices = new ArrayList<>(); //Array to keep nodes
	private static double w; //length of robot arm
	
	//constructor
	public Grid() {
		this.distance = 0.1 -(w/2);
		this.maxNodesEachRow = (int) Math.floor((1-w)/distance); 
	}
	
	
	//Methods
	
    //Method to sample vertices
    public void sampleGrid() {
        double x = w/2, y = w/2;
        //loop to generate nodes in grid
        for(int i = 0; i<maxNodesEachRow; i++) {
            for(int j = 0; j<maxNodesEachRow; j++) {
                String g = assignGroundType(x,y); //g equals ground type
                Node n = new Node(x,y,g); //calls constructor in Node class
                assignNeighbours(n, i, j);
                vertices.add(n); //is added to the collection of nodes
                x+=distance;
            }
            x = w/2; //reset x-value
            y += distance;
           
        }
        
    }
	
		
	
	//Assign neighbours to node n
	private void assignNeighbours(Node n,int i,int j){
		if(i==0 && j==0) {
			return;
		}
		if(!(j==0)) {
			assignLeftNeighbour(n,i,j);
		}
		if(!(i==0)) {
			assignDownNeighbour(n,i,j);
		}
		}
	
	//assignLeftNeighbo
	private void assignLeftNeighbour(Node n, int i, int j) {
		int index = i*maxNodesEachRow + j-1;
		n.addNeighbour(3, vertices.get(index));
	}
	//assignDownNeighbour
	private void assignDownNeighbour(Node n, int i, int j) {
		int index = i*maxNodesEachRow+j-maxNodesEachRow;
		n.addNeighbour(2, vertices.get(index));
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
		return distance;
	}
	
	//get k
	
	
	//main for testing and debugging
	public static void main(String[] args) {
		Grid g = new Grid();
		g.load();
		g.sampleGrid();
		System.out.println(g.vertices);
		System.out.println(g.maxNodesEachRow);
		System.out.println("First node: " + g.vertices.get(0).getNeighbours());
		System.out.println("Second node: " + g.vertices.get(1).getNeighbours());
		System.out.println("Third node: " + g.vertices.get(2).getNeighbours());
		System.out.println("Last node: " + g.vertices.get(99).getNeighbours());
		
		

		
	}
	
	

}
