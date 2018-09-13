package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;

public class State {
	
	/**
	 * Class which controls the validation of every state 
	 * for every move made by any moving box/obstacle with 
	 * robot.
	 * 
	 * Run by Solver class.
	 */
	
	// Problem specification should be taken in here. 
	// Not in grid or any other class.
	private final ProblemSpec ps;
	private final Grid grid;
	private PathForAllMovingBoxes movingBoxesPath;
	private PathForRobot pathForRobot;
	private MovingBoxDiscretizer d;

	private RobotConfig rc;
	private List<Box> MovingBoxes, MovingObstacles;
	private List<Node> robotPath;
	
	
	public State(ProblemSpec ps) throws IOException {
		this.ps = ps;
		this.grid = new Grid(ps);
		this.movingBoxesPath = new PathForAllMovingBoxes(grid);
		ArrayList<ArrayList<Node>> nodeList = movingBoxesPath.getPathForAllMovingBoxes();
		this.d = new MovingBoxDiscretizer(nodeList);
		this.rc = ps.getInitialRobotConfig();
	}
	
	public void returnRobotPath() {
		
	}
	
	// getters and setters
	
	public ProblemSpec getProblemSpec() {
		return ps;
	}

	public PathForAllMovingBoxes getP() {
		return movingBoxesPath;
	}


	public MovingBoxDiscretizer getD() {
		return d;
	}

	public RobotConfig getRobotConfig() {
		return rc;
	}

	public List<Box> getMovingBoxes() {
		return MovingBoxes;
	}

	public void setMovingBoxes(List<Box> movingBoxes) {
		MovingBoxes = movingBoxes;
	}

	public List<Box> getMovingObstacles() {
		return MovingObstacles;
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	
	public String returnLineState() {
		String line = "" + returnRobotString() 
		+ returnMovingBoxesString()
		+ returnMovingObstaclesString();
		return line;
	}
	
	private String returnMovingObstaclesString() {
		// Last part of line
		// Format: <xPos> <yPos>
		return "";
	}

	private String returnMovingBoxesString() {
		// Middle part of line
		// Format: <xPos> <yPos>
		return "";
	}

	private String returnRobotString() {
		// First part of line
		// Format: <xPos> <yPos> <angle>
		return "";
	}

	public static void main(String[] args) {
		
	}
}
