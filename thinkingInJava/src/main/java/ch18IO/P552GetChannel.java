package ch18IO;
/**
 * 利用java.nio提高读写速度
 * 用FileOutputStream，FileRandomAccess和FileOutputStream流产生可写、可读的通道
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class P552GetChannel {
	public static final int BSIZE = 1024;
	public static void main(String[] args)throws IOException{
		// write a file
		FileChannel fc = new FileOutputStream(new File("test.txt")).getChannel();
		fc.write(ByteBuffer.wrap("beginning of file ".getBytes()));
		fc.close();
		
		//add to the end of the file
		fc = new RandomAccessFile("test.txt" , "rw").getChannel();
		fc.position(fc.size());//move to the end
		System.out.println(fc.size());
		fc.write(ByteBuffer.wrap("the added content".getBytes()));
		fc.close();
		
		//read from the file
		fc = new FileInputStream("test.txt").getChannel();
		ByteBuffer byteBuf = ByteBuffer.allocate(BSIZE);
		fc.read(byteBuf);
		byteBuf.flip();
		//fc.close();
		System.out.println(fc.size());
		while(byteBuf.hasRemaining()){
			System.out.print((char)byteBuf.get());
		}
		
	}
}
