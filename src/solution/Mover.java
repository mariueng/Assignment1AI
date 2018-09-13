package solution;

import java.util.ArrayList;
import java.util.Arrays;

import com.sun.javafx.geom.Point2D;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Mover {
	
	/**
	 * Class for managing the actual moving of the robot together with a moving box
	 * Input: the discretized path of the movingbox we want to move
	 * Output: Lists of two list. Each with the same number of elements
	 * List1: State of the box: positions of the center point of the movingBox ArrayList<double>
	 * list2: State of the robot: position and rotation of the robot ArrayList<ArrayList<double>>
	 */
	
	//fields
	private ArrayList<Point2D> movingBoxOriginalPath;
	private ArrayList<ArrayList<Double>> resultPathForMovingBox = new ArrayList<>();
	private ArrayList<ArrayList<Double>> resultPathForRobot = new ArrayList<>(); 
	
	//constructor
	public Mover(ArrayList<Point2D> movingBoxOriginalPath) {
		this.movingBoxOriginalPath = movingBoxOriginalPath;
	}
	
	//methods
	
	//make a list with the primitves a robot needs to take when direction changes
	private ArrayList<ArrayList<Double>> changeDirectionOfRobot(Double startX, double startY, double w, char startDir, char goalDir){
		ArrayList<ArrayList<Double>> resultList = new ArrayList<>();
		double secondX;
		double secondY;
		
		if(startDir == 'u' && goalDir == 'r') { //from bottom to left side
			//go back w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, startX, startY-i));
				resultList.add(step);
			}
			secondY = startY-(w/2);
			//rotate
			RobotRotator r = new RobotRotator(0, 'u');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), startX, secondY));
 			}
			//go left w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX-i, secondY));
				resultList.add(step);
			}
			secondX = startX -(w/2);
			//go up w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, secondX, secondY+i));
				resultList.add(step);
			}
			
		}
		else if(startDir == 'u' && goalDir == 'l') { //from bottom to right side
			
		}
		else if(startDir == 'd' && goalDir == 'r') { //from top tp left side
			
		}
		else if(startDir == 'd' && goalDir =='l') {//from top to right side
			
		}
		else if(startDir == 'r' && goalDir=='u') {//from left to bottom
			
		}
		else if(startDir =='r' && goalDir =='d') {//from left to top
			
		}
		else if(startDir =='l' && goalDir == 'u') {//from right to bottom
			
		}
		else if(startDir =='l' && goalDir=='d') {//from right to top
			
		}
		
		return resultList;
	}
	
	//get direction of next step
	private char getDirectionOfNextStep(Point2D from, Point2D to) {
		char r = ' ';
		double startX = from.x;
		double startY = from.y;
		double goalX = to.x;
		double goalY = to.y;
		if(startX < goalX ) {
			r = 'r';
		}
		else if(startX > goalX ) {
			r = 'l';
		}
		else if(startY < goalY) {
			r = 'u';
		}
		else if(startY > goalY) {
			r = 'd';
		}
		if(r == ' ') {
			System.out.println(from + " and " + to + " have the same position");
		}
		
		return r;
	}
	
	//main for testing
	public static void main(String[] args) {
		
	}
	

}
