import java.util.*;
public class CollatzClosestPermutationCombo {

	// Thinking of Collatz permutations as a sequence of ratio multiplications, the same sequence of permutations enacted on any two starting values(that results in the same
	// sequence of "odd or even" permutations) should have an end result that is a similar fraction of the original seed value.  The deviations being caused by the "+1" component.
	// In a closed Collatz loop the end result is 1/1 of the starting value, therefore when searching for looping Collatz values an alternative option to crawling starting values
	// is to crawl permutation combinations where the end value starting value fraction is closest to 1/1.  When testing these permutations you can either start with 1, being a substitution
	// for an arbitrary point on the number line and ignore the "+1" or you can pick out some reasonably high starting seed eg. 10,000 knowing  ahead of time that Collatz does not loop 
	// up to very high values and when running the permutations you simply ignore whether the sequence numbers are odd or even and follow a predetermined permutation sequence.  After
	// running some number of permutation sequences on your test value you then pick out the one that ends closest to the starting value and then try to pick out untested values
	// very high in the number line that will follow the same sequence given some structural similarity to an already tested value with the "closest to 1/1" permutation combination.
	public int permutations;
	public double seed;
	public ArrayList<String> permutationList;
	
	public CollatzClosestPermutationCombo(double a, int b) {
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
				permutationList.add(a+"1");
			}
			return generatePermutations(permutationList, x-1);
		}
		return list;
	}
	
	public static void main(String[] args) {
		double allClosest = Double.MAX_VALUE;
		double startingValue = 10000.0;
		String allClosestSequence = "";
		for(int n=26; n<27; n++) {
			CollatzClosestPermutationCombo test = new CollatzClosestPermutationCombo(startingValue, n);
			test.permutationList = test.generatePermutations(test.permutationList, test.permutations-1);
	//		System.out.println("Permutation list generated");
		/**	for(String str: test.permutationList) {
				System.out.println(str);
			}**/
			double closest = Double.MAX_VALUE;
			String closestPermutations = "";
			for(int i=0;i<test.permutations; i++) {
				for(String a: test.permutationList){
					double x=test.seed;
					for(int j=0; j<a.length(); j++) {
						if(a.charAt(j)=='0') {
							x/=2;
				//			System.out.println("0-> X: "+x);
						}
						else
						{
							x=(x*3)+1;
				//			System.out.println("1-> X: "+x);
						}
					}
			//		System.out.println(a+": "+x);
					if(Math.abs(test.seed-x)<Math.abs(test.seed-closest)) {
						closest = x;
						closestPermutations = a;
					}
				}
	
			}
		//	System.out.println("Closest Permutation: "+closestPermutations+", Ratio to starting value: "+((test.seed-(Math.abs(test.seed-closest)))/test.seed));
			if(Math.abs(test.seed-closest)<Math.abs(startingValue-allClosest)) {
				allClosest = closest;
				allClosestSequence = closestPermutations;
			}
		}
		System.out.println("Closest Permutation: "+allClosestSequence+", Ratio to starting value: "+((startingValue-(Math.abs(startingValue-allClosest)))/startingValue)+", n="+allClosestSequence.length());
	//	System.out.println("End value: "+allClosest);
	}
}
