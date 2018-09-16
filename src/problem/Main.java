package problem;

import java.io.IOException;

import solution.Solver;
import tester.Tester;

public class Main {
    public static void main(String[] args) {
        ProblemSpec ps = new ProblemSpec();
        try {
    		long start = System.currentTimeMillis();
    		//String inputfile = args[0]
    		//String outputFileName = args[1]
            ps.loadProblem("input3.txt"); //It needs to say args[0]
            Solver s = new Solver(ps); //need to add args[1]
            ps.loadSolution("output3.txt"); //Need to say args[1] 
            long elapsedTimeMillis = System.currentTimeMillis()-start;
            System.out.println("Time elapsed: " + (elapsedTimeMillis) + " [ms]");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");

    }
}


