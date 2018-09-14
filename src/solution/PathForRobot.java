package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import problem.ProblemSpec;

public class PathForRobot {
	
	/**
	 * This class will have input: [inital x and y coordinate, path for the next moving box you want to move, grid]
	 * Output should be an ArrayList of Nodes the robot has to pass through in order to reach the moving box
	 */
	
	//fields
	private ArrayList<Node> robotPath = new ArrayList<>();
	private Node startNode;
	private Node helpingStartNode;
	private boolean needHelpingStartNode = true;
	private Node goalNode;
	private Node helpingGoalNode;
	private boolean needHelpingGoalNode = true;
	private ArrayList<Node> pathForNextMovingBox;
	private Grid grid;
	private char directionOfRobot;
	
	private double startX;
	private double startY;
	
	//constructor
	public PathForRobot(double startX, double startY, ArrayList path, Grid grid) throws IOException {
		this.grid = grid;
		this.pathForNextMovingBox = path;
		double x = startX;
		double y = startY;
		this.startNode = makeStartNode(x, y);
		this.helpingStartNode = makeHelpingInitNode(startNode);
		makeGoalNode();
		this.helpingGoalNode = makeHelpingGoalNode(goalNode);
		PathFinder pf = new PathFinder(startNode, goalNode, grid); //make PathFinder object
		this.robotPath = pf.findPath(); //finds a path from startNode to GoalNode
		
	}
	
	/*
	 * METHODS
	 */
	
	//make a node in the center position of robot
	private Node makeStartNode(double x, double y) {
		Node startNode = new Node(x,y,"FS");
		//check if already exists in grid
		for(Node n:grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(startNode, n);
			if(distance <0.01) {
				needHelpingStartNode = false;
				return n;
			}
		}
		return startNode;
	}
	
	
	//make a helping node for startNode
	private Node makeHelpingInitNode(Node startNode) {
		if(needHelpingStartNode==false) {
			return null;
		}
		Node helpingStartNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(startNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToInitialNode = grid.getVertices().get(index); //the node we want to connect to initialNode 
		double y = closestNeighborToInitialNode.getyValue();
		double x = startNode.getxValue();
		helpingStartNode = new Node(x,y,"FS");
		helpingStartNode.addNeighbour(4, startNode);
		helpingStartNode.addNeighbour(5, closestNeighborToInitialNode);
		return helpingStartNode;
	}
	
	
	//make a node at the end position for where the robot should end up
	private void makeGoalNode() {
		double x = pathForNextMovingBox.get(0).getxValue();
		double y = pathForNextMovingBox.get(0).getyValue();
		
		char direction = calculateDirection();
		if(direction == 'r') {
			Node n = new Node(x-(grid.getLength()/2), y, "FS");
			goalNode = n;
		}
		else if(direction == 'l') {
			Node n = new Node(x+(grid.getLength()/2), y, "FS");
			goalNode = n;
		}
		else if(direction == 'u') {
			Node n = new Node(x, y-(grid.getLength()/2), "FS");
			goalNode = n;
		}
		else if(direction == 'd'){
			Node n = new Node(x, y+(grid.getLength()/2), "FS");
			goalNode = n;
		}
		
		
	}
	//make a helping node for startNode
	private Node makeHelpingGoalNode(Node goalNode) {
		if(needHelpingGoalNode==false) {
			return null;
		}
		Node helpingGoalNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToGoalNode = grid.getVertices().get(index); //the node we want to connect to goalNode
		double y = closestNeighborToGoalNode.getyValue();
		double x = goalNode.getxValue();
		helpingGoalNode = new Node(x,y,"FS");
		helpingGoalNode.addNeighbour(4, goalNode);
		helpingGoalNode.addNeighbour(5, closestNeighborToGoalNode);
		return helpingGoalNode;
	}
	
	//connect goalNode to Grid
	public void connectToGrid(Node n) {
		List<Double> distances = new ArrayList<>();
		for(Node node :grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, node);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToGoalNode = grid.getVertices().get(index); //the node we want to connect to the goalNode 
		closestNeighborToGoalNode.addNeighbour(5, goalNode);
	}
	
	//calculate what side of the box you want to put your robot in
	private char calculateDirection() {
		char c;
		Node first = pathForNextMovingBox.get(0);
		Node second = pathForNextMovingBox.get(1);
		
		if(first.getyValue() < second.getyValue()) {
			c = 'u';
			this.directionOfRobot = 'f';
		}
		else if(first.getxValue() > second.getxValue()) {
			c = 'l';
			this.directionOfRobot ='u';
		}
		else if(first.getxValue()<second.getxValue()) {
			c ='r';
			this.directionOfRobot = 'u';
		}
		else {
			c='d';
			this.directionOfRobot = 'f';
		}
		return c;
	}
	
	//calculate distance between two nodes
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		double d =  Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
		return d;
	}

	//write path to file
	public void writeToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\robotPathData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-value" + "\t" +"Y-value" + "\t" + "Ground type" + "\n");
		for(Node n:robotPath) {
			writer.write(n.getxValue() + "\t" + n.getyValue() + "\t" + n.getGroundType()+"\n");
		}
		writer.close();
	}
	
	//getPath
	public ArrayList<Node> getRobotPath(){
		return this.robotPath;
	}
	//getDirectionOfRobot
	public char getDirectionOfRobot() {
		return this.directionOfRobot;
	}
	
	
	
	//main for testing
	public static void main(String[] args) throws IOException {
		Grid g = new Grid();
		PathForAllMovingBoxes pf = new PathForAllMovingBoxes(g);
		ArrayList path = pf.getPathForAllMovingBoxes().get(1);
		PathForRobot p = new PathForRobot(0.8, 0.75, path , g);
		System.out.println(p.robotPath);
	}

}
