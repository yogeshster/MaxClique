import edu.rit.pj2.Job;
import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.TerminateException;
import edu.rit.util.Random;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Cluster version of the program that finds the maximum clique, given a graph. It builds the graph from a given
 * graph input file, does a specified number of random restarts and finds a clique of maximum size among those random
 * restarts.
 * 
 * Usage: java pj2 threads=<numOfThreads> workers=<numOfWorkers> jar=<nameOfJar> MaxCliqueClu <filename> <seed> <numberOfIterations>
 * <numOfThreads>       =   Number of threads among which the iterations are distributed(optional)
 * <numOfWorkers>       =   Number of workers to be involved in the computation(optional)
 * <filename>           =   name of the file that contains the graph.
 * <seed>               =   Random number generator's seed
 * <numberOfIterations> =   number of random restarts on the graph
 * 
 * @author  Yogesh Jagadeesan and Dler Ahmad
 * @version 03-Dec-2013
 */
public class MaxCliqueClu extends Job{
    
   /**
    * Worker task class.
    */
    private static class WorkerTask extends Task{
        
        /**
         * Task main program.
         * 
         * @param   args    random number seed entered by the user.
         */
        @Override
        public void main (String[] args) throws Exception{
            
            //Initialize and read graph from tuple space
            final long seed = Long.parseLong (args[0]);
            final Graph graph = readTuple (new Graph());
            final Clique best = new Clique (graph.getNumberOfVertices());

            // Do iterations in parallel.
            workerFor() .exec (new Loop(){
                
                Clique current;
                Clique thrBest;
                Random prng;
                ArrayList<Integer> currentClique;
                
                @Override
                public void start(){
                    
                    // Set up pseudorandom number generator, current and thread local max cliques
                    prng = new Random (seed + taskRank()*1000 + rank());
                    thrBest = threadLocal(best);
                    current = new Clique(graph.getNumberOfVertices());
                    currentClique = new ArrayList<Integer>();
                }

                @Override
                public void run (int iter){
                    //Reset for every iteration
                    current.reset();
                    currentClique.clear();
                        
                    //Pick a random vertex from the random number generator.
                    int requiredDegree = 1;
                    int currentVertex = prng.nextInt(graph.getNumberOfVertices());
            
                    //Add vertex into consideration.
                    current.add(currentVertex);
                    currentClique.add(currentVertex);
                    
                    while(true){
                        int successiveVertex = -1;
                        int successiveVertexDegree = -1;
                        int[] neighbour = graph.getNeighbours(currentVertex);
                                
                        //Explore neighbours of the picked vertex
                        for(int neigh=0; neigh<neighbour.length; ++neigh){
                            if(neighbour[neigh]==1 && graph.getDegree(neigh) >= requiredDegree && !current.isVertexInClique(neigh)){  
                        
                                //Check if neighbour forms a clique with the clique so far
                                boolean isStillClique = true;
                                for(int cidx=0; cidx<currentClique.size(); ++cidx){
                                    if(!graph.isConnected(neigh, currentClique.get(cidx))){
                                        isStillClique = false;
                                        break;
                                    }
                                }
                        
                                //Check for neighbour quality to see if it has potential to form a bigger clique by checking its degree
                                if(isStillClique){
                                    if((successiveVertex == -1 && successiveVertexDegree == -1)||(graph.getDegree(neigh) > successiveVertexDegree)){
                                        successiveVertex = neigh;
                                        successiveVertexDegree = graph.getDegree(neigh);
                                    }
                                    else if((graph.getDegree(neigh) == successiveVertexDegree) && (prng.nextBoolean())){
                                        successiveVertex = neigh;
                                        successiveVertexDegree = graph.getDegree(neigh);
                                    }
                                }
                            }
                        }       
                        
                        //the neighbour if estimated to be a potential one, is added to current clique and hence the current clique is grown
                        if(successiveVertex!=-1 && successiveVertexDegree!=-1){
                            current.add(successiveVertex);
                            currentClique.add(successiveVertex);
                            ++requiredDegree;
                    
                            //Attempt to expand clique from this neighbour
                            currentVertex = successiveVertex;
                        }
                        else{
                            break;
                        }
                    }
                    
                    //Compare it with the thread local max clique found so far.
                    thrBest.reduce(current);
                }
            });
            
            //Put best-of-best clique into tuple space.
            putTuple (best);
        }   
    }

   /**
    * Reduction task class.
    */
    private static class ReduceTask extends Task{
        
       /**
        * Reduction task main program.
        * 
        * @param    args    number of vertices of the graph
        */
        @Override
        public void main (String[] args) throws Exception{
            int numberOfVertices = Integer.parseInt(args[0]);
            
            //Determine the best of all cliques from each worker tasl
            Clique bestOfBest = new Clique(numberOfVertices);
            Clique template = new Clique();
            Clique candidateBest;
            while ((candidateBest = tryToTakeTuple (template)) != null){
                bestOfBest.reduce (candidateBest);
            }
            
            //Print the vertices of the max clique
            int[] vertices = bestOfBest.getVertices();
            int size = bestOfBest.getSize();
            System.out.println("Vertices in the clique are as follows:");
            for(int idx=0; idx<vertices.length; ++idx){
                if(vertices[idx]==1){
                    System.out.print(idx+" ");
                }
            }
            System.out.println("\nSize of max clique is: "+size);
        }
    }

    /**
     * Check if all arguments are valid
     * 
     * @param args      Arguments to be validated.
     * @return          True if all arguments are valid. False if even one is invalid.
     */
    boolean isValid(String[] args){
        boolean seedValid=true, iterationsValid=true, fileValid=true;
        try{
            FileReader sample = new FileReader(new File(args[0]));
        }
        catch(Exception e){
            System.err.println("Invalid filename");
            fileValid = false;
        }
        try{
            long seed = Long.parseLong(args[1]);
        }
        catch(Exception e){
            System.err.println("Invalid seed value");
            seedValid = false;
        }
        try{
            int iterations = Integer.parseInt(args[2]);
        }
        catch(Exception e){
            System.err.println("Invalid number of iterations value");
            iterationsValid = false;
        }
        return (seedValid && iterationsValid && fileValid);
    }
    
    /**
     * Program execution begins here.
     * 
     * @param args used to acquire the filename, seed and the number of iterations.
     */
    @Override
    public void main(String[] args) throws Exception {
        
        //Validate
        if(args.length!=3 || !isValid(args)){
            if(args.length!=3){
                System.err.println("Usage: java pj2 MaxCliqueSeq <filename> <seed> <numberOfIterations>");
            }
            throw new TerminateException("An error has occurred in one or more arguments..Exiting...");
        }
        
        //Initialize      
        File file = new File(args[0]);
        long seed = Long.parseLong(args[1]);
        int numberOfIterations = Integer.parseInt(args[2]);
        
        //Put generated graph in tuple space to be picked up by other worker tasks
        Graph graph = new Graph(file);
        putTuple(graph);
        
        // Do N iterations in multiple worker tasks.
        masterFor (0, numberOfIterations - 1, WorkerTask.class).args (""+seed);

        // Reduce task.
        rule().atFinish().task (ReduceTask.class).args(""+graph.getNumberOfVertices()).runInJobProcess();
    }
}