package com.lsq.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * <p>
 * 文件功能说明：
 *       	通用工具类		
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2018年9月14日-下午5:12:25</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class CommUtil {

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:20:28</li>
	 *         <li>功能说明：obj是否为空</li>
	 *         </p>
	 * @param object
	 * @return
	 */
	public static boolean isNull(Object object) {
		if (null == object || object.toString().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:20:52</li>
	 *         <li>功能说明：obj是否为非空</li>
	 *         </p>
	 * @param object
	 * @return
	 */
	public static boolean isNotNull(Object object) {

		if (null != object && !"".equals(object.toString())) {
			return true;
		}

		return false;
	}

	public static Integer nvl(Integer object1, Integer defaultValue) {

		if (isNotNull(object1)) {

			return object1;
		} else {

			return defaultValue;
		}

	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:19:32</li>
	 *         <li>功能说明：格式化日期(yyyy-MM-dd)</li>
	 *         </p>
	 * @param object
	 * @return
	 */
	public static String getSimpleDateFormat(Object object) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(object);
		return date;
	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:19:55</li>
	 *         <li>功能说明：格式化日期(yyyy-MM-dd HH:mm:ss)</li>
	 *         </p>
	 * @param object
	 * @return
	 */
	public static String getSimpleTimeFormat(Object object) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(object);
		return time;
	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:10:42</li>
	 *         <li>功能说明：bean转toMap(通过json)</li>
	 *         </p>
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> toMap(Object obj) {

		@SuppressWarnings("unchecked")
		Map<String, Object> objJson = JSON.parseObject(JSON.toJSONString(obj), Map.class);

		return objJson;

	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午3:11:03</li>
	 *         <li>功能说明：bean转map(通过beans自带)</li>
	 *         </p>
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> transBean2Map(Object obj) {

		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}

			}
		} catch (Exception e) {
			System.out.println("transBean2Map Error " + e);
		}

		return map;

	}

	public static <T extends Comparable<? super T>> int compare(T o1, T o2, boolean ignoreCase,
			boolean ignoreNullAndEmpty) {

		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return ignoreNullAndEmpty && String.class.isAssignableFrom(o2.getClass()) && "".equals(o2) ? 0 : -1;
		} else if (o2 == null) {
			return ignoreNullAndEmpty && String.class.isAssignableFrom(o1.getClass()) && "".equals(o1) ? 0 : 1;
		} else if (ignoreCase && String.class.isAssignableFrom(o1.getClass())
				&& String.class.isAssignableFrom(o2.getClass())) {
			return ((String) o1).compareToIgnoreCase((String) o2);
		} else {
			if (o1 != null && o1.getClass().isEnum()) {
				o1 = (T) String.valueOf(o1);
			}

			if (o2 != null && o2.getClass().isEnum()) {
				o2 = (T) String.valueOf(o2);
			}

			return ((Comparable) o1).compareTo(o2);
		}
	}

	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午4:18:08</li>
	 *         <li>功能说明：比较大小 (o1 == o2 return 0) (o1 > o2 return 1) (o1 < o2
	 *         return -1)</li>
	 *         </p>
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
		return compare(o1, o2, false, true);
	}
	
	
	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午4:27:35</li>
	 *         <li>功能说明：是否相等 </li>
	 *         </p>
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static <T extends Comparable<? super T>> boolean equals(T o1, T o2) {
		
		return compare(o1,o2) == 0 ? true : false;
	}

}
