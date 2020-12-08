package common.protocol;

import java.util.Arrays;

/**
 * RPC调用的实体类
 */
public class RemoteRequest {

	/**
	 * RPC调用方法
	 */
	private RemoteProcedure remoteProcedure;

	/**
	 * 方法参数
	 */
	private Object[] args;

	/**
	 * 请求唯一ID
	 */
	private String requestId;

	public RemoteProcedure getRemoteProcedure() {
		return remoteProcedure;
	}

	public void setRemoteProcedure(RemoteProcedure remoteProcedure) {
		this.remoteProcedure = remoteProcedure;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(args);
		result = prime * result + ((remoteProcedure == null) ? 0 : remoteProcedure.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RemoteRequest other = (RemoteRequest) obj;
		if (!Arrays.deepEquals(args, other.args)) {
			return false;
		}
		if (remoteProcedure == null) {
			if (other.remoteProcedure != null) {
				return false;
			}
		} else if (!remoteProcedure.equals(other.remoteProcedure)) {
			return false;
		}
		if (requestId == null) {
			if (other.requestId != null) {
				return false;
			}
		} else if (!requestId.equals(other.requestId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RemoteRequest [remoteProcedure=" + remoteProcedure + ", args=" + Arrays.toString(args) + ", requestId="
				+ requestId + "]";
	}

}
