package hadoop.hdfs.hadoopDefinitiveGuide;
/**
 * Writable接口定义两个方法：write(DataOutput out)将其状态写入到二进制格式的DataOutput流
 * 					   readFields(DataInput in)从二进制格式的DataInput流读取其状态
 */
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.StringUtils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class P93WritableInterface {
	
	//序列化
	public static byte[] serialize(Writable writable)throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);
		   //数据输出流,允许应用程序以适当方式将基本 Java数据类型写入输出流中。然后，应用程序可以使用数据输入流将数据读入
		   //创建一个新的数据输出流，将数据写入指定基础输出流out
		
		writable.write(dataOut);
		dataOut.close();
		return out.toByteArray();
	}
	
	//反序列化数据流
	public static byte[] deserialize(Writable writable , byte[] bytes) throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInputStream dataIn = new DataInputStream(in);
		writable.readFields(dataIn);
		dataIn.close();
		return bytes;
	}
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException{
		Writable writable = new IntWritable();
		((IntWritable) writable).set(163);
		byte[] bytes = serialize(writable);
		assertThat(bytes.length , is(4));
		assertThat(StringUtils.byteToHexString(bytes) , is("000000a3"));
		System.out.println("序列化操作完成");
		
		IntWritable newWritable = new IntWritable();
		deserialize(newWritable , bytes);
		assertThat(newWritable.get() , is(163));
		System.out.println(newWritable.get());
		System.out.println("反序列化操作完成");
	}
}
//datanode namespaceID=1811469610 980987310