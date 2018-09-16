package problem;

import java.io.IOException;

import solution.Solver;


public class Main {
    public static void main(String[] args) {
        ProblemSpec ps = new ProblemSpec();
        try {
    		long start = System.currentTimeMillis();
    		String inputfile = args[0];
    		String outputFileName = args[1];
            ps.loadProblem(inputfile); 
            Solver s = new Solver(ps, outputFileName); 
            ps.loadSolution(outputFileName); 
            long elapsedTimeMillis = System.currentTimeMillis()-start;
            System.out.println("Time elapsed: " + (elapsedTimeMillis) + " [ms]");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");

    }
}


