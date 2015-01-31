import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Tuple;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Graph representation as an adjacency matrix. Also stores the degree of every vertex and the total number of vertices.
 * 
 * @author Yogesh Jagadeesan and Dler Ahmad
 */
public class Graph extends Tuple{
    
    private int [][] graph;         //  Adjacency matrix
    private int [] degree;          //  Degree of every vertex(index)
    private int numberOfVertices;   //  Total number of vertices.
    
    /**
     * Default constructor.
     */
    public Graph(){       
    }
    
    /**
     * Parameterized constructor that takes in a file input and constructs the graph from it.
     * 
     * @param file          File to construct the graph from(undirected).
     * @throws Exception    Not used.
     */
    public Graph(File file) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int count=0;
        while((line=br.readLine())!=null){
            String[] numbers = line.split(" ");
            if(count==0){
                numberOfVertices = Integer.parseInt(numbers[0]);
                graph = new int[numberOfVertices][numberOfVertices];
                degree = new int[numberOfVertices];
            }
            else{
                int vertex1 = Integer.parseInt(numbers[0]);
                int vertex2 = Integer.parseInt(numbers[1]);
                graph[vertex1][vertex2] = 1;
                graph[vertex2][vertex1] = 1;
                ++degree[vertex1];
                ++degree[vertex2];
            }
            ++count;
        }
        br.close();
    }
    
    /**
     * Given a vertex, return its degree
     * 
     * @param vertex    The vertex to get the degree of.
     * @return          Degree of the vertex
     */
    public int getDegree(int vertex){
        return degree[vertex];
    }
    
    /**
     * Get the number of vertices in the graph
     * @return number of vertices in the graph
     */
    public int getNumberOfVertices(){
        return numberOfVertices;
    }
    
    /**
     * Given a vertex, return the vertices its immediately connected to.
     * 
     * @param vertex    The vertex whose neighbours is required.
     * @return          An array containing the neighbours of the vertex.
     */
    public int[] getNeighbours(int vertex){
        return graph[vertex];
    }
    
    /**
     * If two vertices are connected or not.
     * 
     * @param _vertex1  First vertex
     * @param _vertex2  Second vertex
     * @return          true if the value is 1, false otherwise
     */
    public boolean isConnected(int _vertex1, int _vertex2){
        if(graph[_vertex1][_vertex2] == 1){
            return true;
        }
        return false;
    }
 
    /**
     * Write this graph to tuple space.
     * 
     * @param out           The outstream to write the graph to.
     * @throws IOException  Not used
     */
    @Override
    public void writeOut(OutStream out)throws IOException{
        out.writeInt(numberOfVertices);
        for(int idx=0; idx<numberOfVertices; ++idx){
            out.writeIntArray(graph[idx]);
        }
        out.writeIntArray(degree);
    }
    
    /**
     * Read graph from tuple space.
     * 
     * @param in            The instream to read the graph from.
     * @throws IOException  Not used.
     */
    @Override
    public void readIn(InStream in) throws IOException{
        numberOfVertices = in.readInt();
        graph = new int[numberOfVertices][];
        for(int idx=0; idx<numberOfVertices; ++idx){
            graph[idx] = in.readIntArray();
        }
        degree = in.readIntArray();
    }
}