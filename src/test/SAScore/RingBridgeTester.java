package test.SAScore;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import code.SAScoreCalc;

public class RingBridgeTester {
	private SAScoreCalc calculator;
	IAtomContainer molecule;
	@Before
	public void setUp() throws Exception {
		SmilesParser   sp  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		molecule = sp.parseSmiles("C1CC2CCC1C2");
		calculator = new SAScoreCalc(molecule);
	}

	@After
	public void tearDown() throws Exception {
		calculator = null;
		molecule = null;
	}

	@Test
	public void checkResult() throws CDKException {
		int res = calculator.getNumberOfRingBridgeAtoms();
		assertEquals(1,res,0);
	}
	@Test 
	public void checkDistance() {
		IAtom from = molecule.getAtom(2);
		IAtom to = molecule.getAtom(4);
		int res = calculator.getShortestDistanceFromToIn(from, to, molecule);
		assertEquals(2,res,0);
	}
	@Test
	public void findMatchingBridgeHeadReturnsSomething() {
		HashMap<IAtom, List<Integer>> map = new HashMap<IAtom, List<Integer>>();
		List<Integer> testSet = new ArrayList<Integer>(3);
		testSet.add(2);
		List<Integer> badSet = new ArrayList<Integer>(3);
		badSet.add(1);
		IAtom one = new Atom();
		one.setID("Head1");
		map.put(one, testSet);
		IAtom two = new Atom();
		two.setID("BadOne");
		map.put(two, badSet);
		IAtom res= calculator.findMatchingBridgeHead(map,testSet);
		assert(res != null);
	}
	@Test
	public void findMatchingBridgeHeadReturnsCorrectOne() {
		HashMap<IAtom, List<Integer>> map = new HashMap<IAtom, List<Integer>>();
		List<Integer> testSet = new ArrayList<Integer>(3);
		testSet.add(2);
		List<Integer> badSet = new ArrayList<Integer>(3);
		badSet.add(1);
		IAtom two = new Atom();
		two.setID("BadOne");
		map.put(two, badSet);
		IAtom one = new Atom();
		one.setID("Head1");
		map.put(one, testSet);
		

		IAtom res= calculator.findMatchingBridgeHead(map,testSet);
		assert(res.equals(one));
	}
}
