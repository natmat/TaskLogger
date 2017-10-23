package tasklogger;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

public class TaskLoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetExcelFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskList() {
		ArrayList<String> tl = TaskLoader.getTaskList();
		assertTrue("TL default", (tl.size() == 1));
		assertTrue("Tl[0]",  tl.get(0).equals(TaskLoader.getDefaultTaskName()));
	}

	@Test
	public void testGetDefaultTaskName() {
		assertEquals("Def task true",  true, TaskLoader.getDefaultTaskName().equals("[Enter new task info/code]"));
		assertNotEquals("Def string empty", true, TaskLoader.getDefaultTaskName().length() == 0);
	}
}


