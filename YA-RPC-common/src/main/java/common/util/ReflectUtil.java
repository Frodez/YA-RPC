package common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射用辅助工具类
 */
public final class ReflectUtil {

	private ReflectUtil() {

	}

	public static boolean isAnnotationPresent(Class<?> type,
			Class<? extends Annotation> annotationClass) {
		if (Object.class == type) {
			return false;
		}
		if (type.isAnnotationPresent(annotationClass)) {
			return true;
		}
		if (isAnnotationPresent(type.getSuperclass(), annotationClass)) {
			return true;
		}
		for (Class<?> interfaceType : type.getInterfaces()) {
			if (isAnnotationPresent(interfaceType, annotationClass)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAnnotationPresent(Method method,
			Class<? extends Annotation> annotationClass) {
		List<Method> methods = findMethods(method.getDeclaringClass(), method);
		return methods.stream().anyMatch(m -> m.isAnnotationPresent(annotationClass));
	}

	public static List<Method> findMethods(Class<?> klass, Method method) {
		List<Method> methods = new ArrayList<>();
		try {
			Method result = klass.getMethod(method.getName(), method.getParameterTypes());
			if (result != null) {
				methods.add(result);
			}
			methods.addAll(findMethods(klass.getSuperclass(), method));
			for (Class<?> interfaceType : klass.getInterfaces()) {
				methods.addAll(findMethods(interfaceType, method));
			}
			return methods;
		} catch (Exception e) {
			return methods;
		}
	}

}
