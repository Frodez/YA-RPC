package common.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.protocol.RemoteProcedure;
import common.protocol.RemoteRequest;
import common.protocol.SignHelper;
import common.server.RPCFunction.RPCFunctionHelper;
import common.util.ReflectUtil;

/**
 * 服务端执行RPC调用和注册可用RPC调用的接口的默认实现
 */
public class DefaultRPCDispatcher implements RPCDispatcher {

	private ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();

	private List<RemoteProcedure> remoteProcedures = Collections.synchronizedList(new LinkedList<>());

	/**
	 * 在本地注册服务端的某一个RPC调用实现
	 * @param service
	 */
	@Override
	public void registService(Object service) {
		Class<?> klass = findRPCServiceClass(service);
		boolean hasRPCFunction = Stream.of(klass.getMethods()).anyMatch(RPCFunctionHelper::isRPCFunction);
		if (!hasRPCFunction) {
			throw new IllegalArgumentException(
					"class " + service.getClass().getName() + " must have at least one remote procedure");
		}
		serviceMap.put(klass.getName(), service);
		List<RemoteProcedure> procedures = Stream.of(klass.getMethods()).filter(RPCFunctionHelper::isRPCFunction)
				.map(SignHelper::toRemoteProcedure).collect(Collectors.toList());
		remoteProcedures.addAll(procedures);
	}

	/**
	 * 在本地从注册信息中删去某一个RPC调用实现
	 * @param service
	 */
	@Override
	public void unregistService(Object service) {
		Class<?> klass = findRPCServiceClass(service);
		if (!serviceMap.containsKey(klass.getName())) {
			return;
		}
		List<RemoteProcedure> procedures = Stream.of(klass.getMethods()).filter(RPCFunctionHelper::isRPCFunction)
				.map(SignHelper::toRemoteProcedure).collect(Collectors.toList());
		remoteProcedures.removeAll(procedures);
		serviceMap.remove(klass.getName());
	}

	/**
	 * 获取当前在本地拥有的所有RPC调用实现（用于向服务注册中心注册）
	 * @return
	 */
	@Override
	public List<RemoteProcedure> allProcedures() {
		return new ArrayList<>(remoteProcedures);
	}

	/**
	 * 执行RPC调用
	 * @param request RPC调用请求
	 * @return 返回值
	 */
	@Override
	public Object process(RemoteRequest request) {
		try {
			RemoteProcedure procedure = request.getRemoteProcedure();
			String className = SignHelper.className(procedure.getFunctionName());
			Object service = serviceMap.get(className);
			if (service == null) {
				throw new RuntimeException("can't find service " + className);
			}
			Method method = SignHelper.fromRemoteProcedure(procedure);
			return method.invoke(service, request.getArgs());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Class<?> findRPCServiceClass(Object service) {
		if (service.getClass().getInterfaces().length == 0) {
			throw new IllegalArgumentException("the service must have at least one interface");
		}
		List<Class<?>> klasses = Stream.of(service.getClass().getInterfaces())
				.filter(klass -> ReflectUtil.isAnnotationPresent(klass, RPCService.class)).collect(Collectors.toList());
		if (klasses.size() != 1) {
			throw new IllegalArgumentException(
					"the service must have one and only one interface which has the annotation of RPCService");
		}
		return klasses.get(0);
	}

}
