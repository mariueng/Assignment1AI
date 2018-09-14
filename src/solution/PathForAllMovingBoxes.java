package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import problem.ProblemSpec;

public class PathForAllMovingBoxes {
	
	/**
	 * This class will manage paths for all moving boxes
	 * When running method "makePathForAllMovongBoxes", it should return
	 * a valid arraylist of Hashmaps<Box, Arraylist<Node>>
	 */
	
	//fields
	public ArrayList<ArrayList<Node>> pathForAllMovingBoxes = new ArrayList<>();
	private int numberOfMovingBoxes;
	private Grid grid;
	
	//constructor
	public PathForAllMovingBoxes(Grid grid) throws IOException {
		this.grid =grid;
		numberOfMovingBoxes = grid.getPS().getMovingBoxes().size();
		for(int i=0; i<numberOfMovingBoxes; i++) {
			PathForMovingBox p = new PathForMovingBox(i, grid);
			pathForAllMovingBoxes.add(p.getPathForMovingBox());
		}
	}
	
	/*
	 * METHODS
	 */
	
	//get list of all paths
	public ArrayList<ArrayList<Node>> getPathForAllMovingBoxes(){
		return pathForAllMovingBoxes;
	}
	
	public char getDirectionOfLastStep(Node first, Node second) {
		char c;
		
		if(first.getyValue() < second.getyValue()) {
			c = 'u';
		}
		else if(first.getxValue() > second.getxValue()) {
			c = 'l';
		}
		else if(first.getxValue()<second.getxValue()) {
			c ='r';
		}
		else {
			c='d';
		}
		return c;
	}
	
	//write all paths to file
	public void writeToFile() throws IOException {
		FileWriter file = new FileWriter("C:\\Users\\jakob\\git\\Assignment1AI\\src\\solution\\pathData.txt");
		BufferedWriter writer = new BufferedWriter(file);
		writer.write("X-value" + "\t" +"Y-value" + "\t" + "Ground type" + "\n");
		for(ArrayList<Node> list:pathForAllMovingBoxes) {
			for(Node n:list ) {
			writer.write(n.getxValue() + "\t" + n.getyValue() + "\t" + n.getGroundType()+"\n");
		}}
		writer.close();
	}

	
	//mainForTesting
	public static void main(String[] args) throws IOException {
		ProblemSpec ps = new ProblemSpec();
		Grid grid = new Grid(ps);
		PathForAllMovingBoxes p = new PathForAllMovingBoxes(grid);
		for(ArrayList<Node> list:p.getPathForAllMovingBoxes()) {
			System.out.println(list);
		}
	}
}
