import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class FastaSequence {
	
	private String header;
	private String sequence;
	
	public String getHeader() {
		return header;
	}
	
	public String getSequence() {
		return sequence;
	}
	
	public float getGCRatio() {
		String x = sequence;
		int gcCount = 0; 
		for (int i = 0; i < x.length();i++) 
		{
			char nucleotide = x.charAt(i);
			gcCount = (nucleotide=='C') ? gcCount + 1:gcCount;
			gcCount = (nucleotide=='G') ? gcCount + 1:gcCount;
		}
		//System.out.println("gcCount: " + gcCount);
		float ratio = (float) gcCount/x.length();
		//System.out.println("The GC ratio is " + ratio);
		return ratio;
	}
	
	
	public FastaSequence(String header, String sequence) {
		this.header = header;
		this.sequence = sequence;
	}
	
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception
	{
		String header = null;
		List<FastaSequence> bigList = new ArrayList<FastaSequence>();
		
		
		StringBuilder sequence = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
		for(String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
		{
			if (nextLine.startsWith(">") && header == null) 
			{
				header = nextLine.substring(1);
				continue;
			} else if (nextLine.startsWith(">") == false) {
				sequence.append(nextLine);
			} else if (nextLine.startsWith(">") == true && header != null) 
			{
				bigList.add(new FastaSequence(header,sequence.toString()));
				header = nextLine.substring(1);
				sequence.setLength(0);
			}
		}
		reader.close();
		bigList.add(new FastaSequence(header,sequence.toString()));
		
		return bigList;
	}
	
	public static void writeUnique(String inFile, String outFile) throws Exception {
		List<FastaSequence> fastaList = 
		FastaSequence.readFastaFile(inFile);
		
		HashMap<String,Integer> numberMap = new HashMap<String,Integer>();
		
		for( FastaSequence fs: fastaList) 
		{
			Integer count = numberMap.get(fs.getSequence());
			//System.out.println(count);
			if (count == null) 
			{
				count = 0;
			} 
			count++;
			numberMap.put(fs.getSequence(), count);
		}
		
		List<Map.Entry<String,Integer>> valueList = new ArrayList<>(numberMap.entrySet());
		
		Collections.sort(valueList, new Comparator<Map.Entry<String,Integer>>()
		{
			public int compare(Map.Entry<String,Integer> a, Map.Entry<String,Integer> b) 
			{
				return a.getValue()-b.getValue();
			}
		});
		
		/* This works!!!
		for (Map.Entry<String,Integer> x: valueList) {
			System.out.println(x.getKey() + "=" + x.getValue());
		}
		*/
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFile)));
		for (Map.Entry<String,Integer> x: valueList) {
			String header = ">" + x.getValue();
			String body = x.getKey();
			writer.write(header + "\n" + body + "\n");
		}
		writer.flush(); writer.close();
	}
	
	public static void main(String[] args) throws Exception {
		List<FastaSequence> fastaList = 
		FastaSequence.readFastaFile("C:\\UNC_Fall_2020\\BINF_Advanced_Programming\\Labs\\Lab_04\\lab4_test.fasta");

		for( FastaSequence fs : fastaList)
		{
			System.out.println(fs.getHeader());
			System.out.println(fs.getSequence());
			System.out.println(fs.getGCRatio());
		}
		
		FastaSequence.writeUnique("C:\\UNC_Fall_2020\\BINF_Advanced_Programming\\Labs\\Lab_04\\lab4_test.fasta", "outfile");
	}
}
