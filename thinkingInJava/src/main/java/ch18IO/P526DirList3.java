package ch18IO;
import java.io.*;
import java.util.regex.*;
import java.util.*;
/**
*前两种方法缺点是FilenameFilter类紧密的和P526DirList2类绑定在一起
*定义list()参数的匿名内部类，会使程序变小
*/
public class P526DirList3{
	public static void main(final String[] args){
		File fileDir = new File(".");
		String[] files;
		
		if(args.length == 0){
			files = fileDir.list();
		}else{
			files = fileDir.list(new FilenameFilter(){
				private Pattern pattern = Pattern.compile(args[0]);
				public boolean accept(File dir , String name){
					return pattern.matcher(name).matches();
				}
			});
		}
		Arrays.sort(files , String.CASE_INSENSITIVE_ORDER);
		for(String file : files){
			System.out.println(file);
		}
	}
}