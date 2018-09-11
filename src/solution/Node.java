package solution;

import java.util.Arrays;
import java.util.List;

public class Node implements Comparable<Node> {
	
	private final double x, y;
	private String groundType = null;
	private final static List<String> validGroundTypes = Arrays.asList("MO", "SO", "MB", "FS");
	// Neighbours are listed as follows: (up, right, down, left, initial/goalNode, helpingNode).
	private List<Node> neighbours = Arrays.asList(null, null, null, null, null, null);
	private double gValue; //distance from start node
	private double hValue; //heuristics: estimated distance to 
	private double totalCost;
	private Node parentNode;
	
	
	// Constructs a Node with position (x, y) and specified groundType. Run in class Grid.
	public Node(double x, double y, String groundType) {
		this.x = x;
		this.y = y;
		setGroundType(groundType);
	}
	
	// Sets the groundType (after moving object in grid).
	public void setGroundType(String groundType) {
		this.groundType = groundType;
	}
	
	// Adds node n as a neighbour to this, and this as a neighbour to n.
	public void addNeighbour(int i, Node n) {
		this.neighbours.set(i, n);
		if(i == 4) {
			n.neighbours.set(i, this); //adding initialNode
		}
		else if(i==5) {
			n.neighbours.set(5, this); //adding helping node
		}
		else if (i >= 2) {
			n.neighbours.set(i - 2, this);
		} else {
			n.neighbours.set(i + 2, this);
		}
	}
	//Remove this as a neigbhour from Node n
	public void removeThisNodeAsANeighbor() {
		for(int i=0;i<4;i++) {
			if(!(this.getNeighbours().get(i)==null)) {
				if(i <2) {
					this.neighbours.get(i).neighbours.set(i+2, null);
				}
				else {
					this.neighbours.get(i).neighbours.set(i-2, null);
				}
			}
		}
	}
	
	
	// Returns neighbours.
	public List<Node> getNeighbours() {
		return this.neighbours;
	}
	
	// ToString.
	public String toString() {
		return "" + "(" + x + ", " + y + ", " + groundType + ")";
	}
	//getters
	public double getxValue() {
		return this.x;
	}
	public double getyValue() {
		return this.y;
	}
	public String getGroundType() {
		return this.groundType;
	}
	
	//Method for comparable interface. Used in class PathFinder in A* search
	@Override
	public int compareTo(Node other) {
		return Double.compare(this.getTotalCost(),	 other.getTotalCost());
	}
	
	//getters used fro calculating distances and comparing nodes to expand
	public double getTotalCost() { //get total cost for this node
		return this.totalCost;
	}
	public double getGValue() { //get distance from start node
		return this.gValue;
	}
	public double getHValue() { //get distance to goal node
		return this.hValue;
	}
	//getParent method used in pathfinder
	public Node getParentNode() {
		return this.parentNode;
	}
	
	
	//setters for estimating totalCost
	public void setGValue(double i) { // step-distance from start node in path. 
		this.gValue = i;
	}
	public void setHValue(double j) {
		this.hValue = j;
	}
	public void setTotalCost(double gValue, double hValue) {
		this.totalCost = gValue + hValue;
	}
	public void setParent(Node n) {
		this.parentNode = n;
	}

	
	// Main for debugging purposes.
	public static void main(String[] args) {
		Node n = new Node(1, 1, "SO");
		Node m = new Node(2, 2, "FS");
		Node z = new Node(3,3, "FS");
		n.addNeighbour(1, m);
		n.addNeighbour(2, z);
		System.out.println(m.getNeighbours());
		System.out.println(z.getNeighbours());
		n.removeThisNodeAsANeighbor();
		System.out.println(m.getNeighbours());
		System.out.println(z.getNeighbours());
		
	}

}
