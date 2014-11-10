package hadoop.hdfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

public class HdfsBasicOpt {
	private static Configuration conf ;
	static FileSystem fs;
	static{
		conf = new Configuration();
		try {
			//fs要使用get(uri , conf)方式获取。如果使用get(conf)会出错
			fs = FileSystem.get(URI.create("hdfs://10.108.115.45:9091") , conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 创建目录
	 * @throws IOException
	 */
	public static void makedir() throws IOException{
		Path path = new Path("/user/hadoop/In/");
		fs.mkdirs(path);
	}
	
	/**
	 * 上传本地文件
	 * @throws IOException
	 */
	public static void copyToHdfs() throws IOException{
		Path src = new Path("test.txt"); //File.separator
		Path dst = new Path("/user/hadoop/In/");
		fs.copyFromLocalFile(src, dst);
	}
	
	/**
	 * 查找某个文件在hdfs上的位置
	 * @param fileName
	 * @throws IOException
	 */
	public static void findFileLocation(String fileName) throws IOException{
		Path fPath = new Path("/user/hadoop/In/test.txt");
		FileStatus fileStatus = fs.getFileStatus(fPath);
		//获取所有块所在的结点
		BlockLocation[] blockLocation = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
		for(int i = 0; i < blockLocation.length; i++){
			System.out.println(blockLocation[i].getHosts()[0] + " " + blockLocation[i].getHosts()[1]);
		}
	}
	
	/**
	 * 查找hdfs集群的结点数
	 * @throws IOException
	 */
	public static void getHdfsNodeNum() throws IOException{
		//获取分布式文件系统
		DistributedFileSystem dfs = (DistributedFileSystem)fs;
		DatanodeInfo[] dataNodeInfo = dfs.getDataNodeStats();
		for(int i = 0; i < dataNodeInfo.length; i++){
			System.out.println("name:" + dataNodeInfo[i].getName() + 
					"  hostname:" + dataNodeInfo[i].getHostName());
		}
	}
	
	public static void main(String[] args) throws IOException{
		//makedir();
		//copyToHdfs();
		
		findFileLocation("fs");
		//getHdfsNodeNum();
	}
}
