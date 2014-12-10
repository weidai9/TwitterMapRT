import java.util.ArrayList;
import java.util.List;

public class WorkerClass implements Runnable {
	private static SNSSender snssender = null;
	private static AwsSqshelper awsSqshelper = new AwsSqshelper();
	public WorkerClass() {
		snssender = new SNSSender();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;
		while(i<100) {
			if(awsSqshelper.GetMessageFromQueue()==null){
		    try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			snssender.NotificationSender(awsSqshelper.GetMessageFromQueue());	
			i++;
		}
	}

	public static void main(String[] args) {
		List<WorkerClass> workers = new ArrayList<WorkerClass>();
		for (int i = 0; i < 4; i++) {
			WorkerClass worker = new WorkerClass();
			workers.add(worker);
		}
		/*if(awssqshelper.GetMessageFromQueue()!=null){
			System.out.println(awssqshelper.GetMessageFromQueue());
		}*/

		for (int i = 0; i < 4; i++) {
			WorkerClass worker = workers.get(i);
			worker.run();
		}
	}
}