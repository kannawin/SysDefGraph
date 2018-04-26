import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.tinkerpop.blueprints.*;


public class Traversal
{

	//TODO: traversal methods											STATUS:
	// point a to b															DONE
	// components contained (unique types)									COMPLETE
	// contained components (full list of all contained)					IN PROGRESS
	//TODO: return logical build tree.........  ????						PARTIALLY COMPLETE
	// can get levels of separation of components
	// can return just a list of associated components						
	//TODO: add/remove nodes and its containing nodes						IN PROGRESS
	
	//probably not needed, but the absolute stop point for getting to root 
	private final static String rootID = "";
	private final static String rootLabel = "vcesystem";
	private static ArrayList<ArrayList<Vertex>> logicalPaths;
	
	/**
	 * takes the graph and id of the start point and end point
	 * returns an efficient shortest path
	 * 
	 * @param graph
	 * @param id1
	 * @param id2
	 * @return
	 */
	public static ArrayList<Vertex> logicalPath(Graph graph, String id1, String id2)
	{
		Vertex start = graph.getVertex(id1);
		Vertex end = graph.getVertex(id2);
		
		//System.out.println(start.getProperty("Label").toString() + "\tStart");
		//System.out.println(end.getProperty("Label").toString() + "\tEnd");
		
		
		ArrayList<Vertex> startPathToRoot = logicalPathToRoot(start, rootLabel);
		ArrayList<Vertex> endPathToRoot = logicalPathToRoot(end, rootLabel);
		ArrayList<Vertex> finalPath = new ArrayList<Vertex>();
		
		int i1 = 0;
		int i2 = 0;
		
		for(int i = 0; i < startPathToRoot.size();i++) 
		{
			if(endPathToRoot.indexOf(startPathToRoot.get(i)) > 0)
			{
				i1 = i;
				i2 = endPathToRoot.indexOf(startPathToRoot.get(i));
			}
		}
		
		for(int i = 0; i < i1; i++)
		{
			finalPath.add(startPathToRoot.get(i));
		}
		for(int i = i2; i >= 0; i--)
		{
			finalPath.add(endPathToRoot.get(i));
		}
		
		for(Vertex v : finalPath)
		{
			//System.out.println(v.getProperty("Label").toString());
		}
		
		
		return finalPath;
	}
	
	/**
	 * Given a node it follows the cut and contract theorem to the set root node
	 * 
	 * in later iterations the root can dynamic or static that everything is sourced from
	 * @param node
	 * @param graph
	 * @return
	 */
	public static ArrayList<Vertex> logicalPathToRoot(Vertex node, String root)
	{
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		boolean flag = true;
		
		//add initial node
		path.add(node);
		
		while(flag)
		{
			Vertex next = null;
			
			for(Edge e : node.getEdges(Direction.IN, "contains")) 
			{
				next = e.getVertex(Direction.OUT);
				break;
			}
			boolean accept = false;
			//sets the node to iterate from next
			try {
				accept = next.getEdges(Direction.IN, "contains").iterator().next().getVertex(Direction.OUT).getId().toString().equals(null);
			}
			catch(NoSuchElementException e) {
				accept = false;
			}
			if(!next.getProperty("Label").toString().equals(root) && !accept)
			{
				node = next;
				path.add(next);
			}else if(next.getProperty("Label").toString().equals(root)) {
				path.add(next);
				flag = false;
			}
			
		}
		
		
		return path;
	}
	
	public static void deleteTree(Vertex node, Graph graph)
	{
		ArrayList<Vertex> connectingNodes = new ArrayList<Vertex>();
		
		connectingNodes.add(node);
		connectingNodes = vertexGrabber(node, connectingNodes);
		
		//System.out.println(connectingNodes.size());
		
		for(int i = connectingNodes.size() - 1; i > 0; i--)
		{
			Vertex temp = connectingNodes.get(i);
			graph.removeVertex(temp);
			System.out.println(i);
		}
		
		//System.out.println(connectingNodes.size());
		
	}
	
	private static ArrayList<Vertex> vertexGrabber(Vertex nextNode, ArrayList<Vertex> list)
	{
		for(Edge e : nextNode.getEdges(Direction.OUT, "contains"))
		{
			try 
			{
				Vertex temp = e.getVertex(Direction.IN);
				list.add(temp);
				vertexGrabber(temp, list);
			} catch(NullPointerException z) {}
		}
		
		return list;
	}
	
	
	public static ArrayList<Vertex> containedComponents(Vertex node)
	{
		ArrayList<Vertex> componentTypes = new ArrayList<Vertex>();
		logicalPaths = new ArrayList<ArrayList<Vertex>>();
		componentTypes.add(node);
		componentTypes = componentsHelper(componentTypes, node);
		
		return componentTypes;
	}
	
	private static ArrayList<Vertex> componentsHelper(ArrayList<Vertex> types, Vertex nextNode)
	{
		int i = 0;
		ArrayList<Vertex> helperArray = new ArrayList<Vertex>();

		helperArray.add(nextNode);
		
		for(Edge e : nextNode.getEdges(Direction.OUT, "contains"))
		{
			try {
				Vertex temp = e.getVertex(Direction.IN);

				types.add(temp);
				helperArray.add(temp);
			
				types = componentsHelper(types,temp);
				i++;
			} catch(NullPointerException z) {}
		}
		
		return types;
	}
	
	
	
	/**
	 * Grabs and prints the types of components, uses a recursive function to go layer by layer.
	 * only prints out how far away they are from the parent component
	 * @param node
	 * @param graph
	 */
	public static void containedComponentsByType(Vertex node, Graph graph) 
	{
		ArrayList<String> componentTypes = new ArrayList<String>();
		logicalPaths = new ArrayList<ArrayList<Vertex>>();
		componentTypes.add(node.getProperty("Label").toString());
		componentTypes = typesHelper(componentTypes, node);
		
		printLogical();
	}
	
	private static ArrayList<String> typesHelper(ArrayList<String> types, Vertex nextNode)
	{
		int i = 0;
		ArrayList<Vertex> helperArray = new ArrayList<Vertex>();

		helperArray.add(nextNode);
		
		for(Edge e : nextNode.getEdges(Direction.OUT, "contains")) 
		{
			try 
			{
				Vertex temp = e.getVertex(Direction.IN);
				if(types.indexOf(temp.getProperty("Label").toString()) < 0) {
					types.add(temp.getProperty("Label").toString());
					helperArray.add(temp);
					logicalPaths.add(logicalPathToRoot(temp,types.get(0)));
				}
				types = typesHelper(types,temp);
				i++;
			} catch(NullPointerException z) {}
		}
		
		return types;
	}
	
	public static void printLogical() 
	{
		int largeSize = 0;
		
		for(int i = 0; i < logicalPaths.size(); i++) 
		{
			if(logicalPaths.get(i).size() > largeSize) 
			{
				largeSize = logicalPaths.get(i).size();
			}
		}
		ArrayList<ArrayList<String>> path = new ArrayList<ArrayList<String>>();
		for(int i = largeSize; i > 0; i--) 
		{
			ArrayList<String> temp = new ArrayList<String>();
			for(ArrayList<Vertex> av : logicalPaths) {
				if(av.size() > 0) 
				{
					String property = av.get(av.size() - 1).getProperty("Label").toString();
					if(temp.indexOf(property) < 0) 
					{
						temp.add(property);
					}
					av.remove(av.size() - 1);
				}
			}
			path.add(temp);
		}

		for(ArrayList<String> as : path)
		{
			for(String s : as) 
			{
				//System.out.print(s + ", ");
			}
			//System.out.println("\n");
		}

	}
	
}
