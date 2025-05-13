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
		
	public static void main(String[] args) {
		int ttlSeq=100; // total sequences to save
		int maxNSeq=20; // max sequences per sequence length
		ArrayList<Double> allClosest = new ArrayList<Double>();
		double startingValue = 1000000000.0;
		ArrayList<String> allClosestSequence = new ArrayList<String>();
		for(int i=0;i<ttlSeq;i++) {
			allClosest.add(Double.MAX_VALUE);
			allClosestSequence.add("");
		}
		for(int n=5; n<32; n++) {
			CollatzSequenceGenerator test = new CollatzSequenceGenerator(startingValue, n);
			test.permutationList = test.generatePermutations(test.permutationList, test.permutations-1);
	//		System.out.println("Permutation list generated");
		/**	for(String str: test.permutationList) {
				System.out.println(str);
			}**/
			double closest = Double.MAX_VALUE;
			ArrayList<String> closestPermutations = new ArrayList<String>();
			ArrayList<Double> differences = new ArrayList<Double>();
			for(int i=0;i<maxNSeq;i++) {
				differences.add(Double.MAX_VALUE);
				closestPermutations.add("");
			}

				
			for(String a: test.permutationList){
			//	System.out.println("test string");
				double x=test.seed;
				for(int i=0; i<a.length(); i++) {
					if(a.charAt(i)=='0') {
						x/=2;
			//			System.out.println("0-> X: "+x);
					}
					else
					{
						x=(x*3)+1;
			//			System.out.println("1-> X: "+x);
					}
				}
					
			
				for(int j=0; j<maxNSeq; j++) {
					if(Math.abs(test.seed-x)<Math.abs(test.seed-differences.get(j))) {
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
					if(Math.abs(test.seed-dif)<Math.abs(test.seed-allClosest.get(l))) {
						allClosest.add(l, dif);
						allClosest.remove(ttlSeq);
						allClosestSequence.add(l, closestPermutations.get(differences.indexOf(dif)));
						allClosestSequence.remove(ttlSeq);
						break;
					}
				}
			}
		}
		for(String a: allClosestSequence) {
			System.out.println("Closest Permutation: "+a+", Ratio to starting value: "+((startingValue-/**Math.abs**/(startingValue-allClosest.get(allClosestSequence.indexOf(a))))/startingValue)+", n="+a.length());
		}
	}
}
