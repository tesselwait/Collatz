import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import collatz.CollatzSequenceGenerator.*;
import collatz.CollatzSequenceGenerator.SequenceTree;
public class CollatzThreadHost {
	public int cores;
	public double max, threadSet;
	public int threadsClosed;
	public boolean incrementFlag, writeFlag;
	public ArrayList<ArrayList<Object>> results;
	public SequenceTree tree;
	
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
			for(ArrayList<Object> b: getData()){
				output.printf("Base: %,.0f ; Match: "+b.get(0)+", Permutations: "+b.get(1), b.get(2));
				if(b.size()>3)
					System.out.print(", sequence: "+b.get(3));
				System.out.println();
			}
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
		for(ArrayList<Object> b: getData()){
			System.out.printf("Base: %,.0f ; Match: "+b.get(0)+", Permutations: "+b.get(1), b.get(2));
			if(b.size()>3)
				System.out.print(", sequence: "+b.get(3));
			System.out.println();
		}
	}
	
	public ArrayList<ArrayList<Object>> getData(){
		return results;
	}
	
	public static void main(String[] args) {
		double totalTestStrings=1000000000.0;  // Total seed values to check against permutations.  Using BigDecimal: 1 billion == 5min, 10 billion == 50min. 20 cores.
		double startIndex = 100000000000.0;  //  starting seed value in double range
		BigDecimal startIndexArbitrary = new BigDecimal("1000000000000000000000000.0"); //  starting seed value for arbitrarily large numbers
		int cores = Runtime.getRuntime().availableProcessors();
		
	//	CollatzSequenceGenerator seqGen = new CollatzSequenceGenerator(startIndex, 13);
	//	SequenceSpace seqGen2 = new SequenceSpace(); // --
	//	int[] pair = seqGen2.generateList(306, 485, false).get(0); // --
	//	seqGen2.generateSequences(10000, pair[0], pair[1]); // --
	//	SequenceTree tree = seqGen.constructTreeFromList(seqGen2.testSequences); // -- from SequenceSpace generator
	//	SequenceTree tree = seqGen.constructTree(seqGen.generateBestMatchPermutation(100, 20)); // from CollatzSequenceGenerator
		
		CollatzThreadHost crawler = new CollatzThreadHost(totalTestStrings, totalTestStrings/(1.0*cores));	
		for(int i=0; i<cores; i++) {
			//CollatzClosestPermutationCombo object = new CollatzClosestPermutationCombo(0.0+i*crawler.threadSet, crawler.threadSet, tree, "Thread "+i, crawler);  // Tree version. runs about half as fast as single sequence crawler

			// -- Crawler for single permutation against arbitrarily large seed values.  Testing ranges beyond current exhaustively tested Collatz values which are beyond double range. BigDecimal slower than double.
			CollatzClosestPermutationCombo object = new CollatzClosestPermutationCombo(startIndexArbitrary.add(new BigDecimal(i*crawler.threadSet)), crawler.threadSet, "10000010101010101010101010101010101010101010100000000000000000000000000000000000000000001000000000000000100000000000000000000100000000000101000101010101010101010101010101010101010101010101010101010101010101000101010101010100010101010101010101010100010101010101010101000101010101010101010101010101010101010101010101010100010101000001010101010101000101010100010101010000010101010101000100010101000101010100010101010100010101000101000101010101000101010100010000010101010101000101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010", "Thread "+i, crawler);
			
			//CollatzClosestPermutationCombo object = new CollatzClosestPermutationCombo(0.0+i*crawler.threadSet, crawler.threadSet, "10101010101010101010101000000000000000010101010101010101010101000010101010100000000101010101010101010101010000000010101010101010101010101", "Thread "+i, crawler);
			
			//CollatzClosestPermutationCombo object = new CollatzClosestPermutationCombo(0.0+i*crawler.threadSet, crawler.threadSet, seqGen.generateBestMatchPermutation(50, 5), "Thread "+i, crawler);
			// ^ Integrates sequence generator directly into crawler.  Tests list of sequences rather than one.  Comparative slowdown: (sequences per segment)^(number of segments).  Segmented version or constructing a tree should be faster
			
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
