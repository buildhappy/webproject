package ch18IO;

import java.io.*;
import java.util.regex.*;
import java.util.*;
/**
*查看当前目录下的所有文件；或者查看某个文件是否存在
*/
public class P526DirList{
	public static void main(String[] args){
		File fileDir = new File(".");
		String[] files;
		
		if(args.length == 0){
			files = fileDir.list();
		}else{
			files = fileDir.list(new FileDirFilter(args[0]));
		}
		Arrays.sort(files , String.CASE_INSENSITIVE_ORDER);
		for(String file : files){
			System.out.println(file);
		}
	}
}


class FileDirFilter implements FilenameFilter{
	private Pattern pattern;
	public FileDirFilter(String regex){
		this.pattern = Pattern.compile(regex);
	}
	public boolean accept(File dir, String name){
		return pattern.matcher(name).matches();
	}	
}