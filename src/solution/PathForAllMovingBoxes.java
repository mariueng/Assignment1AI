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
			for(Node n:grid.getVertices()) {
				n.setParent(null);
			}
		}
	}
	
	/*
	 * METHODS
	 */
	
	//get list of all paths
	public ArrayList<ArrayList<Node>> getPathForAllMovingBoxes(){
		return pathForAllMovingBoxes;
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
		PathForAllMovingBoxes p = new PathForAllMovingBoxes();
		System.out.println(p.getPathForAllMovingBoxes());
		p.grid.writeToFile();
	}
}
