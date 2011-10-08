import org.apache.zookeeper.test.QuorumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import java.io.File;

// This test requires zookeeper 3.4.0 built from SVN trunk and setting
// "build.test.dir" system property
public class LBCORE224Test 
{
	private static final String BUILT_TEST_DIR_PROP_KEY = "build.test.dir";
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void before() 
	{
		File tempDir = tempFolder.newFolder("foo");
		System.setProperty(BUILT_TEST_DIR_PROP_KEY, tempDir.getAbsolutePath());
	}
	
	@After
	public void after() throws Exception 
	{
		System.clearProperty(BUILT_TEST_DIR_PROP_KEY);
	}

	@Test
	public void shouldNotThrowIllegalMonitorStateException () throws Exception
	{
		QuorumUtil qU = new QuorumUtil(1);
		try
		{
			for (int i = 0; i < 10; i++) 
			{
				qU.startQuorum();
				qU.shutdownAll();
				System.err.println("iteration "+i);
			}
		}
		finally
		{
			qU.tearDown();
		}
	}
	
	@Test
	public void justSomeStackTrace()
	{
		RuntimeException ex=new RuntimeException("Foo!");
		ex.printStackTrace();
	}
}

