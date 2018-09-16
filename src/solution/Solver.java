package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import problem.Box;
import problem.ProblemSpec;
import problem.StaticObstacle;

public class Solver {
	
	/**
	 * The class for solving the problem an generating the output file
	 */
	
	//fields
	private ProblemSpec ps;
	private double w;
	private Grid grid;
	private int numberOfPrimitives; //number of steps in the final output file
	private int numberOfMovingBoxes;
	private List<Box> movingBoxes, movingObstacles;
	private List<StaticObstacle> staticObstacles;
	private int numberOfMovingObstacles;
	private int numberOfStaticObstacles;
	
	private static ArrayList<PathForRobot> pathsForRobotBeforeMovingBox = new ArrayList<>();
	// add last orientation for robot to use for next path
	public ArrayList<Double> initialRotationValue = new ArrayList<>();
	
	// add last position of robot to use as initial in next path
	
	private ArrayList<ArrayList<Double>> outPut = new ArrayList<ArrayList<Double>>();
	
	//constructor
	public Solver(ProblemSpec ps) throws IOException {
		this.ps = ps;
        w = ps.getRobotWidth();
        this.movingBoxes = ps.getMovingBoxes();
        Collections.reverse(movingBoxes);
        Collections.reverse(ps.getMovingBoxEndPositions());
        this.numberOfMovingBoxes = movingBoxes.size();
        this.movingObstacles = ps.getMovingObstacles();
        this.numberOfMovingObstacles = movingObstacles.size();
        this.staticObstacles = ps.getStaticObstacles();
    	this.numberOfStaticObstacles = staticObstacles.size();
        //generate first step based on initial conditions
    	makeInitialConditionOfBoard(); // maybe unnecessary method now?
        
        //run
		run();
		writeSolutionToFile(); //add outputFileName
	}
	
	/**
	 * METHODS
	 */
	
	

	private void run() throws IOException {
		makeGrid(ps);
		Box currentBoxToMove;
		PathForMovingBox pathForMovingBox;
		MovingBoxDiscretizer discMovingBoxPath; //path for box
		PathForRobot pathForRobot;
		RobotPathDiscretizer discPathForRobot; //path for robot to box
		InitialRotationPathForRobot robotRotation; //robot rotation before staring its path to the box
		
		for (int i = 0; i < numberOfMovingBoxes; i++) {
			int indexOFLastStep = outPut.size()-1; //the index value of the last step we took moving the previous box
			currentBoxToMove = movingBoxes.get(i);
			pathForMovingBox = new PathForMovingBox(currentBoxToMove, grid);
			ArrayList<Node> movingBoxPathNodes = pathForMovingBox.getPathForMovingBox(); //list of nodes describing path for moving box
			discMovingBoxPath = new MovingBoxDiscretizer(movingBoxPathNodes); //discrete path for movingbox
			double xRobot = outPut.get(indexOFLastStep).get(0);
			double yRobot = outPut.get(indexOFLastStep).get(1);
			pathForRobot = new PathForRobot(xRobot, yRobot, movingBoxPathNodes, this.grid); 
			pathsForRobotBeforeMovingBox.add(pathForRobot);
			ArrayList<Node> robotPathNodes = pathForRobot.getRobotPath();
			discPathForRobot = new RobotPathDiscretizer(robotPathNodes);
			double initOrientationValue = outPut.get(indexOFLastStep).get(2);
			robotRotation = new InitialRotationPathForRobot(currentBoxToMove, initOrientationValue, i);
			
			
			orientRobot(robotRotation);
			moveRobotToBox(discPathForRobot);
			moveRobotAndBox(i, discMovingBoxPath);
			goBackBeforGettingNewBox(discMovingBoxPath);
			
			//change nodes who was MB to FS and vice versa
			Point2D startPoint = movingBoxes.get(i).getPos();
			Point2D endPoint = ps.getMovingBoxEndPositions().get(i);
			changeGroundTypeForOldMB(i);
			changeGroundTypeForNewMB(i);
			}
		}
		//iterate over each box and add each steps to outputList
		/*
		 *  For each moving box do:
		 *  1. find discrete path for box to goal
		 *  2. find (discrete) orientation for robot to align to next box to move
		 *  3. find path from robot pos to box (special case initial)
		 *  5. discretize robotpath 
		 *  6. make an ArrayList<Double> of the given state and writeToFile ????
		 *  7. fill next lists with values from the last line of outputfile
		 *  	this is done so that the last value in the last move corresponds
		 *  	with the first move in the next path.
		 *  8. Update board with the changed nodes (MB to FS and vice versa)
		 */


