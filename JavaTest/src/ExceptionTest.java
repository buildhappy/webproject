public class ExceptionTest {
	public static void main(String[] args) {
		new Thread(new Thread1()).start();
		try{
			Thread.sleep(10);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		new Thread(new Thread2()).start();
	}
}


class Thread1 implements Runnable{
	public void run(){
		synchronized(ExceptionTest.class){
			System.out.println("In thread1..");
			System.out.println("Thread1 waiting..");
			try{
				ExceptionTest.class.wait();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			System.out.println("Thread1 is going on..");
			System.out.println("Thread1 is being over..");
		}
	}
}

class Thread2 implements Runnable{
	public void run(){
		synchronized(ExceptionTest.class){
			System.out.println("In thread2.. ");
			System.out.println("Thread2 waiting..");
			System.out.println("thread2 notify other thread can relaese wait status..");
			ExceptionTest.class.notify();
			System.out.println("Thread2 is sleeping 20 millisecond..");
			try{
				Thread.sleep(20);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println("thread2 is going on..");
		System.out.println("thread2 is being over..");
	}
}