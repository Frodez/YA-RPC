package common.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import common.protocol.DefaultProtocolTransformer;
import common.protocol.ProtocolTransformer;
import common.protocol.RemoteProcedure;
import common.protocol.RemoteRequest;
import common.protocol.RemoteResponse;
import common.protocol.RemoteServer;

/**
 * 客户端RPC调用和发现可用RPC调用的接口的默认实现
 */
public class DefaultRPCRegistry implements RPCRegistry {

	private HttpClient client;
	private List<RemoteServer> remoteServers;
	private ConcurrentHashMap<RemoteProcedure, List<String>> registryMap;
	private ProtocolTransformer transformer;

	public DefaultRPCRegistry() {
		client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(5000))
				.followRedirects(HttpClient.Redirect.NORMAL).build();
		remoteServers = new ArrayList<>();
		registryMap = new ConcurrentHashMap<>();
		transformer = new DefaultProtocolTransformer();
	}

	/**
	 * RPC调用
	 * @param remoteRequest RPC请求
	 * @return RemoteResponse RPC响应
	 */
	@Override
	public void refresh(List<RemoteServer> remoteServers) {
		Map<String, RemoteServer> map = new HashMap<>(this.remoteServers.size());
		for (RemoteServer remoteServer : this.remoteServers) {
			map.put(remoteServer.getServerAddress(), remoteServer);
		}
		List<RemoteServer> insertServers = new ArrayList<>();
		List<RemoteServer[]> updateServers = new ArrayList<>();
		List<RemoteServer> deleteServers = new ArrayList<>();
		for (RemoteServer remoteServer : remoteServers) {
			RemoteServer localServer = map.get(remoteServer.getServerAddress());
			if (localServer == null) {
				insertServers.add(remoteServer);
			} else if (!localServer.equals(remoteServer)) {
				updateServers.add(new RemoteServer[] { localServer, remoteServer });
			} else {
				map.remove(remoteServer.getServerAddress());
			}
		}
		deleteServers.addAll(map.values());
		for (RemoteServer server : insertServers) {
			insertAddress(server);
		}
		for (RemoteServer[] oldAndNewServers : updateServers) {
			RemoteServer localServer = oldAndNewServers[0];
			RemoteServer remoteServer = oldAndNewServers[1];
			removeAddress(localServer);
			insertAddress(remoteServer);
		}
		for (RemoteServer server : deleteServers) {
			removeAddress(server);
		}
		this.remoteServers = remoteServers;
	}

	@Override
	public RemoteResponse call(RemoteRequest remoteRequest) {
		List<String> addresses = registryMap.get(remoteRequest.getRemoteProcedure());
		if (addresses == null || addresses.isEmpty()) {
			throw new IllegalArgumentException("There is no remote server for this remote procedure call");
		}
		Random random = new Random();
		int choose = Math.abs(random.nextInt()) % addresses.size();
		String address = addresses.get(choose);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(address))
				.POST(BodyPublishers.ofByteArray(transformer.remoteRequest(remoteRequest))).build();
		try {
			byte[] bytes = client.send(request, HttpResponse.BodyHandlers.ofByteArray()).body();
			RemoteResponse remoteResponse = transformer.remoteResponse(bytes);
			return remoteResponse;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeAddress(RemoteServer server) {
		for (RemoteProcedure procedure : server.getProcedures()) {
			List<String> list = registryMap.get(procedure);
			if (list != null) {
				list.remove(server.getServerAddress());
			}
		}
	}

	private void insertAddress(RemoteServer server) {
		for (RemoteProcedure procedure : server.getProcedures()) {
			List<String> list = registryMap.computeIfAbsent(procedure, p -> new ArrayList<>());
			if (!list.contains(server.getServerAddress())) {
				list.add(server.getServerAddress());
			}
		}
	}

}
