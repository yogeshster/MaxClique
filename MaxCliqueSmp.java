import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;
import edu.rit.pj2.TerminateException;
import edu.rit.util.Random;
import java.io.File;
import java.util.ArrayList;
import java.io.FileReader;

/**
 * Parallel version of the program that finds the maximum clique, given a graph. It builds the graph from a given
 * graph input file, does a specified number of random restarts and finds a clique of maximum size among those random
 * restarts.
 * 
 * Usage: java pj2 threads=<numOfThreads> MaxCliqueSmp <filename> <seed> <numberOfIterations>
 * <numOfThreads>       =   Number of parallel team threads among which the iterations are distributed(optional)
 * <filename>           =   name of the file that contains the graph.
 * <seed>               =   Random number generator's seed
 * <numberOfIterations> =   number of random restarts on the graph
 * 
 * @author  Yogesh Jagadeesan and Dler Ahmad
 * @version 03-Dec-2013
 */

public class MaxCliqueSmp extends Task{
    
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
        final long seed = Long.parseLong(args[1]);
        final int numberOfIterations = Integer.parseInt(args[2]);
        
        //Generate graph from the file.
        final Graph graph = new Graph(file);
        final int numberOfVertices = graph.getNumberOfVertices();               
        final Clique best = new Clique(graph.getNumberOfVertices());    //Holds max clique from all threads
        
        parallelFor(0,(numberOfIterations-1)).exec(new Loop(){

            Clique current;
            Clique thrBest;
            Random prng;
            ArrayList<Integer> currentClique;
            
            @Override
            public void start(){
                current = new Clique(numberOfVertices);
                thrBest = threadLocal(best);
                currentClique = new ArrayList<Integer>();
                prng = new Random (seed + rank());
            }
            
            @Override
            public void run(int iter) throws Exception {
                
                //Reset for every iteration
                current.reset();
                currentClique.clear();
                        
                //Pick a random vertex from the random number generator.
                int requiredDegree = 1;
                int currentVertex = prng.nextInt(numberOfVertices);
            
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
     
        //Print the max clique found after all iterations.
        int[] vertices = best.getVertices();
        int size = best.getSize();
        System.out.println("Vertices in the clique are as follows:");
        for(int idx=0; idx<vertices.length; ++idx){
            if(vertices[idx]==1){
                System.out.print(idx+" ");
            }
        }
        System.out.println("\nSize of max clique is: "+size);
    }
}