	//make first step (initial condition of board)
	private void makeInitialConditionOfBoard(){
		ArrayList<Double> result = new ArrayList<>();
		double xRobot = this.ps.getInitialRobotConfig().getPos().getX();
		double yRobot = this.ps.getInitialRobotConfig().getPos().getY();
		double alpha = this.ps.getInitialRobotConfig().getOrientation();
		result.addAll(Arrays.asList(xRobot, yRobot, alpha));
		for(int i =0; i<numberOfMovingBoxes; i++) {
			double xBox = ps.getMovingBoxes().get(i).getPos().getX()+w/2;
			double yBox = ps.getMovingBoxes().get(i).getPos().getY()+w/2;
			result.addAll(Arrays.asList(xBox, yBox));
		}
		for(int i = 0; i<numberOfMovingObstacles; i++) {
			double width = ps.getMovingObstacles().get(i).getWidth();
			double xObs = ps.getMovingObstacles().get(i).getPos().getX() + width/2;
			double yObs = ps.getMovingObstacles().get(i).getPos().getY() + width/2;
			result.addAll(Arrays.asList(xObs, yObs));
		}
		outPut.add(result);
	}
	
	//method for changing ground types for nodes that lies within the area of the moving box before search
	private void changeGroundTypeForOldMB(int i) {
		double x = ps.getMovingBoxes().get(i).getPos().getX()-w/2;
		double y = ps.getMovingBoxes().get(i).getPos().getY()-w/2;
		x = doubleFormatter(x);
		y = doubleFormatter(y);
		Rectangle2D.Double r = new Rectangle2D.Double(x,y,2*w+0.01, 2*w+0.01); //fake rectangle around end position
		for(Node n : grid.getVerticesInMovingBoxes()) {
			Point2D node = new Point2D.Double(n.getxValue(), n.getyValue());
			if(r.contains(node)) {
				boolean markedLater = n.getisMarkedMBBySomeLaterMovingBox(i, ps);
				if(!(markedLater)) {
					n.setGroundType("FS");
				}
			}
		}
	}
	//change groundtype for nodes that lies within the final position of the moving box
	private void changeGroundTypeForNewMB(int i) {
		double x = ps.getMovingBoxEndPositions().get(i).getX();
		double y = ps.getMovingBoxEndPositions().get(i).getY();
		x = doubleFormatter(x);
		y = doubleFormatter(y);
		Rectangle2D.Double r = new Rectangle2D.Double(x-w/2,y-w/2,2*w, 2*w); //fake rectangle around end position
		for(Node n : grid.getVertices()) {
			Point2D node = new Point2D.Double(n.getxValue(), n.getyValue());
			if(r.contains(node)) {
				n.setGroundType("MB");
			}
		}
	}
	
