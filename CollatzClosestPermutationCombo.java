import java.util.*;
import collatz.CollatzSequenceGenerator.SequenceTree;
import collatz.CollatzSequenceGenerator.Node;
public class CollatzClosestPermutationCombo extends Thread {
	// Thinking of Collatz permutations as a sequence of ratio multiplications, the same sequence of permutations enacted on any two starting values(that results in the same
	// sequence of "odd or even" permutations) should have an end result that is a similar fraction of the original seed value.  The deviations being caused by the "+1" component.
	// In a closed Collatz loop the end result is 1/1 of the starting value, therefore when searching for looping Collatz values an alternative option to crawling starting values
	// is to crawl permutation combinations where the end value starting value fraction is closest to 1/1.  When testing these permutations you can either start with 1, being a substitution
	// for an arbitrary point on the number line and ignore the "+1" or you can pick out some reasonably high starting seed eg. 100,000,000 knowing  ahead of time that Collatz does not loop 
	// up to very high values and when running the permutations you simply ignore whether the sequence numbers are odd or even and follow a predetermined permutation sequence.  After
	// running some number of permutation sequences on your test value you then pick out the one that ends closest to the starting value and then try to pick out untested values
	// very high in the number line that will follow the same sequence given some structural similarity to an already tested value with the "closest to 1/1" permutation combination.
	// Two or more "Close to 1" sequences can be combined end to end, creating another search problem for best offsetting ratios beyond the range of exhaustive permutation search.
	// This method quickly produced a "1.002.." permutation.  A promising feature of this approach is that starting values can be crawled running only the number of steps that match
	// the sequence allowing search orders of magnitude higher in the number line per unit of compute.  The biggest obstacle thus far is the closest sequence best match was only about
	// half of a 137 permutation sequence after testing up to ten trillion.
	public double seed, permutations;
	public CollatzThreadHost host;
	public String testBase, threadName;
	public boolean dataOffloaded;
	public Random gen;
	public ArrayList<String> testBaseSets;
	public SequenceTree sequenceTree;
	public ArrayList<Object> threadData;	
	
	public CollatzClosestPermutationCombo(double a, double b, String testSequence, String c, CollatzThreadHost q) {
		seed=a;
		permutations=b;
		threadName=c;
		testBase=testSequence;
		host=q;
		gen=new Random();
		threadData = new ArrayList<Object>();
		dataOffloaded=false;
	}
	public CollatzClosestPermutationCombo4(double a, double b, ArrayList<String> testSequenceSets, String c, CollatzThreadHost q) {
		seed=a;
		permutations=b;
		threadName=c;
		testBaseSets=testSequenceSets;
		host=q;
		gen=new Random();
		threadData = new ArrayList<Object>();
		dataOffloaded=false;
	}	

	public CollatzClosestPermutationCombo4(double a, double b, SequenceTree tree, String c, CollatzThreadHost q) {
		seed=a;
		permutations=b;
		threadName=c;
		sequenceTree=tree;
		host=q;
		gen=new Random();
		threadData = new ArrayList<Object>();
		dataOffloaded=false;
	}
	
	public ArrayList<Object> baseCompletesPermutationSequence(double baseNum, String sequence) {  // runs a test value against a move sequence until an odd/even mismatch.
		double base = baseNum;                                                                // returns full sequence boolean and number of matched steps
		ArrayList<Object> data = new ArrayList<Object>();
		for(int j=0; j<sequence.length(); j++) {
			if(sequence.charAt(j)=='1') {
				if(base%2==1) {
				//	System.out.print(""+base+"-> ");
					base=(3*base)+1;
				//	System.out.println("1-> X: "+base);
				}
				else {
				//	System.out.println(j);
					data.add(false);
					data.add(j);
					return data;
				}
			}
			else
			{
				if(base%2==0) {
					//System.out.print(""+base+"-> ");
					base=(base/2);
				//	System.out.println("0-> X: "+base);
				}
				else {
					//System.out.println(j);
					data.add(false);
					data.add(j);
					return data;
				}
			}
			if(j==sequence.length()-1) {
				data.add(true);
				data.add(j);
			}
		}
		return data;
	}
	public ArrayList<Object> runSequenceMatchSet(double min, double max, String sequence){ // runs "baseCompletePermSeq(..)" over a range of values and returns best match
		int highest=0;
		int x=1;
		ArrayList<Object> farthestMatch= new ArrayList<Object>();
		for(double i=1.0*min; i<1.0*max;i++) {
			if(i%1000000000==0)
				System.out.println(threadName+": "+ x++ +" Billion");
			ArrayList<Object> matcher = baseCompletesPermutationSequence(i, sequence);

			if((Integer)matcher.get(1)>highest) {
				farthestMatch = matcher;
				farthestMatch.add(i);
				highest=(Integer)matcher.get(1);
			}
		}
		System.out.println("Base: "+farthestMatch.get(2)+", full String:"+farthestMatch.get(0)+", Permutations: "+farthestMatch.get(1));
		return farthestMatch;
	}

