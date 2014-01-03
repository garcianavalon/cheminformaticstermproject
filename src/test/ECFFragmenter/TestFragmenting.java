package test.ECFFragmenter;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesGenerator;

import code.ECFFragment;
import code.ECFFragmenter;
import code.InputHandler;

public class TestFragmenting {

	String[] testSMILES = { "ccc" };
	ArrayList<ArrayList<ECFFragment>> expectedResults;
	ArrayList<ArrayList<ECFFragment>> results;
	ArrayList<ArrayList<String>> expectedResultSets;
	ArrayList<ArrayList<String>> resultSets;
	ArrayList<ECFFragment> countResults;
	SmilesGenerator sg;
	ECFFragmenter fragmenter;
	InputHandler handler;

	@Before
	public void setUp() throws Throwable {
		sg = new SmilesGenerator();
		fragmenter = new ECFFragmenter();
		handler = new InputHandler();
		this.results = new ArrayList<ArrayList<ECFFragment>>();
		this.expectedResults = new ArrayList<ArrayList<ECFFragment>>();
		for (int i = 0; i < testSMILES.length; i++) {
			this.expectedResults
					.add(new ArrayList<ECFFragment>());
			fragmenter.generateFragments(handler.readSmiles(testSMILES[i]));
			this.results.add(fragmenter.getFragmentsAsSMILES());
		}

		this.expectedResults.get(0).add(
				new ECFFragment(2, canonicalize("C[Y]")));
		this.expectedResults.get(0).add(
				new ECFFragment(1, canonicalize("[Y]C[Y]")));
		this.expectedResults.get(0).add(
				new ECFFragment(2, canonicalize("CC[Y]")));
		expectedResultSets = getSet(expectedResults);
		resultSets = getSet(results);
		// Prepare for testCountGeneration
		ArrayList<String> testList = new ArrayList<String>();
		for (ECFFragment se : expectedResults.get(0)) {
			for (int i = 0; i < se.getCount(); i++) {
				testList.add(se.getKey());
			}
		}
		junitx.util.PrivateAccessor.setField(fragmenter, "fragmentSMILES",
				testList);
		junitx.util.PrivateAccessor.invoke(fragmenter, "generateFragmentCount",
				new Class[0], new Object[0]);
		this.countResults = fragmenter.getFragmentsAsSMILES();
	}

	private ArrayList<ArrayList<String>> getSet(
			ArrayList<ArrayList<ECFFragment>> setOfBags) {
		ArrayList<ArrayList<String>> setOfSets = new ArrayList<ArrayList<String>>();
		for (ArrayList<ECFFragment> bag : setOfBags) {
			ArrayList<String> set = new ArrayList<String>();
			setOfSets.add(set);
			for (ECFFragment fragment : bag) {
				if (!set.contains(fragment.getKey()))
					set.add(fragment.getKey());
			}
		}
		return setOfSets;
	}

	@Test
	public void assertExpectedResultSetsSubsetOfResultSets() {
		for (int i = 0; i < testSMILES.length; i++)
			for (String s : this.expectedResultSets.get(i))
				junitx.framework.ListAssert
						.assertContains(resultSets.get(i), s);
	}

	@Test
	public void assertResultSetsSubsetOfExpectedResultSets() {
		for (int i = 0; i < testSMILES.length; i++)
			for (String s : this.resultSets.get(i))
				junitx.framework.ListAssert.assertContains(
						expectedResultSets.get(i), s);
	}

	@Test
	public void assertExpectedResultsSubsetOfResults() {
		for (int i = 0; i < testSMILES.length; i++)
			for (ECFFragment frag : expectedResults.get(i))
				junitx.framework.ListAssert.assertContains(
						"Did not find Object :" + frag + " in list " + i,
						results.get(i), frag);
	}

	@Test
	public void assertResultsSubsetOfExpectedResults() {
		for (int i = 0; i < testSMILES.length; i++)
			for (ECFFragment frag : results.get(i))
				junitx.framework.ListAssert.assertContains(
						"Did not find Object :" + frag + " in list " + i,
						expectedResults.get(i), frag);
	}

	@Test
	public void testCountGeneration() {
		for (ECFFragment frag : expectedResults.get(0))
			junitx.framework.ListAssert.assertContains(countResults, frag);
	}

	private String canonicalize(String smiles) {
		try {
			return sg.create(handler.readSmiles(smiles));
		} catch (CDKException e) {
			System.err.println("CDKException occured for fragment:\n" + smiles);
			System.err.println("Message:");
			System.err.println(e.getMessage());
			System.err.println("Stacktrace:");
			e.printStackTrace();
			return null;
		}
	}

}

