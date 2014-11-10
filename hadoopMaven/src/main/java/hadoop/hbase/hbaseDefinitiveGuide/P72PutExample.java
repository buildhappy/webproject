package hadoop.hbase.hbaseDefinitiveGuide;
/**
 * 往HBase中存放数据
 */
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import hadoop.hbase.hbaseDefinitiveGuide.util.HBaseHelper;
public class P72PutExample {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException{
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		//conf.set("hbase.zookeeper.master", "10.108.112.137:2222");
		//conf.set("mapred.job.tracker", "10.108.112.137:9099");
		conf.set("hbase.zookeeper.property.clientPort", "2222");//2181
		conf.set("hbase.master","10.108.112.181:60000");
		HBaseHelper helper = HBaseHelper.getHelper(conf);
		helper.createTable("student", "info");
		HTable hTable = new HTable(conf , "student");
		Put put = new Put(Bytes.toBytes("buildhappy"));
		put = put.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("buildhappy"));
		put = put.add(Bytes.toBytes("info") , Bytes.toBytes("age") , Bytes.toBytes("24"));
		
		hTable.put(put);
	}
}
