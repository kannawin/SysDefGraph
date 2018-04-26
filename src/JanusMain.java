import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;


public class JanusMain
{


	public static void main(String[] args)
	{
		
		
		for(int z = 0; z < 50; z++) {
			Graph graph = new TinkerGraph();
			
			ArrayList<Long> testTimers = new ArrayList<Long>();
			Long[] graphSizes = new Long[4];
			
			//Adds initial memory allocation and the memory used in initialization
			graphSizes[0] = Runtime.getRuntime().totalMemory();
			graphSizes[1] = Runtime.getRuntime().freeMemory();
			
			//test 0, load in the graph of nodes
			testTimers.add(System.nanoTime());
			GraphFactory.vetexFactory("/home/dna/Desktop/Graph/DataDump/nodes/");
			testTimers.add(System.nanoTime());
			
			//Prints out memory used after nodes are brought into the graph
			graphSizes[2] = Runtime.getRuntime().freeMemory();
			
			//test 1: Create edges from edges csv
			testTimers.add(System.nanoTime());
			GraphFactory.edgeFactory("/home/dna/Desktop/Graph/DataDump/edges.csv");
			testTimers.add(System.nanoTime());
			
			//Prints out memory used after edges are input into the graph
			graphSizes[3] = Runtime.getRuntime().freeMemory();

			
			graph = GraphFactory.getGraph();
			
			//test 2: find the shortest path between two known nodes			
			String startNode = "29ff96320eb348e48f7293e96bf8458e";		//PSU
			String endNode = "48453f8815c94df1a96753c326ad500d";		//Storage Disk
			

			testTimers.add(System.nanoTime());
			Traversal.logicalPath(graph, startNode, endNode);
			testTimers.add(System.nanoTime());
			
			//System.out.println("\n\n\n");
			
			//test 3: find the connected components of type to a certain node (in this case, to a storage chassis)
			testTimers.add(System.nanoTime());
			Traversal.containedComponentsByType(graph.getVertex("53f9a706b4ab4be2b99625f0352e2ba1"), graph);
			testTimers.add(System.nanoTime());
			
			
			
			//ArrayList<Vertex> nullArray = new ArrayList<Vertex>();
			
			//get a storage controller
			//nullArray.add(graph.getVertex("53f9a706b4ab4be2b99625f0352e2ba1"));
			
			//from a switch
			//Vertex test = graph.getVertex("10.3.32.16");
			//GephiExport.organizePathCSV(Traversal.containedComponents(test), graph);
			
		//	graph = GraphFactory.getGraph();
			//System.out.println("success");
			
			printTimers(testTimers, graphSizes);
		}
	}
	
	public static void printTimers(ArrayList<Long> timers, Long[] graphSizes)
	{
		String inputLine = "";
		for(int i = 0; i < timers.size(); i += 2) {
			long netTime = timers.get(i+1) - timers.get(i);
			double inSeconds = (double)netTime/1000000000.0;
			System.out.println("Test " + (i/2) + ":\t" + inSeconds + " seconds");
			inputLine += inSeconds + ",";
		}
		inputLine += graphSizes[0] + "," + graphSizes[1] + "," + graphSizes[2] + "," + graphSizes[3] + "\n";
		
		try {
			Files.write(Paths.get("data_export.csv"), inputLine.getBytes(), StandardOpenOption.APPEND);
		}catch(IOException e) {}
	}
	
}
