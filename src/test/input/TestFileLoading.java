package test.input;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;

import code.IOHandler;


public class TestFileLoading {
	
	private IIteratingChemObjectReader<IAtomContainer> reader;
	@Before
	public void setUp() throws Exception {
		IOHandler handler = new IOHandler();
		reader = handler.getIteratorForFile("./pubchem_files/aspirin.sdf");
	}

	@After
	public void tearDown() throws Exception {
		reader = null;
	}

	@Test
	public void SDFfileReturnsSomething() throws FileNotFoundException {
		assert(reader != null);
	}
	@Test
	public void SDFfileReturnsCorrectReader() {
		assert(reader instanceof IteratingSDFReader);
	}

}
