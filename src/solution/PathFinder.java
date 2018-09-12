	package solution;
	
	import java.io.BufferedWriter;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.PriorityQueue;
	
	
	public class PathFinder {
		
		//class for finding a path from one initial node to a goal node. Will be used to find path for every Moving Box
		//pastebin: https://pastebin.com/Zb5hx7JK
		
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
	
		
		//constructor
		public PathFinder(Node init, Node goal) throws IOException {
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
			//writeToFile();
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
				if(counter >1000) {
					System.err.print("Time error. Opened 1000 nodes without finding goal node.");
					isFinished = true;
				}
			}return path;
		}
		
		//method when expanding node
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
			
		
		//main for testing
		public static void main(String[] args) throws IOException {
			Node a = new Node(1, 1, "FS");
			Node b = new Node(2,1,"FS");
			b.addNeighbour(3, a);
			Node c = new Node(3,1, "FS");
			c.addNeighbour(3, b);
			Node d = new Node(1,2,"FS");
			d.addNeighbour(2, a);
			Node e = new Node(2,2,"MB");
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
		}
		
	}
