package telran.library;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LibraryAppl {
public static void main(String[] args) {
	ConfigurableApplicationContext ctx=
			SpringApplication.run(LibraryAppl.class, args);
	Scanner scanner=new Scanner(System.in);
	while(true) {
		System.out.println("Enter exit for the server shutdown");
		String line=scanner.nextLine();
		if(line.equals("exit"))
			break;
	}
	ctx.close();
	scanner.close();
}
}
