Assumptions:

=> Gets the input graph as filename from the user.

=> The file should be in the following format.
	<NumberOfVertices><space><NumberOfEdges>
	<0><space><29>	=>this would constitute an undirected edge between vertex 0 and vertex 29
	<96><space><5>  =>this would constitute an undirected edge between vertex 96 and vertex 5
	.
	..
	...

=> Since an undirected graph is what is being assumed, an edge from vertex1 to vertex2 would mean an edge exists from vertex2 to vertex1 as well.

=> Vertices are represented as numbers starting from 0 upto "NumberOfVertices-1"

=> Both the vertices and total number of edges fit within the range of integers and are positive.


Program Execution Instructions:

=> MaxCliqueSeq.java is the sequential program. It is is executed as follows...

	java pj2 MaxCliqueSeq <filename> <seed> <numberOfIterations>
 		<filename>           =   name of the file that contains the graph.
 		<seed>               =   Random number generator's seed
 		<numberOfIterations> =   number of random restarts on the graph


=> MaxCliqueSmp.java is the multicore parallel program. Is is executed as follows...

	java pj2 threads=<numOfThreads> MaxCliqueSmp <filename> <seed> <numberOfIterations>

 		<numOfThreads>       =   Number of parallel team threads among which the iterations are distributed(optional)
 		<filename>           =   name of the file that contains the graph.
 		<seed>               =   Random number generator's seed
 		<numberOfIterations> =   number of random restarts on the graph


=> MaxCliqueClu.java is the cluster parallel program. It is executed as follows...

	java pj2 threads=<numOfThreads> workers=<numOfWorkers> jar=<nameOfJar> MaxCliqueClu <filename> <seed> <numberOfIterations>

 		<numOfThreads>       =   Number of threads among which the iterations are distributed(optional)
 		<numOfWorkers>       =   Number of workers to be involved in the computation(optional)
 		<filename>           =   name of the file that contains the graph.
 		<seed>               =   Random number generator's seed
 		<numberOfIterations> =   number of random restarts on the graph



=> Outputs are the vertices containing the possible maximum clique and the size of the clique.