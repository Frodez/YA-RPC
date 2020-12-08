package common.protocol;

/**
 * RPC调用的反序列化接口
 */
public interface ProtocolDeserializer {

	RemoteServer remoteServer(byte[] bytes);

	RemoteProcedure remoteProcedure(byte[] bytes);

	RemoteRequest remoteRequest(byte[] bytes);

	RemoteResponse remoteResponse(byte[] bytes);

}
