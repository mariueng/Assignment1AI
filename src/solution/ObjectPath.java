package solution;
 import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 import problem.Box;
import sun.security.provider.certpath.Vertex;
 public class ObjectPath {
	
	//Class for making a path for a given box
	
	//fields
	private Node initialNode;
	private Node helpingInitNode;
	private Node goalNode;
	private Node helpingGoalNode;
	private Map<Box, ArrayList<Node>> pathForObject = new HashMap<Box, ArrayList<Node>>();
	private Box movingBox;
	private Grid grid;
	private boolean needHelpingInitNode = true;
	private boolean needHelpingGoalNode = true;
	private double maxDistanceToNodeInGrid;
 	
	//constructor
 	public ObjectPath(int i) throws IOException {
		this.grid = new Grid();
		this.movingBox = grid.getPS().getMovingBoxes().get(i);
		this.initialNode = getInitialNode(movingBox);
		this.helpingInitNode = makeHelpingInitNode(initialNode);
		this.goalNode = makeGoalNode(movingBox);
		this.helpingGoalNode = makeHelpingGoalNode(goalNode);
		this.maxDistanceToNodeInGrid = grid.getLength()*0.025*Math.sqrt(2);
		changeGroundTypeForOldMB();
		PathFinder pf = new PathFinder(initialNode, goalNode);
		System.out.println(pf);
		System.out.println(pf.getSizeOfPath());
		changeGroundTypeForNewMB();
		
	}
	
		/*
		 * METHODS
		 */
	
	
	//methods for making start node based on box center point from start position
	private Node getInitialNode(Box movingBox) {
		double x = movingBox.getPos().getX()+0.5*grid.getLength();
		double y = movingBox.getPos().getY() + 0.5*grid.getLength();
		//check if there is already a node in the center point of the box
		for(Node n:grid.getVertices()) {
			if(n.getxValue()==x && n.getyValue()==y) {
				this.needHelpingInitNode = false;
				return n; //found a node in the center point of the box. Don't need to make new node
			}
		}
		Node init = new Node(x, y, "MB");
		return init; //return the new node made based on the center point of the box
	}
	
	
	//method for making helping node for the initial node, such that all edges are straight
	private Node makeHelpingInitNode(Node initialNode) {
		if(needHelpingInitNode==false) {
			return this.initialNode;
		}
		Node helpingInitNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(initialNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToInitialNode = grid.getVertices().get(index); //the node we want to connect to initialNode 
		double x = closestNeighborToInitialNode.getxValue();
		double y = initialNode.getyValue();
		helpingInitNode = new Node(x,y,"FS");
		helpingInitNode.addNeighbour(4, initialNode);
		helpingInitNode.addNeighbour(5, closestNeighborToInitialNode);
		return helpingInitNode;
	}
	private Node makeHelpingGoalNode(Node goaNode) {
		if(needHelpingGoalNode==false) {
			return this.goalNode;
		}
		Node helpingGoalNode = null;
		List<Double> distances = new ArrayList<>();
		for(Node n:grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, n);
			distances.add(distance);
		}
		int index = distances.indexOf(Collections.min(distances));
		Node closestNeighborToGoalNode = grid.getVertices().get(index); //the node we want to connect to initialNode 
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
		//System.out.println(p);
		double x = p.getX() + grid.getLength()*0.5;
		double y = p.getY() + grid.getLength()*0.5;
		//check if there is already a node in this end position
		for(Node n:grid.getVertices()) {
			if(n.getxValue()==x && n.getyValue()==y) {
				return n; //found a node in goal position
			}
		}
		Node n = new Node(x, y, "FS");
		return n;
	}
	
	//calculate distance between two nodes
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}
	//method for changing ground types for nodes that lies within the area of the moving box before search
	private void changeGroundTypeForOldMB() {
		double x = initialNode.getxValue();
		double y = initialNode.getyValue();
		for(Node n:grid.getVertices()) {
			if(n.getGroundType().equals("MB")) {
				double distance = calculateDistanceBetweenTwoNodes(n, initialNode);
				if(distance < grid.getDistance()*2) {
					n.setGroundType("FS");
				}
			}
		}
	}
	
	private void changeGroundTypeForNewMB() {
		double x = goalNode.getxValue();
		double y = goalNode.getyValue();
		for(Node n:grid.getVertices()) {
			if(n.getGroundType().equals("FS")) {
				double distance = calculateDistanceBetweenTwoNodes(n, goalNode);
				if(distance < grid.getDistance()*2) {
					n.setGroundType("MB");
				}
			}
		}
	}
	
	//main for testing
	public static void main(String[] args) throws IOException {
		ObjectPath o = new ObjectPath(0);
		System.out.println("init: " + o.initialNode);
		System.out.println("goal:" +o.goalNode);
		System.out.println("helping initNode: " + o.helpingInitNode);
		System.out.println("helping goalNode: " + o.helpingGoalNode);
		
	}
 }
