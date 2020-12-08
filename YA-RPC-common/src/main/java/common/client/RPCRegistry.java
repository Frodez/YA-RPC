package common.client;

import java.util.List;

import common.protocol.RemoteRequest;
import common.protocol.RemoteResponse;
import common.protocol.RemoteServer;

/**
 * 客户端RPC调用和发现可用RPC调用的接口
 */
public interface RPCRegistry {

	/**
	 * 更新可用的RPC调用
	 * @param remoteServers 最新可用的RPC调用
	 */
	void refresh(List<RemoteServer> remoteServers);

	/**
	 * RPC调用
	 * @param remoteRequest RPC请求
	 * @return RemoteResponse RPC响应
	 */
	RemoteResponse call(RemoteRequest remoteRequest);

}
