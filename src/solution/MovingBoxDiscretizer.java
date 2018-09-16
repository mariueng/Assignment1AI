package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;


import problem.ProblemSpec;

public class MovingBoxDiscretizer {
	
	/**
	 * Class for discretizing a moving box path
	 * Input: ArrayList<Nodes>
	 * Output: ArrayList<Point2D> discretized points describing path for a moving boxes. 
	 */
	
	//fields
	private ArrayList<Node> nodePath;
	private ArrayList<Point2D> discretePathForMovingBox = new ArrayList<>();
	
	//constructor
	public MovingBoxDiscretizer(ArrayList<Node> nodePaths) {
		this.nodePath = nodePaths;
		discretePathForMovingBox = run();
	}
	
	/*
	 * METHODS
	 */
	public ArrayList<Point2D> run(){
		ArrayList<Point2D> resultList = new ArrayList<>();
		int numberOfNodesInNodeList = nodePath.size();
		for(int i = 0; i < numberOfNodesInNodeList - 1; i++) {
			Node one = nodePath.get(i);
			Node two = nodePath.get(i+1);
			double startX = one.getxValue();
			double startY = one.getyValue();
			double distance = calculateDistanceBetweenTwoNodes(one, two);
			int numberOfSteps = (int) Math.floor(distance/0.001);
			char direction = calculateDirectionFromNodeToNextNode(one, two);
			if(direction =='u') {
				for(int j = 0; j<numberOfSteps; j++) {
				    Point2D point2D = new Point2D.Double(startX,startY+0.001*j);
					resultList.add(point2D);
				}
			}
			else if(direction =='d') {
				for(int j = 0; j<numberOfSteps; j++) {
				    Point2D point2D = new Point2D.Double(startX,startY-0.001*j);
					resultList.add(point2D);
				}
			}
			else if(direction =='l') {
				for(int j = 0; j<numberOfSteps; j++) {
				    Point2D point2D = new Point2D.Double(startX-0.001*j,startY);
					resultList.add(point2D);
				}
			}
			else if(direction =='r') {
				for(int j = 0; j<numberOfSteps; j++) {
				    Point2D point2D = new Point2D.Double(startX+0.001*j,startY);
					resultList.add(point2D);
				}
			}
		}
		double x = nodePath.get(numberOfNodesInNodeList-1).getxValue();
		double y = nodePath.get(numberOfNodesInNodeList-1).getyValue();
		Point2D endPoint = new Point2D.Double(x,y);
		resultList.add(endPoint);
		
		return resultList;
	}
	
	//calculate distance between two nodes
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}
	//calculate direction from one node to another
	private char calculateDirectionFromNodeToNextNode(Node one, Node two) {
		char c;
		if(one.getyValue() < two.getyValue()) {
			c = 'u';
		}
		else if(one.getxValue() >two.getxValue()) {
			c = 'l';
		}
		else if(one.getxValue()<two.getxValue()) {
			c ='r';
		}
		else {
			c='d';
		}
		return c;
	}
	
	//getters
	public ArrayList<Point2D> getDiscretePathForMovingBox(){
		return discretePathForMovingBox;
	}
	
	//write path
	public void writeToFile(int i) throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-value" + "\t" +"Y-value" + "\n");
		for (Point2D point : discretePathForMovingBox) {
			writer.write(point.getX() + "\t" + point.getY()+"\n");
		}
		writer.close();
	}
	
	//constructor for testing
	public static void main(String[] args) throws IOException {
		Grid grid = new Grid();
		PathForMovingBox p = new PathForMovingBox(grid.getPS().getMovingBoxes().get(0), grid);
		System.out.println(p.getPathForMovingBox());
		MovingBoxDiscretizer d = new MovingBoxDiscretizer(p.getPathForMovingBox());
	}

}
