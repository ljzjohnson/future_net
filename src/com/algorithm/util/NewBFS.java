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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import com.filetool.util.Data_Process;
import com.routesearch.route.Dijkstra;
import com.routesearch.route.Parms;

/**
 * description
 * 
 * @author luzhongguo
 * @version 1.0, 2016年5月9日
 */

public class NewBFS {

	private static int pathCost;// 每条路径的代价
	private static final int selectNum = 200;
	private static ArrayList<ArrayList<Integer>> successedPath;
	private static ArrayList<ArrayList<Integer>> savedPath;
	private static ArrayList<ArrayList<Integer>> middlePath;
	private static ArrayList<ArrayList<Integer>> NBMatrx;
	private static ArrayList<Integer> sucPath;
	public static int minCost;

	private static int allCost = 0;

	/**
	 * 计算每条路径的代价
	 * 
	 * @param path
	 * @return
	 */
	private static int pathCost(ArrayList<Integer> path) {
		int sumCost = 0;
		for (int i = 0; i < path.size() - 1; i++) {
			sumCost += Parms.costmatrix[path.get(i)][path.get(i + 1)];
		}
		return sumCost;
	}

	/**
	 * 返回两条路径中重复边路径的编号 <br>
	 * 注：没有重复边返回值size()==0
	 * 
	 * @param path1
	 * @param path2
	 * @return
	 */
	private static ArrayList<Integer> getRepeatNode(ArrayList<Integer> path1,
			ArrayList<Integer> path2) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(path1.size()
				+ path2.size());
		ArrayList<Integer> nodeList = new ArrayList<Integer>();
		for (Integer integer : path1) {
			map.put(integer, 1);
		}
		for (Integer integer : path2) {
			Integer cnt = map.get(integer);
			if (cnt != null) {
				map.put(integer, ++cnt);
				continue;
			}
			map.put(integer, 1);
		}
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			if (entry.getValue() != 1) {
				nodeList.add(entry.getKey());
			}
		}
		return nodeList;
	}

	public static ArrayList<Integer> reverse(ArrayList<Integer> indexPath) {
		int[] temp = null;
		ArrayList<Integer> path = new ArrayList<Integer>();
		for (Integer integer : indexPath) {
			temp = Data_Process.tempArray[integer];
			path.add(temp[1]);
		}
		path.add(temp[2]);
		return path;
	}

	/**
	 * 给定两点，找出不重复的点<br>
	 * 以第2条路径为模板，除去第一条路径中与第2条路径中相重复的点
	 * 
	 * @param repeatSubPath
	 *            :重复边索引
	 * @param path1
	 *            ：路径1（边索引）
	 * @param path2
	 *            ：路径2（边索引）
	 * @return
	 */
	private static boolean connectNode(ArrayList<Integer> repeatIndex,
			ArrayList<Integer> path1, ArrayList<Integer> path2) {
		int successCnt = 0;// 执行成功计数器
		/** 代价矩阵复制 */
		int[][] cutMatrix = new int[Parms.costmatrix.length][Parms.costmatrix.length];
		for (int i = 0, L = cutMatrix.length; i < L; i++) {
			for (int j = 0; j < L; j++) {
				cutMatrix[i][j] = Parms.costmatrix[i][j];
			}
		}
		for (Integer is : repeatIndex) {
			/** 删除代价矩阵中经过的路径 */
			ArrayList<Integer> tmpPath1 = reverse(path1);
			ArrayList<Integer> tmpPath2 = reverse(path2);
			for (int i = 0; i < tmpPath2.size() - 1; i++) {
				cutMatrix[tmpPath2.get(i)][tmpPath2.get(i + 1)] = 0;
			}
			for (int i = 0; i < tmpPath1.size() - 1; i++) {
				cutMatrix[tmpPath1.get(i)][tmpPath1.get(i + 1)] = 0;
			}
			int[] temp = Data_Process.tempArray[is];// 根据重复边索引得到对应的点

			int cutPathCost = Dijkstra.dijkstra(cutMatrix, temp[1], temp[2]);
			ArrayList<Integer> newPathOfPoint = Dijkstra.path;// 得到点路径
			if (newPathOfPoint == null)
				continue;// 找不到非重复的结束本次循环(之前return false)
			ArrayList<Integer> newPath = pointToEdgeOne(temp[1], newPathOfPoint); // 将点路径转化为边索引（返回类型为ArrayList<Integer>）

			/** 使用新找到的路径取代重复边 */
			int index = path1.indexOf(is);
			path1.remove(index);
			path1.addAll(index, newPath);

			++successCnt;// 成功执行计数加1

			// 测试输出
			allCost -= temp[3];
			allCost += cutPathCost;
			System.out.println("子路径代价：" + cutPathCost);
			System.out.println("子路径节点编号：" + newPathOfPoint);
			System.out.println("子路径边索引编号：" + newPath);
		}
		return successCnt == repeatIndex.size();
	}
	/**
	 * 将子路径（点索引）转换为边索引
	 * 
	 * @param startPoint
	 * @param path
	 * @return
	 */
	public static ArrayList<Integer> pointToEdgeOne(int startPoint,
			ArrayList<Integer> path) {
		ArrayList<Integer> edgePath = new ArrayList<Integer>();
		edgePath.add(Parms.edgematrix[startPoint][path.get(0)]);
		for (int i = 0, L1 = path.size() - 1; i < L1; i++) {
			edgePath.add(Parms.edgematrix[path.get(i)][path.get(i + 1)]);
		}
		return edgePath;
	}

	/**
	 * 统计路径中必经节点个数
	 * 
	 * @param path
	 * @return
	 */
	private static int spNodesNumOfPath(ArrayList<Integer> path,
			ArrayList<Integer> condList) {
		int spNodesNum = 0;
		for (int i = 0; i < condList.size(); i++) {
			if (path.contains(condList.get(i))) {
				spNodesNum++;
			}
		}
		return spNodesNum;
	}

	/**
	 * 类似广度优先遍历
	 * 
	 * @param startPoint
	 */
	private static void broadFirstSearch(int startPoint, ArrayList<Integer> list) {
		int cuurentPoint, neighborpoint;
		ArrayList<Integer> condList = list;
		NBMatrx = Parms.SPMatrix;
		middlePath = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> path = new ArrayList<Integer>();
		ArrayList<Integer> path1 = new ArrayList<Integer>();
		path.add(startPoint);// 访问结点startPoint
		savedPath.add(path);
		while (savedPath.size() != 0) {
			for (int i = 0; i < savedPath.size(); i++) {
				path1 = savedPath.get(i);
				cuurentPoint = path1.get(path1.size() - 1);
				for (int j = 0; j < NBMatrx.get(cuurentPoint).size(); j++) {
					neighborpoint = NBMatrx.get(cuurentPoint).get(j);
					ArrayList<Integer> tempPath = new ArrayList<Integer>(path1);
					if (!tempPath.contains(neighborpoint)) {// 首先判断有无重点，有则舍弃
						if (neighborpoint == Parms.target) {// 判断是否是终点
							if (tempPath.size() > condList.size()) {
								// 判断该点对应路径是否包含所有必经节点，不包含，舍弃该路径，否则保存成功路径
								if (tempPath.containsAll(condList)) {
									tempPath.add(neighborpoint);
									int tempCost = pathCost(tempPath);
									if (tempCost < pathCost) {
										pathCost = tempCost;
										sucPath = tempPath;// 保存最优解
									}
									successedPath.add(tempPath);// 保存所有可行解
								}
							}
						} else if (NBMatrx.get(neighborpoint).get(0) != Integer.MIN_VALUE) {// 判断邻节点是否有子节点
							if (pathCost != Integer.MAX_VALUE
									&& pathCost < pathCost(tempPath)) {
								continue;
							} else {
								tempPath.add(neighborpoint);
								middlePath.add(tempPath);
							}
						}
					}
				}
			}
			if (middlePath.size() > selectNum) {
				savedPath = new ArrayList<ArrayList<Integer>>();
				selectOptPath(condList);
				middlePath = new ArrayList<ArrayList<Integer>>();
			} else {
				savedPath = middlePath;
				middlePath = new ArrayList<ArrayList<Integer>>();
			}
		}
	}

	/*
	 * 选择包含节点最多的selectNum条路径
	 */
	private static void selectOptPath1(ArrayList<Integer> condList) {
		int listLength = middlePath.size();
		int index = 0;
		int[] spNodesNum = new int[listLength];
		HashMap<Integer, Integer> willBeSorted = new HashMap<Integer, Integer>();
		for (int i = 0; i < listLength; i++) {
			spNodesNum[i] = spNodesNumOfPath(middlePath.get(i), condList);
			willBeSorted.put(i, spNodesNum[i]);
		}
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(
				willBeSorted.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1,
					Entry<Integer, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		for (int i = 0; i < selectNum; i++) {
			index = list.get(i).getKey();
			savedPath.add(middlePath.get(index));
		}
	}

	// private static void selectOptPath2(ArrayList<Integer> condList) {
	// int listLength = middlePath.size();
	// int index = 0;
	// int[] spNodesNum = new int[listLength];
	// ResultNode tempNode = new ResultNode();
	// ResultNode[] willBeSorted = new ResultNode()[listLength];
	// HashMap<Integer, Integer> willBeSorted = new HashMap<Integer, Integer>();
	// for (int i = 0; i < listLength; i++) {
	// spNodesNum[i] = spNodesNumOfPath(middlePath.get(i), condList);
	// willBeSorted.put(i, spNodesNum[i]);
	// }
	// List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer,
	// Integer>>(
	// willBeSorted.entrySet());
	// Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
	// @Override
	// public int compare(Entry<Integer, Integer> o1,
	// Entry<Integer, Integer> o2) {
	// return o2.getValue() - o1.getValue();
	// }
	// });
	//
	// for (int i = 0; i < selectNum; i++) {
	// index = list.get(i).getKey();
	// savedPath.add(middlePath.get(index));
	// }
	// }

	public static void selectOptPath(ArrayList<Integer> condList) {
		int[] DemandTotal = new int[condList.size() + 1];
		int[] DemandNum = new int[middlePath.size()];
		int[] Weight = new int[middlePath.size()];
		for (int i = 0; i < middlePath.size(); i++) {
			int demandtmp = spNodesNumOfPath(middlePath.get(i), condList);
			DemandNum[i] = demandtmp;
			Weight[i] = pathCost(middlePath.get(i));
			DemandTotal[demandtmp]++;
		}
		int total = 0, k = condList.size();
		for (; k >= 0; k--) {
			total += DemandTotal[k];
			if (total > selectNum)
				break;
		}
		total = total - DemandTotal[k];// paths amount demand number larger than
		// Thresh
		int Thresh = k++;

		// sort weight of paths whose demand number equal to Thresh to determine
		// ThreshLocal used to select shortest NUMPATH-total paths
		int[] DemandEqThresh = new int[DemandTotal[Thresh]];
		int[] IndexOfThresh = new int[DemandTotal[Thresh]];
		for (int i = 0; i < DemandTotal[Thresh]; i++)
			DemandEqThresh[i] = Integer.MAX_VALUE;
		for (int index = 0, i = 0; i < middlePath.size(); i++) {
			if (DemandNum[i] == Thresh) {
				DemandEqThresh[index] = Weight[i];
				IndexOfThresh[index] = i;
				index++;
			}
		}
		Arrays.sort(DemandEqThresh);
		int ThreshLocal = DemandEqThresh[selectNum - total];

		int Count = 0;
		for (int i = 0; i < middlePath.size(); i++)
			// store paths demand number larger than Thresh
			if (DemandNum[i] > Thresh && Count < selectNum) {
				savedPath.add(middlePath.get(i));
				Count++;
			}

		int Count0 = Count;
		for (int i = 0; i < middlePath.size() && Count < selectNum; i++)
			// store best NUMPATH-total paths demand num equal to Thresh
			if (DemandNum[i] == Thresh && Weight[i] <= ThreshLocal) {
				savedPath.add(middlePath.get(i));
				Count++;
				// System.out.print(i + " ");
			}
		// System.out.println();
		//
		// for (int i = 0; i < DemandEqThresh.length && Count0 < selectNum; i++)
		// {
		// if (DemandEqThresh[i] <= ThreshLocal) {
		// System.err.print(IndexOfThresh[i] + " ");
		// Count0++;
		// }
		// }

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

	public static Node pointToEdge(ArrayList<Integer> path) {
		ArrayList<Integer> edgePath = new ArrayList<Integer>();
		int cost = pathCost(path);
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
			ArrayList<ArrayList<Integer>> sucPath2) {
		Queue<ResultNode> queue = new PriorityQueue<ResultNode>();
		ArrayList<Node> path1 = new ArrayList<Node>();
		ArrayList<Node> path2 = new ArrayList<Node>();

		Node node1 = new Node();
		for (ArrayList<Integer> tmpPath1 : sucPath1) {
			node1 = pointToEdge(tmpPath1);
			node1.setLocationTrue();
			path1.add(node1);
		}
		for (ArrayList<Integer> tmpPath2 : sucPath2) {
			path2.add(pointToEdge(tmpPath2));
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
		Data_Process.spMatrix();
		ArrayList<ArrayList<Integer>> sucPath1 = null;
		ArrayList<ArrayList<Integer>> sucPath2 = null;

		for (int i = 0; i < Parms.cond_set.size(); i++) {
			savedPath = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> condList = new ArrayList<Integer>();
			successedPath = new ArrayList<ArrayList<Integer>>();
			pathCost = Integer.MAX_VALUE;
			for (int j = 0, L = Parms.cond_set.get(i).size(); j < L; j++) {
				condList.add(Parms.cond_set.get(i).get(j));
			}
			broadFirstSearch(Parms.source, condList);
			Parms.result[i] = Data_Process.finalResult(sucPath);
			allCost += pathCost;
			System.out.println("代价:" + pathCost);
			System.out.println("路径编号：" + sucPath);
			System.out.println("索引编号：" + Parms.result[i]);
			if (i == 0) {
				sucPath1 = successedPath;
			} else {
				sucPath2 = successedPath;
			}
		}
		ResultNode node = getBestResult(sucPath1, sucPath2);

		/** 重边处理，进一步优化 */
		ArrayList<Integer> repeatIndexList = getRepeatNode(node.path1,// 获得重复边索引list
				node.path2);
		if (repeatIndexList.size() != 0) {// 有重边，去除重边
			boolean isSuccess = connectNode(repeatIndexList, node.path1,
					node.path2);
			// 测试输出
			if (isSuccess)
				System.out.println("成功去除重边...");
			else
				System.out.println("去除重边失败...");
		}
		Parms.result[0] = Data_Process.listToString(node.path1);
		Parms.result[1] = Data_Process.listToString(node.path2);

		// 测试输出
		System.out.println("索引编号：" + Parms.result[0]);
		System.out.println("索引编号：" + Parms.result[1]);
		System.out.println("边重复个数：" + repeatEdge(Parms.result));
		System.out.println("代价：" + allCost);

	}
}
