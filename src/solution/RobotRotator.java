package solution;

import java.util.ArrayList;

public class RobotRotator {
	/**
	 * Class for rotating the robot. Shall be used when moving a box
	 * Input: double currentOrientation, char goalDirectionOfRobot
	 * Output: ArrayList<double> discretized (0.001 steps) path 
	 * The double value must be between 0.0 and 1.57. That is sufficient for moving all boxes in all possible valid directions. 
	 */
	
	//fields
	private ArrayList<Double> resultList = new ArrayList<>();
	private double initialRotation;
	private char goalDirectionOfRobot;
	
	//constructor
	public RobotRotator(double initialRotation, char dir) {
		this.initialRotation = initialRotation;
		this.goalDirectionOfRobot = dir;
		resultList = run();
	}
	
	//methods
	private ArrayList<Double> run(){
		ArrayList<Double> list = new ArrayList<>();
		if(goalDirectionOfRobot =='u') {
			for(Double i = initialRotation; i<1.571; i+=0.01) {
			list.add(i);
			}
		}
		else {
			for(Double i = initialRotation; i>0;i-=0.01) {
				list.add(i);
			}
		}
		return list;
	}
	
	//getters
	public ArrayList<Double> getOrientationList(){
		return resultList;
	}
	


}
