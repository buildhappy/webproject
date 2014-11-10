package hadoop.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HbaseBasicOpt {
	static HBaseConfiguration cfg = null;
	static {
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum" , "10.108.115.45");
		conf.set("hbase.zookeeper.property.clientPort" , "9091");
		cfg = new HBaseConfiguration(conf);
	}
	
	/**
	 * 创建表
	 * @param tableName
	 * @throws IOException 
	 * @throws ZooKeeperConnectionException 
	 * @throws MasterNotRunningException 
	 */
	public static  void createTable(String tableName) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		HBaseAdmin admin = new HBaseAdmin(cfg);
		if(admin.tableExists(tableName)){
			System.out.println("table exits");
		}else{
			HTableDescriptor desc = new HTableDescriptor(tableName);
			desc.addFamily(new HColumnDescriptor("name"));
			admin.createTable(desc);
			System.out.println("create done");
		}
	}
	
	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		createTable("home");
	}
}
