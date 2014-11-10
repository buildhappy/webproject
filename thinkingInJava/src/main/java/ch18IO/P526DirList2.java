package ch18IO;
import java.io.*;
import java.util.regex.*;
import java.util.*;
/**
*使用匿名内部类实现
*/
public class P526DirList2{
	public static FilenameFilter filter(final String regex){
		return new FilenameFilter(){
			private Pattern pattern;
			public boolean accept(File dir , String name){
				return pattern.matcher(name).matches();
			}
		};
	}
	
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