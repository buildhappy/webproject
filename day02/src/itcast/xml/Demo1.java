package itcast.xml;

public class Demo1 {

	/**
	 * 演示改变Java虚拟机默认最大内存
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//Java虚拟机默认最大分配64M内存
		//可以自己指定（run configuration->arguments->VM->-Xmx800m指定最大为800m）
		byte b[] = new byte[1024*1024*500];//在虚拟机中开辟500M的空间
		Thread.sleep(1000*20);//睡眠20秒
	}
}
