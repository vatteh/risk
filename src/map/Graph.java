package map;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Graph data structure that helps represent the edges in the game board. We use
 * it to help us know who's adjacent to whom.
 * 
 * @author Duong Pham
 * @version April 14, 2009
 * 
 */
public class Graph implements Serializable{

	private static final long serialVersionUID = -6737918696257719101L;
	private final int NUM_VERTICES = 43;
	private int[][] adjacencyMatrix;

	/**
	 * This is the constructor. It creates an empty graph at first (filled with
	 * zeros). It then fills up the the adjacency-matrix with all territory
	 * edges.  Lastly, it makes sure that territories are not adjacent to itself.
	 */
	public Graph() {
		adjacencyMatrix = new int[NUM_VERTICES][NUM_VERTICES];
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix.length; j++) {
				adjacencyMatrix[i][j] = 0;
			}
		}
		this.addTerritoryEdges();
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			adjacencyMatrix[i][i] = 0;
		}
	}

	/**
	 * Adds an edge to the graph.
	 * 
	 * @param source
	 *            the source vertex.
	 * @param destination
	 *            the destination vertex.
	 */
	public void addEdge(int source, int destination) {
		adjacencyMatrix[source][destination] = 1;
		adjacencyMatrix[destination][source] = 1;
	}

	/**
	 * Checks if a vertex is adjacent to another.
	 * 
	 * @param source
	 *            the source vertex.
	 * @param destination
	 *            the destination vertex.
	 * @return true if adjacent, false otherwise.
	 */
	public boolean isAdjacent(int source, int destination) {
		return adjacencyMatrix[source][destination] == 1;
	}

	/**
	 * Returns a list of unique ID numbers of all adjacent territories.
	 * 
	 * @param vertex
	 *            the vertex you want to know what's adjacent to.
	 * @return a list of all adjacent vertices.
	 */
	public ArrayList<Integer> getAdjacentList(int vertex) {
		ArrayList<Integer> adjacentList = new ArrayList<Integer>();
		for (int i = 0; i < NUM_VERTICES; i++) {
			if (this.isAdjacent(vertex, i) == true) {
				adjacentList.add(i);
			}
		}
		return adjacentList;
	}

	/**
	 * This is a private helper method that adds all the edges of the board to
	 * the graph.  All edges have been confirmed to be correct.
	 */
	private void addTerritoryEdges() {
		addEdge(1, 1);
		addEdge(1, 2);
		addEdge(1, 6);
		addEdge(1, 32);
		addEdge(2, 2);
		addEdge(2, 6);
		addEdge(2, 7);
		addEdge(2, 9);
		addEdge(3, 3);
		addEdge(3, 4);
		addEdge(3, 9);
		addEdge(3, 13);
		addEdge(4, 4);
		addEdge(4, 7);
		addEdge(4, 8);
		addEdge(4, 9);
		addEdge(5, 5);
		addEdge(5, 6);
		addEdge(5, 7);
		addEdge(5, 8);
		addEdge(5, 15);
		addEdge(6, 6);
		addEdge(6, 7);
		addEdge(7, 7);
		addEdge(7, 8);
		addEdge(7, 9);
		addEdge(8, 8);
		addEdge(9, 9);
		addEdge(10, 10);
		addEdge(10, 11);
		addEdge(10, 12);
		addEdge(11, 11);
		addEdge(11, 12);
		addEdge(11, 13);
		addEdge(11, 25);
		addEdge(12, 12);
		addEdge(12, 13);
		addEdge(13, 13);
		addEdge(14, 14);
		addEdge(14, 15);
		addEdge(14, 16);
		addEdge(14, 17);
		addEdge(14, 20);
		addEdge(15, 15);
		addEdge(15, 17);
		addEdge(16, 16);
		addEdge(16, 17);
		addEdge(16, 18);
		addEdge(16, 19);
		addEdge(16, 20);
		addEdge(17, 17);
		addEdge(17, 19);
		addEdge(18, 18);
		addEdge(18, 19);
		addEdge(18, 20);
		addEdge(18, 23);
		addEdge(18, 25);
		addEdge(18, 33);
		addEdge(19, 19);
		addEdge(19, 27);
		addEdge(19, 33);
		addEdge(19, 37);
		addEdge(20, 20);
		addEdge(20, 25);
		addEdge(21, 21);
		addEdge(21, 22);
		addEdge(21, 25);
		addEdge(21, 26);
		addEdge(22, 22);
		addEdge(22, 23);
		addEdge(22, 24);
		addEdge(22, 25);
		addEdge(22, 26);
		addEdge(22, 33);
		addEdge(23, 23);
		addEdge(23, 25);
		addEdge(23, 33);
		addEdge(24, 24);
		addEdge(24, 26);
		addEdge(25, 25);
		addEdge(26, 26);
		addEdge(27, 27);
		addEdge(27, 28);
		addEdge(27, 29);
		addEdge(27, 33);
		addEdge(27, 37);
		addEdge(28, 28);
		addEdge(28, 29);
		addEdge(28, 34);
		addEdge(28, 35);
		addEdge(28, 36);
		addEdge(28, 37);
		addEdge(29, 29);
		addEdge(29, 33);
		addEdge(29, 35);
		addEdge(30, 30);
		addEdge(30, 32);
		addEdge(30, 34);
		addEdge(30, 36);
		addEdge(30, 38);
		addEdge(31, 31);
		addEdge(31, 32);
		addEdge(31, 34);
		addEdge(32, 32);
		addEdge(32, 34);
		addEdge(32, 38);
		addEdge(33, 33);
		addEdge(34, 34);
		addEdge(34, 36);
		addEdge(35, 35);
		addEdge(35, 40);
		addEdge(36, 36);
		addEdge(36, 37);
		addEdge(36, 38);
		addEdge(37, 37);
		addEdge(38, 38);
		addEdge(39, 39);
		addEdge(39, 41);
		addEdge(39, 42);
		addEdge(40, 40);
		addEdge(40, 41);
		addEdge(40, 42);
		addEdge(41, 41);
		addEdge(41, 42);
		addEdge(42, 42);
	}
	
}
