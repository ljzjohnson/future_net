package com.filetool.util;

import java.util.ArrayList;

import com.routesearch.route.Parms;

public class Data_Process {

    private static ArrayList<ArrayList<int[]>> SPGraph = new ArrayList<ArrayList<int[]>>();
    static String[] graph_split;
    static int edges_num;
    static int vertices_num;
    static int vertices_cnt = 0;
    public static int[][] tempArray;
    public static int[][] newTempArray;
    static int[][] costmatrix;
    static int[][] edgematrix;

    public void data_prepocess(String[] graphContent, String[] condition) {
	vertice_edge(graphContent);
	createSPGraph();
	condition_data(condition);
    }

    /**
     * 获取顶点、边等信息
     * 
     * @param graphContent
     */

    public void vertice_edge(String[] graphContent) {
	graph_split = graphContent;
	edges_num = graph_split.length;
	tempArray = new int[edges_num][4];
	int string_to_int;
	int max_vertices = 0;
	int max_edges = 0;
	int[] oneOrZero = new int[2000];
	String[] tempSubStrng;
	for (int i = 0; i < edges_num; i++) {
	    tempSubStrng = graph_split[i].split(",");
	    tempArray[i][0] = Integer.parseInt(tempSubStrng[0]);
	    string_to_int = Integer.parseInt(tempSubStrng[1]);
	    oneOrZero[string_to_int] = 1;
	    if (tempArray[i][0] >= max_edges) {
		max_edges = tempArray[i][0];// 计算最大变索引值
	    }
	    if (string_to_int >= max_vertices) {
		max_vertices = string_to_int;
	    }
	    tempArray[i][1] = string_to_int;
	    string_to_int = Integer.parseInt(tempSubStrng[2]);
	    oneOrZero[string_to_int] = 1;
	    if (string_to_int >= max_vertices) {
		max_vertices = string_to_int;
	    }
	    tempArray[i][2] = string_to_int;
	    tempArray[i][3] = Integer.parseInt(tempSubStrng[3]);
	}
	for (int i = 0; i < 2000; i++) {
	    vertices_cnt += oneOrZero[i];
	}

	newTempArray = new int[max_edges + 1][4];
	for (int i = 0; i < edges_num; i++) {
	    newTempArray[tempArray[i][0]] = tempArray[i];
	}

	// 2.找顶点数(vertices_num)
	vertices_num = max_vertices + 1;

	// 3.找邻接矩阵和边索引
	costmatrix = new int[vertices_num][vertices_num];
	edgematrix = new int[vertices_num][vertices_num];
	for (int i = 0; i < edges_num; i++) {
	    int x_row = tempArray[i][1];
	    int y_row = tempArray[i][2];
	    costmatrix[x_row][y_row] = tempArray[i][3];
	    edgematrix[x_row][y_row] = tempArray[i][0];
	}

	Parms.costmatrix = costmatrix; // 赋值邻接矩阵
	Parms.edgematrix = edgematrix; // 赋值边索引
	Parms.vertices_cnt = vertices_cnt;// 赋值顶点个数,点连续的情况下,二者相等
	Parms.vertices_num = vertices_num; // 赋值最大顶点数
	Parms.edges_num = edges_num; // 赋值边数
	Parms.maxedges_num = max_edges;
    }

