package common.discovery;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import common.protocol.DefaultProtocolTransformer;
import common.protocol.ProtocolTransformer;
import common.protocol.RemoteServer;
import common.server.RPCDispatcher;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 服务端服务注册的默认实现
 */
public class DefaultServerDiscovery {

	private ScheduledExecutorService executorService;

	private ProtocolTransformer transformer;

	private RedisClient redis;

	private StatefulRedisConnection<String, String> connection;

	private RedisCommands<String, String> commands;

	private String serverAddress;

	private RPCDispatcher dispatcher;

	public DefaultServerDiscovery(String registryCenterAddress, String serverAddress, RPCDispatcher dispatcher) {
		this.serverAddress = serverAddress;
		this.dispatcher = dispatcher;
		executorService = Executors.newSingleThreadScheduledExecutor();
		transformer = new DefaultProtocolTransformer();
		redis = RedisClient.create(registryCenterAddress);
		connection = redis.connect();
		commands = connection.sync();
		executorService.scheduleAtFixedRate(this::refresh, 0, DiscoveryConstant.REGIST_INTERVAL_SECONDS,
				TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(this::clean, 0, DiscoveryConstant.REGIST_INTERVAL_SECONDS,
				TimeUnit.SECONDS);
	}

	/**
	 * 推送最新的RPC调用信息到服务注册中心注册
	 */
	public void refresh() {
		RemoteServer remoteServer = new RemoteServer();
		remoteServer.setServerAddress(serverAddress);
		remoteServer.setProcedures(dispatcher.allProcedures());
		remoteServer.setServerTime(new Date());
		commands.zadd(DiscoveryConstant.SERVER_REGISTRY_KEY, new Date().getTime() / 1000,
				new String(transformer.remoteServer(remoteServer)));
	}

	/**
	 * 定期清理服务注册中心上已过期的RPC调用信息
	 */
	public void clean() {
		double expireAt = new Date().getTime() / 1000 - DiscoveryConstant.REGIST_KEEP_SECONDS;
		commands.zremrangebyscore(DiscoveryConstant.SERVER_REGISTRY_KEY, Range.create(0.0, expireAt));
	}

}
