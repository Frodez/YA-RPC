package server;

import common.discovery.DefaultServerDiscovery;
import common.server.DefaultRPCDispatcher;
import common.server.DefaultRPCServer;
import common.server.RPCDispatcher;
import facade.TestService;

/**
 * 测试用服务端
 */
public class ServerBootstrap {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: xxx.jar {registryAddress} {registryCenterAddress}");
			return;
		}
		// 这是服务端启动流程
		// 先启动RPCDispatcher
		// 再启动ServerDiscovery
		RPCDispatcher dispatcher = new DefaultRPCDispatcher();
		DefaultServerDiscovery discovery = new DefaultServerDiscovery(args[1], args[0],
				dispatcher);
		// 启动RPCServer
		// 把需要暴露的方法实例注入到RPCDispatcher
		@SuppressWarnings("unused")
		DefaultRPCServer server = new DefaultRPCServer(args[0], dispatcher);
		dispatcher.registService(new TestService() {

			@Override
			public Float sum(Float a, Float b) {
				return a + b;
			}

			@Override
			public String uppercase(String str) {
				return str.toUpperCase();
			}

		});
		// 如果不想等待下一次定期推送注册信息时将上述注入方法信息推送到注册中心，可以手动调用refresh主动推送
		discovery.refresh();
	}

}
