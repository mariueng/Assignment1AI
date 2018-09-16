package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.sun.scenario.effect.impl.prism.PrCropPeer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import problem.Box;
import problem.MovingObstacle;
import problem.StaticObstacle;

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
	private Grid grid;
	private ArrayList<Rectangle2D> rectangles = new ArrayList<>();
	
	//constructor
	public Mover(ArrayList<Point2D> movingBoxOriginalPath, Point2D initialPositionOfRobot, double initialRotationOfRobot, double w, Grid grid) {
		this.w = w;
		this.grid = grid;
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
	public void nextStep() {
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
				double robotAlpha = -1.0;
				if(dirOfLastStep=='u' || dirOfLastStep =='d') {
					robotAlpha = 0.0;
				}
				else {
					robotAlpha = 1.57;
				}
				ArrayList<Double> step = new ArrayList<>();
				step.addAll(Arrays.asList(xRobot, yRobot, robotAlpha, xBox, yBox));
				resultPathCombined.add(step);
			}
			
		}
	}
	
	//METHOD GOING DOWN
	private ArrayList<ArrayList<Double>> goDown(double rotation, double length, double startX, double startY, ArrayList<ArrayList<Double>> resultList){
		for(double i =0; i<length; i+=0.001) {
			ArrayList<Double> step = new ArrayList<>();
			step.addAll(Arrays.asList(rotation, startX, startY-i));
			resultList.add(step);
		}
		return resultList;
	}
	
	//METHOD GOING UP
	private ArrayList<ArrayList<Double>> goUp(double rotation, double length, double startX, double startY, ArrayList<ArrayList<Double>> resultList){
		for(double i =0; i<length; i+=0.001) {
			ArrayList<Double> step = new ArrayList<>();
			step.addAll(Arrays.asList(rotation, startX, startY+i));
			resultList.add(step);
		}
		return resultList;
	}
	//METHOD GOING LEFT
	private ArrayList<ArrayList<Double>> goLeft(double rotation, double length, double startX, double startY, ArrayList<ArrayList<Double>> resultList){
		for(double i =0; i<length; i+=0.001) {
			ArrayList<Double> step = new ArrayList<>();
			step.addAll(Arrays.asList(rotation, startX-i, startY));
			resultList.add(step);
		}
		return resultList;
	}
	//METHOD GOING RIGHT
	private ArrayList<ArrayList<Double>> goRight(double rotation, double length, double startX, double startY, ArrayList<ArrayList<Double>> resultList){
		for(double i =0; i<length; i+=0.001) {
			ArrayList<Double> step = new ArrayList<>();
			step.addAll(Arrays.asList(rotation, startX+i, startY));
			resultList.add(step);
		}
		return resultList;
	}
	
	//METHOD ROTATE
	private ArrayList<ArrayList<Double>> rotate(double initRotation, char resultDir, double startX, double startY, ArrayList<ArrayList<Double>> resultList){
		RobotRotator r = new RobotRotator(initRotation, resultDir);
		ArrayList<Double> orientationList = r.getOrientationList();
		int numberOfSteps = orientationList.size();
		for(int i =0; i<numberOfSteps; i++) {
			ArrayList<Double> step = new ArrayList<>();
			step.addAll(Arrays.asList(orientationList.get(i), startX, startY));
			resultList.add(step);
		}
		return resultList;
	}
	
	//check if collisionfree path
	private boolean checkIfCollisionFreeTurn(double x, double y, char startDir, char goalDir) {
		double xRobot;
		double yRobot;
		boolean result = true;
		
		if(startDir == 'u') { //from bottom 
			xRobot = x;
			yRobot = y-(w/2);
		} else if(startDir =='d') { //from top
			xRobot = x;
			yRobot = y + (w/2);
		}else if(startDir == 'l') { //from right
			yRobot = y;
			xRobot = x +w/2;
		} else { //from left
			yRobot = y;
			xRobot = x -(w/2);
		}
		Rectangle2D area = new Rectangle2D.Double(xRobot, yRobot,w/2, w/2);
		rectangles.add(area);
		//check for movingObstacles
		for(Box obstacle:grid.getPS().getMovingObstacles()) {
			if(obstacle.getRect().intersects(area)) {
				result = false;
			}
		}
		
		//check for static obstacles
		for(StaticObstacle obstacle:grid.getPS().getStaticObstacles()) {
			if(obstacle.getRect().intersects(area)) {
				result = false;
			}
		}
		
		return result;
	}
	
	/*
	 * Make a list with the primitves a robot needs to take when direction changes
	 * The output is a matrix [(x,y,a), (x,y,a) .... (x,y,a)] describing position of the robot
	 */
	private ArrayList<ArrayList<Double>> changeDirectionOfRobot(Double startX, double startY, char startDir, char goalDir){
		ArrayList<ArrayList<Double>> resultList = new ArrayList<>();
		double secondX;
		double secondY;
		int normalProcedure = 1;
		int collisionProcedure = 2;
		int procedure;
		if(checkIfCollisionFreeTurn(startX, startY, startDir, goalDir)) {
			procedure = normalProcedure;
		} else {
			procedure = collisionProcedure;
		}
		
		
		//find out what instance of turn you want:
		if(startDir == 'u' && goalDir == 'r') { //from bottom to left side
			if(procedure == normalProcedure) { //no collision detected
				//go down w/2
				resultList = goDown(0.0, w/2, startX, startY, resultList);
				secondY = startY-(w/2);
				//rotate
				resultList = rotate(0, 'u', startX, secondY, resultList);
				//go left w/2
				resultList = goLeft(1.57, w/2, startX, secondY, resultList);
				secondX = startX -(w/2);
				//go up w
				resultList = goUp(1.57, w, secondX,secondY, resultList);
			}
			else {//detected collision
				//go left w
				resultList = goLeft(0.0, w, startX, startY, resultList);
				secondX = startX-(w);
				//rotate
				resultList = rotate(0, 'u', secondX, startY, resultList);
				//go up w/2
				resultList = goUp(1.57, w/2, secondX,startY, resultList);
				secondY = startY+(w/2);
				//go right w/2
				resultList = goRight(1.57, w/2, secondX, secondY, resultList);
			}
		}
		else if(startDir == 'u' && goalDir == 'l') { //from bottom to right side
			if(procedure == normalProcedure) {
				//go down w/2
				resultList = goDown(0.0, w/2, startX, startY, resultList);
				secondY = startY-(w/2);
				//rotate
				resultList = rotate(0, 'u', startX, secondY, resultList);
				//go right w/2
				resultList = goRight(1.57, w/2, startX, secondY, resultList);
				secondX = startX +(w/2);
				//go up w
				resultList = goUp(1.57, w, secondX,secondY, resultList);
			} else {
				//go right w
				resultList = goRight(0.0, w, startX, startY, resultList);
				secondX = startX+(w);
				//rotate
				resultList = rotate(0, 'u', secondX, startY, resultList);
				//go up w/2
				resultList = goUp(1.57, w/2, secondX,startY, resultList);
				secondY = startY+(w/2);
				//go left w/2
				resultList = goLeft(1.57, w/2, secondX, secondY, resultList);
			}
			
		}
		else if(startDir == 'd' && goalDir == 'r') { //from top to left side
			if(procedure == normalProcedure) {
				//go up w/2
				resultList = goUp(0.0, w/2, startX,startY, resultList);
				secondY = startY+(w/2);
				//rotate
				resultList = rotate(0, 'u', startX, secondY, resultList);
				//go left w/2
				resultList = goLeft(1.57, w/2, startX, secondY, resultList);
				secondX = startX -(w/2);
				//go down w
				resultList = goDown(1.57, w, secondX, secondY, resultList);
			} else {
				//go left w
				resultList = goLeft(0.0, w, startX,startY, resultList);
				secondX = startX-(w);
				//rotate
				resultList = rotate(0, 'u', secondX, startY, resultList);
				//go down w/2
				resultList = goDown(1.57, w/2, secondX, startY, resultList);
				secondY = startY -(w/2);
				//go right w/2
				resultList = goRight(1.57, w/2, secondX, secondY, resultList);
			}
			
		}
		else if(startDir == 'd' && goalDir =='l') {//from top to right side
			if(procedure == normalProcedure) {
				//go up w/2
				resultList = goUp(0.0, w/2, startX,startY, resultList);
				secondY = startY+(w/2);
				//rotate
				resultList = rotate(0, 'u', startX, secondY, resultList);
				//go right w/2
				resultList = goRight(1.57, w/2, startX, secondY, resultList);
				secondX = startX +(w/2);
				//go down w
				resultList = goDown(1.57, w, secondX, secondY, resultList);
			} else {
				//go right w
				resultList = goRight(0.0, w, startX,startY, resultList);
				secondX = startX+(w);
				//rotate
				resultList = rotate(0, 'u', secondX, startY, resultList);
				//go down w/2
				resultList = goDown(1.57, w/2, secondX, startY, resultList);
				secondY = startY -(w/2);
				//go left w/2
				resultList = goLeft(1.57, w/2, secondX, secondY, resultList);
			}
			
		}
		else if(startDir == 'r' && goalDir=='u') {//from left to bottom
			if(procedure == normalProcedure) {
				//go left w/2
				resultList = goLeft(1.57, w/2, startX, startY, resultList);
				secondX = startX-(w/2);
				//rotate
				resultList = rotate(1.57, 'f', secondX, startY, resultList);
				//go down w/2
				resultList = goDown(0.0, w/2, secondX, startY, resultList);
				secondY = startY -(w/2);
				//go right w
				resultList = goRight(0.0, w, secondX, secondY, resultList);
			}else {
				//go down w
				resultList = goDown(1.57, w, startX, startY, resultList);
				secondY = startY-(w);
				//rotate
				resultList = rotate(1.57, 'f', startX, secondY, resultList);
				//go right w/2
				resultList = goRight(0.0, w/2, startX, secondY, resultList);
				secondX = startX +(w/2);
				//go up w/2
				resultList = goRight(0.0, w/2, secondX, secondY, resultList);
			}
			
		}
		else if(startDir =='r' && goalDir =='d') {//from left to top
			if(procedure ==normalProcedure) { 
				//go left w/2
				resultList = goLeft(1.57, w/2, startX, startY, resultList);
				secondX = startX-(w/2);
				//rotate
				resultList = rotate(1.57, 'f', secondX, startY, resultList);
				//go up w/2
				resultList = goUp(0.0, w/2, secondX,startY, resultList);
				secondY = startY +(w/2);
				//go right w
				resultList = goRight(0.0, w, secondX, secondY, resultList);
			} else {
				//go up w
				resultList = goUp(1.57, w, startX, startY, resultList);
				secondY = startY+(w);
				//rotate
				resultList = rotate(1.57, 'f', startX, secondY, resultList);
				//go right w/2
				resultList = goRight(0.0, w/2, startX, secondY, resultList);
				secondX = startX +(w/2);
				//go down w/2
				resultList = goDown(0.0, w/2, secondX, secondY, resultList);
			}
				
		}
		else if(startDir =='l' && goalDir == 'u') {//from right to bottom
			if(procedure == normalProcedure) {
				//go right w/2
				resultList = goRight(1.57, w/2, startX, startY, resultList);
				secondX = startX+(w/2);
				//rotate
				resultList = rotate(1.57, 'f', secondX, startY, resultList);
				//go down w/2
				resultList = goDown(0.0, w/2, secondX, startY, resultList);
				secondY = startY -(w/2);
				//go left w
				resultList = goLeft(0.0, w, secondX, secondY, resultList);
			}else {
				//go down w
				resultList = goDown(1.57, w, startX, startY, resultList);
				secondY = startY-(w);
				//rotate
				resultList = rotate(1.57, 'f', startX, secondY, resultList);
				//go left w/2
				resultList = goLeft(0.0, w/2, startX, secondY, resultList);
				secondX = startX -(w/2);
				//go up w/2
				resultList = goUp(0.0, w/2, secondX, secondY, resultList);
			}
			
		}
		else if(startDir =='l' && goalDir=='d') {//from right to top
			if(procedure == normalProcedure) {
				//go right w/2
				resultList = goRight(1.57, w/2, startX, startY, resultList);
				secondX = startX+(w/2);
				//rotate
				resultList = rotate(1.57, 'f', secondX, startY, resultList);
				//go up w/2
				resultList = goUp(0.0, w/2, secondX,startY, resultList);
				secondY = startY +(w/2);
				//go left w
				resultList = goLeft(0.0, w, secondX, secondY, resultList);
			}else {
				//go up w
				resultList = goUp(1.57, w, startX, startY, resultList);
				secondY = startY+(w);
				//rotate
				resultList = rotate(1.57, 'f', startX, secondY, resultList);
				//go left w/2
				resultList = goLeft(0.0, w/2, startX, secondY, resultList);
				secondX = startX -(w/2);
				//go down w/2
				resultList = goDown(0.0, w/2, secondX, secondY, resultList);
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
	
	//getters
	public Point2D getFinalPositionOfRobot() {
		double x = resultPathCombined.get(-1).get(0);
		double y = resultPathCombined.get(-1).get(1);
		Point2D position = new Point2D.Double(x,y);
		return position;
	}
	public ArrayList<ArrayList<Double>> getResultList(){
		return resultPathCombined;
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
	

}
