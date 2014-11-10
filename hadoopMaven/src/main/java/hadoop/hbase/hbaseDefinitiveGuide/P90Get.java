package hadoop.hbase.hbaseDefinitiveGuide;

import java.io.IOException;

import hadoop.hbase.hbaseDefinitiveGuide.util.HBaseHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;

public class P90Get {
	public static void main(String[] args) throws IOException{
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		conf.set("hbase.zookeeper.property.clientPort", "2222");
		conf.set("hbase.master", "10.108.112.181");
		
		HBaseHelper helper = HBaseHelper.getHelper(conf);
		if(!helper.exitsTable("apps")){
			helper.createTable("apps", "info");
		}
		
		HTable table = new HTable(conf ,"apps");
		Get get = new Get(Bytes.toBytes("running"));
		get.addColumn(Bytes.toBytes("info") , Bytes.toBytes("name"));
		Result res = table.get(get);
		byte[] val = res.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
		System.out.println("value:" + new String(val));
		
		//中文测试
		String row1 = "第一行";Base64.encodeBytes(Bytes.toBytes("第一行"));
		Put put1 = new Put(Bytes.toBytes(row1));
		put1.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("捕鱼达人"));
		table.put(put1);
		get = new Get(Bytes.toBytes(row1));
		get.addColumn(Bytes.toBytes("info") , Bytes.toBytes("name"));
		res = table.get(get);
		byte[] value = res.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
		System.out.println("result:" + new String(value));
	}
}