	//calculate distance
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}
	
	
	
	
	/**
	 * Move robot and box
	 */
	private void moveRobotAndBox(int relativePos, MovingBoxDiscretizer discMovingBoxPath) {
		int indexOfLastStep = outPut.size()-1;
		ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
		
		double xRobot = lastStep.get(0);
		double yRobot = lastStep.get(1);
		double alpha = lastStep.get(2);
		Point2D robotPos = new Point2D.Double(xRobot, yRobot);
		ArrayList<Point2D> boxPath = discMovingBoxPath.getDiscretePathForMovingBox();
		Mover mover = new Mover(boxPath, robotPos, alpha, w, grid);
		mover.nextStep();
		ArrayList<ArrayList<Double>> steps = mover.getResultList();
		int numberOfSteps = steps.size();
		int indexForXPositionOfBox = (2*relativePos)+3;
		int indexForYPositionOfBox = (2*relativePos)+4;
		for(int j=0; j<numberOfSteps; j++) {
			ArrayList<Double> step = new ArrayList<>();
			for(double v:lastStep) {
				step.add(v);
			}
			double xR = steps.get(j).get(0);
			double yR = steps.get(j).get(1);
			double a = steps.get(j).get(2);
			double xB = steps.get(j).get(3);
			double yB = steps.get(j).get(4);
			step.set(0, xR);
			step.set(1, yR);
			step.set(2, a);
			step.set(indexForXPositionOfBox, xB);
			step.set(indexForYPositionOfBox, yB);
			ArrayList<Double> resultStep = new ArrayList<>();
			for(Double d:step) {
				double b = doubleFormatter(d);
				resultStep.add(b);
			}
			outPut.add(resultStep);
		}
	}
	
	/**
	 * Move robot to box
	 */
	private void moveRobotToBox(RobotPathDiscretizer discRobotPath) {
		int indexOfLastStep = outPut.size()-1;
		ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
		ArrayList<Point2D> stepsForMoving = discRobotPath.getDiscretePathForRobot();
		int numberOfSteps = stepsForMoving.size();
		for(int j = 0; j<numberOfSteps; j++) {
			ArrayList<Double> step = new ArrayList<>();
			for(double v:lastStep) {
				step.add(v);
			}
			double x = stepsForMoving.get(j).getX();
			double y = stepsForMoving.get(j).getY();
			step.set(0, x);
			step.set(1, y);
			ArrayList<Double> resultStep = new ArrayList<>();
			for(Double d:step) {
				double b = doubleFormatter(d);
				resultStep.add(b);
			}
			outPut.add(resultStep);
		}
		
	}
	
	
	/**
	 * Orient robot before moving it to the box
	 * @throws IOException 
	 */
	private void orientRobot(InitialRotationPathForRobot initRotPathForRobot) throws IOException {
		int indexOfLastStep = outPut.size()-1;
		ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
		ArrayList<Double> stepsForOrientation = initRotPathForRobot.getResultList();
		int numberOfSteps = stepsForOrientation.size();
		for(int j = 0; j<numberOfSteps; j++) {
			ArrayList<Double> step = new ArrayList<>();
			for(double v:lastStep) {
				step.add(v);
			}
			double value = stepsForOrientation.get(j);
			step.set(2, value);
			ArrayList<Double> resultStep = new ArrayList<>();
			for(Double d:step) {
				double b = doubleFormatter(d);
				resultStep.add(b);
			}
			outPut.add(resultStep);
		}
	}
	
	/**
	 * Go back a distance w/2 after finishing one box
	 * @param discMovingBoxPath
	 */
	//before making path for robot in order to come to next box, it will have to go back w/2 such that it can rotate without collision
		private void goBackBeforGettingNewBox(MovingBoxDiscretizer discMovingBoxPath) {
			int indexOfLastStep = outPut.size()-1;
			int indexOfSecondLastStep = outPut.size()-3;
			double xLast = outPut.get(indexOfLastStep).get(0);
			double yLast = outPut.get(indexOfLastStep).get(1);
			double xSecond = outPut.get(indexOfSecondLastStep).get(0);
			double ySecond =  outPut.get(indexOfSecondLastStep).get(1);
			ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
			
			if(xLast > xSecond) { //need to go left w/2
				for(double j =0; j<w/2; j+=0.001) {
					ArrayList<Double> nextStep = new ArrayList<>();
					for(Double value:lastStep) {
						nextStep.add(value);
					}
					nextStep.set(0, xLast-j);
					outPut.add(nextStep);
				}
			}
			else if(xLast < xSecond ) {//need to go right w/2
				for(double j =0; j<w/2; j+=0.001) {
					ArrayList<Double> nextStep = new ArrayList<>();
					for(Double value:lastStep) {
						nextStep.add(value);
					}
					nextStep.set(0, xLast+j);
					outPut.add(nextStep);
				}
			}
			else if(yLast > ySecond) { //need to go down w/2
				for(double j =0; j<w/2; j+=0.001) {
					ArrayList<Double> nextStep = new ArrayList<>();
					for(Double value:lastStep) {
						nextStep.add(value);
					}
					nextStep.set(1, yLast-j);
					outPut.add(nextStep);
				}
				
			}
			else if(yLast < ySecond) { //need to go up w/2
				for(double j =0; j<w/2; j+=0.001) {
					ArrayList<Double> nextStep = new ArrayList<>();
					for(Double value:lastStep) {
						nextStep.add(value);
					}
					nextStep.set(1, yLast+j);
					outPut.add(nextStep);
				}
			}
			
		}
		
		
		

	/**
	 * Make grid
	 */
	private void makeGrid(ProblemSpec ps) {
		Grid grid = new Grid(ps);
		this.grid = grid;
	}
	
	

	
	/**
	 * writeSolutionToFile
	 */
	
	public void writeSolutionToFile() throws IOException { //Add outputFileName
		//absolute path 
		String path = new File("").getAbsolutePath();
		//String output = path "\"+outputFileName
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\output3.txt");
		int numberOfPrimitiveSteps = outPut.size();
		BufferedWriter writer = new BufferedWriter(file);
		writer.write(numberOfPrimitiveSteps + "\n");
		for(ArrayList<Double> step : outPut ) {
			for(Double value : step) {
				writer.write(value + " ");
			}
			writer.write("\n");
		}
		writer.close();
	}
	
	
	/**
	 * GETTERS
	 */
	private ProblemSpec getProblemSpec() {
		return ps;
	}
	
//	// Retrieving discrete paths for robot outside this class
//	public static ArrayList<ArrayList<Point2D>> getDiscPathsForRobotBeforeMovingBox() {
//		return resultMoveRobotForNextBox;
//	}
	
	
	//formatter for making pretty numbers
	public double doubleFormatter(double number) {
		NumberFormat formatter = new DecimalFormat("#0.000");
		String formatted = formatter.format(number);
		double formattedNumber = Double.parseDouble(formatted);
		return formattedNumber;
	}
	
	// Retrieving paths for robot as PathForRobot objects outside this class
	public static ArrayList<PathForRobot> getPathsForRobotBeforeMovingBox() {
		return pathsForRobotBeforeMovingBox;
	}
		
	public Grid getGrid() {
		return grid;
	}
	
	/**main for testing
	 * @throws IOException 
	 * 
	 */
	
	public static void main(String[] args) throws IOException {
		ProblemSpec ps = new ProblemSpec();
		try {
            ps.loadProblem("input1.txt");
            Solver s = new Solver(ps);
            ps.loadSolution("output1.txt");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");
		Solver solver = new Solver(ps);
		
	}
	
	
	

}
