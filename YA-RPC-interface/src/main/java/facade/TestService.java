package facade;

import common.server.RPCFunction;
import common.server.RPCService;

/**
 * RPC调用接口
 */
@RPCService
public interface TestService {

	@RPCFunction
	Float sum(Float a, Float b);

	@RPCFunction
	String uppercase(String str);

}
