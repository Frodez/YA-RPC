package common.server;

import java.util.List;

import common.protocol.RemoteProcedure;
import common.protocol.RemoteRequest;

/**
 * 服务端执行RPC调用和注册可用RPC调用的接口
 */
public interface RPCDispatcher {

	/**
	 * 在本地注册服务端的某一个RPC调用实现
	 * @param service
	 */
	void registService(Object service);

	/**
	 * 在本地从注册信息中删去某一个RPC调用实现
	 * @param service
	 */
	void unregistService(Object service);

	/**
	 * 获取当前在本地拥有的所有RPC调用实现（用于向服务注册中心注册）
	 * @return
	 */
	List<RemoteProcedure> allProcedures();

	/**
	 * 执行RPC调用
	 * @param request RPC调用请求
	 * @return 返回值
	 */
	Object process(RemoteRequest request);

}
