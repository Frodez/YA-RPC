package common.protocol;

/**
 * RPC调用响应的实体类
 */
public class RemoteResponse {

	/**
	 * RPC调用方法
	 */
	private RemoteProcedure procedure;

	/**
	 * 请求唯一ID
	 */
	private String requestId;

	/**
	 * 错误
	 */
	private String error;

	/**
	 * 返回值
	 */
	private Object result;

	public RemoteProcedure getProcedure() {
		return procedure;
	}

	public void setProcedure(RemoteProcedure procedure) {
		this.procedure = procedure;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((procedure == null) ? 0 : procedure.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
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
		RemoteResponse other = (RemoteResponse) obj;
		if (error == null) {
			if (other.error != null) {
				return false;
			}
		} else if (!error.equals(other.error)) {
			return false;
		}
		if (procedure == null) {
			if (other.procedure != null) {
				return false;
			}
		} else if (!procedure.equals(other.procedure)) {
			return false;
		}
		if (requestId == null) {
			if (other.requestId != null) {
				return false;
			}
		} else if (!requestId.equals(other.requestId)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RemoteResponse [procedure=" + procedure + ", requestId=" + requestId + ", error=" + error + ", result="
				+ result + "]";
	}

}
