package common.protocol;

/**
 * RPC调用的序列化接口
 */
public interface ProtocolSerializer {

	byte[] remoteServer(RemoteServer remoteServer);

	byte[] remoteProcedure(RemoteProcedure remoteProcedure);

	byte[] remoteRequest(RemoteRequest remoteRequest);

	byte[] remoteResponse(RemoteResponse remoteResponse);

}
