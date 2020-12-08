package common.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpServer;

import common.protocol.DefaultProtocolTransformer;
import common.protocol.ProtocolTransformer;
import common.protocol.RemoteRequest;
import common.protocol.RemoteResponse;

/**
 * 默认的RPC调用的服务端<br>
 * 使用http来接收客户端的RPC调用请求和进行响应
 */
public class DefaultRPCServer {

	private HttpServer server;

	private ProtocolTransformer transformer;

	public DefaultRPCServer(String address, RPCDispatcher dispatcher) {
		transformer = new DefaultProtocolTransformer();
		try {
			server = HttpServer.create(new InetSocketAddress(URI.create(address).getPort()), 0);
			server.createContext("/", exchange -> {
				byte[] bytes = exchange.getRequestBody().readAllBytes();
				RemoteRequest request = transformer.remoteRequest(bytes);
				RemoteResponse response;
				try {
					Object result = dispatcher.process(request);
					response = new RemoteResponse();
					response.setProcedure(request.getRemoteProcedure());
					response.setRequestId(request.getRequestId());
					response.setResult(result);
				} catch (Exception e) {
					response = new RemoteResponse();
					response.setProcedure(request.getRemoteProcedure());
					response.setRequestId(request.getRequestId());
					response.setError(e.getMessage());
				}
				exchange.sendResponseHeaders(200, 0);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(transformer.remoteResponse(response));
					os.flush();
				}
			});
			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
