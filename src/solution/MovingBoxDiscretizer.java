package solution;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.geom.Point2D;

public class MovingBoxDiscretizer {
	
	/**
	 * Class for discretizing all paths in the arraylist of paths for moving boxes
	 * Input: ArrayList<ArrayLIst<Nodes>
	 * Output: ArrayList<ArrayList<Point2D> discretized points describing path for all moving boxes. 
	 */
	
	//fields
	private ArrayList<ArrayList<Point2D>> discretPathsForMovingBoxes = new ArrayList<>();
	private ArrayList<ArrayList<Node>> nodePaths;
	
	//constructor
	public MovingBoxDiscretizer(ArrayList<ArrayList<Node>> nodePaths) {
		this.nodePaths = nodePaths;
		discretPathsForMovingBoxes = run();
	}
	
	/*
	 * METHODS
	 */
	public ArrayList<ArrayList<Point2D>> run(){
		for(ArrayList<Node> nodeList:nodePaths) {
			ArrayList<Point2D> resultList = new ArrayList<>();
			int numberOfNodesInNodeList = nodeList.size();
			for(int i =0;i<numberOfNodesInNodeList-2;i++) {
				Node one = nodeList.get(i);
				Node two = nodeList.get(i+1);
				double startX = one.getxValue();
				double startY = one.getyValue();
				double goalX = two.getxValue();
				double goalY = two.getyValue();
				double distance = calculateDistanceBetweenTwoNodes(one, two);
				int numberOfSteps = (int) Math.floor(distance/0.001);
				char direction = calculateDirectionFromNodeToNextNode(one, two);
				if(direction =='u') {
					for(int j = 0; j<numberOfSteps; j++) {
					    Point2D point2D = new Point2D();
						point2D.x = (float) startX;
						point2D.y = (float) (startY+0.001*j);
						resultList.add(point2D);
					}
				}
				else if(direction =='d') {
					for(int j = 0; j<numberOfSteps; j++) {
					    Point2D point2D = new Point2D();
						point2D.x = (float) startX;
						point2D.y = (float) (startY-0.001*j);
						resultList.add(point2D);
					}
				}
				else if(direction =='l') {
					for(int j = 0; j<numberOfSteps; j++) {
					    Point2D point2D = new Point2D();
						point2D.x =  (float) (startX-(0.001*j));
						point2D.y = (float) startY;
						resultList.add(point2D);
					}
				}
				else if(direction =='r') {
					for(int j = 0; j<numberOfSteps; j++) {
					    Point2D point2D = new Point2D();
						point2D.x =  (float) (startX+(0.001*j));
						point2D.y = (float) startY;
						resultList.add(point2D);
					}
				}
				Point2D endPoint = new Point2D();
				double x = nodeList.get(numberOfNodesInNodeList-1).getxValue();
				double y = nodeList.get(numberOfNodesInNodeList-1).getyValue();
				endPoint.x =  (float) x;
				endPoint.y = (float) y;
				resultList.add(endPoint);
			}
			
			discretPathsForMovingBoxes.add(resultList);
		}
		
		return discretPathsForMovingBoxes;
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
	public ArrayList<ArrayList<Point2D>> getDiscretPathsForMovingBoxes(){
		return discretPathsForMovingBoxes;
	}
	
	//writeToFile
	
	//constructor for testing
	public static void main(String[] args) throws IOException {
		Grid grid = new Grid();
		PathForAllMovingBoxes p = new PathForAllMovingBoxes(grid);
		MovingBoxDiscretizer d = new MovingBoxDiscretizer(p.getPathForAllMovingBoxes());
		for(ArrayList<Point2D> list:d.getDiscretPathsForMovingBoxes()) {
			System.out.println(list);
		}
	}

}
