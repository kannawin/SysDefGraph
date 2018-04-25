import java.util.ArrayList;
import java.io.*;
import com.tinkerpop.blueprints.*;

public class GephiExport
{
	//TODO: build path
	//TODO: go through each node and define edges
	//TODO: Export as JSON file for gephi
	//   rename Label to label
	
	
	
	
	//TODO: refine how the graph reads in the .csv files. right now it reads it in as two different datasets
	/**
	 * Takes in the arraylist of all nodes that you want graphed to show connections to each other
	 *   - make sure that every node you want is connected somehow, methods in the traversal class can help
	 * @param nodes
	 * @param graph
	 */
	public static void organizePathCSV(ArrayList<Vertex> nodes, Graph graph) 
	{
		int id = 10;
		int edgeId = 10000;
		ArrayList<String> attributes = new ArrayList<String>();
		
		File file = new File("nodes_export.csv");
		File file0 = new File("edges_export.csv");
		
		try 
		{
			for(Vertex v : nodes)
			{
				if(v != null)
				{
					for(String s : v.getPropertyKeys()) 
					{
						if(attributes.indexOf(s) == -1)
						{
							attributes.add(s);
						}
					}
				}
			}
			
			String line = "Id,";
			String edgeLine = "Source,Target,Type,Id,Label,timeset,Weight,From";
			
			
			for(String s : attributes) 
			{
				line += s + ",";
			}
			
			line = line.substring(0,line.length() - 2);
			
			FileWriter fw = new FileWriter(file);
			FileWriter fw0 = new FileWriter(file0);
			fw.write(line + "\n");
			fw0.write(edgeLine + "\n");
			
			for(Vertex v : nodes) 
			{
				if(v != null)
				{
					String[] props = new String[attributes.size()];
					for(int i = 0; i < props.length;i++)
					{
						props[i] = "na";
					}
					for(String s : v.getPropertyKeys()) 
					{
						props[attributes.indexOf(s)] = (String) v.getProperty(s);
					}
					
					for(Edge e : v.getEdges(Direction.OUT, "contains"))
					{
						Vertex x = e.getVertex(Direction.IN);
						if(x != null) 
						{
							int identity = nodes.indexOf(x) + 10;
							edgeLine = v.getId().toString() + "," + x.getId().toString() + ",Directed," + (edgeId++) + ",,,1,Inventory";
							fw0.write(edgeLine + "\n");
							fw0.flush();
						}
					}
					
					
					
					line = v.getId().toString() + ",";
					for(int i = 0; i < props.length;i++) 
					{
						line += props[i] + ",";
					}
					line = line.substring(0,line.length() - 2);
					
					
				
					fw.write(line + "\n");
					fw.flush();
				}

			}
		} catch(IOException e) {}
		
	}
	/**
	 * Gephi doesn't accept .json files. However the gremlin console does
	 * @param nodes
	 * @param graph
	 */
	public static void organizePathJSON(ArrayList<Vertex> nodes, Graph graph) 
	{
		int index = 10 + nodes.size();
		try
		{
			File file = new File("gephi_export.json");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			
			//go through all nodes, retrieve connecting nodes and create a json line for it following the format
			for(Vertex v : nodes) 
			{
				if(v != null && v.getPropertyKeys().size() > 0) {
					String opener = "{\"id\":" + (nodes.indexOf(v) + 10) + ",\"label\":\"" + v.getProperty("Label").toString() + "\",\"inE\":{\"contains\":[";
					String edges = "";
					for(Edge e : v.getEdges(Direction.OUT, "contains")) 
					{
						if(nodes.indexOf(e.getVertex(Direction.IN)) != -1 && e.getVertex(Direction.IN) != null) 
						{
							edges += "{\"id\":" + (index++) + ",\"outV\":" + (nodes.indexOf(e.getVertex(Direction.IN)) + 10) + "},";
						}
					}
					if(edges.length() > 2) 
					{
						edges = edges.substring(0, edges.length() - 1);
					}
					String properties = "]},\"properties\":{";
					for(String s : v.getPropertyKeys()) 
					{
						properties += s + ":[{\"id\":" + (index++) + ",\"value\":\"" + v.getProperty(s).toString() + "\"}],";
					}
					properties = properties.substring(0,properties.length() - 1);
					properties += "}}";
					
					String inLine = opener + edges + properties;
					
					//This is where it'll print to a .json file
					fw.write(inLine);
					
				}
			}
		} catch(IOException e) {}
		
	}
	

}
