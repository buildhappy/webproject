package hadoop.mapreduce.hbaseDefinitiveGuide;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class P312ImportFromFile {
	public static final String NAME = "P312ImportFromFile";//为后续使用定义一个作业名
	public enum Counters{LINES}
	
	static class ImportMapper extends 
	Mapper<LongWritable , Text , ImmutableBytesWritable , Put>{
		private byte[] family = null;
		private byte[] qualifier = null;
		
		/**
		 * prepares the column family and qualifier
		 */
		protected void setup(Context context)//context作为了map和reduce执行中各个函数的一个桥梁，这个设计和java web中的session对象、application对象很相似
		throws IOException , InterruptedException{
			String colum = context.getConfiguration().get("conf.column");
			byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(colum));
			family = colkey[0];
			if(colkey.length > 1){
				qualifier = colkey[1];
			}
		}
		/**
		 * maps the input,将InputFormat提供的键值对转化成OutputFormat需要的类型
		 */
		@Override
		public void map(LongWritable offset , Text line , Context context)
		throws IOException{
			try{
				String lineString = line.toString();
//				String[] items = lineString.split("," , -1);
//				ImmutableBytesWritable rowkey = new ImmutableBytesWritable(items[0].getBytes());
//				KeyValue kv = new KeyValue(Bytes.toBytes(items[0]) , Bytes.toBytes(items[1]),
//						Bytes.toBytes(items[2]) ,System.currentTimeMillis() , Bytes.toBytes(items[3]));
//				if(null != kv){
//					context.write(rowkey, kv);
//				}
				
				byte[] rowkey = DigestUtils.md5(lineString);//行健经过md5散列之后随机生成的键值
				Put put = new MyPut(rowkey);
				put.add(family , qualifier , Bytes.toBytes(lineString));//存储原始数据到给定的表中的一列
				context.write(new ImmutableBytesWritable(rowkey), put);//Put类的定义存在版本差异
				
				context.getCounter(Counters.LINES).increment(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * parse the command line parameters,使用Apache Commans CLI类解析命令行参数。
	 * 这些已经是hbase的一部分，用户可以方便的处理作业的参数
	 * ImportFromFile
	 */
	private static CommandLine parseArgs(String[] args) throws ParseException{
		Options options = new Options();
		Option opt = new Option("t" , "table" , true , "table to import into (must exit)");
				//Describes a single command-line option,前两个参数short/long representation of the option
		opt.setArgName("table-name");
		opt.setRequired(true);
		options.addOption(opt);
		
		opt = new Option("c" , "column" , true , "colunm to store row data into (must exit)");
		opt.setArgName("family:qualifier");
		opt.setRequired(true);
		options.addOption(opt);
		
		opt = new Option("i" , "input" , true , "the directory or file to read from");
		opt.setArgName("path-in-HDFS");
		opt.setRequired(true);
		options.addOption(opt);
		
		options.addOption("d", "debug", false , "switch on DEBUG log level");
		
		CommandLineParser parser = new PosixParser();//CommandLineParser根据特定的Options解析String数组并返回CommandLine
		CommandLine cmd = null;//CommandLine展示根据Options解析而来的参数列表
		
		try{
			cmd = parser.parse(options, args);
		}catch(Exception e){
			System.err.println("ERROR:" + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(NAME, options , true);
			System.exit(-1);
		}
		if(cmd.hasOption("d")){
			Logger log = Logger.getLogger("mapreduce");
			log.setLevel(Level.DEBUG);
		}
		return cmd;
	}
	
	
	/**
	 * 运行传递的参数：-t testtable -i test-data.txt -c data:json
	 */
	public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, InterruptedException {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "10.108.112.181");
		conf.set("hbase.zookeeper.property.clientPort", "2222");
		conf.set("hbase.master", "10.108.112.181");
		String[] otherArgs = new GenericOptionsParser(conf , args).getRemainingArgs();//传递命令行参数到解析器中，并优先解析-Dxyz属性
		CommandLine cmd = parseArgs(otherArgs);

		System.out.println("CommandLine: " + cmd.toString());
		if(cmd.hasOption("d")){
			conf.set("conf.debug", "true");
		}
		String table = cmd.getOptionValue("t");
		String input = cmd.getOptionValue("i");
		String column = cmd.getOptionValue("c");
		conf.set("conf.column", column);
		
		Job job = new Job(conf , "Import from file " + input + "into tbale " + table);//使用特定的类定义作业
		job.setJarByClass(P312ImportFromFile.class);
		job.setMapperClass(ImportMapper.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, table);
		job.setOutputKeyClass(ImmutableBytesWritable.class);
		job.setNumReduceTasks(0);//只包含map阶段,框架将跳过reduce阶段
		FileInputFormat.addInputPath(job, new Path(input));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

class MyPut extends Put implements Writable{

	public MyPut(byte[] row) {
		super(row);
	}

	public void readFields(DataInput arg0) throws IOException {
	}

	public void write(DataOutput arg0) throws IOException {
	}
	
}
