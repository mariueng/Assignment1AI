package solution;

import java.nio.file.Path;
import java.time.chrono.MinguoChronology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class PathFinder {
	
	//class for finding a path from one initial node to a goal node. Will be used to find path for every Moving Box
	
	//fields
	private Node goalNode;
	private Node initialNode;
	private ArrayList<Node> path = new ArrayList<>();
	private boolean isFinished = false;
	private double goalX;
	private double goalY;
	private final static double bigNumber = 10000;
	private static int numberOfStepsWhenStuck = 5;
	
	//constructor
	public PathFinder(Node init, Node goal) {
		this.initialNode = init;
		this.goalNode = goal;
		path.add(init);
		goalX = goal.getxValue();
		goalY = goal.getyValue();
	}

	
		/*
		 * METHODS
		 */
	
	//A* search. Find path
	private boolean findPath() {
		Node currentNode = initialNode;
		while(! isFinished) {
			List<Double> distances = Arrays.asList(bigNumber, bigNumber, bigNumber, bigNumber); 
			
			//make a list keeping track on the distances for the neighbour nodes to goal node
			for(Node n : currentNode.getNeighbours()) {
				int index = currentNode.getNeighbours().indexOf(n);
				if (! (n == null)) {
					double distance = getDistanceToGoalNode(n);
					distances.set(index, distance);
					if(distance==0) {
						isFinished = true;
						path.add(n);
						return true;
					}
				}
			}
			//list distances is now filled with the distance all neighbors has to goal node
			int indexOfnextNodeToVisit = distances.indexOf(Collections.min(distances));
			currentNode = currentNode.getNeighbours().get(indexOfnextNodeToVisit);
			while(path.contains(currentNode)) { //checking that node has not already been visited
				distances.remove(indexOfnextNodeToVisit);
				currentNode = currentNode.getNeighbours().get(indexOfnextNodeToVisit);
				}
			path.add(currentNode);
			
		}
		
		return false;
	}
	
	//Helping method: Calculate distance to goal
	private double getDistanceToGoalNode(Node n) {
		double x = n.getxValue();
		double y = n.getyValue();
		return Math.sqrt(Math.pow(goalX-x,2) + Math.pow(goalY-y, 2));
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
		
		PathFinder p = new PathFinder(a,i);
		p.findPath();
		System.out.println(p.path);
	}
}
