package solution;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.VetoableChangeListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
	
	
		/*
		 * /METHODS
		 */
	
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
                x+=distance; //incresing x-value for each node
            }
            x = w/2; //reset x-value
            y += distance; //increasy y-value when starting to sample next row
           
        }
        deleteSamplesWithinStaticObstacle(); //deleting samples in collision with static obstacles
        
    }
	
		
	//Helping method for deleting samples that has groundtype Static Obstacles
	private void deleteSamplesWithinStaticObstacle() {
		ArrayList<Integer> indexesWithSO = new ArrayList<>(); //list for keeping index posistion of Nodes with SO groundtype
		for(Node n:vertices) {
			if(n.getGroundType().equals("SO")) {
				n.removeThisNodeAsANeighbor(); //method in class Node. Secures consistency by remoing edges to nodes with SO
				indexesWithSO.add(vertices.indexOf(n));
			}
		}
		
		Collections.reverse(indexesWithSO); //reverese to simplyfe the delete
		for(Integer i:indexesWithSO) {
			vertices.remove(vertices.get(i));
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
	
	//write grid to txtFile
	public void writeToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\gridData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-value" + "\t" +"Y-value" + "\t" + "Ground type" + "\n");
		for(Node n:vertices) {
			writer.write(n.getxValue() + "\t" + n.getyValue() + "\t" + n.getGroundType()+"\n");
		}
		writer.close();
	}
	
	
	
	//main for testing and debugging
	public static void main(String[] args) throws IOException {
		Grid g = new Grid();
		g.load();
		g.sampleGrid();	
		System.out.println(g.vertices);
		g.writeToFile();
	}
	
	

}
