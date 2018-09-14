package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.geom.Point2D;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Mover {
	
	/**
	 * Class for managing the actual moving of the robot together with a moving box
	 * Input: the discretized path of the movingbox we want to move
	 * Output: Lists of list. [(xr,yr,a,xb,yb), (xr,yr,a,xb,yb),...,(xr,yr,a,xb,yb)]
	 * That list describes the robot and box position for the whole move
	 */
	
	//fields
	private ArrayList<Point2D> movingBoxOriginalPath;
	private Point2D initialPositionOfRobot;
	private ArrayList<ArrayList<Double>> resultPathCombined = new ArrayList<>();
	private double w;
	
	//constructor
	public Mover(ArrayList<Point2D> movingBoxOriginalPath, Point2D initialPositionOfRobot, double initialRotationOfRobot, double w) {
		this.w = w;
		this.movingBoxOriginalPath = movingBoxOriginalPath;
		this.initialPositionOfRobot = initialPositionOfRobot;
		//adding first step to resultPathCombines
		ArrayList<Double> firstStep = new ArrayList<>();
		Point2D pos = initialPositionOfRobot;
		double xRobot = pos.getX();
		double yRobot = pos.getY();
		double alpha = initialRotationOfRobot;
		double xBox = movingBoxOriginalPath.get(0).getX();
		double yBox = movingBoxOriginalPath.get(0).getY();
		firstStep.addAll(Arrays.asList(xRobot, yRobot, alpha, xBox, yBox));
		resultPathCombined.add(firstStep);
	}
	
	/*
	 * METHODS
	 */
	private void nextStep() {
		for(int i = 0; i<movingBoxOriginalPath.size()-1; i++) {
			char dirOfNextStep = getDirectionOfNextStep(movingBoxOriginalPath.get(i), movingBoxOriginalPath.get(i+1));
			char dirOfLastStep = dirOfNextStep;
			if(i != 0) {
				dirOfLastStep = getDirectionOfNextStep(movingBoxOriginalPath.get(i-1), movingBoxOriginalPath.get(i));
			}
			double xBox = movingBoxOriginalPath.get(i).getX();
			double yBox = movingBoxOriginalPath.get(i).getY();
			double xRobot;
			double yRobot;
			//find robot position
			if(dirOfLastStep=='u') {
				xRobot = xBox;
				yRobot = yBox-w/2;
			}
			else if(dirOfLastStep =='d') {
				xRobot = xBox;
				yRobot = yBox+w/2;
			}
			else if(dirOfLastStep == 'r') {
				xRobot = xBox - w/2;
				yRobot = yBox;
			}
			else{
				xRobot = xBox + w/2;
				yRobot = yBox;
			}
			
			//when changing direction 
			if(dirOfLastStep != dirOfNextStep) {
				ArrayList<ArrayList<Double>> robotTurnList = new ArrayList<>();
				robotTurnList = changeDirectionOfRobot(xRobot, yRobot, dirOfLastStep, dirOfNextStep);
				for(ArrayList<Double> position :robotTurnList){
					double robotX = position.get(1);
					double robotY = position.get(2);
					double robotAlpha = position.get(0);
					ArrayList<Double> step = new ArrayList<>();
					step.addAll(Arrays.asList(robotX, robotY, robotAlpha, xBox, yBox));
					resultPathCombined.add(step);
				}
			}
			
			//when continuing in the same direction
			if(dirOfLastStep == dirOfNextStep) {
				double robotAlpha = resultPathCombined.get(i).get(2); //get alpha from the last step
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(xRobot, yRobot, robotAlpha, xBox, yBox));
				resultPathCombined.add(step);
			}
			
		}
	}
	
	/*
	 * Make a list with the primitves a robot needs to take when direction changes
	 * The output is a matrix [(x,y,a), (x,y,a) .... (x,y,a)] describing position of the robot
	 */
	private ArrayList<ArrayList<Double>> changeDirectionOfRobot(Double startX, double startY, char startDir, char goalDir){
		ArrayList<ArrayList<Double>> resultList = new ArrayList<>();
		double secondX;
		double secondY;
		
		//find out what instance of turn you want:
		if(startDir == 'u' && goalDir == 'r') { //from bottom to left side
			//go down w/2
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
				resultList.add(step);
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
			//go down w/2
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
				resultList.add(step);
 			}
			//go right w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX+i, secondY));
				resultList.add(step);
			}
			secondX = startX +(w/2);
			//go up w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, secondX, secondY+i));
				resultList.add(step);
			}
			
		}
		else if(startDir == 'd' && goalDir == 'r') { //from top to left side
			//go up w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, startX, startY+i));
				resultList.add(step);
			}
			secondY = startY+(w/2);
			//rotate
			RobotRotator r = new RobotRotator(0, 'u');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), startX, secondY));
				resultList.add(step);
 			}
			//go left w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX-i, secondY));
				resultList.add(step);
			}
			secondX = startX -(w/2);
			//go down w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, secondX, secondY-i));
				resultList.add(step);
			}
			
		}
		else if(startDir == 'd' && goalDir =='l') {//from top to right side
			//go up w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, startX, startY+i));
				resultList.add(step);
			}
			secondY = startY+(w/2);
			//rotate
			RobotRotator r = new RobotRotator(0, 'u');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), startX, secondY));
				resultList.add(step);
 			}
			//go left w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX-i, secondY));
				resultList.add(step);
			}
			secondX = startX -(w/2);
			//go down w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, secondX, secondY-i));
				resultList.add(step);
			}
			
		}
		else if(startDir == 'r' && goalDir=='u') {//from left to bottom
			//go left w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX-i, startY));
				resultList.add(step);
			}
			secondX = startX-(w/2);
			//rotate
			RobotRotator r = new RobotRotator(1.57, 'f');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), secondX, startY));
				resultList.add(step);
 			}
			//go down w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX, startY-i));
				resultList.add(step);
			}
			secondY = startY -(w/2);
			//go right w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX+i, secondY));
				resultList.add(step);
			}
			
		}
		else if(startDir =='r' && goalDir =='d') {//from left to top
			//go left w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX-i, startY));
				resultList.add(step);
			}
			secondX = startX-(w/2);
			//rotate
			RobotRotator r = new RobotRotator(1.57, 'f');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), secondX, startY));
				resultList.add(step);
 			}
			//go up w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX, startY+i));
				resultList.add(step);
			}
			secondY = startY +(w/2);
			//go right w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX+i, secondY));
				resultList.add(step);
			}
			
		}
		else if(startDir =='l' && goalDir == 'u') {//from right to bottom
			//go right w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX+i, startY));
				resultList.add(step);
			}
			secondX = startX+(w/2);
			//rotate
			RobotRotator r = new RobotRotator(1.57, 'f');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), secondX, startY));
				resultList.add(step);
 			}
			//go down w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX, startY-i));
				resultList.add(step);
			}
			secondY = startY -(w/2);
			//go left w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX-i, secondY));
				resultList.add(step);
			}
			
		}
		else if(startDir =='l' && goalDir=='d') {//from right to top
			//go right w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(1.57, startX+i, startY));
				resultList.add(step);
			}
			secondX = startX+(w/2);
			//rotate
			RobotRotator r = new RobotRotator(1.57, 'f');
			ArrayList<Double> orientationList = r.getOrientationList();
			int numberOfStep = orientationList.size();
			for(int i = 0;i<numberOfStep;i++) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(orientationList.get(i), secondX, startY));
				resultList.add(step);
 			}
			//go up w/2
			for(double i =0.0; i<w/2; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX, startY+i));
				resultList.add(step);
			}
			secondY = startY -(w/2);
			//go left w
			for(double i =0.0; i<w; i+=0.001) {
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(0.0, secondX-i, secondY));
				resultList.add(step);
			}
		}
			
		
		return resultList;
	}
	
	//get direction of next step
	private char getDirectionOfNextStep(Point2D from, Point2D to) {
		char r = ' ';
		double startX = from.getX();
		double startY = from.getY();
		double goalX = to.getX();
		double goalY = to.getY();
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
	
	//write to file
	public void writeToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-Robot" + "\t" +"Y-Robot" + "\t" + "Alpha" + "\t" + "X-Box" + "\t" +"Y-Box" +"\n");
		for(ArrayList<Double> step : resultPathCombined ) {
			writer.write(step.get(0) +"\t" + step.get(1)+"\t"+step.get(2)+"\t"+step.get(3)+"\t"+step.get(4)+"\n");
		}
		writer.close();
	}
	
	//main for testing
	public static void main(String[] args) throws IOException {
		Grid grid = new Grid();
		PathForAllMovingBoxes p = new PathForAllMovingBoxes(grid);
		MovingBoxDiscretizer d = new MovingBoxDiscretizer(p.getPathForAllMovingBoxes());
		ArrayList<Point2D> list = d.getDiscretPathsForMovingBoxes().get(0);
		Point2D robotPos = new Point2D.Double(0.125,0.15);
		double rotation = 1.57;
		double w = grid.getLength();
		Mover mover = new Mover(list, robotPos, rotation, w);
		mover.nextStep();
		mover.writeToFile();
		//System.out.println(mover.resultPathCombined);
		
		
		
		
	}
	

}
