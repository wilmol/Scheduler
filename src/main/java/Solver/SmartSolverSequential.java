package Solver;

import CommonInterface.ISolver;
import Graph.EdgeWithCost;
import Graph.Graph;
import Graph.Vertex;

import java.lang.reflect.InvocationTargetException;

/**
 * Picks the best solver (A*, BnB) to run based on the input.
 *
 * @author Will Molloy
 */
public class SmartSolverSequential extends AbstractSolver {

    private enum Solver {
        AStar(AStarSolver.class), BnB(DFSSolver.class);
        private Class<? extends AbstractSolver> solver;

        Solver(Class<? extends AbstractSolver> solver) {
            this.solver = solver;
        }

        public Class<? extends AbstractSolver> getSolver() {
            return solver;
        }
    }
    private Solver solver;
    private AbstractSolver currentSolver;

    public SmartSolverSequential(Graph<Vertex, EdgeWithCost<Vertex>> graph, int processorCount) {
        super(graph, processorCount);
        determineSolverToUse();
    }

    /**
     * Pick the solver to use based on the program arguments.
     *
     * TODO Current ideas:
     *  Get memory available to the JVM (machine dependent)
     *  get average size of search state (depends on num vertices)
     *  estimate number of search states (i.e. size of A* queue)
     *  if this exceeds available memory BnB will be selected.
     *
     * But there are some cases where BnB is faster even when A* terminates:
     *  1 processor
     *  few edges (sparse graph)
     *  .. any more?
     */
    private void determineSolverToUse() {

         // "AI is just a bunch of if/then statements"
         // These decisions are in priority order
        if (processorCount == 1){ // BnB since upper bound is that of using one core
            solver = Solver.BnB;
        } else if (graph.getInwardEdgeMap().isEmpty() && graph.getOutwardEdgeMap().isEmpty()){ // No edges
            solver = Solver.BnB;
        } else {
            solver = Solver.AStar;
        }

        initialiseSolver();
    }

    private void initialiseSolver() {
        try {
            currentSolver = solver.getSolver().getDeclaredConstructor(Graph.class, int.class).newInstance(graph, processorCount);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSolve() {
        currentSolver.doSolve();
    }
}
