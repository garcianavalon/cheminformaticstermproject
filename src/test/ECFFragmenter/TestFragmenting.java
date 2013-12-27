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
	ArrayList<AbstractMap.SimpleEntry<String, Integer>> countResults;
	SmilesGenerator sg;
	ECFFragmenter fragmenter;
	InputHandler handler;

	@Before
	public void setUp() throws Throwable {
		sg = new SmilesGenerator();
		fragmenter = new ECFFragmenter();
		handler = new InputHandler();
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
		// Prepare for testCountGeneration
		ArrayList<String> testList = new ArrayList<String>();
		for (SimpleEntry<String, Integer> se : expectedResults.get(0)) {
			for (int i = 0; i < se.getValue(); i++) {
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
	public void assertExpectedResultsSubsetOfResults() {
		for (int i = 0; i < testSMILES.length; i++)
			for (SimpleEntry<String, Integer> se : expectedResults.get(i))
				junitx.framework.ListAssert.assertContains(
						"Did not find Object :" + se + " in list " + i,
						results.get(i), se);
	}

	@Test
	public void assertResultsSubsetOfExpectedResults() {
		for (int i = 0; i < testSMILES.length; i++)
			for (SimpleEntry<String, Integer> se : results.get(i))
				junitx.framework.ListAssert.assertContains(
						"Did not find Object :" + se + " in list " + i,
						expectedResults.get(i), se);
	}

	@Test
	public void testCountGeneration() {
		for (SimpleEntry<String, Integer> se : expectedResults.get(0))
			junitx.framework.ListAssert.assertContains(countResults, se);
	}

	private String canonicalize(String smiles) {
		return sg.createSMILES(handler.readSmiles(smiles));
	}

}

