package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.geom.Point2D;

import problem.ProblemSpec;

public class Solver {
	
	/**
	 * The class for solving the problem an generating the output file
	 */
	
	//fields
	private ProblemSpec ps;
	private double w;
	private Grid grid;
	private int numberOfMovingBoxes;
	private ArrayList<ArrayList<Double>> discPathsForInitialRobotOrientation = new ArrayList<>();
	private ArrayList<ArrayList<Point2D>> discPathsForMovingBoxes = new ArrayList<>();
	private PathForAllMovingBoxes paths;
	private static ArrayList<PathForRobot> pathsForRobotBeforeMovingBox = new ArrayList<>();
	private static ArrayList<ArrayList<Point2D>> discPathsForRobotBeforeMovingBox = new ArrayList<>();

	private ArrayList<ArrayList<Double>> outPut;
	private int numberOfPrimitiveSteps;
	private int numberOfValuesInEachState;
	
	//constructor
	public Solver(ProblemSpec ps) throws IOException {
		this.ps = ps;
        w = ps.getRobotWidth();
		run(ps);
	}
	
	//methods
	
	private void run(ProblemSpec ps) throws IOException {
		makeGrid(ps);
		makeDiscPathsForMovingBoxes();
		makeDiscRobotPathBeforeMovingBoxList();
		makeRobotInitialOrientationList();
		
		//iterate over each box and add each steps to outputList
		for(int i = 0; i<numberOfMovingBoxes; i++) {
			ArrayList<Double> outputForThisBox = new ArrayList<>();
			orientRobot(ps, i, outputForThisBox);
			moveRobotToBox(ps, i, outputForThisBox);
			moveRobotAndBox(ps, i, outputForThisBox);
			outPut.add(outputForThisBox);
		}
		// Commented out since outPut is empty for now
		//writeSolutionToFile();
	}
	
	
	/**
	 * Move robot and box
	 */
	private void moveRobotAndBox(ProblemSpec ps2, int i, ArrayList<Double> outputForThisBox) {
		
	}
	
	/**
	 * Move robot to box
	 */
	private void moveRobotToBox(ProblemSpec ps2, int i, ArrayList<Double> outputForThisBox) {
		
	}
	
	
	/**
	 * Orient robot before moving it to the box
	 */
	private void orientRobot(ProblemSpec ps2, int i, ArrayList<Double> outputForThisBox) {
		// TODO Auto-generated method stub
		
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
		for (int i = 0; i < discPathsForMovingBoxes.size(); i++) {
			// get orientations
			InitialRotationPathForRobot irpr = new InitialRotationPathForRobot(ps, i);
			// add orientations
			discPathsForInitialRobotOrientation.add(irpr.getResultList());
		}
	}
	
	
	/**
	 * make a list containing paths for robot to boxes
	 * @throws IOException 
	 */
	// may be specialized for first path - check!
	private void makeDiscRobotPathBeforeMovingBoxList() throws IOException {
		for (int i = 0; i < discPathsForMovingBoxes.size(); i++) {
			// get paths
			PathForRobot path = new PathForRobot(i, paths.getPathForAllMovingBoxes(), grid);
			// discretize paths
			this.pathsForRobotBeforeMovingBox.add(path);
			RobotPathDiscretizer discPath = new RobotPathDiscretizer(path.getRobotPath());
			// add paths
			discPathsForRobotBeforeMovingBox.add(discPath.getDiscretPathsForMovingBoxes());
		}
	}
	
	/**
	 * make Path for all moving boxes
	 * @throws IOException 
	 */
	private void makeDiscPathsForMovingBoxes() throws IOException {
		// instantiate object(s)
		this.paths = new PathForAllMovingBoxes(grid);
		// get paths
		ArrayList<ArrayList<Node>> nodePaths = paths.getPathForAllMovingBoxes();
		// discretize paths
		MovingBoxDiscretizer discPaths = new MovingBoxDiscretizer(nodePaths);
		// add paths
		this.discPathsForMovingBoxes = discPaths.getDiscretPathsForMovingBoxes();
	}
	
	
	
	/**
	 * writeSolutionToFile
	 */
	public void writeSolutionToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\mariu\\git\\Assignment1AI\\output1.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write(numberOfPrimitiveSteps);
		for(ArrayList<Double> list : outPut) {
			for(Double value : list) {
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
		return discPathsForRobotBeforeMovingBox;
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
