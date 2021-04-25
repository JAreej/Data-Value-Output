
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Areej {

	public static void main(String[] args) {
		
		try {
			Scanner scanner = new Scanner(new File("/Users/areejjumaa/Documents/DATA_2021-03-31_2145.csv"));
			Scanner scanner2 = new Scanner(new File("/Users/areejjumaa/Documents/DataDictionary_2021-03-31.csv"));
			Scanner scanner3 = new Scanner(new File("/Users/areejjumaa/Documents/ihCCOntology_Excerpt.csv"));
			
			 scanner.useDelimiter(",");
			 scanner2.useDelimiter(",");
			 scanner3.useDelimiter(",");
			 
			 while (scanner.hasNext()) 
		        {
		            System.out.print(scanner.next() + ",");
		        }
			 scanner.close();
			 
			 while (scanner2.hasNext()) 
		        {
		            System.out.print(scanner2.next() + ",");
		        }
			 scanner2.close();
		
			 
			 while (scanner3.hasNext()) 
		        {
		            System.out.print(scanner3.next() + ",");
		        }
			 scanner3.close();
		
		
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
		
		//String line = br.readLine();
		
		//Scanner scanner = new Scanner(new File("/Data-Value-Output/src/main/java/DataDictionary_2021-03-31.csv"));
		
		

	}

}
