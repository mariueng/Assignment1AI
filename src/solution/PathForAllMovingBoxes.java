package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PathForAllMovingBoxes {
	
	/**
	 * This class will manage paths for all moving boxes
	 * When running method "makePathForAllMovongBoxes", it should return
	 * a valid arraylist of Hashmaps<Box, Arraylist<Node>>
	 */
	
	//fields
	private ArrayList<ArrayList<Node>> pathForAllMovingBoxes = new ArrayList<>();
	private int numberOfMovingBoxes;
	private Grid grid;
	
	//constructor
	public PathForAllMovingBoxes() throws IOException {
		this.grid = new Grid();
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
	

	
	//mainForTesting
	public static void main(String[] args) throws IOException {
		PathForAllMovingBoxes p = new PathForAllMovingBoxes();
		System.out.println(p.getPathForAllMovingBoxes());
	}
}
