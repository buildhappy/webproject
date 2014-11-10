import java.io.IOException;

class Goods{
	static class Destination{
		private String des;
		public Destination(String des){
			this.des = des;
		}
		public String getName(){
			return des;
		}
		public String toString(){
			return des;
		}
	}
	
	class Label{
		private String info;
		public Label(String info){
			this.info = info;
		}
		public String getInfo(){
			return info;
		}
		public String toString(){
			return info;
		}
	}
	
	static private Destination destination;
	private Label label;
	
	public void setDestination(String des){
		destination = new Destination(des);
	}
	
	public void setLabel(String info){
		label = new Label(info);
	}

	public Destination getDestination() {
		return destination;
	}

	public Label getLabel() {
		return label;
	}
	
	
}



class Actor{
	public void act(){}
}

class HappyActor extends Actor{
	public void act(){
		System.out.println("HappyAct");
	}
}

class SadActor extends Actor{
	public void act(){
		System.out.println("SadActor");
	}
}

class Stage{
	public Actor actor = new HappyActor();
	
	public void perform(){
		actor.act();
	}
	
	public void change(){
		actor = new SadActor();
	}
}



public class Test {
	public static void main(String[] args) throws IOException{
//		String des = "Beijing";
//		String info = "food";
//		Goods goods = new Goods();
//		goods.setDestination(des);
//		goods.setLabel(info);
//		System.out.println(goods.getDestination());
//		System.out.println(goods.getLabel());
		


//		String s = "aa";
//		s += "sd";
//		System.out.println(s.toString());
		
//		StringBuffer sb = new StringBuffer();
//		for(int i = 0; i < 10; i++){
//			sb.append(i);
//		}
//		sb.insert(0, "d");
//		System.out.println(sb);
		
//		String s1 = "a";
//		String s2 = s1 + "b";
//		String s3 = "a" + "b";
//		System.out.println("s2:" + s2.getClass().toString() + s2.hashCode());
//		System.out.println("ab:" + "ab".getClass().toString() + "ab".hashCode());
//		System.out.println("s3:" + s3.getClass().toString() + s3.hashCode());
//		System.out.println(s2 == "ab");
//		System.out.println(s3 == "ab");
//
//		String s = "a+b+c";
//		String[] sb = s.split("[+]");//+是转义字符，要用[]包起来
//		System.out.println(sb.length);
//		for(int i = 0; i < sb.length; i++){
//			System.out.println(sb[i]);
//		}
		
//		Integer i1 = new Integer(1);
//		Integer i2 = new Integer(1);
//		System.out.println(i1.hashCode());
//		System.out.println(i2.hashCode());
//		System.out.println(i1 == i2);
		
//		String s = "a b,c";
//		String regex = "" + "," + "|" + " ";//正则表达式
//		System.out.println(regex);
//		String[] ss = s.split(regex);
//		for(int i = 0; i < ss.length; i++){
//			System.out.println(ss[i]);
//		}
		
//		String str = "中国abc";
//		byte[] b = str.getBytes("GBK");//GBK,UTF-8,GB2312
//		for(int i = 0; i < b.length; i++)
//			System.out.println(b[i]);
//		System.out.println(b.length);
		
		Stage stage = new Stage();
		stage.perform();
		stage.change();
		stage.perform();
	}
}










