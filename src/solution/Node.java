package solution;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import problem.Box;
import problem.MovingBox;
import problem.ProblemSpec;

public class Node implements Comparable<Node> {
	
	/**
	 * Class for making nodes that contribute in the grid discretizing the space
	 * The nodes have attributes and methods managing their position, their neighbours, and their behavoiur in A* search. 
	 */
	
	private final double x, y;
	private String groundType;
	// Neighbours are listed as follows: (up, right, down, left, initial/goalNode, helpingNode).
	private List<Node> neighbours = Arrays.asList(null, null, null, null, null, null);
	private double gValue; //distance from start node
	private double hValue; //heuristics: estimated distance to 
	private Node parentNode;
	
	
	// Constructs a Node with position (x, y) and specified groundType. This constructor is ran in class Grid.
	public Node(double x, double y, String groundType) {
		this.x = x;
		this.y = y;
		setGroundType(groundType);
	}
	
	/*
	 * Sets the groundType. Ran in class Grid after searhcing the area around the node. 
	 * Possible groundtypes is: FS, MB, MO, SO
	 */
	public void setGroundType(String groundType) {
		this.groundType = groundType;
	}
	
	// Adds node n as a neighbour to this node, and this node as a neighbour to n. Based on index values in the list of vertices
	public void addNeighbour(int i, Node n) {
		this.neighbours.set(i, n);
		if(i == 4) {
			n.neighbours.set(i, this); //index value used when adding initialNode (startNode in a search)
		}
		else if(i==5) {
			n.neighbours.set(5, this); //adding helping node for the initialNode when doing a search.
		}
		else if (i >= 2) {
			n.neighbours.set(i - 2, this);
		} else {
			n.neighbours.set(i + 2, this);
		}
	}
	//Remove this as a neigbhour from Node n. PS: Should also take initialNode and helpingNode into account!
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
		return this.hValue + this.gValue;
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
	
	//see if node is marked as MB-groundtype by some later movingbox
	private boolean isMarkedMBBySomeLaterMovingBox(int i, ProblemSpec ps) {
		boolean result = false;
		double x = this.getxValue();
		double y = this.getyValue();
		double w = ps.getRobotWidth();
		int numberOfLaterMovingBoxes = ps.getMovingBoxes().size() - (i+1); //if i is 0 and there are a total of 2 movingBoxes, then it yields 1 (MB left)
		Rectangle2D.Double r = new Rectangle2D.Double(x-w/2,y-w/2,w, w); 
		for(int j = i+1; j<=numberOfLaterMovingBoxes; j++) { //loop through moving boxes
			MovingBox MB =(MovingBox) ps.getMovingBoxes().get(j);
			if(r.intersects(MB.getRect())) {
				result = true;
			}
		}
		return result;
	}
	//get ismark
	public boolean getisMarkedMBBySomeLaterMovingBox(int i, ProblemSpec ps) {
		return isMarkedMBBySomeLaterMovingBox(i, ps);
	}
	
	//setters for estimating totalCost
	public void setGValue(double i) { // step-distance from start node in path. 
		this.gValue = i;
	}
	public void setHValue(double j) {
		this.hValue = j;
	}

	public void setParent(Node n) {
		this.parentNode = n;
	}


}
