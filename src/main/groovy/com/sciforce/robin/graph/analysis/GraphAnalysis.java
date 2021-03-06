/*
 * Copyright (c) 2018 Sciforce Solutions.
 */
package com.sciforce.robin.graph.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.sciforce.robin.graph.view.CellState;
import com.sciforce.robin.graph.view.Graph;
import com.sciforce.robin.graph.view.GraphView;

/**
 * A singleton class that provides algorithms for graphs. Assume these
 * variables for the following examples:<br>
 * <code>
 * ICostFunction cf = DistanceCostFunction();
 * Object[] v = graph.getChildVertices(graph.getDefaultParent());
 * Object[] e = graph.getChildEdges(graph.getDefaultParent());
 * GraphAnalysis mga = GraphAnalysis.getInstance();
 * </code>
 * 
 * <h3>Shortest Path (Dijkstra)</h3>
 * 
 * For example, to find the shortest path between the first and the second
 * selected cell in a graph use the following code: <br>
 * <br>
 * <code>Object[] path = mga.getShortestPath(graph, from, to, cf, v.length, true);</code>
 * 
 * <h3>Minimum Spanning Tree</h3>
 * 
 * This algorithm finds the set of edges with the minimal length that connect
 * all vertices. This algorithm can be used as follows:
 * <h5>Prim</h5>
 * <code>mga.getMinimumSpanningTree(graph, v, cf, true))</code>
 * <h5>Kruskal</h5>
 * <code>mga.getMinimumSpanningTree(graph, v, e, cf))</code>
 * 
 * <h3>Connection Components</h3>
 * 
 * The union find may be used as follows to determine whether two cells are
 * connected: <code>boolean connected = uf.differ(vertex1, vertex2)</code>.
 * 
 * @see ICostFunction
 */
public class GraphAnalysis
{

	/**
	 * Holds the shared instance of this class.
	 */
	protected static GraphAnalysis instance = new GraphAnalysis();

	/**
	 *
	 */
	protected GraphAnalysis()
	{
		// empty
	}

	/**
	 * @return Returns the sharedInstance.
	 */
	public static GraphAnalysis getInstance()
	{
		return instance;
	}

	/**
	 * Sets the shared instance of this class.
	 * 
	 * @param instance The instance to set.
	 */
	public static void setInstance(GraphAnalysis instance)
	{
		GraphAnalysis.instance = instance;
	}

	/**
	 * Returns the shortest path between two cells or their descendants
	 * represented as an array of edges in order of traversal. <br>
	 * This implementation is based on the Dijkstra algorithm.
	 * 
	 * @param graph The object that defines the graph structure
	 * @param from The source cell.
	 * @param to The target cell (aka sink).
	 * @param cf The cost function that defines the edge length.
	 * @param steps The maximum number of edges to traverse.
	 * @param directed If edge directions should be taken into account.
	 * @return Returns the shortest path as an alternating array of vertices
	 * and edges, starting with <code>from</code> and ending with
	 * <code>to</code>.
	 * 
	 * @see #createPriorityQueue()
	 */
	public Object[] getShortestPath(Graph graph, Object from, Object to,
									ICostFunction cf, int steps, boolean directed)
	{
		// Sets up a pqueue and a hashtable to store the predecessor for each
		// cell in tha graph traversal. The pqueue is initialized
		// with the from element at prio 0.
		GraphView view = graph.getView();
		FibonacciHeap q = createPriorityQueue();
		Hashtable<Object, Object> pred = new Hashtable<Object, Object>();
		q.decreaseKey(q.getNode(from, true), 0); // Inserts automatically

		// The main loop of the dijkstra algorithm is based on the pqueue being
		// updated with the actual shortest distance to the source vertex.
		for (int j = 0; j < steps; j++)
		{
			FibonacciHeap.Node node = q.removeMin();
			double prio = node.getKey();
			Object obj = node.getUserObject();

			// Exits the loop if the target node or vertex has been reached
			if (obj == to)
			{
				break;
			}

			// Gets all outgoing edges of the closest cell to the source
			Object[] e = (directed) ? graph.getOutgoingEdges(obj) : graph
					.getConnections(obj);

			if (e != null)
			{
				for (int i = 0; i < e.length; i++)
				{
					Object[] opp = graph.getOpposites(new Object[] { e[i] },
							obj);

					if (opp != null && opp.length > 0)
					{
						Object neighbour = opp[0];

						// Updates the priority in the pqueue for the opposite node
						// to be the distance of this step plus the cost to
						// traverese the edge to the neighbour. Note that the
						// priority queue will make sure that in the next step the
						// node with the smallest prio will be traversed.
						if (neighbour != null && neighbour != obj
								&& neighbour != from)
						{
							double newPrio = prio
									+ ((cf != null) ? cf.getCost(view
											.getState(e[i])) : 1);
							node = q.getNode(neighbour, true);
							double oldPrio = node.getKey();

							if (newPrio < oldPrio)
							{
								pred.put(neighbour, e[i]);
								q.decreaseKey(node, newPrio);
							}
						}
					}
				}
			}

			if (q.isEmpty())
			{
				break;
			}
		}

		// Constructs a path array by walking backwards through the predessecor
		// map and filling up a list of edges, which is subsequently returned.
		ArrayList<Object> list = new ArrayList<Object>(2 * steps);
		Object obj = to;
		Object edge = pred.get(obj);

		if (edge != null)
		{
			list.add(obj);

			while (edge != null)
			{
				list.add(0, edge);

				CellState state = view.getState(edge);
				Object source = (state != null) ? state
						.getVisibleTerminal(true) : view.getVisibleTerminal(
						edge, true);
				boolean isSource = source == obj;
				obj = (state != null) ? state.getVisibleTerminal(!isSource)
						: view.getVisibleTerminal(edge, !isSource);
				list.add(0, obj);

				edge = pred.get(obj);
			}
		}

		return list.toArray();
	}

