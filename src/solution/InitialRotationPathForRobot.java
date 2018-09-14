package solution;

import java.io.IOException;
import java.util.ArrayList;

import problem.ProblemSpec;

public class InitialRotationPathForRobot {
	/**
	 * Class for making sure that the robot will orient itself correctly before it starts to move the next box.
	 * Input: ProblemSpec, int index (of the box you want to move)
	 * Output: ArrayList<double> (each primitive will be 0.001 different for the one before. Each double will describe the orientation of the robot)
	 */
	
	//fields
	private ArrayList<Double> resultList = new ArrayList<>();
	private double initialRotation;
	private char goalDirectionOfRobot;
	private int index;
	private ProblemSpec ps;
	
	//constructor
	public InitialRotationPathForRobot(ProblemSpec ps, int i) throws IOException {
		this.ps = ps;
		this.index = i;
		this.initialRotation = getInitialRotation();
		this.goalDirectionOfRobot = getGoalDirectionOfRobot();
		this.resultList = getPositions();
	}
	
	//methods
	
	//get resultList
	private ArrayList<Double> getPositions(){ //uses class RobotRotator to generate resultList
		ArrayList<Double> result = new ArrayList<>();
		RobotRotator rotator = new RobotRotator(initialRotation, goalDirectionOfRobot);
		result = rotator.getOrientationList();
		return result;
		
	}
	//get initial value for rotation
	private double getInitialRotation() {
		return ps.getInitialRobotConfig().getOrientation();
	}
	
	//get the goal direction of the robot
	
	private char getGoalDirectionOfRobot() throws IOException {
		PathForRobot p = (PathForRobot) Solver.getPathsForRobotBeforeMovingBox().get(index);
		return p.getDirectionOfRobot();
	}
	
	/**
	 * Getters
	 */
	public ArrayList<Double> getResultList(){
		return resultList;
	}
	
	//main for testing
	public static void main(String[] args) throws IOException {
		ProblemSpec ps = new ProblemSpec();
		Solver solver = new Solver(ps);
		InitialRotationPathForRobot i = new InitialRotationPathForRobot(solver.getGrid().getPS(), 0);
		System.out.println(i.getResultList());
	}
	
	
	

}