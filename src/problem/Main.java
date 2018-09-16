package problem;

import java.io.IOException;

import solution.Solver;
import tester.Tester;

public class Main {
    public static void main(String[] args) {
        ProblemSpec ps = new ProblemSpec();
        try {
            ps.loadProblem("input1.txt");
            Solver s = new Solver(ps);
            ps.loadSolution("output1.txt");
        } catch (IOException e) {
            System.out.println("IO Exception occured");
        }
        System.out.println("Finished loading!");

    }
}


