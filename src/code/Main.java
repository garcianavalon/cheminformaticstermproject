package code;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.smiles.SmilesParser;

public class Main {

	public static void main(String[] args) throws InvalidSmilesException {
		SmilesParser parser = new SmilesParser(
				DefaultChemObjectBuilder.getInstance());
		ECFFragmenter fragments = new ECFFragmenter();
		fragments.generateFragments(parser
				.parseSmiles("OC(=O)C1=C(C=CC=C1)OC(=O)C"));
		System.out.println((fragments.getFragmentsAsSMILES()).size());
		int cnt = 0;
		for (ECFFragment frag : fragments
				.getFragmentsAsSMILESSortedByFrequency()) {
			cnt += frag.getCount();
			System.out.println(frag.getKey() + " " + frag.getCount() + "x");
		}
		System.out.println(cnt);
	}

}
