package code;

import java.util.ArrayList;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

public class Main {


	public static void main(String[] args) throws CDKException {
		InputHandler handler = new InputHandler();
		SmilesParser parser = new SmilesParser(
				DefaultChemObjectBuilder.getInstance());
		IAtomContainer aspirin = parser
				.parseSmiles("cc");
		ECFFragmenter fragments = new ECFFragmenter();
		fragments.generateFragments(aspirin);

		System.out.println((fragments.getFragmentsAsSMILES()).size());
		int cnt = 0;
		for (ECFFragment frag : fragments
				.getFragmentsAsSMILESSortedByFrequency()) {

			ArrayList<ECFFragment> myFrags = new ArrayList<ECFFragment>();
			myFrags.add(frag);
			System.out.println("Contribution of frag " + frag.getKey() + " " + FragmentContributionsCalc.getStoredContributions(myFrags));
			cnt += frag.getCount();
			System.out.println(frag.getKey() + " " + frag.getCount() + "x");
		}
		
		System.out.println(cnt);
		SAScoreCalc calcScore = new SAScoreCalc(aspirin);
		
		System.out.println("hard Mol 7.5820	7.33: " + calcScore.calculateScore(parser.parseSmiles("CCc1c(C)c2cc5nc(nc4[nH]c(cc3nc(cc1[nH]2)C(=O)C3(C)CC)c(CCC(=O)OC)c4C)C(C)(O)C5(O)CCC(=O)OC")));
		System.out.println("Hard molecule Score:" + calcScore.calculateScore(parser.parseSmiles("COC4C=C(C)CC(C=CC=CC#CC1CC1Cl)OC(=O)CC3(O)CC(OC2OC(C)C(O)C(C)(O)C2OC)C(C)C(O3)C4C")));
		System.out.println("Easy molecule Score:" + calcScore.calculateScore(parser.parseSmiles("COC(=O)c1ccccc1NC(=O)CC(c2ccccc2)c3ccccc3")));
		System.out.println("Another Easy molecule Score:" + calcScore.calculateScore(parser.parseSmiles("CC5CC(C)C(O)(CC4CC3OC2(CCC1(OC(C=CCCC(O)=O)CC=C1)O2)C(C)CC3O4)OC5C(Br)=C")));

	}

}

