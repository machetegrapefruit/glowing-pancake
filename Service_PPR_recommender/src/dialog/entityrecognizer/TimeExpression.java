package dialog.entityrecognizer;

import org.joda.time.DateTime;

public class TimeExpression {
	public enum ValueType {
			DATETIME,
			STRING
	}
	private ValueType startType;
	private ValueType endType;
	private DateTime startDT;
	private DateTime endDT;
	private String startStr;
	private String endStr;
	
	public TimeExpression(Object start, Object end) {
		if (start instanceof DateTime) {
			this.startType = ValueType.DATETIME;
			this.startDT = (DateTime) start;
		} else if (start instanceof String) {
			this.startType = ValueType.STRING;
			this.startStr = (String) start;
		}
		if (end instanceof DateTime) {
			this.endType = ValueType.DATETIME;
			this.endDT = (DateTime) end;
		} else if (end instanceof String) {
			this.endType = ValueType.STRING;
			this.endStr = (String) end;
		}
	}
	public ValueType getStartType() {
		return this.startType;
	}
	public ValueType getEndType() {
		return this.endType;
	}
	public DateTime getStartDT() {
		return startDT;
	}
	public DateTime getEndDT() {
		return endDT;
	}
	public String getStartStr() {
		return startStr;
	}
	public String getEndStr() {
		return endStr;
	}
	public String toString() {
		String message = "start=";
		if (startType == ValueType.STRING) {
			message += startStr;
		} else if (startType == ValueType.DATETIME) {
			message += startDT;
		}
		message += ", end=";
		if (endType == ValueType.STRING) {
			message += endStr;
		} else if (endType == ValueType.DATETIME) {
			message += endDT;
		}
		return message;
	}
}
