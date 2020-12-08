package common.discovery;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import common.client.RPCRegistry;
import common.protocol.DefaultProtocolTransformer;
import common.protocol.ProtocolTransformer;
import common.protocol.RemoteServer;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * 客户端服务发现的默认实现
 */
public class DefaultClientDiscovery {

	private ScheduledExecutorService executorService;

	private ProtocolTransformer transformer;

	private RedisClient redis;

	private StatefulRedisConnection<String, String> connection;

	private RedisCommands<String, String> commands;

	private RPCRegistry registry;

	public DefaultClientDiscovery(String registryCenterAddress, RPCRegistry registry) {
		this.registry = registry;
		executorService = Executors.newSingleThreadScheduledExecutor();
		transformer = new DefaultProtocolTransformer();
		redis = RedisClient.create(registryCenterAddress);
		connection = redis.connect();
		commands = connection.sync();
		executorService.scheduleAtFixedRate(this::refresh, 0, DiscoveryConstant.REGIST_INTERVAL_SECONDS,
				TimeUnit.SECONDS);
	}

	/**
	 * 定期从服务注册中心获取最新的RPC调用注册信息
	 */
	public void refresh() {
		double now = new Date().getTime() / 1000;
		double expireAt = now - DiscoveryConstant.REGIST_KEEP_SECONDS;
		List<RemoteServer> results = commands
				.zrangebyscore(DiscoveryConstant.SERVER_REGISTRY_KEY, Range.create(expireAt, now)).stream()
				.map(result -> transformer.remoteServer(result.getBytes()))
				.collect(Collectors.toMap(RemoteServer::getServerAddress, u -> u,
						BinaryOperator.maxBy(Comparator.comparing(RemoteServer::getServerTime))))
				.values().stream().collect(Collectors.toList());
		registry.refresh(results);
	}

}
