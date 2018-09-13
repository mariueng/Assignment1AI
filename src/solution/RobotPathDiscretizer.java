package solution;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.geom.Point2D;

import problem.ProblemSpec;

public class RobotPathDiscretizer {
	
	/**
	 * Class for discretizing a path for the robot, consisting of node object
	 * Input: ArrayList<Node>
	 * Output: ArrayList<Point2D>
	 */
	
	//fields
	private ArrayList<Point2D> discretPathsForRobot = new ArrayList<>();
	private ArrayList<Node> robotPath;
	
	//constructor
	public RobotPathDiscretizer(ArrayList<Node> path) {
		this.robotPath = path;
		discretPathsForRobot = run();
	}
	
	/*
	 * METHODS
	 */
	public ArrayList<Point2D> run(){
			ArrayList<Point2D> resultList = new ArrayList<>();
			int numberOfNodesInNodeList = robotPath.size();
			for(int i =0;i<numberOfNodesInNodeList-2;i++) {
				Node one = robotPath.get(i);
				Node two = robotPath.get(i+1);
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
			}
			Point2D endPoint = new Point2D();
			double x = robotPath.get(numberOfNodesInNodeList-1).getxValue();
			double y = robotPath.get(numberOfNodesInNodeList-1).getyValue();
			endPoint.x =  (float) x;
			endPoint.y = (float) y;
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
	public ArrayList<Point2D> getDiscretPathsForMovingBoxes(){
		return discretPathsForRobot;
	}
	
	
	//main for testing
	public static void main(String[] args) throws IOException {
		ProblemSpec ps = new ProblemSpec();
		Grid g = new Grid(ps);
		PathForAllMovingBoxes pf = new PathForAllMovingBoxes(g);
		PathForRobot p = new PathForRobot(0, pf.getPathForAllMovingBoxes(),g);
		ArrayList<Node> nodePath= p.getRobotPath();
		RobotPathDiscretizer d = new RobotPathDiscretizer(nodePath);
		System.out.println(d.robotPath);
		System.out.println(d.getDiscretPathsForMovingBoxes());
	}

}
