package org.webdriver.patatiumwebui.utils;

import java.io.File;

public class FileManger {

	/**
	 * 递归删除文件
	 * @param file
	 */
	private void deleteFile(File file)
	{
		if (file.isDirectory()) {
			File[] files=file.listFiles();
			//递归条用删除方法
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		file.delete();
	}
	/**
	 * 删除文件夹和密码
	 * @param workspaceRootPath 文件目录
	 */
	public  void clearFile(String workspaceRootPath)
	{
		File file=new File(workspaceRootPath);
		if (file.exists()) {
			deleteFile(file);
		}

	}


	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}


}
