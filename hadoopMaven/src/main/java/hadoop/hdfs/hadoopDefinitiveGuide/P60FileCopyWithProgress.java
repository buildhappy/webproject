/**
 * 将本地文件复制到hadoop并显示进度
 */
package hadoop.hdfs.hadoopDefinitiveGuide;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

public class P60FileCopyWithProgress {
	public static void main(String[] args) throws IOException{
		String localSrc = "test.txt";
		String dst = "hdfs://10.108.112.137:9091/user/hadoop/In/" + localSrc;
		InputStream input = new BufferedInputStream(new FileInputStream(localSrc));
		Configuration conf = new Configuration();
		
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		OutputStream out = fs.create(new Path(dst) , new Progressable(){
			public void progress(){
				System.out.print(".");
			}
		});
		IOUtils.copyBytes(input, out, 4096, true);
	}
	
}
