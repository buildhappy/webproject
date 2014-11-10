import java.io.UnsupportedEncodingException;


public class E2SubString {
	public static void main(String[] args) throws Exception{
		String str = "我a爱中华abc我爱船只def";
		String str2 = "我ABC汉";
		int num = trimGBK(str.getBytes("GBK") , 5);//GBK,UTF-8,GB2312
		System.out.println(str.substring(0, num));
		System.out.println(str.substring(0, 5));
		System.out.println(str2.length());
	}
	
	public static int trimGBK(byte[] buf , int n){
		int num = 0;
		boolean bChineseFirstHalf = false;
		for(int i = 0; i < n; i++){
			if(buf[i] < 0 && !bChineseFirstHalf){
				bChineseFirstHalf = true;
			}else{
				num++;
				bChineseFirstHalf = false;
			}
		}
		return num;
	}
}
