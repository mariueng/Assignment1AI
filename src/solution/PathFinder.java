	package solution;
	
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.PriorityQueue;
	
	
	public class PathFinder {
		
		//class for finding a path from one initial node to a goal node. Will be used to find path for every Moving Box and robot heading for next box

		
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
		private Grid grid;
	
		
		//constructor
		public PathFinder(Node init, Node goal, Grid grid) throws IOException {
			this.grid = grid;
			this.initialNode = init;
			calculateGvalueForNode(initialNode);
			calculateHvalueForNode(initialNode);
			open.add(initialNode);
			closed.add(initialNode);
			this.goalNode = goal;
			this.goalX = goal.getxValue();
			this.goalY = goal.getyValue();
			this.startX = init.getxValue();
			this.startY = init.getyValue();
		}
	
		
			/*
			 * METHODS
			 */
		
		//A* search. Find path from start node to goal node
		public ArrayList<Node> findPath() {
			int counter = 0;
			while(isFinished == false && open.size()>0) {
				expandNode(open.poll()); 
				counter ++;
				if(counter == grid.getNumberOfFreeSpaceNodes()-10) {
					System.err.print("Did not find a path from : " + initialNode + " to " + goalNode);
					isFinished = true;
				}
			}return path;
		}
		
		//method when expanding node. A* search
		private void expandNode(Node node) {
			for(Node neighbor:node.getNeighbours()) {
				if(!(neighbor == null) && neighbor.getGroundType().equals("FS")) {
				double gValue = node.getGValue() + calculateDistanceBetweenTwoNodes(neighbor, node);
				if(closed.contains(neighbor)) {
					if(neighbor.getGValue() > gValue) {
						neighbor.setGValue(gValue);
						neighbor.setParent(node);
					}
				}
				else {
					if(neighbor == goalNode) {
						isFinished = true;
						neighbor.setParent(node);
						this.path = addParentNodesInPath(neighbor);
						path.add(initialNode);
						Collections.reverse(path);
						for(Node n:grid.getVertices()) {
							n.setParent(null); //reverse the parentNode-attribute for all nodes in the grid, st. it will not confuse other searches
						}
						return;
					}
					neighbor.setParent(node);
					neighbor.setGValue(gValue);
					calculateHvalueForNode(neighbor);
					closed.add(neighbor);
					open.add(neighbor);
				}
				}
			}
		}
	
	
		//addingParentNodes in path
		private ArrayList<Node> addParentNodesInPath(Node currentNode) {
		boolean isComplete = (currentNode.getParentNode() ==null);
		if(isComplete == false) {
			path.add(currentNode);
			addParentNodesInPath(currentNode.getParentNode());
			}
		return path;
		}
			
		
		//Helping method: Calculate distance to goal
		private void calculateHvalueForNode(Node n) {
			double x = n.getxValue();
			double y = n.getyValue();
			n.setHValue(2*(Math.pow(goalX-x,2) + Math.pow(goalY-y, 2)));
	
		}
		//helping method: calculate distance from start ndoe
		private void calculateGvalueForNode(Node n) {
			double x = n.getxValue();
			double y = n.getyValue();
			n.setGValue(Math.pow(startX-x,2) + Math.pow(startY-y, 2));
		}
		//calculate distance between two nodes
			private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
				return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
			}
	
		
		// Getters
		public Node getNode(int i) {
			return path.get(i);
		}
		
		//get size of path
		public int getSizeOfPath() {
			return path.size();
		}
		
		//tostring
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "" + path;
		}
		
		
	}
