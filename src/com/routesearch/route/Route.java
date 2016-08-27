// DONOT change the package path.
// DONOT change the file name and the class declaration.
// DONOT change the function signature.
//请勿修改包路径
//请勿修改文件名和类声明
//请勿修改函数签名
/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-4-11
 * @version V1.0
 */
package com.routesearch.route;

import com.algorithm.util.BFSDirectThree;
import com.filetool.util.Data_Process;

public final class Route {
	/**
	 * 你需要完成功能的入口
	 * 
	 * @author XXX
	 * @since 2016-3-4
	 * @version V1
	 */
	public static String[] searchRoute(String[] graphContent, String[] condition) {
		Data_Process process = new Data_Process();
		process.data_prepocess(graphContent, condition); // 数据预处理

		// DijBFS.BFSMain();
		// DijBFSOne.BFSMain();
		BFSDirectThree.BFSMain();
		// NewBFS.BFSMain();
		return Parms.result;

	}

}