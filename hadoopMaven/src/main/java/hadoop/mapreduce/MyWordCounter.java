package hadoop.mapreduce;

import hadoop.mapreduce.WordCount.IntSumReducer;
import hadoop.mapreduce.WordCount.TokenizerMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MyWordCounter {
	
	public static class MapperClass extends
			Mapper<Object, Text, Text, IntWritable> {
		// Mapper的泛型约束<keyin, value, keyout, value>
		//keyin, value为输入参数的类型和值
		//keyout, value为输出参数的类型和值

		private Text textKey = new Text();// 类似于String str = new String();
		private IntWritable intValue = new IntWritable(1);// 类似于int i = 1

		/**
		 * value为map的输入值，context为输出值()
		 */
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// 获取值：map读取一行数据，将值存在value中
			String str = value.toString();
			StringTokenizer stringTokenizer = new StringTokenizer(str);// 默认使用空格对字符串进行分割
			// 遍历stringTokenizer,将值存入context中
			while (stringTokenizer.hasMoreTokens()) {
				textKey.set(stringTokenizer.nextToken());
				context.write(textKey, intValue);// context.write("my" , 1);
			}
			System.out.println("map:" + context.getCurrentValue());//查看当前map中的context值，即文件中的一行
		}
	}

	public static class ReducerClass extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		/**
		 * Reducer<keyin, value, keyout, value>
		 * @param key     输入值，形如my
		 * @param values  输入值，形如[1,1]
		 * @param context 输出值
		 */
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context)
				throws IOException, InterruptedException {
			System.out.println("reduce key :" + key.toString());
			while(values.iterator().hasNext()){
				System.out.println("reduce values:" + values.iterator().next());
			}
			int sum = 0;
			while (values.iterator().hasNext()) {
				sum += values.iterator().next().get();
			}
			context.write(key, new IntWritable(sum));
			//System.out.println("reduce context:" + context.getCurrentValue());//查看结果的值
		}
	}


	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		Job job = new Job(conf);
		job.setJarByClass(MyWordCounter.class);
		job.setJobName("wordcounter");
		job.setMapperClass(MapperClass.class);
		job.setReducerClass(ReducerClass.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		Path inPath = new Path(
				"hdfs://10.108.112.181:9091/user/hadoop/In/test.txt");
		FileInputFormat.addInputPath(job, inPath);
		Path outPath = new Path(
				"hdfs://10.108.112.181:9091/user/hadoop/In/res");
		FileOutputFormat.setOutputPath(job, outPath);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
