package uint.test;

import org.junit.Test;

import com.filetool.main.Main;

public class JUintTest {
	@Test
	public void mainTest() {
		String[] args = new String[3];
		args[0] = "resource/case0/topo.csv";
		args[1] = "resource/case0/demand.csv";
		args[2] = "resource/case0/result.csv";
		Main.main(args);
	}

}
