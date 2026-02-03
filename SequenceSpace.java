import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
public class SequenceSpace {
	//  Original sequence generator filtered all allowable sequences by closest to 1/1 ratio in 5 different lengths and then manually constructed
	//  a tree composed from 5 sets of offsetting sequence lists connected end to end.
	
	//  This program is an alternate sequence generator starting with the ratio of '1' and '0' permutations: .63 < '0's/'1's < .64  or ~61% 0s
	//  based on previous results and then randomly composes sequences adhering to that ratio, narrowing the search space and allowing targeted generation
	//  and comparison of many sequences that are more likely close to 1/1 ratio.  Can be directly crawled or used to find separate offsetting ratios.
	public ArrayList<String> testSequences;
	private double bestRatio;
	private double testBase;
	public SequenceSpace() {
		testSequences=new ArrayList<String>();
		bestRatio=Double.MAX_VALUE;
		testBase=1000000000.0;
	}
	public double runString(String str) {
		double start = testBase;
		double seed = start;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i)=='1')
				seed=(3*seed)+1;
			else
				seed/=2;
		}
		return seed/start;
	}
	
	public ArrayList<int[]> generateList(int oneLimit, int zeroLimit, boolean includeAll) {  // generates lists of {ones, zeros} pairs within a specific ones/zeros ratio value
			ArrayList<int[]> list = new ArrayList<int[]>();
			if(includeAll) {
				for(int i=0; i<=oneLimit; i++) {   // across all values under oneLimit and zeroLimit
					for(int j=0;j<=zeroLimit;j++) { // --------
						double a = (1.0*i)/(j*1.0);
						if(a>.63 && a<.64) {  // ratio of 1 permutation to 0 permutation for current close matches
							int[] arr = {i,j};
							list.add(arr);
						}
					}
				}
			}
			else {
				int[] arr = {oneLimit, zeroLimit};
				list.add(arr);
			}			
		//	for(int[] b: list)  
				//System.out.println(b[0]+", "+b[1]);
			return list;
	}
	
	public void generateSequences(int seqTotal, int ones, int zeros) { // generates random sequences complying with the 1-0 ratio
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
			if(testString.charAt(0)=='1'&& testString.charAt(testString.length()-1)=='1') {  // prevent 1s at both first and last permutation
				int temp2=ones+zeros-2;
				while(temp2>0&&(testString.charAt(temp2)=='1'||(testString.charAt(temp2-1)=='1') || testString.charAt(temp2+1)=='1')) {
					temp2--;
				}
				testString = new String(testString.substring(0, temp2)+"1"+testString.substring(temp2+1, testString.length()-1));
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

	public void sequenceMatcher(int oneLimit, int zeroLimit, boolean allPairs, int sequenceSet, double base) {
		testBase=base;
		ArrayList<ArrayList<Object>> ratioSet = new ArrayList<ArrayList<Object>>();
		for(int[] a: generateList(oneLimit, zeroLimit, allPairs)) {
			//System.out.println(a[0]+", "+a[1]);
			generateSequences(sequenceSet, a[0], a[1]);  // sequenceSet is number of random sequences to generate per 1/0 count pair
			ArrayList<Object> bestMatch = findBestMatch();
			ratioSet.add(bestMatch);
			System.out.println("Ratio: "+bestMatch.get(0)+", "+bestMatch.get(1)+", n="+bestMatch.get(1).toString().length());
		}
		double currentBestRatio = Double.MAX_VALUE;
		int bestA=-1, bestB=-1;
		boolean ab=true;
		for(int i=0; i<ratioSet.size(); i++) {  
			for(int j=i+1;j<ratioSet.size();j++) {
				double rt = runString(""+ratioSet.get(i).get(1)+ratioSet.get(j).get(1));
				if(Math.abs(1-rt)<Math.abs(1-currentBestRatio)) {
					ab=true;
					currentBestRatio=rt;
					bestA=i;
					bestB=j;
				}
				rt = runString(""+ratioSet.get(j).get(1)+ratioSet.get(i).get(1));
				if(Math.abs(1-rt)<Math.abs(1-currentBestRatio)) {
					ab=false;
					currentBestRatio=rt;
					bestA=i;
					bestB=j;
				}
			}
		}
		System.out.println();
		if(ratioSet.size()!=1){
			System.out.print("Best Ratio: "+currentBestRatio);
			if(ab)
				System.out.println(", "+ratioSet.get(bestA).get(1)+ratioSet.get(bestB).get(1)+", n="+((Integer)(ratioSet.get(bestA).get(1).toString().length()+ratioSet.get(bestB).get(1).toString().length())));
			else
				System.out.println(", "+ratioSet.get(bestB).get(1)+ratioSet.get(bestA).get(1)+", n="+((Integer)(ratioSet.get(bestB).get(1).toString().length()+ratioSet.get(bestA).get(1).toString().length())));
			
			System.out.println("Ratio: "+ratioSet.get(bestA).get(0)+", "+ratioSet.get(bestA).get(1)+", n="+ratioSet.get(bestA).get(1).toString().length());
			System.out.println("Ratio: "+ratioSet.get(bestB).get(0)+", "+ratioSet.get(bestB).get(1)+", n="+ratioSet.get(bestB).get(1).toString().length());
		}
	}

	public String reorderStrings(int iterations, double base, String x1){ // iteratively moves a '1' in an existing sequence to find closest ratio within sequence length
		testBase=base;
		String x = x1;
		for(int h=0 ; h<iterations; h++) {
			ArrayList<Integer> ones = new ArrayList<Integer>();
			String bestShiftString = "";
			for(int i=0; i<x.length(); i++) {
				if(x.charAt(i)=='1')
					ones.add(i);
			}
			double[] bestShift = new double[3];
			bestShift[2]=Double.MAX_VALUE;
			
			for(int z: ones) {
				ArrayList<Integer> moveable = new ArrayList<Integer>();
				if(x.charAt(0)=='0'&& (x.charAt(1)=='0'||z==1))
					moveable.add(0);
				for(int i=1; i<x.length()-1; i++) {
					if((x.charAt(i-1)=='0'||i-1==z) && x.charAt(i)=='0' && (x.charAt(i+1)=='0'||i+1==z))
						moveable.add(i);
				}
				if((x.charAt(x.length()-2)=='0'||z==x.length()-2)&& x.charAt(x.length()-1)=='0')
					moveable.add(x.length()-1);
				

				for(int y: moveable) {
					String temp=x;
					if(y==0) {
						temp="1"+temp.substring(1);
					}
					else
						if(y==temp.length()-1)
							temp=temp.substring(0, temp.length()-2)+"1";
						else
							temp=temp.substring(0, y)+"1"+temp.substring(y+1);
					if(z==0) {
						temp="0"+temp.substring(1);
					}
					else
						if(z==temp.length()-1)
							temp=temp.substring(0, temp.length()-1)+"0";
						else {
							temp=temp.substring(0, z)+"0"+temp.substring(z+1);
						}
					
					
					if(bestShift[0]==1.0&&bestShift[1]==0.0) {
						moveable.set(0, moveable.get(moveable.size()-1));
					}
					
					double ratio=runString(temp);
					if(Math.abs(1-ratio)<Math.abs(1-bestShift[2])) {
						bestShift[0]=z;
						bestShift[1]=y;
						bestShift[2]=ratio;
						bestShiftString=temp;
						//System.out.println("begin: "+z+", end: "+y+", ratio: "+ratio);
					}
				}
			}
		//	System.out.println("Best -- begin: "+bestShift[0]+", end: "+bestShift[1]+", ratio: "+bestShift[2]);  
		//	System.out.println("old: "+x);
		//	System.out.println("new: "+bestShiftString);
			x=bestShiftString;
		}
		System.out.println();
		return x;
	}

	public String lowestSequence(int ones, int zeros) {
		String a = "";
		while(ones>0) {
			a+="10";
			ones--;
			zeros--;
		}
		while(zeros>0) {
			a+="0";
			zeros--;
		}
		return a;
	}
	
	public String highestSequence(int ones, int zeros) {
		String a = "";
		while(ones>0) {
			a="10"+a;
			ones--;
			zeros--;
		}
		while(zeros>0) {
			a="0"+a;
			zeros--;
		}
		return a;
	}
	
	
	public void overUnderReorderSet(int ones, int zeros, int saveSetSize) {  // generates pairs where the highest/lowest string ratios are over and under 1.0 then applies reorder string and prints top X results
		ArrayList<int[]> testPairs = new ArrayList<int[]>();
		ArrayList<ArrayList<Object>> closestSet = new ArrayList<ArrayList<Object>>(); 
		ArrayList<int[]> pairs = generateList(ones, zeros, true);
		for(int[] pair: pairs) {
			String strHigh = highestSequence(pair[0], pair[1]);
			String strLow = lowestSequence(pair[0], pair[1]);
			if(runString(strLow)<1 && runString(strHigh)>=1) {
				testPairs.add(pair);
			}
		//	System.out.println(""+(pair[0]+pair[1])+": "+runString(str)+", "+str);
		}
		System.out.println(testPairs.size()+" Test Pairs");
		for(int x=0; x<testPairs.size(); x++) {
			System.out.print('*');
		}
		System.out.println();
		for(int[] testPair: testPairs) {
			ArrayList<Object> dataPair = new ArrayList<Object>();
			String testString = reorderStrings(120, Math.pow(10.0,  24), highestSequence(testPair[0], testPair[1]));
			double testRatio = runString(testString);
			dataPair.add(testRatio);
			dataPair.add(testString);
			if(closestSet.size()<saveSetSize) {
				closestSet.add(dataPair);
			}
			else {
				for(int x=closestSet.size()-1; x>=0; x--) {
					if(Math.abs(((Double) dataPair.get(0))-1.0) < Math.abs((Double) closestSet.get(x).get(0))-1.0){
						if(x<closestSet.size()-1) {
							closestSet.set(x+1, closestSet.get(x));
						}
						closestSet.set(x,  dataPair);
					}
				}
			}

			System.out.print('*');

		}
		System.out.println();
		for(ArrayList<Object> closePair: closestSet) {
			System.out.println("Ratio: "+closePair.get(0));
			System.out.println("String: "+closePair.get(1));
			System.out.println();
		}
	}
	

	public void scaleRunString(String base) {  // run a sequence string from a range of starting values in factors of 10
		for(double i=100; i<100000000000000000000000000.0;i*=10) {
			testBase=i;
			System.out.println("Base: "+i+", ratio: "+runString(base));
		}
	}
		
	public static void main(String[] args) {
		SequenceSpace test = new SequenceSpace();
		test.overUnderReorderSet(500, 500, 5);  // produced a 0.9999999999999996 Ratio String with 10^24 base
		//test.scaleRunString("0010000000000000000000001000100101001010001010010101010101010101001010100101010101010100101010101010101010101010101010100000100101001010010101001010101010101001010101010100101010101");
		test.sequenceMatcher(100, 120, true, 10000, 1000000000.0); // (max 1s, max 0s, include all value pairs, sequences per section, base number to run collatz sequences)  // (306, 485)
	//	String testString = "010101010010101010101010101010101010101010101010000010101001000000100000101001010000000001000010000010000001010100101010101010101010101010010010001001000010100101001001001001001010101010100101000001010100010100100000000100101010010101000101010000000010101010100101001010101010101010101010101001010101010000100010100101001010100101010101010101010101010101001010100101010101010101010101010101010100101001000001001010010101010101010100000001001010101010100101010101001010101001011010100000000000001000001001010100001001010101010010101010100101010101010101010010100100100010100100101010101010101010101010100000001000001010101001001010000101010101000000101010101000000010101000010100101000010100101010001010010010100001001010010101010101010101010010101010100101010101010101010000101010100000101000000010000100101010101010101010010101010101001000010101001000000100000001010101010101010010101010101010101000100101010101010101010101010100100101010100101010101001001010101000001001001001010010101010101001010100100000100101010101010101010001010000101010100101001010100101010101010101010010100101010100000000010100000010010101001001010010010101010101000100000101001010000101010101010100010001000101010101010100000000100101001010101010000101001001010101001010010101000000101010101001001010101010101010101010100010000010010010100101010010101001001010100101010101010101010101010101010101010101010101010010100101001010101001010010101010101010010010100101010101";
	//	System.out.println(test.reorderStrings(100, Math.pow(10.0, 24), testString)); // (Number of reorders, testBase, sequence string)
	}
}
