package se.hockersten.timed.vibration.test;

import java.lang.reflect.Method;

import se.hockersten.timed.vibration.main.PracticeTab;
import android.test.AndroidTestCase;

public class TestPractice extends AndroidTestCase {

	public void testNormalizedMinuteDelay() {
		for (int i = 0; i < 10; i++) {
			assertEquals(1, callNormalizedMinuteDelay(i, 1));
		}
		for (int i = 0; i < 10; i+=2) {
			assertEquals(2, callNormalizedMinuteDelay(i, 2));
		}
		for (int i = 1; i < 10; i+=2) {
			assertEquals(1, callNormalizedMinuteDelay(i, 2));
		}
		for (int i = 0; i < 5; i++) {
			assertEquals(5 - i, callNormalizedMinuteDelay(i, 5));
		}
		for (int i = 5; i < 10; i++) {
			assertEquals(10 - i, callNormalizedMinuteDelay(i, 5));
		}
		for (int i = 0; i < 10; i++) {
			assertEquals(10 - i, callNormalizedMinuteDelay(i, 10));
		}
		for (int i = 0; i < 15; i++) {
			assertEquals(15 - i, callNormalizedMinuteDelay(i, 15));
		}
		for (int i = 0; i < 30; i++) {
			assertEquals(30 - i, callNormalizedMinuteDelay(i, 30));
		}
		for (int i = 0; i < 60; i++) {
			assertEquals(60 - i, callNormalizedMinuteDelay(i, 60));
		}
	}
	
	private int callNormalizedMinuteDelay(int currentMinute, int delay) {
		Class<?> args[] = new Class[] {Integer.TYPE, Integer.TYPE};
		try {
			Method method = PracticeTab.class.getDeclaredMethod("normalizedMinuteDelay", args);
			method.setAccessible(true);
			return (Integer) method.invoke(null, currentMinute, delay);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
