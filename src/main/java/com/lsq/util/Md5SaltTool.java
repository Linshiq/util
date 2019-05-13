package com.lsq.util;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2018年9月18日-下午2:35:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class Md5SaltTool {

    private static final String HEX_NUMS_STR="0123456789ABCDEF";   
    private static final Integer SALT_LENGTH = 12;   
    
    private static final String key = "MD5|SHA";
    
    /**   
     * 将16进制字符串转换成字节数组   
     * @param hex   
     * @return   
     */  
    public static byte[] hexStringToByte(String hex) {   
        int len = (hex.length() / 2);   
        byte[] result = new byte[len];   
        char[] hexChars = hex.toCharArray();   
        for (int i = 0; i < len; i++) {   
            int pos = i * 2;   
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4    
                            | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));   
        }   
        return result;   
    }   
       
    /**  
     * 将指定byte数组转换成16进制字符串  
     * @param b  
     * @return  
     */  
    public static String byteToHexString(byte[] b) {   
        StringBuffer hexString = new StringBuffer();   
        for (int i = 0; i < b.length; i++) {   
            String hex = Integer.toHexString(b[i] & 0xFF);   
            if (hex.length() == 1) {   
                hex = '0' + hex;   
            }   
            hexString.append(hex.toUpperCase());   
        }   
        return hexString.toString();   
    }   
       
    /**  
     * 验证口令是否合法  
     * @param password  
     * @param passwordInDb  
     * @param salt 
     * @return  
     * @throws NoSuchAlgorithmException  
     * @throws UnsupportedEncodingException  
     */  
    public static boolean validPassword(String password, String passwordInDb,String type, String salt)   
            throws NoSuchAlgorithmException, UnsupportedEncodingException {   
    		
    	if(type == null || type == ""){
    		type = "MD5";
    	}
    	type = type.toUpperCase();
    	// 无匹配算法直接返回false;
    	if(!key.contains(type)){ 
    		return false;
    	}
    	
    	byte[] tempSalt = hexStringToByte(salt);// 转换盐
    	
        //将16进制字符串格式口令转换成字节数组   
        byte[] pwdInDb = hexStringToByte(passwordInDb);   
//        //声明盐变量   
//        byte[] salt = new byte[SALT_LENGTH];   
//        //将盐从数据库中保存的口令字节数组中提取出来   
//        System.out.println(Arrays.toString(salt));
        System.arraycopy(pwdInDb, 0, tempSalt, 0, SALT_LENGTH);   
        //创建消息摘要对象   
        MessageDigest md = MessageDigest.getInstance(type);   
        //将盐数据传入消息摘要对象   
        md.update(tempSalt);   
        //将口令的数据传给消息摘要对象   
        md.update(password.getBytes("UTF-8"));   
        //生成输入口令的消息摘要   
        byte[] digest = md.digest();   
        //声明一个保存数据库中口令消息摘要的变量   
        byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];   
        //取得数据库中口令的消息摘要   
        System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);   
        //比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同   
        if (Arrays.equals(digest, digestInDb)) {   
            //口令正确返回口令匹配消息   
            return true;   
        } else {   
            //口令不正确返回口令不匹配消息   
            return false;   
        }   
    }   
  
    /**  
     * 获得加密后的16进制形式口令  
     * @param password  
     * @return  
     * @throws NoSuchAlgorithmException  
     * @throws UnsupportedEncodingException  
     */  
    public static String getEncryptedPwd(String password,String salt,String type)   
            throws NoSuchAlgorithmException, UnsupportedEncodingException {   
    	
    	
    	if(type == null || type == ""){
    		type = "MD5";
    	}
    	type = type.toUpperCase();
    	// 无匹配算法直接返回原密码;
    	if(!key.contains(type)){ 
    		return password;
    	}
    	
        //声明加密后的口令数组变量   
        byte[] pwd = null;   
        
        //将随机数放入盐变量中   
        byte[] tempSalt = hexStringToByte(salt);
        
        System.out.println(Arrays.toString(tempSalt));
        //声明消息摘要对象   
        MessageDigest md = null;   
        //创建消息摘要   
        md = MessageDigest.getInstance(type);   
        //将盐数据传入消息摘要对象   
        md.update(tempSalt);   
        //将口令的数据传给消息摘要对象   
        md.update(password.getBytes("UTF-8"));   
        //获得消息摘要的字节数组   
        byte[] digest = md.digest();   
  
        //因为要在口令的字节数组中存放盐，所以加上盐的字节长度   
        pwd = new byte[digest.length + SALT_LENGTH];   
        //将盐的字节拷贝到生成的加密口令字节数组的前12个字节，以便在验证口令时取出盐   
        System.arraycopy(tempSalt, 0, pwd, 0, SALT_LENGTH);   
        //将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节   
        System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);   
        for(int i=0;i<pwd.length;i++){
            System.out.print(pwd[i]);
        }
        //将字节数组格式加密后的口令转化为16进制字符串格式的口令   
        return byteToHexString(pwd);   
    }   
    
    /**
     * @Author linshiqin
     *         <p>
     *         <li>2018年9月19日-上午10:00:30</li>
     *         <li>功能说明：生成盐</li>
     *         </p>
     * @return
     */
    public static String getSalt(){
    	
    	//随机数生成器   
        SecureRandom random = new SecureRandom();   
        //声明盐数组变量   12
        byte[] salt = new byte[SALT_LENGTH]; 
        
        random.nextBytes(salt);  
        
        return byteToHexString(salt);
    }
    
    public static void main(String[] args) {
		
    	String password = "123456";
    	    	
    	try {
    		
    		String salt = getSalt();
    		
    		String enPwd = getEncryptedPwd(password,salt,"md5");
    		System.out.println(enPwd);
    		
    		System.out.println(validPassword("123456", enPwd,"md5",salt));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}