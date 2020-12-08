package common.protocol;

import java.util.List;

/**
 * RPC调用方法信息的实体类
 */
public class RemoteProcedure {

	/**
	 * 方法名
	 */
	private String functionName;

	/**
	 * 参数类型
	 */
	private List<String> parameterTypes;

	/**
	 * 返回值类型
	 */
	private String returnType;

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public List<String> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<String> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
		result = prime * result + ((parameterTypes == null) ? 0 : parameterTypes.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
		RemoteProcedure other = (RemoteProcedure) obj;
		if (functionName == null) {
			if (other.functionName != null) {
				return false;
			}
		} else if (!functionName.equals(other.functionName)) {
			return false;
		}
		if (parameterTypes == null) {
			if (other.parameterTypes != null) {
				return false;
			}
		} else if (!parameterTypes.equals(other.parameterTypes)) {
			return false;
		}
		if (returnType == null) {
			if (other.returnType != null) {
				return false;
			}
		} else if (!returnType.equals(other.returnType)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RemoteProcedure [functionName=" + functionName + ", parameterTypes=" + parameterTypes + ", returnType="
				+ returnType + "]";
	}

}
