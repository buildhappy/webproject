package hadoop.hbase.hbaseDefinitiveGuide;

import hadoop.hbase.hbaseDefinitiveGuide.util.HBaseHelper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class P86CheckAndPut {
	public static void main(String[] args) throws IOException{
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		conf.set("hbase.zookeeper.property.clientPort", "2222");
		conf.set("hbase.master", "10.108.112.181");
		
		HBaseHelper helper = HBaseHelper.getHelper(conf);
		if(!helper.exitsTable("apps")){
			helper.createTable("apps" , "info");
		}
		
		HTable table = new HTable(conf , "apps");
		Put put = new Put(Bytes.toBytes("running"));
		put.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("running"));
		
		boolean res = table.checkAndPut(Bytes.toBytes("running"), Bytes.toBytes("info"), 
					      Bytes.toBytes("name"), null , put);
		
	}
}
