/*   
 * Copyright (c) 2010-2020 Founder LZG. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.algorithm.util;

/**
 * description
 * @author luzhongguo
 * @version 1.0, 2016年4月10日
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.filetool.util.Data_Process;
import com.routesearch.route.Dijkstra;
import com.routesearch.route.Parms;

/**
 * 说明：默认正序<br>
 * 可根据reverseFlag调节
 * 
 * @author chenjingyang
 * 
 */
public class DijBFSOne {

	private static int selectNum = 1000;

	static int successedPathCost;// 每条路径的代价
	// private static ArrayList<Integer> successedPath; // 最优路径
	private static ArrayList<ArrayList<Integer>> successedPath;
	private static ArrayList<Integer> successedValue;

	/*********** 新加倒序 ********************************/
	private static int[][] connMatrix; // 联通矩阵
	private static int source; // 起点
	private static int target; // 终点
	private static boolean reverseFlag = false; // 是否倒序标志
	/****************************************************/
	private static ArrayList<Integer> condList;
	private static ArrayList<Integer> allDemand;// 包含起点，中间节点，终点
	private static int[] addDemandArray;
	private static int condLength, veterNUM;

	static Element[][] costTable; // D^r(p,q)表
	private static ArrayList<ArrayList<Integer>> spmatrix;

	/**
	 * 矩阵转置
	 * 
	 * @param matrix
	 */
	private static void connMatrixConvert(int[][] matrix) {
		connMatrix = new int[Parms.costmatrix.length][Parms.costmatrix.length];
		if (!reverseFlag) {
			connMatrix = matrix;
			source = Parms.source;
			target = Parms.target;
		} else {
			source = Parms.target;
			target = Parms.source;
			for (int i = 0, loop = matrix.length; i < loop; i++) {
				for (int j = 0, loop1 = matrix.length; j < loop1; j++) {
					connMatrix[i][j] = matrix[j][i];
				}
			}
		}
	}

	// 初始化计算各点之间的距离
	public static void initialize() {

		Element costPath0 = null;
		int temp;

		// 起点到中间节点
		for (int i = 0; i < condLength + 1; i++) {
			if (i == 0) {
				temp = 0;
				costPath0 = new Element(temp, null);
				costTable[0][i] = costPath0;
			} else {
				temp = Dijkstra.dijkstra(connMatrix, source, allDemand.get(i));
				costPath0 = new Element(temp, Dijkstra.path);
				costTable[0][i] = costPath0;
			}

		}

		// 起点到终节点
		temp = Dijkstra.dijkstra(connMatrix, source, target);
		costPath0 = new Element(temp, Dijkstra.path);
		costTable[0][condLength + 1] = costPath0;

		// 中间节点对之间的距离
		for (int i = 1; i < condLength + 1; i++)
			for (int j = 1; j < condLength + 1; j++) {
				if (i != j) {
					temp = Dijkstra.dijkstra(connMatrix, allDemand.get(i),
							allDemand.get(j));
					costPath0 = new Element(temp, Dijkstra.path);
				} else {
					temp = 0;
					costPath0 = new Element(temp, null);
				}
				costTable[i][j] = costPath0;
			}

		// 中间节点到终点之间的距离及中间节点到起点
		for (int i = 1; i < condLength + 1; i++) {
			temp = Dijkstra.dijkstra(connMatrix, allDemand.get(i), target);// 中间节点到终点
			costPath0 = new Element(temp, Dijkstra.path);
			costTable[i][condLength + 1] = costPath0;
			// temp = Dijkstra.dijkstra(connMatrix, allDemand.get(i), source);//
			// 中间节点到终点
			// costPath0 = new Element(temp, Dijkstra.path);
			temp = 0;// 中间节点到起点
			costPath0 = new Element(temp, null);
			costTable[i][0] = costPath0;
		}

	}

