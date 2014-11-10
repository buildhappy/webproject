import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * 编写一个程序，将a.txt文件中的单词与b.txt文件中的单词交替合并到c.txt
 * 文件中，a.txt文件中的单词用回车符分隔，b.txt文件中用回车或空格进行分隔。
 */
public class E1FileMerge {

	public static void main(String[] args) throws Exception {
		FileManage readerA = new FileManage("a.txt" , new String[]{"\n"});
		FileManage readerB = new FileManage("b.txt" , new String[]{"\n" , " "});
		FileWriter writer = new FileWriter("c.txt");
		String wordA = null;
		String wordB = null;
		while((wordA = readerA.getNextWord()) != null){
			System.out.println(wordA);
			writer.write(wordA + "\n");
			if((wordB = readerB.getNextWord()) != null){
				writer.write(wordB + "\n");
			}
		}
		
		while((wordB = readerB.getNextWord()) != null){
			System.out.println(wordB);
			writer.write(wordB + "\n");
		}
		writer.close();
	}
}

class FileManage{
	private int pos = 0;
	private String[] words = null;
	
	public FileManage(String fileName , String[] regex) throws Exception{
		File f = new File(fileName);
		FileReader reader = new FileReader(f);
		char[] cbuf = new char[(int)f.length()];
		int len = reader.read(cbuf);	//将文件中的字符读入到字节数组中
		
		String sBuf = new String(cbuf , 0 , len);
		String regexString = null;
		if(regex.length > 1){
			regexString = "" + regex[0] + "|" + regex[1];
		}else{
			regexString = "" + regex[0];
		}
		
		words = sBuf.split(regexString);//对字符串进行分割
		//pos = words.length;
	}
	
	//从字符串words中获取下一个字符串
	public String getNextWord(){
		if(pos == words.length){
			return null;
		}
		return words[pos++];
	}
}
