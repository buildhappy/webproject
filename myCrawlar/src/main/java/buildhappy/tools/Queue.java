package buildhappy.tools;
/**
 * 保存要访问的URL,使用LinkedList
 * @author Administrator
 *
 */
import java.util.LinkedList;

public class Queue{
	private LinkedList<Object> queue = new LinkedList<Object>();
	
	/** 
	 * 入队列
	 */
	public void enQueue(Object t){
		queue.addLast(t);
	}
	/**
	 * 出队列
	 */
	public Object deQueue(){
		return queue.remove();
	}
	/**
	 * 判断队列是否为空
	 */
	public boolean isQueueEmpty(){
		return queue.isEmpty();
	}
	/**
	 * 判断是否包含元素t
	 */
	public boolean contains(Object t){
		return queue.contains(t);
	}
	public boolean empty(){
		return queue.isEmpty();
	}
}