	/**
	 * Dijstra对于不连通的两个点之间输出最大距离值Integer.MAX_VALUE和一个不知道是啥的路径
	 */
	// 产生每个点的邻节点
	public static void spMatrix() {
		spmatrix = new ArrayList<ArrayList<Integer>>(condLength + 1);
		ArrayList<Integer> item;
		for (int i = 0; i < condLength + 1; i++) {
			item = new ArrayList<Integer>();
			for (int j = 0; j < condLength + 2; j++) {
				if (costTable[i][j].getValue() != null) {
					item.add(allDemand.get(j));
				}
			}
			spmatrix.add(item);
		}
	}

	// 删除节点
	public static int[][] deleteVterices(ArrayList<Integer> sPoints,
			int[][] costmatrix) {
		int[][] newMatrix = matrixCopy(costmatrix);
		for (int i = 1; i < sPoints.size() - 1; i++) {
			for (int j = 0; j < newMatrix.length; j++) {
				newMatrix[sPoints.get(i)][j] = 0;
				newMatrix[j][sPoints.get(i)] = 0;
			}
		}
		return newMatrix;
	}

	// 矩阵拷贝
	public static int[][] matrixCopy(int[][] costmatrix) {
		int length = costmatrix.length;
		int[][] newMatrix = new int[length][length];
		for (int i = 0; i < length; i++)
			for (int j = 0; j < length; j++) {
				newMatrix[i][j] = costmatrix[i][j];
			}
		return newMatrix;
	}

	private static void print() {
		for (int i = 0; i < condLength + 1; i++) {
			for (int j = 0; j < condLength + 2; j++) {
				System.out.print(costTable[i][j].getKey() + "  ");
			}
			System.out.println();
		}
	}

