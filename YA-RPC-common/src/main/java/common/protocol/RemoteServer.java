package common.protocol;

import java.util.Date;
import java.util.List;

/**
 * RPC服务端信息实体类
 */
public class RemoteServer {

	/**
	 * 服务端地址
	 */
	private String serverAddress;

	/**
	 * 服务端拥有的RPC调用方法列表
	 */
	private List<RemoteProcedure> procedures;

	/**
	 * RPC服务端信息的发送时间
	 */
	private Date serverTime;

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public List<RemoteProcedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<RemoteProcedure> procedures) {
		this.procedures = procedures;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((procedures == null) ? 0 : procedures.hashCode());
		result = prime * result + ((serverAddress == null) ? 0 : serverAddress.hashCode());
		result = prime * result + ((serverTime == null) ? 0 : serverTime.hashCode());
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
		RemoteServer other = (RemoteServer) obj;
		if (procedures == null) {
			if (other.procedures != null) {
				return false;
			}
		} else if (!procedures.equals(other.procedures)) {
			return false;
		}
		if (serverAddress == null) {
			if (other.serverAddress != null) {
				return false;
			}
		} else if (!serverAddress.equals(other.serverAddress)) {
			return false;
		}
		if (serverTime == null) {
			if (other.serverTime != null) {
				return false;
			}
		} else if (!serverTime.equals(other.serverTime)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RemoteServer [serverAddress=" + serverAddress + ", procedures=" + procedures + ", serverTime="
				+ serverTime + "]";
	}

}
