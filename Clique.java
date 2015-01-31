import edu.rit.io.InStream;
import edu.rit.io.OutStream;
import edu.rit.pj2.Tuple;
import edu.rit.pj2.Vbl;
import java.io.IOException;

/**
 * Holds the vertices of the clique and the size.
 * 
 * @author Yogesh Jagadeesan and Dler Ahmad
 */
public class Clique extends Tuple implements Vbl, Comparable<Clique> {

    private int[] vertices;
    private int size;
    
    /**
     * Default constructor.
     */
    public Clique(){      
    }
    
    /**
     * Given the number of vertices, initialize the clique object.
     * 
     * @param _numberOfVertices     Number of vertices of the clique.
     */
    public Clique(int _numberOfVertices){
        vertices = new int[_numberOfVertices];
    }
    
    /**
     * Create clique object. Called from clone.
     * 
     * @param _vertices     Vertices to clone.
     * @param _size         Size to clone.
     */
    public Clique(int[] _vertices, int _size){
        vertices = new int[_vertices.length];
        System.arraycopy(_vertices, 0, vertices, 0, vertices.length);
        size = _size;
    }

    /**
     * Add a vertex to the clique.
     * 
     * @param _vertex   Vertex to be added to clique.
     */
    public void add(int _vertex){
        vertices[_vertex] = 1;
        ++size;
    }
   
    /**
     * Get the size of the clique.
     * 
     * @return  size of clique.
     */
    public int getSize(){
        return size;
    }
    
    /**
     * Get the vertices of the clique.
     * 
     * @return  Array containing the vertices of the clique.
     */
    public int[] getVertices(){
        return vertices;
    }
    
    /**
     * Reset the current clique object.
     */
    public void reset(){
        size=0;
        for(int idx=0; idx<vertices.length; ++idx){
            vertices[idx] = 0;
        }
    }
    
    /**
     * GIven a vertex, check if its in the current clique.
     * 
     * @param _vertex   The vertex to be checked.
     * @return          True if the vertex is indeed in the clique, false otherwise.
     */
    public boolean isVertexInClique(int _vertex){
        if(vertices[_vertex] == 1){
            return true;
        }
        return false;
    }
    
    /**
     * Set the current clique with the given clique parameter.
     * 
     * @param vbl   The clique to set the current clique as.
     */
    @Override
    public void set(Vbl vbl) {
        Clique candidate = (Clique) vbl;
        this.size = candidate.getSize();
        System.arraycopy(candidate.getVertices(), 0, vertices, 0, vertices.length);
    }

    /**
     * Compare current clique with the given one and keep the larger clique.
     * 
     * @param vbl   The clique to compare the current clique with.
     */
    @Override
    public void reduce(Vbl vbl) {
        Clique candidate = (Clique) vbl;
        if(candidate.size > this.size){
            this.set(candidate);
        }
    }
    
    /**
     * Clone the current clique.
     * @return  The cloned clique.
     */
    @Override
    public Vbl clone(){
        return new Clique(vertices, size);
    }

    /**
     * Compare current clique size with the given clique.
     * 
     * @param o     0 if their sizes are equal, 1 if the current clique is larger, -1 if current clique is smaller.
     * @return 
     */
    @Override
    public int compareTo(Clique o) {
        if(size > o.getSize())
            return 1;
        else if(size == o.getSize())
            return 0;
        return -1;
    }
    
    /**
     * Write current clique to the tuple space.
     * 
     * @param out           Outstream to write the clique to.
     * @throws IOException  not used.
     */
    @Override
    public void writeOut(OutStream out)throws IOException{
       out.writeIntArray(vertices);
       out.writeInt(size);
    }
    
    /**
     * Reads clique from tuple space.
     * 
     * @param in            The instream to read clique from
     * @throws IOException  Not used.
     */
    @Override
    public void readIn(InStream in) throws IOException{
        vertices = in.readIntArray();
        size = in.readInt();
    }
}