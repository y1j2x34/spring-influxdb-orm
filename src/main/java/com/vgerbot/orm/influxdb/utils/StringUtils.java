package com.vgerbot.orm.influxdb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.springframework.util.StringUtils {
	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	public static boolean isBlank(CharSequence cs) {
		if (cs == null || cs.length() < 1) {
			return true;
		}
		int strLen = cs.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 正则匹配替换，类似js中的replace
	 * 
	 * @param source
	 * @param regex
	 * @param func
	 * @return
	 */
	public static final String replace(String source, String regex, ReplaceCallback func) {
		return replace(source, regex, 0, func);
	}

	public static final String replace(String source, String regex, int group, ReplaceCallback func) {
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(source);
		if (m.find()) {
			StringBuffer sb = new StringBuffer();
			int i = 0;
			do {
				String grp = m.group(group);
				int index = source.indexOf(grp, i);
				m.appendReplacement(sb, func.replace(grp, index, source));
				i = index + 1;
			} while (m.find());
			m.appendTail(sb);
			return sb.toString();
		}
		return source;
	}

	/**
	 * 替换回调函数接口
	 * 
	 * @author y1j2x34
	 * @date 2014-7-24
	 */
	public static interface ReplaceCallback {
		String replace(String word, int index, String source);
	}

}
