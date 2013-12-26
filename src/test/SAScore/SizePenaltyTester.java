package test.SAScore;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;

import code.SAScoreCalc;

public class SizePenaltyTester {
	
	private SAScoreCalc calculator;
	@Before
	public void setUp() throws Exception {
		IMolecule molecule= new Molecule();
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