    private static void createSPGraph() {
	ArrayList<ArrayList<int[]>> SPGraph_reverse = new ArrayList<ArrayList<int[]>>();
	// 特殊图初始化
	for (int i = 0; i < vertices_num; i++) {
	    ArrayList<int[]> tempList = new ArrayList<int[]>();
	    ArrayList<int[]> tempList_reverse = new ArrayList<int[]>();
	    int tempArr[] = { Integer.MAX_VALUE, Integer.MAX_VALUE,
		    Integer.MAX_VALUE, Integer.MAX_VALUE };
	    tempList.add(tempArr);
	    tempList_reverse.add(tempArr);
	    SPGraph.add(tempList);
	    SPGraph_reverse.add(tempList_reverse);
	}
	// 顶点出去的加在链表后面
	for (int i = 0; i < edges_num; i++) {
	    SPGraph.get(tempArray[i][1]).add(tempArray[i]);
	    SPGraph_reverse.get(tempArray[i][2]).add(tempArray[i]);
	}
	// 删除有出度的顶点对应的一个链表元素
	for (int i = 0; i < vertices_num; i++) {
	    if (SPGraph.get(i).size() > 1) {
		SPGraph.get(i).remove(0);
	    }
	    if (SPGraph_reverse.get(i).size() > 1) {
		SPGraph_reverse.get(i).remove(0);
	    }
	}
	Parms.SPGraph = SPGraph;
	Parms.SPGraphReverse = SPGraph_reverse;
    }

    public static void spMatrix() {
	ArrayList<ArrayList<Integer>> spmatrix = new ArrayList<ArrayList<Integer>>(
		vertices_num);
	ArrayList<Integer> item;
	int size = 0;
	for (int i = 0; i < vertices_num; i++) {
	    item = new ArrayList<Integer>();
	    // size=1，要么出度为1；要么无出度，只有初始化值
	    size = SPGraph.get(i).size();
	    if (size == 1) {
		if (SPGraph.get(i).get(0)[2] == Integer.MAX_VALUE) {
		    item.add(Integer.MIN_VALUE);// Integer.MIN_VALUE 表示无邻节点
		} else {
		    item.add(SPGraph.get(i).get(0)[2]);// 出度为1
		}
	    } else {
		for (int j = 0; j < size; j++)
		    item.add(SPGraph.get(i).get(j)[2]);// 出度至少为2
	    }
	    spmatrix.add(item);
	}
	Parms.SPMatrix = spmatrix;
    }// 8个出度中可能有相同的入点，用这种方式取邻节点可能会出现死循环

    /**
     * 获取中间集条件信息
     * 
     * @param condition
     */
    public void condition_data(String[] condition) {
	ArrayList<ArrayList<Integer>> cond_set = new ArrayList<ArrayList<Integer>>();
	String[] cond_split;
	String[] cond_temp;
	int length;

	for (int i = 0; i < condition.length; i++) {
	    ArrayList<Integer> item = new ArrayList<Integer>();
	    cond_split = condition[i].split(",");
	    Parms.source = Integer.parseInt(cond_split[1]); // 赋值起点值
	    Parms.target = Integer.parseInt(cond_split[2]); // 赋值终点值
	    cond_temp = cond_split[3].split("\\|");
	    length = cond_temp.length;
	    for (int j = 0; j < length; j++) {
		item.add(Integer.parseInt(cond_temp[j]));
	    }
	    cond_set.add(item);
	}
	Parms.cond_set = cond_set; // 赋值必经节点
    }

    /**
     * 将结果转换为字符串输出
     * 
     * @param bestPath
     * @return
     */
    public static String finalResult(ArrayList<Integer> bestPath) {
	StringBuilder bestResult = new StringBuilder();
	for (int i = 0, L1 = bestPath.size() - 1; i < L1; i++) {
	    Integer vertices = bestPath.get(i);
	    ArrayList<int[]> tempPath = SPGraph.get(vertices);
	    for (int j = 0, L2 = tempPath.size(); j < L2; j++) {
		if (tempPath.get(j)[2] == bestPath.get(i + 1)) {
		    if (i == 0)
			bestResult.append(tempPath.get(j)[0] + "|");
		    else if (i < bestPath.size() - 2)
			bestResult.append(tempPath.get(j)[0] + "|");
		    else
			bestResult.append(tempPath.get(j)[0]);
		}
	    }
	}

	return bestResult.toString();
    }