	/**
	 * Returns the minimum spanning tree (MST) for the graph defined by G=(E,V).
	 * The MST is defined as the set of all vertices with minimal lengths that
	 * forms no cycles in G.<br>
	 * This implementation is based on the algorihm by Prim-Jarnik. It uses
	 * O(|E|+|V|log|V|) time when used with a Fibonacci heap and a graph whith a
	 * double linked-list datastructure, as is the case with the default
	 * implementation.
	 * 
	 * @param graph
	 *            the object that describes the graph
	 * @param v
	 *            the vertices of the graph
	 * @param cf
	 *            the cost function that defines the edge length
	 * 
	 * @return Returns the MST as an array of edges
	 * 
	 * @see #createPriorityQueue()
	 */
	public Object[] getMinimumSpanningTree(Graph graph, Object[] v,
										   ICostFunction cf, boolean directed)
	{
		ArrayList<Object> mst = new ArrayList<Object>(v.length);

		// Sets up a pqueue and a hashtable to store the predecessor for each
		// cell in tha graph traversal. The pqueue is initialized
		// with the from element at prio 0.
		FibonacciHeap q = createPriorityQueue();
		Hashtable<Object, Object> pred = new Hashtable<Object, Object>();
		Object u = v[0];
		q.decreaseKey(q.getNode(u, true), 0);

		for (int i = 1; i < v.length; i++)
		{
			q.getNode(v[i], true);
		}

		// The main loop of the dijkstra algorithm is based on the pqueue being
		// updated with the actual shortest distance to the source vertex.
		while (!q.isEmpty())
		{
			FibonacciHeap.Node node = q.removeMin();
			u = node.getUserObject();
			Object edge = pred.get(u);

			if (edge != null)
			{
				mst.add(edge);
			}

			// Gets all outgoing edges of the closest cell to the source
			Object[] e = (directed) ? graph.getOutgoingEdges(u) : graph
					.getConnections(u);
			Object[] opp = graph.getOpposites(e, u);

			if (e != null)
			{
				for (int i = 0; i < e.length; i++)
				{
					Object neighbour = opp[i];

					// Updates the priority in the pqueue for the opposite node
					// to be the distance of this step plus the cost to
					// traverese the edge to the neighbour. Note that the
					// priority queue will make sure that in the next step the
					// node with the smallest prio will be traversed.
					if (neighbour != null && neighbour != u)
					{
						node = q.getNode(neighbour, false);

						if (node != null)
						{
							double newPrio = cf.getCost(graph.getView()
									.getState(e[i]));
							double oldPrio = node.getKey();

							if (newPrio < oldPrio)
							{
								pred.put(neighbour, e[i]);
								q.decreaseKey(node, newPrio);
							}
						}
					}
				}
			}
		}

		return mst.toArray();
	}

