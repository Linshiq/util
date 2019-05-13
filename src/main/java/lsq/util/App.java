package lsq.util;

import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lsq.util.CommUtil;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private String attributes ;
	private String attributes2;
	
	
	
    public String getAttributes() {
		return attributes;
	}



	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}



	public String getAttributes2() {
		return attributes2;
	}



	public void setAttributes2(String attributes2) {
		this.attributes2 = attributes2;
	}



	public static void main( String[] args )
    {
        System.out.println( "Hello:World!" );
//        String s = JSON.toJSONString("{\"name\":\"lsq\",\"sex\":\"F\",\"phones\":[{\"operator\":\"我家一号\",\"phoneNum\":\"123\"},{\"operator\":\"我家二号\"}]}");
//        Map o = JSON.parseObject(s,Map.class);
//        
//        System.out.println("科学计数法数字");
//        double num1 = 1.70385648E7;
//        System.out.println(num1);
//        BigDecimal bd1 = new BigDecimal(num1);
//        System.out.println(bd1.toPlainString());
//        System.out.println(bd1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
//        System.out.println("普通数字");
        double num2 = 50123.12;
        System.out.println(num2);
        BigDecimal bd2 = new BigDecimal(num2);
        System.out.println(bd2.toPlainString());
        System.out.println(bd2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        
        BigDecimal t = new BigDecimal("50123.12");
        System.out.println(t);
    }
}
