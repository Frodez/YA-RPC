package common.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC协议的序列化和反序列化接口的默认实现<br>
 * 使用json作为默认实现
 */
public class DefaultProtocolTransformer implements ProtocolTransformer {

	Object mapperObject;

	Method writeValueAsBytes;

	Method readValue;

	public DefaultProtocolTransformer() {
		try {
			Class<?> mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
			mapperObject = mapperClass.getDeclaredConstructor().newInstance();
			writeValueAsBytes = mapperClass.getMethod("writeValueAsBytes", Object.class);
			readValue = mapperClass.getMethod("readValue", byte[].class, Class.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] remoteServer(RemoteServer remoteServer) {
		try {
			return (byte[]) writeValueAsBytes.invoke(mapperObject, remoteServer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] remoteProcedure(RemoteProcedure remoteProcedure) {
		try {
			return (byte[]) writeValueAsBytes.invoke(mapperObject, remoteProcedure);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] remoteRequest(RemoteRequest remoteRequest) {
		try {
			return (byte[]) writeValueAsBytes.invoke(mapperObject, remoteRequest);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] remoteResponse(RemoteResponse remoteResponse) {
		try {
			return (byte[]) writeValueAsBytes.invoke(mapperObject, remoteResponse);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RemoteServer remoteServer(byte[] bytes) {
		try {
			return (RemoteServer) readValue.invoke(mapperObject, bytes, RemoteServer.class);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RemoteProcedure remoteProcedure(byte[] bytes) {
		try {
			return (RemoteProcedure) readValue.invoke(mapperObject, bytes, RemoteProcedure.class);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RemoteRequest remoteRequest(byte[] bytes) {
		try {
			RemoteRequest request = (RemoteRequest) readValue.invoke(mapperObject, bytes, RemoteRequest.class);
			Object[] args = request.getArgs();
			for (int i = 0; i < request.getRemoteProcedure().getParameterTypes().size(); i++) {
				String paramType = request.getRemoteProcedure().getParameterTypes().get(i);
				byte[] param = (byte[]) writeValueAsBytes.invoke(mapperObject, args[i]);
				args[i] = readValue.invoke(mapperObject, param, Class.forName(paramType));
			}
			request.setArgs(args);
			return request;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RemoteResponse remoteResponse(byte[] bytes) {
		try {
			RemoteResponse response = (RemoteResponse) readValue.invoke(mapperObject, bytes, RemoteResponse.class);
			Class<?> returnType = Class.forName(response.getProcedure().getReturnType());
			byte[] resultBytes = (byte[]) writeValueAsBytes.invoke(mapperObject, response.getResult());
			Object result = readValue.invoke(mapperObject, resultBytes, returnType);
			response.setResult(result);
			return response;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
