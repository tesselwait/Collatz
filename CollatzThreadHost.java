import java.util.*;
import java.io.*;
public class CollatzThreadHost {
	public int cores;
	public double max, threadSet;
	public int threadsClosed;
	public boolean incrementFlag, writeFlag;
	public ArrayList<ArrayList<Object>> results;
	
	public CollatzThreadHost(double maxVal, double setSize) {
		max=maxVal;
		threadSet=setSize;
		results = new ArrayList<ArrayList<Object>>();
	}
	
	public boolean appendBusy(){
		return writeFlag;
	}

	public int getThreadsClosed(){
		return threadsClosed;
	}

	public void incrementThreadClosed(){
		threadsClosed++;
	}
	
	public void appendResults(ArrayList<Object> a) {
		writeFlag = true;
		results.add(a);
		writeFlag = false;
	}
	
	public void writeData() {
		try (PrintWriter output = new PrintWriter("CollatzOutput.txt")) {
			for(ArrayList<Object> b: getData())
				output.printf("Base: %,.0f ; Match: "+b.get(0)+", Permutations: "+b.get(1)+"\n", b.get(2));
			output.println();
			output.println();
		}
		catch (FileNotFoundException e){
			System.out.println(e);
		}
		// ^file-print
		// ----------
		//  terminal_
		System.out.println("Results:");
		for(ArrayList<Object> b: getData())
			System.out.printf("Base: %,.0f ; Match: "+b.get(0)+", Permutations: "+b.get(1)+"\n", b.get(2));
	}
	
	public ArrayList<ArrayList<Object>> getData(){
		return results;
	}
	
	public static void main(String[] args) {
		double totalTestStrings=1000000000000.0;
		int cores = Runtime.getRuntime().availableProcessors();
		CollatzThreadHost crawler = new CollatzThreadHost(totalTestStrings, totalTestStrings/(1.0*cores));
		for(int i=0; i<cores; i++) {
			CollatzClosestPermutationCombo object = new CollatzClosestPermutationCombo(0.0+i*crawler.threadSet, crawler.threadSet, "10101010101010101010101000000000000000010101010101010101010101000010101010100000000101010101010101010101010000000010101010101010101010101", "Thread "+i, crawler);
			object.start();
		}
		while(crawler.getThreadsClosed()<cores){
			try{
			Thread.sleep(30000);
			System.out.println("30 sec sleep.");
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		crawler.writeData();
	}
}
