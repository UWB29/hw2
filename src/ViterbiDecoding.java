import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ViterbiDecoding {

	private final Map<String, TypeProb> WordProb = new HashMap<>();
	private final Map<String, Double> HMM = new HashMap<>();
	private final String toNoun = "-N";
	private final String toVerb = "-V";
	private final String toAdverb = "-A";
	private final String toEnd = "-E";
	private final String nl = "\n";
	private final char noun = 'N';
	private final char verb = 'V';
	private String[] tokens = new String[3];
	
	public ViterbiDecoding() {
		fillHMM();
		fillWordProb();
	}

	private void fillHMM() {
		HMM.put("S-N", 0.2);
		HMM.put("S-V", 0.3);
		HMM.put("N-N", 0.1);
		HMM.put("N-V", 0.3);
		HMM.put("V-V", 0.1);
		HMM.put("V-N", 0.4);
		HMM.put("N-A", 0.1);
		HMM.put("V-A", 0.4);
		HMM.put("A-E", 0.1);
	}
	
	private void fillWordProb() {
		WordProb.put("learning", new TypeProb());
		WordProb.put("changes", new TypeProb());
		WordProb.put("throughly", new TypeProb());
		
		WordProb.get("learning").addTP(toNoun, 0.001);
		WordProb.get("learning").addTP(toVerb, 0.003);
		WordProb.get("changes").addTP(toNoun, 0.003);
		WordProb.get("changes").addTP(toVerb, 0.004);
		WordProb.get("throughly").addTP(toAdverb, 0.002);
	}
	
	public static void main(String[] args) {
		ViterbiDecoding test = new ViterbiDecoding();
		
		System.out.println(test.calcViterbiMatrix("learning changes throughly"));
	}

	public String calcViterbiMatrix(String string) {
		double temp = 1;
		Scanner sc = new Scanner(string);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		while (sc.hasNext()) {
			tokens[i] = sc.next();
			i++;
		}
		
		sb.append(calcRoad('A', "", 3));
		//if (sc.hasNext()) {
		//	token = sc.next();
		//	if (sc.hasNext()) {
		//		sb = calcRoad('S', token, sc.nextLine(),temp);
		//	} else {
		//		sb = calcRoad('S', token, "",temp);
		//	}
		//}
		
		return sb.toString();
	}

	private Double calcRoad(char start, String way, int tokenNum) {
		StringBuilder sb = new StringBuilder();
		char myStart = start;
		//Scanner sc = new Scanner(rest);
		double t1 = 0.0, t2 = 0.0;
		TypeProb t3;
		
		
		if (tokenNum == 0) myStart = 'S';
		if (tokenNum != 3) {
			t3 = WordProb.get(tokens[tokenNum]);
		} else {
			t3 = WordProb.get(tokens[tokenNum - 1]);
		}
		switch (myStart) {
			case 'S':
				t1 = calcRoad(1, HMM.get(start + way), t3.getTP(way));
				break;
			case 'N':
				t1 = calcRoad(calcRoad(verb, toNoun, tokenNum -1), HMM.get(start + way), t3.getTP(way));
				t2 = calcRoad(calcRoad(noun, toNoun, tokenNum -1), HMM.get(start + way), t3.getTP(way));
				
				t1 = Math.max(t1, t2);
				break;
			case 'V':
				t1 = calcRoad(calcRoad(verb, toVerb, tokenNum -1), HMM.get(start + way), t3.getTP(way));
				t2 = calcRoad(calcRoad(noun, toVerb, tokenNum -1), HMM.get(start + way), t3.getTP(way));
				
				t1 = Math.max(t1, t2);
				break;
			case 'A':
				t1 = calcRoad(verb, toAdverb, tokenNum - 1);
				t2 = calcRoad(noun, toAdverb, tokenNum - 1);
				
				t1 = Math.max(t1, t2);
				
				t1 = t1 * HMM.get("A-E");
				break;
		}
		
		return t1;
	}
	
	private String endRoad(double from) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(nl);
		sb.append("Final = ");
		sb.append(from * HMM.get("A-E"));
		
		return sb.toString();
	}
	
	private double calcRoad(double x, double y, double z) {
		return x * y * z;
	}

}

class TypeProb {
	private final Map<String, Double> tp;
	
	public TypeProb() {
		tp = new HashMap<>();
	}
	
	public void addTP(String Type, double prob) {
		tp.put(Type, prob);
	}
	
	public double getTP(String Type) {
		return tp.get(Type);
	}
	
	public int getTyoeCount() {
		return tp.size();
	}
}