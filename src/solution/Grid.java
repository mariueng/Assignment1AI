package solution;

import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import problem.Box;
import problem.ProblemSpec;
import problem.StaticObstacle;

public class Grid {
	/**
	 * Class for making the grid covering the space. It will run ProblemSpec to know how the space looks like.
	 */
	
	private void load() {
		 ProblemSpec ps = new ProblemSpec();
	        try {
	            ps.loadProblem("input1.txt");
	            this.ps = ps;
	            //ps.loadSolution("output1.txt");
	        } catch (IOException e) {
	            System.out.println("IO Exception occured");
	        }
	        System.out.println("Finished loading!");

	}
	
	//fields
	private ProblemSpec ps;
	private double distance; //distance between each node
	private int maxNodesEachRow;//How many samples for each row/columns
	private ArrayList<Node> vertices = new ArrayList<>(); //Array to keep nodes
	private static double w; //length of robot arm
	
	//constructor
	public Grid(ProblemSpec ps) {
		this.ps = ps;
		this.w = ps.getRobotWidth();
		double t = 0.3; //this value is somewhat random
		this.distance = 0.3;
		this.maxNodesEachRow = (int) Math.floor((1-w)/distance); 
		sampleGrid();
	}
	
	//constructor for testing purposes
	public Grid() {
		load();
		this.w = ps.getRobotWidth();
		double t = 0.1; //this value is somewhat random
		this.distance = t - (w/2);
		this.maxNodesEachRow = (int) Math.floor((1-w)/distance); 
		sampleGrid();
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
                x = doubleFormatter(x);
                
            }
            x = w/2; //reset x-value
            y += distance; //increasy y-value when starting to sample next row
            y = doubleFormatter(y);
        }
        deleteSamplesWithinStaticObstacle(); //deleting samples in collision with static obstacles
    }
    
	//formatter for making pretty numbers
	public double doubleFormatter(double number) {
		NumberFormat formatter = new DecimalFormat("#0.000");
		String formatted = formatter.format(number);
		double formattedNumber = Double.parseDouble(formatted);
		return formattedNumber;
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
		
		Collections.reverse(indexesWithSO); //reverese to simplify the delete
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
	public String assignGroundType(double x, double y) {
		String result = "";
		//make a rectangle area around the node with heigh w/2 and width w/2 and x,y as center point
		Rectangle2D.Double r = new Rectangle2D.Double(x-w/2,y+w/2,w - 0.00000001, w - 0.00000001); 
		for(Box MO : ps.getMovingObstacles()) { //loop through moving obstacles
			if(r.intersects(MO.getRect())) {
				result += "MO";
			}
		}
		for(Box MB : ps.getMovingBoxes()) { //loop through moving boxes
			if(r.intersects(MB.getRect())) {
				if (result.equals("MB")) {
					result += "";
				} else {
					result += "MB";
				}
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
	
	//get nodes in vertices
	public ArrayList<Node> getVertices() {
		return this.vertices;
	}
	//get problemspec
	public ProblemSpec getPS() {
		return this.ps;
	}

	
	//get number of nodes in Vertices
	public int getNumberOfSamples() {
		return vertices.size();

	}
	
	//get length of robot arm
	public double getLength() {
		return w;
	}
	
	//get distance between nodes
	public double getDistance() {
		return distance;
	}
	
	// toString
	public String toString() {
		String result = "";
		int  max_y = vertices.size() / maxNodesEachRow;
		for (int i = 0; i < maxNodesEachRow; i++) {
			for (int j = 0; j < max_y; j++) {
				result += vertices.get(i + j * maxNodesEachRow) + "  |  ";
			}
			result = result.substring(0, result.length() - 1);
			result += "\n";
			result += "----------------------------------------------------------------------------------------------------------------------------------------------";
			result += "\n";
		}
		return result;
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
		
	}
	
	

}
