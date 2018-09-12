package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathForRobot {
	
	/**
	 * This class will have input: [initital node (position of center of robot), int i (index of the next movingBox you want to move)]
	 * Output should be an ArrayList of Nodes the robot has to pass through in order to reach the moving box
	 */
	
	//fields
	private ArrayList<Node> robotPath = new ArrayList<>();
	private Node startNode;
	private Node helpingStartNode;
	private boolean needHelpingStartNode = true;
	private Node goalNode;
	private int index;
	private ArrayList<ArrayList<Node>> pathForAllMovingBoxes;
	private Grid grid;
	
	
	//constructor
	public PathForRobot(int i, ArrayList paths, Grid grid) throws IOException {
		this.grid = grid;
		this.pathForAllMovingBoxes = paths;
		double x = grid.getPS().getInitialRobotConfig().getPos().getX();
		double y = grid.getPS().getInitialRobotConfig().getPos().getY();
		this.startNode = makeStartNode(x, y);
		this.helpingStartNode = makeHelpingInitNode(startNode);
		makeGoalNode();
		this.index = i;
		PathFinder pf = new PathFinder(startNode, goalNode);
		this.robotPath = pf.findPath();
		
	}
	
	/*
	 * METHODS
	 */
	
	//make a node in the center position of robot
	private Node makeStartNode(double x, double y) {
		for(Node n:grid.getVertices()) {
			if(n.getxValue()==x && n.getyValue()==y) {
				needHelpingStartNode = false;
				return n;
			}
		}
		Node startNode = new Node(x,y,"FS");
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
		double x = grid.getPS().getMovingBoxes().get(index).getPos().getX() + grid.getLength()/2;
		double y = grid.getPS().getMovingBoxes().get(index).getPos().getY() + grid.getLength()/2;
		char direction = calculateDirection();
		if(direction == 'r') {
			Node n = new Node(x-(grid.getLength()/2), y, "FS");
			goalNode = n;
			connectToGrid(goalNode);
		}
		else if(direction == 'l') {
			Node n = new Node(x+(grid.getLength()/2), y, "FS");
			goalNode = n;
			connectToGrid(goalNode);
		}
		else if(direction == 'u') {
			Node n = new Node(x, y-(grid.getLength()/2), "FS");
			goalNode = n;
			connectToGrid(goalNode);
		}
		else if(direction == 'd'){
			Node n = new Node(x, y+(grid.getLength()/2), "FS");
			goalNode = n;
			connectToGrid(goalNode);
		}
		
		
	}
	
	//connect goalNode to Grid
	public void connectToGrid(Node n) {
		List<Double> distances = new ArrayList<>();
		for(Node node :grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToGoalNode = grid.getVertices().get(index); //the node we want to connect to the goalNode 
		closestNeighborToGoalNode.addNeighbour(5, goalNode);
	}
	
	//calculate what side of the box you want to put your robot
	private char calculateDirection() {
		char c;
		Node first = pathForAllMovingBoxes.get(index).get(0);
		Node second = pathForAllMovingBoxes.get(index).get(1);
		if(first.getyValue() > second.getyValue()) {
			c = 'u';
		}
		else if(first.getxValue() >second.getxValue()) {
			c = 'l';
		}
		else if(first.getxValue()<second.getxValue()) {
			c ='r';
		}
		else {
			c='d';
		}
		return c;
	}
	
	//calculate distance between two nodes
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}
	//write path to file
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
	
	
	//main for testing
	public static void main(String[] args) throws IOException {
		Grid g = new Grid();
		PathForAllMovingBoxes pf = new PathForAllMovingBoxes(g);
		PathForRobot p = new PathForRobot(0, pf.getPathForAllMovingBoxes(),g);
		System.out.println(p.robotPath);
	}

}
