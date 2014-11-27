package twitterMap;

import java.util.ArrayList;
import java.util.List;

public class WorkerClass implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<WorkerClass> workers = new ArrayList<WorkerClass>();
		for (int i = 0; i < 4; i++) {
			WorkerClass worker = new WorkerClass();
			workers.add(worker);
		}
		for (int i = 0; i < 4; i++) {
			WorkerClass worker = workers.get(i);
			worker.run();
		}
	}

}