    /**
     * 输出路径由点转换成边索引<br>
     * 自带检查重复
     * 
     * @param result
     *            ：点路径
     * @return
     */
    public static String finalResult(String result) {
	String[] strArray = result.split(", ");
	StringBuilder pathRresult = new StringBuilder();
	int x_row, y_col;
	int edgeIndex = -1;
	int arrayLength = strArray.length - 1;
	for (int i = 0; i < arrayLength; i++) {
	    if (i != 0) {
		pathRresult.append("|");
	    }
	    x_row = Integer.parseInt(strArray[i]);
	    y_col = Integer.parseInt(strArray[i + 1]);
	    edgeIndex = Parms.edgematrix[x_row][y_col];
	    pathRresult.append(edgeIndex);
	}
	return pathRresult.toString();
    }

    public static String listToString(ArrayList<Integer> path) {
	StringBuilder pathRresult = new StringBuilder();
	pathRresult.append(path.get(0));
	for (int i = 1; i < path.size(); i++) {
	    pathRresult.append("|");
	    pathRresult.append(path.get(i));
	}
	return pathRresult.toString();
    }

    public static void induceMatric(ArrayList<Integer> demandList) {
	MatrixConvert m = new MatrixConvert(demandList);
	m.convert();
	// m.printTest1();
	// System.out.println("******************************************");
	// m.printTest();
	Parms.newCMatrix = m.newCostMatrix;
    }
}

class MatrixConvert {

    private ArrayList<Integer> demandList;// 必经节点(可延拓为包含起点和终点)
    public int[][] newCostMatrix;// 新生成的邻接矩阵

    private final int CONDITION_A_COST = 3;// 离必经节点0个节点的代价3,7,12
    private final int CONDITION_B_COST = 7;// 离必经节点1个节点的代价
    private final int CONDITION_C_COST = 12;// 离必经节点2个节点的代价

    public MatrixConvert(ArrayList<Integer> list) {
	demandList = list;
	/** 初始化新的数组 */
	newCostMatrix = new int[Parms.costmatrix.length][Parms.costmatrix[0].length];// 注：矩阵大小初始化为最大顶点数
	// for (int i = 0, L = Parms.costmatrix.length; i < L; i++)
	// for (int j = 0, M = Parms.costmatrix[0].length; j < M; j++) {
	// // newCostMatrix[i][j] = Parms.costmatrix[i][j];
	// newCostMatrix[i][j] = Integer.MAX_VALUE;
	// }
    }

    /**
     * 根据倒序图进行三层循环求改变离必经节点三个及三个以内路径的代价值
     */
    public void convert() {
	int cnt = 0;
	for (Integer demandPoint : demandList) {
	    // 取出一个必经点得到流入该必经点的节点
	    ArrayList<int[]> oneLenPointList = Parms.SPGraphReverse
		    .get(demandPoint);

	    for (int[] is : oneLenPointList) {
		int oneLenPoint = is[1];
		if (oneLenPoint != Integer.MAX_VALUE) {
		    newCostMatrix[oneLenPoint][demandPoint] = CONDITION_A_COST;// 完成了一步之遥的计算

		    // System.out.println("MatrixConvert.convert():"+oneLenPoint+"->"+demandPoint+"="+CONDITION_A_COST);

		    calc2LenPoint(demandPoint, oneLenPoint);// 进行二步之遥计算
		}
	    }
	}
    }

