package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.geom.Point2D;

import problem.ProblemSpec;

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
	private int numberOfMovingObstacles;
	private int numberOfStaticObstacles;
	
	private PathForAllMovingBoxes paths;
	private static ArrayList<PathForRobot> pathsForRobotBeforeMovingBox = new ArrayList<>();
	
	//these three list have the same number of elements and contains discretices path in three steps
	private ArrayList<ArrayList<Double>> resultRobotOrientation = new ArrayList<>();
	private ArrayList<ArrayList<Point2D>> resultPathForMovingBoxes = new ArrayList<>();
	private static ArrayList<ArrayList<Point2D>> resultMoveRobotForNextBox = new ArrayList<>();
	
	private ArrayList<ArrayList<Double>> outPut = new ArrayList<>();
	
	//constructor
	public Solver(ProblemSpec ps) throws IOException {
		this.ps = ps;
        w = ps.getRobotWidth();
        this.numberOfMovingBoxes = ps.getMovingBoxes().size();
    	this.numberOfMovingObstacles = ps.getMovingObstacles().size();
    	this.numberOfStaticObstacles = ps.getStaticObstacles().size();
        //generate first step
    	makeInitialConditionOfBoard();
        
        //run
		run(ps);
		System.out.println(outPut);
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
	private void run(ProblemSpec ps) throws IOException {
		makeGrid(ps);
		makeDiscPathsForMovingBoxes();
		makeDiscRobotPathBeforeMovingBoxList();
		makeRobotInitialOrientationList();
		
		//iterate over each box and add each steps to outputList
		for(int i = 0; i<numberOfMovingBoxes; i++) {
			orientRobot(i);
			moveRobotToBox(i);
			moveRobotAndBox(i);
		}

		//writeSolutionToFile();
	}
	
	
	/**
	 * Move robot and box
	 */
	private void moveRobotAndBox(int i) {
		
	}
	
	/**
	 * Move robot to box
	 */
	private void moveRobotToBox(int i) {
		
	}
	
	
	/**
	 * Orient robot before moving it to the box
	 * @throws IOException 
	 */
	private void orientRobot(int i) throws IOException {
		int indexOfLastStep = outPut.size()-1;
		ArrayList<Double> lastStep = outPut.get(indexOfLastStep);
		ArrayList<Double> stepsForOrientation = resultRobotOrientation.get(i);
		int numberOfSteps = stepsForOrientation.size();
		for(int j = 0; j<numberOfSteps; j++) {
			ArrayList<Double> step = lastStep;
			double value = stepsForOrientation.get(j);
			step.set(2, value);
			writeToFile(step);
			
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
	private void makeRobotInitialOrientationList() throws IOException {
		// orientation of robot before moving box, at anytime
		// list of these discretize objects InitialRotationPathForRobot
		for (int i = 0; i < resultPathForMovingBoxes.size(); i++) {
			// get orientations
			InitialRotationPathForRobot irpr = new InitialRotationPathForRobot(ps, i);
			// add orientations
			resultRobotOrientation.add(irpr.getResultList());
		}
	}
	
	
	/**
	 * make a list containing paths for robot to boxes
	 * @throws IOException 
	 */
	// may be specialized for first path - check!
	private void makeDiscRobotPathBeforeMovingBoxList() throws IOException {
		ArrayList<ArrayList<Node>> pathForAll = paths.getPathForAllMovingBoxes();
		ArrayList<Node> tempLastMovingBoxPath = new ArrayList<>(); // used to get initial pos for robot after first iteration.
		for (int i = 0; i < resultPathForMovingBoxes.size(); i++) {
			// get path
			ArrayList<Node> nextMovingBoxpath = pathForAll.get(i);
			double startX = -1.0, startY = -1.0;
			if (i == 0) {
				// First path, robot initialconfig gives start coordinates
				Point2D robotPos = ps.getInitialRobotConfig().getPos();
				startX = robotPos.getX();
				startY = robotPos.getY();
				tempLastMovingBoxPath = nextMovingBoxpath;
			} 
			else {
				// initial position is ending path for last box the robot moved
				int size = tempLastMovingBoxPath.size();
				Node goalForLastMovedBox = tempLastMovingBoxPath.get(size-1);
				Node nodeBeforeGoalForLastMovedBox = tempLastMovingBoxPath.get(size-2);
				char dir = paths.getDirectionOfLastStep(goalForLastMovedBox, goalForLastMovedBox);
				if(dir == 'u') {
					startX = ps.getMovingBoxEndPositions().get(i-1).getX();
					startY = ps.getMovingBoxEndPositions().get(i-1).getY()-w/2;
				}
				else if(dir == 'd') {
					startX = ps.getMovingBoxEndPositions().get(i-1).getX();
					startY = ps.getMovingBoxEndPositions().get(i-1).getY()+w/2;
				}
				else if(dir == 'r') {
					startX = ps.getMovingBoxEndPositions().get(i-1).getX()-w/2;
					startY = ps.getMovingBoxEndPositions().get(i-1).getY();
				}
				else {
					startX = ps.getMovingBoxEndPositions().get(i-1).getX()+w/2;
					startY = ps.getMovingBoxEndPositions().get(i-1).getY();
				}
				tempLastMovingBoxPath = nextMovingBoxpath;
			}
			ArrayList<Node> nodePath = pathForAll.get(i);
			PathForRobot robotPath = new PathForRobot(startX, startY, nodePath, grid);
			// discretize path
			pathsForRobotBeforeMovingBox.add(robotPath);
			RobotPathDiscretizer discPath = new RobotPathDiscretizer(robotPath.getRobotPath());
			// add path
			resultMoveRobotForNextBox.add(discPath.getDiscretPathsForMovingBoxes());
		}
	}
	
	/**
	 * make Path for all moving boxes
	 */
	private void makeDiscPathsForMovingBoxes() throws IOException {
		// instantiate object(s)
		this.paths = new PathForAllMovingBoxes(grid);
		// get paths
		ArrayList<ArrayList<Node>> nodePaths = paths.getPathForAllMovingBoxes();
		// discretize paths
		MovingBoxDiscretizer discPaths = new MovingBoxDiscretizer(nodePaths);
		// add paths
		this.resultPathForMovingBoxes = discPaths.getDiscretPathsForMovingBoxes();
	}
	
	
	
	/**
	 * writeSolutionToFile
	 */
	public void writeToFile(ArrayList<Double> step) throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		numberOfPrimitives ++;
		BufferedWriter writer = new BufferedWriter(file);
		for(Double value: step ) {
			writer.write(value + "\t");
			}
		writer.write("\n");
		writer.close();
	}
	
	public void writeSolutionToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\output1.txt");
		int numberOfPrimitiveSteps = outPut.size();
		BufferedWriter writer = new BufferedWriter(file);
		writer.write(numberOfPrimitiveSteps + "\n");
		for(ArrayList<Double> step : outPut ) {
			for(Double value:step) {
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
	
	// Retrieving discrete paths for robot outside this class
	public static ArrayList<ArrayList<Point2D>> getDiscPathsForRobotBeforeMovingBox() {
		return resultMoveRobotForNextBox;
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