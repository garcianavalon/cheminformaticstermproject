package test.SAScore;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Polymer;
import org.openscience.cdk.interfaces.IAtomContainer;

import code.SAScoreCalc;

public class SizePenaltyTester {
	
	private SAScoreCalc calculator;
	@Before
	public void setUp() throws Exception {
		IAtomContainer molecule= new Polymer();
		molecule.addAtom(new Atom());
		molecule.addAtom(new Atom());
		calculator = new SAScoreCalc(molecule);
	}

	@After
	public void tearDown() throws Exception {
		calculator = null;
	}

	@Test
	public void checkResult() {
		double res = calculator.calcSizePenalty();
		assertEquals(0.006943,res,0.0001);
	}

}
