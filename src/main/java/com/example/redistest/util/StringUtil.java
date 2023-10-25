package com.example.redistest.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringUtil {
	
	/**
	 * 주어진 문자열이 null 또는 공백일 경우 참 반환<br><br>
	 *
	 * StringUtils.isEmpty("") = true
	 *
	 * @param str 문자열
	 * @return null 또는 공백일 경우 true
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
	public static String avoidNull(String s0, String s1) {
        return isEmpty(s0) ? s1 : s0;
    }
	
	public static String avoidNull(String s) {
		return avoidNull(s, "");
	}
	
	public static String numberFormat(String s) {
		return s.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
	}
	
	public static String makeAddress(String zipCode, String address) {
		return (!isEmpty(zipCode) ? "(" + zipCode + ") " : "") + avoidNull(address);
	}
	
	public static boolean equals(String str1, String str2) {
		if( str1 == null ) return str1 == str2;
		else return str1.equals(avoidNull(str2).trim());
	}
	
	public static String onlyNumber(String obj) {
		if(!isEmpty(obj)) {
			String number = String.format("%s", obj);
			number = number.replaceAll("[^0-9]","");
			return number;
		}else {
			return "";
		}
	}

	public static boolean isEmail(String s){
		Pattern pattern = Pattern.compile("^(.+)@(.+)$");
		Matcher matcher = pattern.matcher(s);
		return !matcher.matches();
	}
}