	/**
	 * 计算每条路径的代价
	 * 
	 * @param path
	 * @return
	 */
	private static int pathCost(ArrayList<Integer> path) {
		int sumCost = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			sumCost += costTable[addDemandArray[path.get(i)]][addDemandArray[path
					.get(i + 1)]].getKey();
		}
		return sumCost;
	}

	private static boolean containAll(ArrayList<Integer> tempPath,
			int cuurentPoint, int neighborpoint) {
		ArrayList<Integer> subPath = costTable[addDemandArray[cuurentPoint]][addDemandArray[neighborpoint]]
				.getValue();// 按照索引取值
		ArrayList<Integer> completePath = getCompletePath(tempPath);
		for (Integer integer : subPath) {
			if (completePath.contains(integer)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 类似广度优先遍历
	 * 
	 * @param startPoint
	 */
	private static void broadFirstSearch() {
		int cuurentPoint, neighborpoint, minCost = 0;// 当前点和路径的下一个邻节点
		ArrayList<ArrayList<Integer>> savedPath = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> middlePath = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> path = new ArrayList<Integer>();
		ArrayList<Integer> path1 = new ArrayList<Integer>();
		path.add(Parms.source);// 访问结点startPoint
		savedPath.add(path);// 第一次将起点加入路径，现在其中只包含必经节点
		while (savedPath.size() != 0) {
			for (int i = 0, loop = savedPath.size(); i < loop; i++) {
				path1 = savedPath.get(i);
				cuurentPoint = path1.get(path1.size() - 1);// 取当前路径的最后一个点，作为下一次搜索的起点
				ArrayList<Integer> currentPointList = spmatrix
						.get(addDemandArray[cuurentPoint]);
				for (int j = 0, loop2 = currentPointList.size(); j < loop2; j++) {// 进行下一次搜索
					neighborpoint = currentPointList.get(j);
					ArrayList<Integer> tempPath = new ArrayList<Integer>(path1);
					if (neighborpoint == target) {
						tempPath.add(neighborpoint);
						ArrayList<Integer> sucPath = getCompletePath(tempPath);
						if (sucPath.containsAll(condList)) {
							minCost = pathCost(tempPath);
							successedValue.add(minCost);
							successedPath.add(sucPath);
						}
					} else if (!containAll(tempPath, cuurentPoint,
							neighborpoint)
							&& spmatrix.get(addDemandArray[neighborpoint])
									.size() > 0) {
						tempPath.add(neighborpoint);
						middlePath.add(tempPath);
					}
				}
			}
			if (middlePath.size() > selectNum) {// 大于设置的路径数目就开始删除
				savedPath.clear();// 将当前所有的路径清空
				selectOptPath(savedPath, middlePath);// 删除不和要求的路径，将符合要求的加到savedPath中
				middlePath.clear();// 清空middlePath，用来存储下一次新产生的路径
			} else {
				savedPath.clear();
				savedPath.addAll(middlePath);
				middlePath.clear();
			}
		}
	}
	/*
	 * 选择包含节点最多的selectNum条路径
	 */
	private static void selectOptPath(ArrayList<ArrayList<Integer>> savedPath,
			ArrayList<ArrayList<Integer>> middlePath) {
		int listLength = middlePath.size();
		int index = 0;
		int subPathCost = Integer.MAX_VALUE;
		HashMap<Integer, Integer> willBeSorted = new HashMap<Integer, Integer>();
		for (int i = 0; i < listLength; i++) {
			subPathCost = pathCost(middlePath.get(i));
			willBeSorted.put(i, subPathCost);
		}
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(
				willBeSorted.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1,
					Entry<Integer, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		});

		for (int i = 0; i < selectNum; i++) {
			index = list.get(i).getKey();
			savedPath.add(middlePath.get(index));
		}
	}

	public static ArrayList<Integer> getCompletePath(ArrayList<Integer> bestPath) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(source);
		for (int i = 0; i < bestPath.size() - 1; i++) {
			path.addAll(costTable[addDemandArray[bestPath.get(i)]][addDemandArray[bestPath
					.get(i + 1)]].getValue());
		}
		return path;
	}

	public static String finalResult(ArrayList<Integer> path) {

		StringBuilder pathRresult = new StringBuilder();
		int x_row, y_col;
		int edgeIndex = -1;
		for (int i = 0; i < path.size() - 1; i++) {
			if (i != 0) {
				pathRresult.append("|");
			}
			x_row = path.get(i);
			y_col = path.get(i + 1);
			edgeIndex = Parms.edgematrix[x_row][y_col];
			pathRresult.append(edgeIndex);
		}
		return pathRresult.toString();
	}
	public static int repeatEdge(String[] resultPath) {
		int repeatCount = 0;
		String[] path1 = resultPath[0].split("\\|");
		String[] path2 = resultPath[1].split("\\|");
		for (String string1 : path1) {
			for (int i = 0; i < path2.length; i++) {
				if (string1.equals(path2[i])) {// 字符串的相等利用equals进行判断，不能利用==
					repeatCount++;
					break;
				}
			}
		}
		return repeatCount;
	}

	/**
	 * 在可行解中选择最优路径
	 */
	private static ArrayList<Integer> selectOptSolution() {
		PriorityQueue<ResultNode> queue = new PriorityQueue<ResultNode>();
		for (int i = 0; i < successedPath.size(); i++) {
			ResultNode node = new ResultNode();
			ArrayList<Integer> tempPath = successedPath.get(i);
			node.path1 = tempPath;
			node.allCost = successedValue.get(i);
			queue.offer(node);
		}
		ResultNode node = queue.poll();
		ArrayList<Integer> bestPath = node.path1;
		successedPathCost = node.allCost;
		return bestPath;
	}

	public static Node pointToEdge(ArrayList<Integer> path, int cost) {
		ArrayList<Integer> edgePath = new ArrayList<Integer>();
		for (int i = 0, L1 = path.size() - 1; i < L1; i++) {
			edgePath.add(Parms.edgematrix[path.get(i)][path.get(i + 1)]);
		}
		Node node = new Node();
		node.cost = cost;
		node.path = edgePath;
		return node;
	}

	public static ResultNode getBestResult(
			ArrayList<ArrayList<Integer>> sucPath1,
			ArrayList<Integer> sucVaule1,
			ArrayList<ArrayList<Integer>> sucPath2, ArrayList<Integer> sucVaule2) {
		PriorityQueue<ResultNode> queue = new PriorityQueue<ResultNode>();
		ArrayList<Node> path1 = new ArrayList<Node>();
		ArrayList<Node> path2 = new ArrayList<Node>();

		Node node1 = new Node();
		int x = 0;
		for (ArrayList<Integer> tmpPath1 : sucPath1) {
			node1 = pointToEdge(tmpPath1, sucVaule1.get(x));
			node1.setLocationTrue();
			path1.add(node1);
			x++;
		}
		int y = 0;
		for (ArrayList<Integer> tmpPath2 : sucPath2) {
			path2.add(pointToEdge(tmpPath2, sucVaule2.get(y)));
			y++;
		}

		Node tmpNode1 = new Node();
		Node tmpNode2 = new Node();
		ArrayList<Integer> tmpPath;
		boolean[] cmpArray;
		for (int i = 0; i < path2.size(); i++) {
			tmpNode2 = path2.get(i);
			tmpPath = tmpNode2.path;
			for (int j = 0; j < path1.size(); j++) {
				int count = 0;
				tmpNode1 = path1.get(j);
				cmpArray = tmpNode1.logArray;
				for (Integer edge : tmpPath) {
					if (cmpArray[edge] == true) {
						count++;
					}
				}
				// System.out.println(count);
				ResultNode tmpReNode = new ResultNode(tmpNode1, tmpNode2, count);
				queue.offer(tmpReNode);
			}
		}
		return queue.poll();
	}
	/**
	 * 类似广度优先搜索入口
	 */
	public static void BFSMain() {
		veterNUM = Parms.vertices_num;
		int allcost = 0;
		ArrayList<ArrayList<Integer>> sucPath1 = null;
		ArrayList<Integer> sucVaule1 = null;
		ArrayList<ArrayList<Integer>> sucPath2 = null;
		ArrayList<Integer> sucVaule2 = null;
		Data_Process.spMatrix();
		for (int i = 0; i < Parms.cond_set.size(); i++) {

			/************ 矩阵转换 ****************/
			connMatrixConvert(Parms.costmatrix); // 初始化邻接矩阵
			 connMatrix = deleteVterices(Parms.cond_set.get(i == 0 ? 1 : 0),
			 connMatrix);

			condLength = Parms.cond_set.get(i).size();
			costTable = new Element[condLength + 2][condLength + 2];
			condList = new ArrayList<Integer>();
			allDemand = new ArrayList<Integer>();// 包含起点，中间节点，终点
			addDemandArray = new int[Parms.vertices_num];
			successedPathCost = 0;
			successedPath = new ArrayList<ArrayList<Integer>>();
			successedValue = new ArrayList<Integer>();

			allDemand.add(source);
			addDemandArray[source] = 0;
			Integer tmp = null;
			for (int j = 0; j < condLength; ++j) {
				tmp = Parms.cond_set.get(i).get(j);
				condList.add(tmp);
				addDemandArray[tmp] = j + 1;
			}
			allDemand.addAll(condList);
			allDemand.add(target);
			addDemandArray[target] = allDemand.size() - 1;
			// long start = System.currentTimeMillis();
			initialize();
			// long current = System.currentTimeMillis();
			// System.out.println(current - start);
			spMatrix();
			broadFirstSearch();

			if (i == 0) {
				sucPath1 = successedPath;
				sucVaule1 = successedValue;
			} else {
				sucPath2 = successedPath;
				sucVaule2 = successedValue;
			}
			ArrayList<Integer> bestPath = selectOptSolution();
			allcost += successedPathCost;
			Parms.result[i] = Data_Process.finalResult(bestPath);
			System.out.println("代价:" + successedPathCost);
			System.out.println("路径编号：" + bestPath);
			System.out.println("索引编号：" + Parms.result[i]);
		}
		System.out.println("边重复个数：" + repeatEdge(Parms.result));
		System.out.println("各自最优时的总代价:" + allcost);

		ResultNode node = getBestResult(sucPath1, sucVaule1, sucPath2,
				sucVaule2);
		Parms.result[0] = Data_Process.listToString(node.path1);
		Parms.result[1] = Data_Process.listToString(node.path2);
		System.out.println("索引编号：" + Parms.result[0]);
		System.out.println("索引编号：" + Parms.result[1]);
		System.out.println("边重复个数：" + node.repeatNum);
		System.out.println("最终代价:" + node.allCost);

	}
}
