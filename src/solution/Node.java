package solution;

import java.util.Arrays;
import java.util.List;

public class Node {
	
	private final double x, y;
	private String groundType = null;
	private final static List<String> validGroundTypes = Arrays.asList("MO", "SO", "MB", "FS");
	// Neighbours are listed as follows: (up, right, down, left).
	private List<Node> neighbours = Arrays.asList(null, null, null, null);
	
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
		if (i >= 2) {
			n.neighbours.set(i - 2, this);
		} else {
			n.neighbours.set(i + 2, this);
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
	
	// Main for debugging purposes.
	public static void main(String[] args) {
		Node n = new Node(1, 2, "MO");
		Node m = new Node(2, 2, "FS");
		n.addNeighbour(1, m);
		System.out.println(n.getNeighbours());
		System.out.println(m.getNeighbours());
	}
}
