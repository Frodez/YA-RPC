package common.protocol;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法签名工具类
 */
public class SignHelper {

	private SignHelper() {
	}

	public static String className(String functionName) {
		int klassMethodIndex = functionName.lastIndexOf(".");
		return functionName.substring(0, klassMethodIndex);
	}

	public static String methodName(String functionName) {
		int klassMethodIndex = functionName.lastIndexOf(".");
		return functionName.substring(klassMethodIndex + 1);
	}

	public static String toProcedureSign(Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getDeclaringClass().getName());
		builder.append(".");
		builder.append(method.getName());
		builder.append(" (");
		List<String> parameters = Stream.of(method.getParameters()).map(parameter -> parameter.getType().getName())
				.collect(Collectors.toList());
		builder.append(String.join(",", parameters));
		builder.append(") ");
		builder.append(method.getReturnType().getName());
		return builder.toString();
	}

	public static String toProcedureSign(RemoteProcedure remoteProcedure) {
		StringBuilder builder = new StringBuilder();
		builder.append(remoteProcedure.getFunctionName());
		builder.append(" (");
		builder.append(String.join(",", remoteProcedure.getParameterTypes()));
		builder.append(") ");
		builder.append(remoteProcedure.getReturnType());
		return builder.toString();
	}

	public static RemoteProcedure toRemoteProcedure(Method method) {
		try {
			String functionName = method.getDeclaringClass().getName() + "." + method.getName();
			List<String> parameterTypes = Stream.of(method.getParameters())
					.map(parameter -> parameter.getType().getName())
					.collect(Collectors.toList());
			String returnType = method.getReturnType().getName();
			RemoteProcedure remoteProcedure = new RemoteProcedure();
			remoteProcedure.setFunctionName(functionName);
			remoteProcedure.setParameterTypes(parameterTypes);
			remoteProcedure.setReturnType(returnType);
			return remoteProcedure;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static RemoteProcedure toRemoteProcedure(String procedureSign) {
		String[] strings = procedureSign.split(" ");
		if (strings.length != 3) {
			throw new IllegalArgumentException("Illegal procedureSign");
		}
		String functionName = strings[0];
		List<String> parameterTypes = Stream.of(strings[1].substring(1, strings[1].length() - 1).split(","))
				.collect(Collectors.toList());
		String returnType = strings[2];
		RemoteProcedure remoteProcedure = new RemoteProcedure();
		remoteProcedure.setFunctionName(functionName);
		remoteProcedure.setParameterTypes(parameterTypes);
		remoteProcedure.setReturnType(returnType);
		return remoteProcedure;
	}

	public static Method fromProcedureSign(String procedureSign) {
		try {
			String[] strings = procedureSign.split(" ");
			if (strings.length != 3) {
				throw new IllegalArgumentException("Illegal procedureSign");
			}
			String functionName = strings[0];
			int klassMethodIndex = functionName.lastIndexOf(".");
			String klassString = functionName.substring(0, klassMethodIndex);
			String methodString = functionName.substring(klassMethodIndex + 1);
			String[] parameterStrings = strings[1].substring(1, strings[1].length() - 1).split(",");
			String returnTypeString = strings[2];
			Class<?> klass = Class.forName(klassString);
			Class<?>[] parameterClasses = new Class<?>[parameterStrings.length];
			for (int i = 0; i < parameterClasses.length; i++) {
				parameterClasses[i] = Class.forName(parameterStrings[i]);
			}
			Method method = klass.getDeclaredMethod(methodString, parameterClasses);
			Class<?> returnType = returnTypeString.equals(void.class.getName()) ? void.class
					: Class.forName(returnTypeString);
			if (method.getReturnType() != returnType) {
				throw new NoSuchMethodException("Incorrect return type");
			}
			return method;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Method fromRemoteProcedure(RemoteProcedure remoteProcedure) {
		try {
			String functionName = remoteProcedure.getFunctionName();
			int klassMethodIndex = functionName.lastIndexOf(".");
			String klassString = functionName.substring(0, klassMethodIndex);
			String methodString = functionName.substring(klassMethodIndex + 1);
			String returnTypeString = remoteProcedure.getReturnType();
			Class<?> klass = Class.forName(klassString);
			Class<?>[] parameterClasses = new Class<?>[remoteProcedure.getParameterTypes().size()];
			for (int i = 0; i < parameterClasses.length; i++) {
				parameterClasses[i] = Class.forName(remoteProcedure.getParameterTypes().get(i));
			}
			Method method = klass.getDeclaredMethod(methodString, parameterClasses);
			Class<?> returnType = returnTypeString.equals(void.class.getName()) ? void.class
					: Class.forName(returnTypeString);
			if (method.getReturnType() != returnType) {
				throw new NoSuchMethodException("Incorrect return type");
			}
			return method;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
