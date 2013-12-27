package test.ECFFragmenter;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.smiles.SmilesGenerator;

import code.ECFFragmenter;
import code.InputHandler;

public class TestFragmenting {

	String[] testSMILES = { "ccc" };
	ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>> expectedResults;
	ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>> results;
	ArrayList<ArrayList<String>> expectedResultSets;
	ArrayList<ArrayList<String>> resultSets;

	@Before
	public void setUp() {
		ECFFragmenter fragmenter = new ECFFragmenter();
		InputHandler handler = new InputHandler();
		this.results = new ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>>();
		this.expectedResults = new ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>>();
		for (int i = 0; i < testSMILES.length; i++) {
			this.expectedResults
					.add(new ArrayList<AbstractMap.SimpleEntry<String, Integer>>());
			fragmenter.generateFragments(handler.readSmiles(testSMILES[i]));
			this.results.add(fragmenter.getFragmentsAsSMILES());
		}

		this.expectedResults.get(0).add(
				new SimpleEntry<String, Integer>(canonicalize("C[Y]"), 2));
		this.expectedResults.get(0).add(
				new SimpleEntry<String, Integer>(canonicalize("[Y]C[Y]"), 1));
		this.expectedResults.get(0).add(
				new SimpleEntry<String, Integer>(canonicalize("CC[Y]"), 2));
		expectedResultSets = getSet(expectedResults);
		resultSets = getSet(results);
	}

	private ArrayList<ArrayList<String>> getSet(
			ArrayList<ArrayList<SimpleEntry<String, Integer>>> setOfBags) {
		ArrayList<ArrayList<String>> setOfSets = new ArrayList<ArrayList<String>>();
		for (ArrayList<SimpleEntry<String, Integer>> bag : setOfBags) {
			ArrayList<String> set = new ArrayList<String>();
			setOfSets.add(set);
			for (SimpleEntry<String, Integer> entry : bag) {
				if (!set.contains(entry.getKey()))
					set.add(entry.getKey());
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
	public void assertEquals() {
		for (ArrayList<AbstractMap.SimpleEntry<String, Integer>> res : results)
			junitx.framework.ListAssert.assertContains(expectedResults, res);
	}

	private String canonicalize(String smiles) {
		InputHandler handler = new InputHandler();
		SmilesGenerator sg = new SmilesGenerator();
		return sg.createSMILES(handler.readSmiles(smiles));

	}

}

