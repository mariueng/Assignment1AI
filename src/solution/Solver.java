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
	
	//run problemspec to access lists of moving boxes, moving obstacles etc.
	public void load() {
        ProblemSpec ps = new ProblemSpec();
        this.ps = ps;
        try {
            ps.loadProblem("input1.txt");
            
            ps.loadSolution("output1.txt");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");

    }
	
	//fields
	private ProblemSpec ps;
	private double w;
	private Grid grid;
	private int numberOfMovingBoxes;
	private ArrayList<ArrayList<Double>> pathsForInitialRobotOrientation = new ArrayList<>();
	private ArrayList<ArrayList<Point2D>> pathsForMovingBoxes = new ArrayList<>();
	private ArrayList<ArrayList<Double>> pathsForRobotBeforeMovingBox = new ArrayList<>();
	private ArrayList<ArrayList<Double>> outPut;
	private int numberOfPrimitiveSteps;
	private int numberOfValuesInEachState;
	
	//constructor
	public Solver(ProblemSpec ps) throws IOException {
		load(); //REMOVE THIS. ONLY FOR TESTING THE CLASS
		this.ps = ps;
        w = ps.getRobotWidth();
		run(ps);
	}
	
	//methods
	
	private void run(ProblemSpec ps) throws IOException {
		makeGrid(ps);
		makePathsForMovingBoxes(ps);
		makeRobotPathBeforeMovingBoxList(ps);
		makeRobotInitialOrientationList(ps);
		
		//iterate over each box and add each steps to outputList
		for(int i = 0; i<numberOfMovingBoxes; i++) {
			ArrayList<Double> outputForThisBox = new ArrayList<>();
			orientRobot(ps, i, outputForThisBox);
			moveRobotToBox(ps, i, outputForThisBox);
			moveRobotAndBox(ps, i, outputForThisBox);
			outPut.add(outputForThisBox);
		}
		writeSolutionToFile();
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
		// TODO Auto-generated method stub
		
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
	 */
	private void makeRobotInitialOrientationList(ProblemSpec ps) {
		
	}
	
	
	/**
	 * make a list containing paths for robot to boxes
	 */
	private void makeRobotPathBeforeMovingBoxList(ProblemSpec ps) {
		
	}
	
	/**
	 * make Path for all moving boxes
	 */
	private void makePathsForMovingBoxes(ProblemSpec ps) {
		
	}
	
	
	
	/**
	 * writeSolutionToFile
	 */
		public void writeSolutionToFile() throws IOException {
			FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\output1.txt");
			BufferedWriter writer = new BufferedWriter(file);
			writer.write(numberOfPrimitiveSteps);
			for(ArrayList<Double> list:outPut) {
					for(Double value:list) {
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
	
	/**main for testing
	 * 
	 */
	public static void main(String[] args) {
		Solver solver = new Solver(ps);
	}
	
	
	

}
