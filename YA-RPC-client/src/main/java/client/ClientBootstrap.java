package client;

import java.util.Scanner;

import common.client.DefaultRPCRegistry;
import common.client.RPCRegistry;
import common.client.ServerProxy;
import common.discovery.DefaultClientDiscovery;
import facade.TestService;

/**
 * 测试用客户端
 */
public class ClientBootstrap {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: xxx.jar {registryCenterAddress}");
			return;
		}
		// 这是客户端的启动流程
		// 先启动RPCRegistry
		// 再启动ClientDiscovery
		RPCRegistry registry = new DefaultRPCRegistry();
		DefaultClientDiscovery clientDiscovery = new DefaultClientDiscovery(args[0], registry);
		// 如果不想等待定期拉取最新注册信息的话，可以调用主动更新
		clientDiscovery.refresh();
		// 启动ServerProxy
		// 生成RPC接口的代理实例
		ServerProxy proxy = new ServerProxy(registry);
		TestService service = proxy.serviceInstance(TestService.class);
		// 下面是一个应用例子
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("Please input the method name you want to call:");
				System.out.println(
						"'sum' for the sum method, 'uppercase' for the uppercase method, and 'exit' for exit.");
				System.out.println("And then you should put enter.");
				String input = scanner.nextLine();
				switch (input) {
					case "exit": {
						System.exit(0);
					}
					case "sum": {
						System.out.println("Please input two number.And then you should put enter.");
						Float a = scanner.nextFloat();
						Float b = scanner.nextFloat();
						scanner.nextLine();
						System.out.println("Your inputs are: " + a + " and " + b);
						Float result = service.sum(a, b);
						System.out.println("Result is:" + result);
						break;
					}
					case "uppercase": {
						System.out.println("Please input one string.And then you should put enter.");
						String string = scanner.nextLine();
						System.out.println("Your input is:");
						System.out.println(string);
						String result = service.uppercase(string);
						System.out.println("Result is:" + result);
						break;
					}
					default: {
						System.out.println("incorrect input");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
