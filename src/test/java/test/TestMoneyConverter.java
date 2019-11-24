package test;

import static org.junit.Assert.*;

import java.text.DecimalFormat;

import org.junit.Test;

import deprecated.utils.MoneyConverter;

public class TestMoneyConverter {

	@Test
	public void test2() {	
		String found = (new DecimalFormat("##.00").format(((double)615994L)/100));
		assertEquals("6159,94",found);
		found = (new DecimalFormat("##.00").format(((double)274951L)/100));
		assertEquals("2749,51",found);
	}
	
	@Test
	public void test() {		
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12",""));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12","00"));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12","0"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12","5"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12","50"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0","0"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("00",""));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0",""));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0","0"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("","0"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("","00"));
		assertEquals(50, MoneyConverter.parseDecimalStringToLong("0","5"));
		assertEquals(5, MoneyConverter.parseDecimalStringToLong("0","05"));
		assertEquals(5, MoneyConverter.parseDecimalStringToLong("00","05"));
		
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12"));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12.00"));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12.0"));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12,0"));
		assertEquals(1200, MoneyConverter.parseDecimalStringToLong("12,00"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12,5"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12.5"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12,50"));
		assertEquals(1250, MoneyConverter.parseDecimalStringToLong("12.50"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0.0"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0,0"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("00"));
		assertEquals(0, MoneyConverter.parseDecimalStringToLong("0"));
		assertEquals(50, MoneyConverter.parseDecimalStringToLong("0,5"));
		assertEquals(50, MoneyConverter.parseDecimalStringToLong("0.5"));
		assertEquals(5, MoneyConverter.parseDecimalStringToLong("0,05"));
		assertEquals(5, MoneyConverter.parseDecimalStringToLong("0.05"));
		
		
		assertCombinations(5,"0","05");
		assertCombinations(0,"0","");
		assertCombinations(0,"0","0");
		assertCombinations(0,"0","00");
		assertCombinations(1200,"12","");
		assertCombinations(1200,"12","0");
		assertCombinations(1200,"12","00");
		assertCombinations(1250,"12","50");
		assertCombinations(1205,"12","05");
		assertCombinations(1,"0","01");
		assertCombinations(10,"0","10");
		assertCombinations(10,"0","1");
		
		assertCombinations(10,"","1");
		
		assertEquals("12,78", MoneyConverter.parseLongToNormalizedDecimalString(1278));
		assertEquals("0,01", MoneyConverter.parseLongToNormalizedDecimalString(1));
		assertEquals("0,10", MoneyConverter.parseLongToNormalizedDecimalString(10));
		assertEquals("12,70", MoneyConverter.parseLongToNormalizedDecimalString(1270));
		assertEquals("0,00", MoneyConverter.parseLongToNormalizedDecimalString(0));
		assertEquals("1,00", MoneyConverter.parseLongToNormalizedDecimalString(100));
		assertEquals("10,00", MoneyConverter.parseLongToNormalizedDecimalString(1000));
		
		assertEquals("0,00", MoneyConverter.parseLongToNormalizedDecimalString(00));
		
		assertCombinations("12,78", 1278);
		assertCombinations("0,01", 1);
		assertCombinations("0,10", 10);
		assertCombinations("12,70", 1270);
		assertCombinations("0,00", 0);
		assertCombinations("1,00", 100);
		assertCombinations("10,00", 1000);
		
		assertEquals(-50, MoneyConverter.parseDecimalStringToLong("-","5"));
	}
	
	private void assertCombinations(long expected,String s1,String s2) {
		assertEquals(expected, MoneyConverter.parseDecimalStringToLong(s1,s2));
		assertEquals(expected, MoneyConverter.parseDecimalStringToLong(s1 + ((s2.equals("")?"":MoneyConverter.COMMA_SEPARATOR)) + s2));
		assertEquals(expected, MoneyConverter.parseDecimalStringToLong(s1 + ((s2.equals("")?"":MoneyConverter.DOT_SEPARATOR)) + s2));		
		if(expected>0) {
			assertEquals(-expected, MoneyConverter.parseDecimalStringToLong("-"+s1,s2));
			assertEquals(-expected, MoneyConverter.parseDecimalStringToLong("-"+s1 + ((s2.equals("")?"":MoneyConverter.COMMA_SEPARATOR)) + s2));
			assertEquals(-expected, MoneyConverter.parseDecimalStringToLong("-"+s1 + ((s2.equals("")?"":MoneyConverter.DOT_SEPARATOR)) + s2));		
		}
	}
	
	private void assertCombinations(String expected,long l) {
		assertEquals(expected, MoneyConverter.parseLongToNormalizedDecimalString(l));
		if(l>0) {
			assertEquals("-"+expected,MoneyConverter.parseLongToNormalizedDecimalString(-l));		
		}
	}

}
