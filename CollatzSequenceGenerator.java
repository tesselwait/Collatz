import java.util.ArrayList;
public class CollatzSequenceGenerator {
	
	// Testing found numerous very similar decimal values for varying sequences of the same length giving more options for finding exact matches with real base sequences.
	//
	public double seed;
	public int permutations;
	public ArrayList<String> permutationList;
	
	public CollatzSequenceGenerator(double a, int b) {
		seed=a;
		permutations=b;
		permutationList = new ArrayList<String>();
		permutationList.add("0");
		permutationList.add("1");
	}
	
	public ArrayList<String> generatePermutations(ArrayList<String> list, int x) {  //recursively generate all binary strings of length x
		if(x>0) {
			permutationList = new ArrayList<String>();
			for(String a: list) {
				permutationList.add(a+"0");
				if(a.charAt(a.length()-1)!='1') //all odd permutations in Collatz are followed by at least one even permutation.  Filters consecutive '1'
					permutationList.add(a+"1");
			}
			return generatePermutations(permutationList, x-1);
		}
		return list;
	}
		
	public ArrayList<String> generateBestMatchPermutation(int total, int secTotal) {
		ArrayList<ArrayList<String>> subSequences=new ArrayList<ArrayList<String>>();
		for(int i=0;i<20;i++)
			subSequences.add(new ArrayList<String>());
		int ttlSeq=total; // total sequences to save
		int maxNSeq=secTotal; // max sequences per sequence length
		ArrayList<Double> allClosest = new ArrayList<Double>();
		ArrayList<String> allClosestSequence = new ArrayList<String>();
		for(int i=0;i<ttlSeq;i++) {
			allClosest.add(Double.MAX_VALUE);
			allClosestSequence.add("");
		}
		ArrayList<String> closestPermutations = new ArrayList<String>();
		ArrayList<Double> differences = new ArrayList<Double>();
		for(int n=13; n<32; n++) {
			setPermutations(n);
			resetPermutationList();
			permutationList = generatePermutations(permutationList, permutations-1);
			double closest = Double.MAX_VALUE;
			for(int i=0;i<maxNSeq;i++) {
				differences.add(Double.MAX_VALUE);
				closestPermutations.add("");
			}
			for(String a: permutationList){
				double x=seed;
				for(int i=0; i<a.length(); i++) {
				//	System.out.printf("%.0f\n",x);
					if(a.charAt(i)=='0') {
						x/=2;
			//			System.out.println("0-> X: "+x);
					}
					else
					{
						x=x*3+1;
			//			System.out.println("1-> X: "+x);
					}
				}		
				for(int j=0; j<maxNSeq; j++) {
					if(Math.abs(seed-x)<Math.abs(seed-differences.get(j))) {
						differences.add(j, x); 			
						differences.remove(maxNSeq);
						closestPermutations.add(j, a); 
						closestPermutations.remove(maxNSeq);
						break;
					}
				}
			}
			for(double dif: differences) {
				for(int l=0; l<ttlSeq; l++) {
					if(Math.abs(seed-dif)<Math.abs(seed-allClosest.get(l))) {
						allClosest.add(l, dif);
						allClosest.remove(ttlSeq);
						allClosestSequence.add(l, closestPermutations.get(differences.indexOf(dif)));
						allClosestSequence.remove(ttlSeq);
						break;
					}
				}
			}
			
		}
	//	int len = allClosestSequence.get(0).length(); // segmented version setup
	//	int stringSet=0;                             //segmented version setup
		for(String a: allClosestSequence) {
			if(a.length()==31)
				subSequences.get(0).add(a);
			if(a.length()==13)
				subSequences.get(1).add(a);
			
		//	System.out.println("Closest Permutation: "+a+", Ratio to starting value: "+((seed-/**Math.abs**/(seed-allClosest.get(allClosestSequence.indexOf(a))))/seed)+", n="+a.length());
		}
		// **** hand allocated section ordering - generalize later with a "closest ratio finder"
		ArrayList<ArrayList<String>> orderedSubSequences = new ArrayList<ArrayList<String>>();
		for(int i=0;i<5;i++)
			orderedSubSequences.add(new ArrayList<String>());
		for(String a: subSequences.get(0)) {
			orderedSubSequences.get(0).add(a);
			orderedSubSequences.get(1).add(a);
			orderedSubSequences.get(3).add(a);
			orderedSubSequences.get(4).add(a);
		}
		for(String a: subSequences.get(1))
			orderedSubSequences.get(2).add(a);
		for(int i=0; i<orderedSubSequences.get(2).size(); i++) {
			for(int j=i+1;j<orderedSubSequences.get(2).size(); j++) {
				if(orderedSubSequences.get(2).get(i)==orderedSubSequences.get(2).get(j)) {
					orderedSubSequences.get(2).remove(j);
					j--;
				}
			}
		}
		//return orderedSubSequences;  //  returns an ArrayList composed of ArrayLists of sequence sections.  Will work faster with segmented crawler than sending list of all possible composed sequences.
		ArrayList<String> allSequences = new ArrayList<String>();
		for(String a: orderedSubSequences.get(0))
			for(String b: orderedSubSequences.get(1))
				for(String c: orderedSubSequences.get(2))
					for(String d: orderedSubSequences.get(3))
						for(String e: orderedSubSequences.get(4))
							allSequences.add(a+new StringBuilder(b).reverse().toString()+c+d+e);  // reversed to match hand constucted sequence format
		System.out.println("Sequences: "+allSequences.size());
		return allSequences;
	}
	
	public void setPermutations(int a) {
		permutations=a;
	}
	
	public void resetPermutationList() {
		permutationList = new ArrayList<String>();
		permutationList.add("0");
		permutationList.add("1");
	}
}
