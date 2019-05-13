package lsq.util;

import java.math.BigDecimal;

import com.lsq.util.CommUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    public static void main(String[] args) {
    	
    	String str = "1000";
    	String str2 = "1001";
    	
    	System.out.println(CommUtil.equals("1", "1"));
    	System.out.println(CommUtil.equals("1", "2"));
    	System.out.println(CommUtil.compare(str2, str, false, true));
    	System.out.println(CommUtil.compare(str, str, false, true));
	}
}
