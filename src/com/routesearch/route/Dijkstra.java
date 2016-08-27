package com.routesearch.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Dijkstra {

	public static ArrayList<Integer> path = null;
	public static int target;

	public static int minDistance(int dist[], boolean sptSet[]) {
		// Initialize min value
		int min = Integer.MAX_VALUE, min_index = -1;
		int V = Parms.vertices_num;

		for (int v = 0; v < V; v++)
			if (sptSet[v] == false && dist[v] <= min) {
				min = dist[v];
				min_index = v;
			}
		return min_index;
	}

	/**
	 * 寻找两点间最短路径
	 * 
	 * @param src
	 *            ：起点
	 * @param target
	 *            ：终点
	 * @return 最小代价
	 */
	public static int dijkstra(int[][] costmatrix, int src, int target) {
		path = new ArrayList<Integer>();
		Dijkstra.target = target;
		int V = Parms.vertices_num;

		int dist[] = new int[V]; // The output array. dist[i] will hold
		int parent[] = new int[V]; // the shortest distance from src to i

		Arrays.fill(dist, Integer.MAX_VALUE); // 初始化dist为最大值
		boolean sptSet[] = new boolean[V];

		// Distance of source vertex from itself is always 0
		dist[src] = 0;
		parent[src] = -1;

		// Find shortest path for all vertices
		for (int count = 0; count < V - 1; count++) {
			int u = minDistance(dist, sptSet);
			sptSet[u] = true;
			if (u == target)
				break;
			for (int v = 0; v < V; v++)
				if (!sptSet[v] && costmatrix[u][v] != 0
						&& dist[u] != Integer.MAX_VALUE
						&& dist[u] + costmatrix[u][v] < dist[v]) {
					parent[v] = u;
					dist[v] = dist[u] + costmatrix[u][v];
				}
		}
		getPath(parent, dist, src, target);
		return getWeight(dist, target);
	}

	// 获取起点到终点的路径
	public static void getPath(int parent[], int dist[], int src, int target) {
		path = new ArrayList<Integer>();
		if (dist[target] < Integer.MAX_VALUE && target != src) {// 当前顶点已求得最短路径并且当前顶点不等于源点
			path.add(target);
			int next = parent[target]; // 设置当前顶点的前驱顶点
			while (next != src) { // 若前驱顶点不为一个，循环求得剩余前驱顶点
				path.add(next);
				next = parent[next];
			}
			Collections.reverse(path);// 对路径反序输出变为正
		} else if (target != src) {// 当前顶点未求得最短路径的处理方法
			path = null;
		}
	}

	// 获取路径的权重
	public static int getWeight(int dist[], int target) {
		if (dist[target] == Integer.MAX_VALUE) {
			return -1;
		} else {
			return dist[target];
		}
	}

}