	/**
	 * Returns the minimum spanning tree (MST) for the graph defined by G=(E,V).
	 * The MST is defined as the set of all vertices with minimal lenths that
	 * forms no cycles in G.<br>
	 * This implementation is based on the algorihm by Kruskal. It uses
	 * O(|E|log|E|)=O(|E|log|V|) time for sorting the edges, O(|V|) create sets,
	 * O(|E|) find and O(|V|) union calls on the union find structure, thus
	 * yielding no more than O(|E|log|V|) steps. For a faster implementatin
	 * 
	 * @see #getMinimumSpanningTree(Graph, Object[], ICostFunction,
	 *      boolean)
	 * 
	 * @param graph The object that contains the graph.
	 * @param v The vertices of the graph.
	 * @param e The edges of the graph.
	 * @param cf The cost function that defines the edge length.
	 * 
	 * @return Returns the MST as an array of edges.
	 * 
	 * @see #createUnionFind(Object[])
	 */
	public Object[] getMinimumSpanningTree(Graph graph, Object[] v,
										   Object[] e, ICostFunction cf)
	{
		// Sorts all edges according to their lengths, then creates a union
		// find structure for all vertices. Then walks through all edges by
		// increasing length and tries adding to the MST. Only edges are added
		// that do not form cycles in the graph, that is, where the source
		// and target are in different sets in the union find structure.
		// Whenever an edge is added to the MST, the two different sets are
		// unified.
		GraphView view = graph.getView();
		UnionFind uf = createUnionFind(v);
		ArrayList<Object> result = new ArrayList<Object>(e.length);
		CellState[] edgeStates = sort(view.getCellStates(e), cf);

		for (int i = 0; i < edgeStates.length; i++)
		{
			Object source = edgeStates[i].getVisibleTerminal(true);
			Object target = edgeStates[i].getVisibleTerminal(false);

			UnionFind.Node setA = uf.find(uf.getNode(source));
			UnionFind.Node setB = uf.find(uf.getNode(target));

			if (setA == null || setB == null || setA != setB)
			{
				uf.union(setA, setB);
				result.add(edgeStates[i].getCell());
			}
		}

		return result.toArray();
	}

	/**
	 * Returns a union find structure representing the connection components of
	 * G=(E,V).
	 * 
	 * @param graph The object that contains the graph.
	 * @param v The vertices of the graph.
	 * @param e The edges of the graph.
	 * @return Returns the connection components in G=(E,V)
	 * 
	 * @see #createUnionFind(Object[])
	 */
	public UnionFind getConnectionComponents(Graph graph, Object[] v,
											 Object[] e)
	{
		GraphView view = graph.getView();
		UnionFind uf = createUnionFind(v);

		for (int i = 0; i < e.length; i++)
		{
			CellState state = view.getState(e[i]);
			Object source = (state != null) ? state.getVisibleTerminal(true)
					: view.getVisibleTerminal(e[i], true);
			Object target = (state != null) ? state.getVisibleTerminal(false)
					: view.getVisibleTerminal(e[i], false);

			uf.union(uf.find(uf.getNode(source)), uf.find(uf.getNode(target)));
		}

		return uf;
	}

	/**
	 * Returns a sorted set for <code>cells</code> with respect to
	 * <code>cf</code>.
	 * 
	 * @param states
	 *            the cell states to sort
	 * @param cf
	 *            the cost function that defines the order
	 * 
	 * @return Returns an ordered set of <code>cells</code> wrt.
	 *         <code>cf</code>
	 */
	public CellState[] sort(CellState[] states, final ICostFunction cf)
	{
		List<CellState> result = Arrays.asList(states);

		Collections.sort(result, new Comparator<CellState>()
		{

			/**
			 * 
			 */
			public int compare(CellState o1, CellState o2)
			{
				Double d1 = new Double(cf.getCost(o1));
				Double d2 = new Double(cf.getCost(o2));

				return d1.compareTo(d2);
			}

		});

		return (CellState[]) result.toArray();
	}

	/**
	 * Returns the sum of all cost for <code>cells</code> with respect to
	 * <code>cf</code>.
	 * 
	 * @param states
	 *            the cell states to use for the sum
	 * @param cf
	 *            the cost function that defines the costs
	 * 
	 * @return Returns the sum of all cell cost
	 */
	public double sum(CellState[] states, ICostFunction cf)
	{
		double sum = 0;

		for (int i = 0; i < states.length; i++)
		{
			sum += cf.getCost(states[i]);
		}

		return sum;
	}

	/**
	 * Hook for subclassers to provide a custom union find structure.
	 * 
	 * @param v
	 *            the array of all elements
	 * 
	 * @return Returns a union find structure for <code>v</code>
	 */
	protected UnionFind createUnionFind(Object[] v)
	{
		return new UnionFind(v);
	}

	/**
	 * Hook for subclassers to provide a custom fibonacci heap.
	 */
	protected FibonacciHeap createPriorityQueue()
	{
		return new FibonacciHeap();
	}

}
