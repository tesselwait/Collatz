import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
public class SequenceSpace {
	//  Original sequence generator filtered all allowable sequences by closest to 1/1 ratio in 5 different lengths and then manually constructed
	//  a tree composed from 5 sets of off-setting sequence lists connected end to end.
	
	//  This program is an alternate sequence generator starting with the ratio of '1' and '0' permutations: .63 < '1's/'0's < .64  or 63-64% 0s
	//  based on previous results and then randomly composes sequences adhering to that ratio, narrowing the search space and allowing targeted generation
	//  and comparison of many sequences that are more likely close to 1/1 ratio.  Can be directly crawled or used to find separate offsetting ratios.
	private ArrayList<String> testSequences;
	private double bestRatio;
	public SequenceSpace4() {
		testSequences=new ArrayList<String>();
		bestRatio=Double.MAX_VALUE;
	}
	public double runString(String str) {
		double start = 1000000000;
		double seed = start;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i)=='1')
				seed=(3*seed)+1;
			else
				seed/=2;
		}
		return seed/start;
	}
	
	public ArrayList<int[]> generateList() {
			ArrayList<int[]> list = new ArrayList<int[]>();
		//	int m=54; 
		//	int n=85;  
		//	for(int i=m; i<=m; i++) {  
		//		for(int j=n;j<=n;j++) {
			for(int i=0; i<=100; i++) {  
				for(int j=0;j<=120;j++) {
					double a = (1.0*i)/(j*1.0);
					if(a>.63 && a<.64) {  // ratio of 1 permutation to 0 permutation for current close matches
						int[] arr = {i,j};
						list.add(arr);
					}
				}
			}
		//	for(int[] b: list)  
				//System.out.println(b[0]+", "+b[1]);
			return list;
	}
	
	public void generateSequences(int[] nums, int seqTotal, int ones, int zeros) { // generates random sequences complying with the 1-0 ratio
		testSequences=new ArrayList<String>();
		for(int g=0; g<seqTotal; g++) {
			Random gen = new Random();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int i=0; i<ones; i++) {
				temp.add(gen.nextInt(ones+zeros));
			}
			String testString="";
			for(int i=0; i<ones+zeros; i++) {
				testString+="0";
			}
			for(int i=0; i<ones; i++) {
				int rand = gen.nextInt(ones+zeros);
				while(testString.charAt(rand)=='1'||(rand!=0 && testString.charAt(rand-1)=='1') || rand!=ones+zeros-1 && testString.charAt(rand+1)=='1') {
					rand++;
					if(rand==ones+zeros)
						rand=0;
				}
				//System.out.println(rand+", step: "+i);
				if(rand!=ones+zeros-1)
					testString=new String(testString.substring(0, rand)+"1"+testString.substring(rand+1));
				else
					testString = new String(testString.substring(0, rand)+"1");
			}
		//	System.out.println(testString);
			testSequences.add(testString);
		}
	}
		
	public ArrayList<Object> findBestMatch(){
		bestRatio=Double.MAX_VALUE;
		ArrayList<Object> best=new ArrayList<Object>();
		for(String a: testSequences) {
			double temp=runString(a);
			if(Math.abs(1-temp)<Math.abs(1-bestRatio)) {
				best=new ArrayList<Object>();
				best.add(temp);
				best.add(a);
				bestRatio=temp;
			}
		}
		return best;
	}
		
	public void runCollatz(int seed){
		while(seed!=1) {
			System.out.println(seed);
			if(seed%2==0)
				seed/=2;
			else
				seed=3*seed+1;
		}
		System.out.println(seed);
	}
		
	public static void main(String[] args) {
		SequenceSpace4 test = new SequenceSpace();
			for(int[] a: test.generateList()) {
				//System.out.println(a[0]+", "+a[1]);
				test.generateSequences(a, 10000, a[0], a[1]);  // 2nd param number of random sequences to generate per 1/0 count pair
				ArrayList<Object> bestMatch = test.findBestMatch();
				System.out.println("Ratio: "+bestMatch.get(0)+", "+bestMatch.get(1)+", n="+bestMatch.get(1).toString().length());
			}
	}
}
