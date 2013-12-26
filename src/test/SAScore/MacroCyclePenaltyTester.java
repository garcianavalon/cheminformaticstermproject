package test.SAScore;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import code.SAScoreCalc;

public class MacroCyclePenaltyTester {

	private SAScoreCalc calculator;
	@Before
	public void setUp() throws Exception {
		SmilesParser   sp  = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IAtomContainer molecule  = sp.parseSmiles("c1cccccccccc1");
		calculator = new SAScoreCalc(molecule);
	}

	@After
	public void tearDown() throws Exception {
		calculator = null;
	}

	@Test
	public void checkResult() throws CDKException {
		double res = calculator.calcMacroCyclePenalty();
		assertEquals(0.30103,res,0.0001);
	}
}
