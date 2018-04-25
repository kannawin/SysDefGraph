import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class GraphFactory 
{

	private static Graph graph;
	
	/**
	 * Takes in a directory full of .csv files and instantiates nodes and properties
	 * @param directory
	 */
	
	public static void vetexFactory(String directory)
	{
		graph = new TinkerGraph();
		
		
		File file = new File(directory);
		File[] dir = file.listFiles();
		

		
		for(int i = 0; i < dir.length; i++)
		{
			file = new File(dir[i].getPath());
			FileReader in = null;
			
			try
			{
				in = new FileReader(file);
				BufferedReader br = new BufferedReader(in);
				String nextLine = br.readLine();
				String[] attributes = nextLine.split(",");
				ArrayList<String> butes = new ArrayList<String>();
				for(int j = 0; j < attributes.length;j++) 
				{
					butes.add(attributes[j]);
				}
				
				int label = butes.indexOf("Id");
				
				while((nextLine = br.readLine()) != null)
				{
					String[] tempLine = nextLine.split(",");
					
					//just ignores duplicate nodes
					try 
					{
						Vertex temp = graph.addVertex(tempLine[label]);
						for(int j = 0; j < attributes.length - 1; j++) 
						{
							if(j != label)
							{
								temp.setProperty(attributes[j], tempLine[j]);
							}
						}
					} catch(IllegalArgumentException z) {}
						
				}
			}
			catch(FileNotFoundException z)
			{
				z.printStackTrace();
			}
			catch(IOException z) 
			{
				z.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Creates edges to the graph given a csv with the correct format for relations
	 * @param relations
	 */
	public static void edgeFactory(String relations) 
	{
		
		File file = new File(relations);
		FileReader in = null;
		try
		{
			in = new FileReader(file);
			BufferedReader br = new BufferedReader(in);
			String nextLine = br.readLine();
			
			while((nextLine = br.readLine()) != null)
			{
				String[] inOut = nextLine.split(",");
				
				//Source is index 0
				Vertex source = graph.getVertex(inOut[0]);
				//Target is index 1
				Vertex target = graph.getVertex(inOut[1]);
				
				
				try
				{
					graph.addEdge(null,source,target,"contains");
					graph.addEdge(null, target, source, "consumes");
				} catch(NullPointerException e) {}
			}
			br.close();
			in.close();
			
		} catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static Graph getGraph() 
	{
		return graph;
	}
	
}
