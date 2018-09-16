package solution;
 import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import problem.Box;
import problem.ProblemSpec;
 public class PathForMovingBox {
	
	//Class for making a path for a given box
	
	//fields
	private Node initialNode;
	private Node helpingInitNode;
	private Node goalNode;
	private Node helpingGoalNode;
	private Box movingBox;
	private Grid grid;
	private ArrayList<Node> path; //output from this class
 	
	//constructor
 	public PathForMovingBox(Box movingBox, Grid grid) throws IOException {
		this.grid = grid;
		this.movingBox = movingBox;
		this.initialNode = getInitialNode(movingBox);
		this.helpingInitNode = makeHelpingInitNode(initialNode);
		this.goalNode = makeGoalNode(movingBox);
		this.helpingGoalNode = makeHelpingGoalNode(goalNode);
		PathFinder pf = new PathFinder(initialNode, goalNode, grid); //find a path from initial node to goalNode
		path = pf.findPath();
		removeDuplicates();
	}
	
		/*
		 * METHODS
		 */

 	public void removeDuplicates() {
 		int size = path.size()-1;
 		for(int i = size; i>0;i--) {
 			if(path.get(i).getxValue() == path.get(i-1).getxValue() && path.get(i).getyValue() == path.get(i-1).getyValue()) {
 				path.remove(i);
 			}
 		}
 	}
	
	//methods for making start node based on box center point from start position
	private Node getInitialNode(Box movingBox) {
		double x = movingBox.getPos().getX() + 0.5*grid.getLength();
		double y = movingBox.getPos().getY() + 0.5*grid.getLength();
		//check if there is already a node in the center point of the box
		Node init = new Node(x, y, "FS");
		return init; //return the new node made based on the center point of the box
	}
	
	
	//method for making helping node for the initial node, such that all edges are straight
	private Node makeHelpingInitNode(Node initialNode) {
		Node helpingInitNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVerticesInFreeSpace()) {
			double distance = calculateDistanceBetweenTwoNodes(initialNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToInitialNode = grid.getVerticesInFreeSpace().get(index); //the node we want to connect to initialNode via a helping node
		double x = closestNeighborToInitialNode.getxValue();
		double y = initialNode.getyValue();
		helpingInitNode = new Node(x,y,"FS");
		helpingInitNode.addNeighbour(4, initialNode);
		helpingInitNode.addNeighbour(5, closestNeighborToInitialNode);
		return helpingInitNode;
	}
	private Node makeHelpingGoalNode(Node goaNode) {
		Node helpingGoalNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVerticesInFreeSpace()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToGoalNode = grid.getVerticesInFreeSpace().get(index); //the node we want to connect to goalNode 
		double x = closestNeighborToGoalNode.getxValue();
		double y = goaNode.getyValue();
		helpingGoalNode = new Node(x,y,"FS");
		helpingGoalNode.addNeighbour(4, goaNode);
		helpingGoalNode.addNeighbour(5, closestNeighborToGoalNode);
		return helpingGoalNode;
	}
	
	//method for making goalNode
	private Node makeGoalNode(Box movingBox) {
		Point2D p = grid.getPS().getMovingBoxEndPositions().get(grid.getPS().getMovingBoxes().indexOf(movingBox));
		double x = p.getX() + grid.getLength()*0.5;
		double y = p.getY() + grid.getLength()*0.5;
		Node n = new Node(x, y, "FS");
		return n;
	}
	
	//calculate distance between two nodes
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		if(one.getxValue() == two.getxValue() && one.getyValue() == two.getyValue()) {
			return 0;
		}
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}

	
	//getPath. Method used in PathForAllMovingBoxes
	public ArrayList<Node> getPathForMovingBox(){
		return this.path;
	}
	
	
	//write path to file
	public void writeToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-value" + "\t" +"Y-value" + "\t" + "Ground type" + "\n");
		for(Node n:path) {
			writer.write(n.getxValue() + "\t" + n.getyValue() + "\t" + n.getGroundType()+"\n");
		}
		writer.close();
	}
	public static void main(String[] args) throws IOException {
		Grid g = new Grid();
		PathForMovingBox p = new PathForMovingBox(g.getPS().getMovingBoxes().get(0), g);
		System.out.println(p.path);
	}
	

 }
