package HuffmanPackage;
import java.util.Scanner;
public class MainClass {
	public static void main(String[] args) {
		try {
			Compression c=new Compression();
			c.GUI();
		} catch(Exception e) {
			System.out.println("Error to perform this operation");
		}
	}
}