	public ArrayList<Object> filterGroupSequences(double baseNum, ArrayList<String> sequences) {  // list version of "baseCompletes" - temp stand in for segmented version
		ArrayList<Object> data = new ArrayList<Object>();
		double base = baseNum;
		ArrayList<String> sequencesCopy = (ArrayList<String>) sequences.clone();
		for(int j=0; j<sequencesCopy.get(0).length(); j++) {
			Iterator<String> iterator = sequencesCopy.iterator();
			while(iterator.hasNext()) {
				String a = iterator.next();
				if(a.charAt(j)=='1') {
					if(base%2==1) {
					//	System.out.print(""+base+"-> ");
						if(iterator.hasNext()==false)
							base=(3*base)+1;
					//	System.out.println("1-> X: "+base);
					}
					else {
						iterator.remove();
						if(sequencesCopy.size()==0) {
							data.add(false);
							data.add(j);
							return data;
						}
					}
				}
				else
				{
					if(base%2==0)
					{
						if(iterator.hasNext()==false)
							//System.out.print(""+base+"-> ");
							base=(base/2);
							//	System.out.println("0-> X: "+base);
					}
					else {
						//System.out.println(j);
						iterator.remove();
						if(sequencesCopy.size()==0) {
							data.add(false);
							data.add(j);
							return data;
						}
					}
				}
			}
			if(j==sequencesCopy.get(0).length()) {
				data.add(true);
				data.add(j);
			}
		}
		return data;
	}
	public ArrayList<Object> runSequenceMatchSetFromGroup(double min, double max, ArrayList<String> sequences){ // list version of runSequenceMatchSet
		int highest=0;
		int x=1;
		ArrayList<Object> farthestMatch= new ArrayList<Object>();
		for(double i=1.0*min; i<1.0*max;i++) {
			if(i%100000000==0)
				System.out.println(threadName+": "+ x++ +" Billion");
			ArrayList<Object> matcher = filterGroupSequences(i, sequences);
	
			if((Integer)matcher.get(1)>highest) {
				farthestMatch = matcher;
				farthestMatch.add(i);
				highest=(Integer)matcher.get(1);
			}
		}
		System.out.println("Base: "+farthestMatch.get(2)+", full String:"+farthestMatch.get(0)+", Permutations: "+farthestMatch.get(1));
		return farthestMatch;
	}
	public ArrayList<Object> baseCompletesSequenceTree(double baseNum, SequenceTree tree) {  // runs a test value against a tree composed of a set of sequences.
		double base = baseNum;                                                               
		ArrayList<Object> data = new ArrayList<Object>();
		String seq="";
		Node curNode = tree.root;
		int count=0;
		while(!(curNode.left==null&&curNode.right==null)) {
			if(base%2==1) {
				if(curNode.left!=null) {
					base=(3*base)+1;
					seq=seq+"1";
					count+=1;
					curNode=curNode.left;
				}
				else {
					data.add(false);
					data.add(count);
					data.add(seq);
					return data;
				}
			}
			else {
				if(curNode.right!=null) {
					base=base/2;
					seq=seq+"0";
					count+=1;
					curNode=curNode.right;
				}
				else {
					data.add(false);
					data.add(count);
					data.add(seq);	
					return data;
				}
			}
		}
		data.add(true);
		data.add(count);
		data.add(seq);
		return data;
	}
	
	public ArrayList<Object> runSequenceMatchSetFromTree(double min, double max, SequenceTree tree){ // runs "baseCompletePermSeq(..)" over a range of values and returns best match
		int highest=0;
		int x=1;
		ArrayList<Object> farthestMatch= new ArrayList<Object>();
		for(double i=1.0*min; i<1.0*max;i++) {
			if(i%1000000000==0)
				System.out.println(threadName+": "+ x++ +" Billion");
			ArrayList<Object> matcher = baseCompletesSequenceTree(i, tree);

			if((Integer)matcher.get(1)>highest) {
				farthestMatch = matcher;
				farthestMatch.add(i);
				highest=(Integer)matcher.get(1);
			}
		}
		System.out.println("Base: "+farthestMatch.get(2)+", full String:"+farthestMatch.get(0)+", Permutations: "+farthestMatch.get(1));
		return farthestMatch;
	}
	
	
	public void run() {
		System.out.println ("Thread " +
		Thread.currentThread().getId() +
		" is running");
	
		threadData=runSequenceMatchSetFromTree(seed, seed+permutations, sequenceTree);
		while(!dataOffloaded){
			if(host.appendBusy()){ 
				try {
					Thread.sleep(500*gen.nextInt(10));
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
			else{
				host.appendResults(threadData);
				dataOffloaded = true;
			}
		}
		host.incrementThreadClosed();
		System.out.println(threadName + " closed.");
	}			
}
