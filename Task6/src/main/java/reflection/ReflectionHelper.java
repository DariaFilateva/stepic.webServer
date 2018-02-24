package reflection;

import java.lang.reflect.Field;

public class ReflectionHelper {

	public static Object createInstance(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setFieldValue(Object object, String element, String value) {
		try {
			Field field = object.getClass().getDeclaredField(element);
			field.setAccessible(true);
			
			switch (field.getType().getSimpleName().toUpperCase()) {
				case "STRING":
					field.set(object, value);
					break;
				case "INT": 
					field.set(object, Integer.decode(value));
			}
			
			field.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}