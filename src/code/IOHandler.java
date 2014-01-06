package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.smiles.SmilesParser;

public class IOHandler {

	public IAtomContainer readSmiles(String smiles) {
		SmilesParser parser = new SmilesParser(
				DefaultChemObjectBuilder.getInstance());
		try {
			return parser.parseSmiles(smiles);
		} catch (InvalidSmilesException e) {
			System.err.println("Invalid SMILES Format: " + smiles + "\n"
					+ e.getMessage());
			return null;
		}
	}

	public IIteratingChemObjectReader<IAtomContainer> getIteratorForFile(
			String filename) throws FileNotFoundException {

		File file = new File(filename);
		// get the extension
		int i = filename.lastIndexOf('.');
		String extension = "";
		if (i > 0) {
			extension = filename.substring(i + 1);
		}
		// create reader
		IIteratingChemObjectReader<IAtomContainer> iterativeReader = null;

		if (extension.equalsIgnoreCase("sdf")) {
			iterativeReader = new IteratingSDFReader(new FileInputStream(file),
					DefaultChemObjectBuilder.getInstance());
		} else if (extension.equalsIgnoreCase("smiles")
				|| extension.equalsIgnoreCase("smi")) {
			iterativeReader = new IteratingSMILESReader(new FileInputStream(
					file), DefaultChemObjectBuilder.getInstance());
		}
		return iterativeReader;
	}

	public HashMap<String, Double> loadStoredFragmentContributions(boolean useAromaticity) throws IOException, ClassNotFoundException {
		String extra = useAromaticity ? "aromatic" : "standard";
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(extra+"map.ser"));
		HashMap<String, Double> fragmentContributions = (HashMap<String, Double>) ois.readObject();
		ois.close();
        return fragmentContributions;
	}
	public void serializeObject(Object o,String filename) throws FileNotFoundException, IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(o);
        oos.close();
	}
}