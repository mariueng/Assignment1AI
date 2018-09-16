package solution;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
	private int numberOfPrimitives;
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
        this.numberOfMovingBoxes = movingBoxes.size();
        this.movingObstacles = ps.getMovingObstacles();
        this.numberOfMovingObstacles = movingObstacles.size();
        this.staticObstacles = ps.getStaticObstacles();
    	this.numberOfStaticObstacles = staticObstacles.size();
        //generate first step
    	makeInitialConditionOfBoard(); // maybe unnecessary method now?
        
        //run
		run();
		writeSolutionToFile();
	}
	
	/**
	 * METHODS
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
	
	
	
	//run
	private void run() throws IOException {
		makeGrid(ps);
		// not used anymore
//		makeDiscPathsForMovingBoxes();
//		makeDiscRobotPathBeforeMovingBoxList();
//		makeRobotInitialOrientationList();
		
		// box
		Box currentBoxToMove;
		PathForMovingBox pathForMovingBox;
		MovingBoxDiscretizer discMovingBoxPath;
		
		// robot
		PathForRobot pathForRobot;
		RobotPathDiscretizer discPathForRobot;
		InitialRotationPathForRobot initRotPathForRobot;
		
		// final ArrayList<Double> representing the last step of the last path moved
		// Format: LastOrient, LastXRobot, LastYRobot, ...
		ArrayList<Double> helpList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			helpList.add(0.0);
		}
		
		for (int i = 0; i < numberOfMovingBoxes; i++) {
			currentBoxToMove = movingBoxes.get(i);
			// find path for moving box
			pathForMovingBox = new PathForMovingBox(currentBoxToMove, grid);
			// Discretize moving box path
			discMovingBoxPath = new MovingBoxDiscretizer(pathForMovingBox.getPathForMovingBox());
			// find path for robot from robot pos to moving box pos, and
			// find correct orientation of robot according to next box to move
			if (i == 0) {
				// initial position for first path
				Point2D initRobotPos = ps.getInitialRobotConfig().getPos();
				pathForRobot = new PathForRobot(initRobotPos.getX(), initRobotPos.getY(), 
						pathForMovingBox.getPathForMovingBox(), this.grid);
				pathsForRobotBeforeMovingBox.add(pathForRobot);
				// initial orientation for first path
				initRotPathForRobot = new InitialRotationPathForRobot(currentBoxToMove, 
						ps.getInitialRobotConfig().getOrientation());
				helpList.set(0, initRotPathForRobot.getResultList().get(initRotPathForRobot.getResultList().size() - 1));
				Node n = pathForRobot.getRobotPath().get(pathForRobot.getRobotPath().size() - 1);
				helpList.set(1, n.getxValue());
				helpList.set(2, n.getyValue());
			} else {
				// initial position for next path
				pathForRobot = new PathForRobot(helpList.get(1), helpList.get(2), 
						pathForMovingBox.getPathForMovingBox(), this.grid);
				// initial orientation for next path
				initRotPathForRobot = new InitialRotationPathForRobot(currentBoxToMove, 
						helpList.get(0));
			}
			discPathForRobot = new RobotPathDiscretizer(pathForRobot.getRobotPath());
			orientRobot(initRotPathForRobot);
			moveRobotToBox(discPathForRobot);
			moveRobotAndBox(i, discMovingBoxPath);
			
			//change nodes who was MB to FS and vice versa
			ArrayList<Node> MovingBoxPathJustExecuted = pathForMovingBox.getPathForMovingBox();
			Node initialNode = MovingBoxPathJustExecuted.get(0);
			Node goalNode = MovingBoxPathJustExecuted.get(MovingBoxPathJustExecuted.size() - 1);
			changeGroundTypeForOldMB(initialNode, goalNode);
			changeGroundTypeForNewMB(goalNode);
			
			
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
//		for(int i = 0; i<numberOfMovingBoxes; i++) {
//			orientRobot(i);
//			moveRobotToBox(i);
//			moveRobotAndBox(i);
//			//change nodes who was MB to FS and vice versa
//			ArrayList<Node> MovingBoxPathJustExecuted = paths.getPathForAllMovingBoxes().get(i);
//			Node initialNode = MovingBoxPathJustExecuted.get(0);
//			Node goalNode = MovingBoxPathJustExecuted.get(MovingBoxPathJustExecuted.size() - 1);
//			changeGroundTypeForOldMB(initialNode, goalNode);
//			changeGroundTypeForNewMB(goalNode);
//		}

	}
	
	//method for changing ground types for nodes that lies within the area of the moving box before search
	private void changeGroundTypeForOldMB(Node initialNode, Node goalNode) {
		double x = initialNode.getxValue();
		double y = initialNode.getyValue();
		for(Node n : grid.getVertices()) {
			if(n.getGroundType().equals("MB")) {
				double distance = calculateDistanceBetweenTwoNodes(n, initialNode);
				if(distance < grid.getDistance()*2) {
					n.setGroundType("FS");
				}
			}
		}
	}
	//change groundtype for nodes that lies within the final position of the moving box
	private void changeGroundTypeForNewMB(Node goalNode) {
		double x = goalNode.getxValue();
		double y = goalNode.getyValue();
		double l = grid.getLength()/2;
		for(Node n : grid.getVertices()) {
			double distance = calculateDistanceBetweenTwoNodes(goalNode, n);
			if(distance <=l) {
				n.setGroundType("MB");
			}
		}
	}
	
	private double calculateDistanceBetweenTwoNodes(Node one, Node two) {
		return Math.sqrt(Math.pow(one.getxValue()-two.getxValue() , 2) + Math.pow(one.getyValue()-two.getyValue(), 2));
	}
	
	
	/**
	 * Move robot and box
	 */
	private void moveRobotAndBox(int relativePos, MovingBoxDiscretizer discMovingBoxPath) {
		int indexOfLastStep = outPut.size()-1;
		ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
		
		double xRobot = (double) lastStep.get(0);
		double yRobot = (double) lastStep.get(1);
		double alpha = (double) lastStep.get(2);
		Point2D robotPos = new Point2D.Double(xRobot, yRobot);
		ArrayList<Point2D> boxPath = discMovingBoxPath.getDiscretePathForMovingBox();
		Mover mover = new Mover(boxPath, robotPos, alpha, w);
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
	 * Make grid
	 */
	private void makeGrid(ProblemSpec ps) {
		Grid grid = new Grid(ps);
		this.grid = grid;
	}
	
	
	/**
	 *	make a list for initialOrientations
	 * @throws IOException 
	 */
	// may be specialized for first path - check!
//	private void makeRobotInitialOrientationList() throws IOException {
//		// orientation of robot before moving box, at anytime
//		// list of these discretize objects InitialRotationPathForRobot
//		for (int i = 0; i < resultPathForMovingBoxes.size(); i++) {
//			// get orientations
//			double initOrientation = initialRotationValue.get(i);
//			InitialRotationPathForRobot irpr = new InitialRotationPathForRobot(movingBoxes.get(i), initOrientation);
//			// add orientations
//			resultRobotOrientation.add(irpr.getResultList());
//		}
//	}
	
	
	/**
	 * make a list containing paths for robot to boxes
	 * @throws IOException 
	 */
	// may be specialized for first path - check!
//	private void makeDiscRobotPathBeforeMovingBoxList() throws IOException {
//		ArrayList<ArrayList<Node>> pathForAll = paths.getPathForAllMovingBoxes();
//		ArrayList<Node> tempLastMovingBoxPath = new ArrayList<>(); // used to get initial pos for robot after first iteration.
//		for (int i = 0; i < resultPathForMovingBoxes.size(); i++) {
//			// get path
//			ArrayList<Node> nextMovingBoxpath = pathForAll.get(i);
//			double startX = -1.0, startY = -1.0;
//			if (i == 0) {
//				// First path, robot initialconfig gives start coordinates
//				Point2D robotPos = ps.getInitialRobotConfig().getPos();
//				startX = robotPos.getX();
//				startY = robotPos.getY();
//				tempLastMovingBoxPath = nextMovingBoxpath;
//				initialRotationValue.add(ps.getInitialRobotConfig().getOrientation());
//			} 
//			else {
//				// initial position is ending path for last box the robot moved
//				int size = tempLastMovingBoxPath.size();
//				Node goalForLastMovedBox = tempLastMovingBoxPath.get(size-1);
//				Node nodeBeforeGoalForLastMovedBox = tempLastMovingBoxPath.get(size-2);
//				char dir = paths.getDirectionOfLastStep(goalForLastMovedBox, goalForLastMovedBox);
//				if(dir == 'u') {
//					startX = ps.getMovingBoxEndPositions().get(i-1).getX();
//					startY = ps.getMovingBoxEndPositions().get(i-1).getY()-w/2;
//					initialRotationValue.add(0.0);
//				}
//				else if(dir == 'd') {
//					startX = ps.getMovingBoxEndPositions().get(i-1).getX();
//					startY = ps.getMovingBoxEndPositions().get(i-1).getY()+w/2;
//					initialRotationValue.add(0.0);
//				}
//				else if(dir == 'r') {
//					startX = ps.getMovingBoxEndPositions().get(i-1).getX()-w/2;
//					startY = ps.getMovingBoxEndPositions().get(i-1).getY();
//					initialRotationValue.add(1.57);
//				}
//				else {
//					startX = ps.getMovingBoxEndPositions().get(i-1).getX()+w/2;
//					startY = ps.getMovingBoxEndPositions().get(i-1).getY();
//					initialRotationValue.add(1.57);
//				}
//				tempLastMovingBoxPath = nextMovingBoxpath;
//			}
//			ArrayList<Node> nodePath = pathForAll.get(i);
//			PathForRobot robotPath = new PathForRobot(startX, startY, nodePath, grid);
//			// discretize path
//			pathsForRobotBeforeMovingBox.add(robotPath);
//			RobotPathDiscretizer discPath = new RobotPathDiscretizer(robotPath.getRobotPath());
//			// add path
//			resultMoveRobotForNextBox.add(discPath.getDiscretPathsForMovingBoxes());
//		}
//	}
	
	/**
	 * make Path for all moving boxes
	 */
//	private void makeDiscPathForMovingBox() throws IOException {
//		// instantiate object(s)
//		this.paths = new PathForAllMovingBoxes(grid);
//		// get paths
//		ArrayList<ArrayList<Node>> nodePaths = paths.getPathForAllMovingBoxes();
//		// discretize paths
//		MovingBoxDiscretizer discPaths = new MovingBoxDiscretizer(nodePaths);
//		// add paths
//		this.resultPathForMovingBoxes = discPaths.getDiscretPathsForMovingBoxes();
//	}
	
	
	
	/**
	 * writeSolutionToFile
	 */
	public void writeToFile(ArrayList<Double> step) throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\mariu\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		numberOfPrimitives ++;
		BufferedWriter writer = new BufferedWriter(file);
		for(Double value: step ) {
			writer.write(value + "\t");
			}
		writer.write("\n");
		writer.close();
	}
	
	public void writeSolutionToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\mariu\\git\\COMP3702\\Assignment1AI\\output1.txt");
		int numberOfPrimitiveSteps = outPut.size();
		BufferedWriter writer = new BufferedWriter(file);
		writer.write(numberOfPrimitiveSteps + "\n");
		for(ArrayList<Double> step : outPut ) {
			for(Double value : step) {
				writer.write(value + "\t");
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
