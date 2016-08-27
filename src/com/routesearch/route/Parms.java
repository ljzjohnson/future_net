package com.routesearch.route;

import java.util.ArrayList;

/**
 * 整个过程需要传递的参数
 * 
 * @author chenjingyang
 *
 */
public class Parms {

	public static int vertices_num; // 顶点数
	public static int vertices_cnt;// 不重复顶点数
	public static int edges_num; // 实际边数
	public static int maxedges_num; // 最大边数
	public static int source; // 起点
	public static int target; // 终点
	public static ArrayList<ArrayList<Integer>> cond_set; // 必经节点
	public static int[][] costmatrix; // 邻接矩阵
	public static int[][] edgematrix; // 边索引
	public static int cost; // 所需代价
	public static String[] result = new String[2];
	public static ArrayList<ArrayList<int[]>> SPGraph;
	public static ArrayList<ArrayList<int[]>> SPGraphReverse;
	public static ArrayList<ArrayList<Integer>> SPMatrix;
	public static ArrayList<int[]> SPMatrix1;
	public static int[][] newCMatrix;

}
