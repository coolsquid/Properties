package coolsquid.properties.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PrReflection {

	private static final Field MODIFIERS;

	static {
		try {
			MODIFIERS = Field.class.getDeclaredField("modifiers");
			MODIFIERS.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, E> void setFinalValue(Class<? super T> classToAccess, T instance, E value, int fieldIndex) {
		Field field = classToAccess.getDeclaredFields()[fieldIndex];
		field.setAccessible(true);
		try {
			MODIFIERS.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(instance, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}