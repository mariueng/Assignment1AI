package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;


public class PathFinder {
	
	//class for finding a path from one initial node to a goal node. Will be used to find path for every Moving Box
	
	//fields
	private Node goalNode;
	private Node initialNode;
	private ArrayList<Node> path = new ArrayList<>(); //result list, containing all nodes from start to end node
	private PriorityQueue<Node> open = new PriorityQueue<>(); //contains nodes that are visited but not expanded. Pending nodes.
	private ArrayList<Node> closed = new ArrayList<>(); //contains nodes that have been visited and also expanded 
	private boolean isFinished = false;
	private double goalX;
	private double goalY;
	private double startX;
	private double startY;
	private final static double bigNumber = 10000;
	
	//constructor
	public PathFinder(Node init, Node goal) {
		this.initialNode = init;
		this.goalNode = goal;
		this.goalX = goal.getxValue();
		this.goalY = goal.getyValue();
		this.startX = init.getxValue();
		this.startY = init.getyValue();
	}

	
		/*
		 * METHODS
		 */
	
	//A* search. Find path
	private ArrayList<Node> findPath() {
		open.add(this.initialNode);

		while(isFinished == false || open.size() > 0) {
			Node currentNode = open.poll();
			if(currentNode == goalNode) {
				isFinished = true;
				addParentNodesInPath(currentNode);
				path.add(initialNode);
				Collections.reverse(path);
				
				System.out.println("Path from " + initialNode + " to " + goalNode + ": " + path);
				return path;
			}
			for(Node neighbour:currentNode.getNeighbours()) {
				if((!(neighbour==null)) && (!open.contains(neighbour)) && (neighbour.getGroundType().equals("FS")) && (!closed.contains(neighbour))) {
					neighbour.setParent(currentNode);
					neighbour.setGValue(getDistanceFromStartNode(initialNode));
					neighbour.setHValue(getDistanceToGoalNode(goalNode));
					if( (!open.contains(neighbour)) && (!(closed.contains(neighbour))) ) {
						open.add(neighbour);
					}
				}
				}
			closed.add(currentNode);
		} return path;
	}
	//addingParentNodes in path
	private void addParentNodesInPath(Node currentNode) {
	boolean isComplete = (currentNode.getParentNode() ==null);
	if(isComplete == false) {
		path.add(currentNode);
		addParentNodesInPath(currentNode.getParentNode());
		}
	//path.add(initialNode);
	}
		
	
	//Helping method: Calculate distance to goal
	private double getDistanceToGoalNode(Node n) {
		double x = n.getxValue();
		double y = n.getyValue();
		n.setHValue(Math.pow(goalX-x,2) + Math.pow(goalY-y, 2));
		return n.getHValue();
	}
	//helping method: calculate distance from start ndoe
	private double getDistanceFromStartNode(Node n) {
		double x = n.getxValue();
		double y = n.getyValue();
		n.setGValue(Math.pow(startX-x,2) + Math.pow(startY-y, 2));
		return n.getGValue();
	}

	
	// Getters
	public Node getNode(int i) {
		return path.get(i);
	}
	
	//main for testing
	public static void main(String[] args) {
		Node a = new Node(1, 1, "FS");
		Node b = new Node(2,1,"FS");
		b.addNeighbour(3, a);
		Node c = new Node(3,1, "FS");
		c.addNeighbour(3, b);
		Node d = new Node(1,2,"FS");
		d.addNeighbour(2, a);
		Node e = new Node(2,2,"FS");
		e.addNeighbour(3, d);
		e.addNeighbour(2, b);
		Node f = new Node(3,2,"FS");
		f.addNeighbour(3, e);
		f.addNeighbour(2, c);
		Node g = new Node(1,3,"FS");
		g.addNeighbour(2, d);
		Node h = new Node(2,3,"FS");
		h.addNeighbour(3, g);
		h.addNeighbour(2, e);
		Node i = new Node(3,3,"FS");
		i.addNeighbour(3, h);
		i.addNeighbour(2, f);
		PathFinder p = new PathFinder(a, i);
		p.findPath();
	}
}
