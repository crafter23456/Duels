package net.Duels.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
	
	public static void setValue(Object instance, String name, Object value) {
		try {
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValueWithNewType(Class<?> clazz, String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
			field.set(null, value);
		} catch (Exception ex) {
		}
	}

	public static void setValue(Class<?> clazz, String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <T> T invokeMethod(Object instance, String name) {
		try {
			Method method = instance.getClass().getDeclaredMethod(name, new Class[0]);
			method.setAccessible(true);
			return (T) method.invoke(instance, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T getValue(Object instance, String name) {
		try {
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T getValue(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getClass(String s) {
		try {
			return Class.forName(s);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