    /**
     * 计算离必经节点2步之遥点的距离<br>
     * 注意：<br>
     * 1.计算2步之遥时要判断该点是否同时是1步之遥，若是应舍弃<br>
     * 2.若该点本身就是必经节点，为多余计算，应该舍弃
     * 
     * @param twoLenPoint
     */
    public void calc2LenPoint(int demandPoint, int oneLenPoint) {
	// 取出一个必经点得到流入该必经点的节点
	ArrayList<int[]> twoLenPointList = Parms.SPGraphReverse
		.get(oneLenPoint);
	for (int[] is : twoLenPointList) {
	    int twoLenPoint = is[1];
	    if (twoLenPoint != Integer.MAX_VALUE
		    && !demandList.contains(twoLenPoint)
		    && nodeValid(twoLenPoint)) {
		newCostMatrix[twoLenPoint][oneLenPoint] = CONDITION_B_COST;// 完成了两步之遥的计算

		// System.out.println("MatrixConvert.calc2LenPoint():" +
		// twoLenPoint + "->" + oneLenPoint + "=" + CONDITION_B_COST);

		calc3LenPoint(demandPoint, oneLenPoint, twoLenPoint);// 进行三步之遥计算
	    }
	}
    }

    /**************
     * (存在问题)*********************<br>
     * 计算离必经节点3步之遥点的距离<br>
     * 注意：<br>
     * 1.计算3步之遥时要判断该点是否同时是1步之遥，若是应舍弃<br>
     * 2.计算3步之遥时要判断该点是否同时是2步之遥，若是应舍弃(这一步难点！！！！！！！)<br>
     * 3.若该点本身就是必经节点，为多余计算，应该舍弃
     * 
     * @param threeLenPoint
     */
    public void calc3LenPoint(int demandPoint, int oneLenPoint, int twoLenPoint) {
	ArrayList<int[]> threeLenPointList = Parms.SPGraphReverse
		.get(twoLenPoint);
	for (int[] is : threeLenPointList) {
	    int threeLenPoint = is[1];
	    if (threeLenPoint != Integer.MAX_VALUE// 正常点
		    && !demandList.contains(threeLenPoint)// 该点不是必经点
		    && nodeValid(threeLenPoint, oneLenPoint, 1)) {// 该点不是一步之遥点
		// && nodeValid(threeLenPoint, demandPoint, 2)) {// 该点不是二步之遥点
		newCostMatrix[threeLenPoint][twoLenPoint] = CONDITION_C_COST;// 完成了两步的计算

		// System.out.println("MatrixConvert.calc3LenPoint():"+threeLenPoint+"->"+twoLenPoint+"="+CONDITION_C_COST);
	    }
	}
    }

    /**
     * 检查一个节点的下级(一级，二级)是否与指定节点重复
     * 
     * @param startPoint
     * @param beingChecked
     * @param type
     * @return
     */
    public boolean nodeValid(int startPoint) {
	ArrayList<int[]> childPointList = Parms.SPGraph.get(startPoint);
	// 一级子节点(没有子节点的异常隐含排除)
	for (int[] is : childPointList) {
	    if (demandList.contains(is[2])) {
		return false;
	    }
	}
	return true;
    }

    /**
     * 检查一个节点的下级(一级，二级)是否与指定节点重复
     * 
     * @param startPoint
     * @param beingChecked
     * @param type
     * @return
     */
    public boolean nodeValid(int startPoint, int beingChecked, int type) {
	ArrayList<int[]> childPointList = Parms.SPGraph.get(startPoint);
	if (type == 1) { // 一级子节点(没有子节点的异常隐含排除)

	    /************** 此处存在逻辑缺陷 *****************/
	    for (int[] is : childPointList) {
		if (is[2] == beingChecked) {
		    return false;
		}

	    }
	    /******************************************/

	} else {// 二级子节点
	    for (int[] is : childPointList) {
		ArrayList<int[]> childChildPointList = Parms.SPGraph.get(is[1]); // 二级子节点(没有子节点的异常隐含排除)
		for (int[] isr : childChildPointList) {
		    if (demandList.contains(isr[2])) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    public void printTest() {
	for (int[] element : newCostMatrix) {
	    for (int i : element) {
		System.out.print(i + "\t");
	    }
	    System.out.println();
	}
    }

    public void printTest1() {
	for (int[] element : Parms.costmatrix) {
	    for (int i : element) {
		System.out.print(i + "\t");
	    }
	    System.out.println();
	}
    }

}
