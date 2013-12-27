package test.ECFFragmenter;


import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import code.ECFFragmenter;
import code.InputHandler;

public class TestFragmenting {
	
	String[] testSMILES = {"ccc"};
	ArrayList<ArrayList<AbstractMap.SimpleEntry <String, Integer>>> expectedResults;
	ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>> results;
	
	
	@Before
	public void setUp() {
		ECFFragmenter fragmenter = new ECFFragmenter();
	    InputHandler handler = new InputHandler();
	    this.results = new ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>>();
	    this.expectedResults = new ArrayList<ArrayList<AbstractMap.SimpleEntry<String, Integer>>>();
		for (int i = 0; i<testSMILES.length ; i++) {
			this.expectedResults.add(new  ArrayList<AbstractMap.SimpleEntry<String, Integer>>());
			fragmenter.generateFragments(handler.readSmiles(testSMILES[i]));
			this.results.add(fragmenter.getFragmentsAsSMILES());
		}
		
		this.expectedResults.get(0).add(new SimpleEntry<String, Integer>("C[Y]", 2));
		this.expectedResults.get(0).add(new SimpleEntry<String, Integer>("[Y]C[Y]", 1));
		this.expectedResults.get(0).add(new SimpleEntry<String, Integer>("CC[Y]", 2));
	}
	@Test
	public void testSmiles() {
			for (ArrayList<AbstractMap.SimpleEntry<String, Integer>> res : results)
			junitx.framework.ListAssert.assertContains(expectedResults, res);
	}
		
}

