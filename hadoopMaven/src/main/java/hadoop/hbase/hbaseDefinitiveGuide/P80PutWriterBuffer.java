package hadoop.hbase.hbaseDefinitiveGuide;

import java.io.IOException;

import hadoop.hbase.hbaseDefinitiveGuide.util.HBaseHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Base64;

public class P80PutWriterBuffer {

	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		conf.set("hbase.zookeeper.property.clientPort", "2222");
		conf.set("hbase.master", "10.108.112.181");
		HBaseHelper helper = HBaseHelper.getHelper(conf);
		//helper.dropTable("apps");
		//helper.createTable("apps", "info");
		HTable table = new HTable(conf , "apps");
		System.out.println("isAutoflush: " + table.isAutoFlush());
		table.setAutoFlush(false);
		
		String row1 = Base64.encodeBytes(Bytes.toBytes("第一行"));
		Put put1 = new Put(Bytes.toBytes(row1));
		put1.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("捕鱼达人"));
		table.put(put1);
		Get get = new Get(Bytes.toBytes(row1));
		get.addColumn(Bytes.toBytes("info") , Bytes.toBytes("name"));
		Result res = table.get(get);
		byte[] value = res.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
		System.out.println("res:" + new String(value));
//		Put put1 = new Put(Bytes.toBytes(row1));
//		put1.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("捕鱼达人"));
//		table.put(put1);
//		Get get = new Get(Bytes.toBytes("第一行"));
//		Result res = table.get(get);
//		System.out.println("res:" + res);
		
		Put put2 = new Put(Bytes.toBytes("row2"));
		put2.add(Bytes.toBytes("info") , Bytes.toBytes("name") , Bytes.toBytes("running"));
		table.put(put2);
		get = new Get(Bytes.toBytes("row2"));
		res = table.get(get);
		System.out.println("res:" + res);
		
		table.flushCommits();
		
		get = new Get(Bytes.toBytes(row1));
		res = table.get(get);
		System.out.println("res:" + res);
		get = new Get(Bytes.toBytes("row2"));
		res = table.get(get);
		//System.out.println("res:" + res);
		System.out.println(Base64.decode(new String(res.getRow())));
	}

}
