package solution;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.geom.Rectangle;

import problem.ProblemSpec;

public class Grid {
	
	//run problemspec to access lists of moving boxes, moving obstacles etc.
	public void load() {
        ProblemSpec ps = new ProblemSpec();
        try {
            ps.loadProblem("input1.txt");
            ps.loadSolution("output1.txt");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");
        w = ps.getRobotWidth();

    }
	
	//fields
	private double d;
	private int k;
	private ArrayList<Node> vertices = new ArrayList<>();
	private double w;
	
	//constructor
	public Grid(double distance, int k) {
		this.d = distance;
		this.k = k; 
	}
	
	
	//methods
	private String getGroundTypeOnPosition(double x, double y) {
	String s = "";
	
	return s;
	}
	
	//Method to sample vertices
	public void sampleGrid() {
		int a = (int) Math.floor((1-w)/d); //How many samples for each row/columns
		System.out.println(w);
		int b = (int) Math.pow(a,2); //total number of samples
		double x = w/2, y = w/2;
		for(int i = 0; i<a; i++) {
			for(int j = 0; i<a; j++) {
				//addNewNode and find groundtype etc
				//make square around x,y and check if collides
				String g = assignGroundType(x,y);
				if(!g.equals("SO")) {
					Node n = new Node(x,y,g);
				}
				x+=d;
			}
			x = w/2;
			y += d;
			
		}
	}
		
	//Assign ground type
	private String assignGroundType(double x, double y) {
		String result = "";
		
		
		return result;
	}
		
	//getters
	private int getNumberOfSamples() {
		int a = (int) Math.floor((1-w)/d); //How many samples for each row/columns
		int row;
		int col;
		System.out.println(w);
		return a;

	}
	
	//main for testing and debugging
	public static void main(String[] args) {
		Grid g = new Grid(0.1,15);
		g.load();
		System.out.println(g.getNumberOfSamples());
	}
	
	

}
