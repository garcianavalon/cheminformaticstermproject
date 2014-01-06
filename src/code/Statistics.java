package code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.sun.tools.javac.util.Paths;

public class Statistics {

	public static void main(String args[]) throws IOException {
		createFragmentFrequencyHistogramAnd40MostCommonFragments("mostcommon.txt", "fragmentFrequency.jpeg");
	}
	public Statistics() {
		
	}
	
	public static void createFragmentFrequencyHistogramAnd40MostCommonFragments(String filename1, String filename2) throws IOException {
		File dir = Paths.get("statistics").toFile();
		if (!dir.exists())
			dir.mkdir();
		File file1 = dir.toPath().resolve(filename1).toFile();
		File file2 = dir.toPath().resolve(filename2).toFile();
		if (!file1.exists())
			file1.createNewFile();
		if (!file2.exists())
			file2.createNewFile();
		FileInputStream fis = null;
		HashMap<String, Integer> fragmentFrequency = null;
		try {
			fis = new FileInputStream("count.ser");
		} catch (Exception e) {
			System.err.println("File not found");
			System.out.println(e.getMessage());
		}
        ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fis);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
        try {
			fragmentFrequency = (HashMap<String, Integer>) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ArrayList<ECFFragment> fragments = new ArrayList<ECFFragment>();
        for (Map.Entry<String, Integer> entry : fragmentFrequency.entrySet()) {
        	fragments.add(new ECFFragment(entry.getValue(), entry.getKey()));
        }
        Collections.sort(fragments);
        int singletonCount = 0;
        ECFFragment currentFragment = fragments.get(fragments.size() -1);
        while (currentFragment.getCount() == 1) {
        	singletonCount++;
        	currentFragment = fragments.get(fragments.size() - 1 - singletonCount);
        }
        double singletonPercent = (100 * singletonCount / fragments.size() );
        FileWriter mostCommon = new FileWriter(file1);
        BufferedWriter writer = new BufferedWriter(mostCommon);
        writer.write(singletonPercent + "% Singletons" );
        for (int i = 0; i < 40; i++) {
        	writer.newLine();
        	writer.write(fragments.get(i).getKey());
        	}
        for (int i = 0; i < 40; i++) {
        	writer.newLine();
        	System.out.println(fragments.get(i).getCount() + " " + fragments.get(i).getKey());
        	writer.write(new Integer(fragments.get(i).getCount()).toString());
        	}
            writer.close();
        double[][] data = new double[2][fragments.size()];
        for (int i = 0; i < fragments.size() ; i++) {
        	data[0][i] = fragments.get(i).getCount();
        	data[1][i] = i+1;
        }
    
        System.out.println("creating plot " +  fragments.size());
        DefaultXYDataset dataSet = new DefaultXYDataset();
        dataSet.addSeries(new Integer(0), data);
        ValueAxis yAxis = new LogAxis("Logarithmic Frequency");
        ValueAxis xAxis = new NumberAxis("Fragment Number");
        XYItemRenderer renderer = new DefaultXYItemRenderer();
        dataSet.addSeries(new Integer(0), data);
        XYPlot plot = new XYPlot(dataSet, xAxis, yAxis, renderer);
        ChartUtilities.saveChartAsJPEG(file2, new JFreeChart(plot), 1024, 768);
	}
	

	
	public void createFragmentScoreHistogram(String filename) {
		
	}

}
