import org.emitdo.oal.DOFInterface;
import org.emitdo.oal.DOFInterfaceID;
import org.emitdo.oal.DOFType;
import org.emitdo.oal.value.DOFBoolean;
import org.emitdo.oal.value.DOFDateTime;


public class TBAInterface {

	public static final DOFType Active = DOFBoolean.TYPE;
	public static final DOFType AlarmTime = DOFDateTime.TYPE;
	
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID = DOFInterfaceID.create("[63:{EDE7}]");

	public static final int PROPERTY_ACTIVE_ID = 1;
	public static final int PROPERTY_ALARM_TIME_ID = 2;
	public static final int METHOD_SET_ALARM_ID = 3;
	public static final int EVENT_ALARM_ID = 4;
	public static final int EXCEPTION_BAD_TIME_VALUE_ID = 5;

	public static final DOFInterface.Property PROPERTY_ACTIVE;
	public static final DOFInterface.Property PROPERTY_ALARM_TIME;
	public static final DOFInterface.Method METHOD_SET_ALARM;
	public static final DOFInterface.Event EVENT_ALARM;
	public static final DOFInterface.Exception EXCEPTION_BAD_TIME_VALUE;
	
	
	static {
		DEF = new DOFInterface.Builder(IID)
				.addProperty(1, true, true, Active)
				.addProperty(2, false, true, AlarmTime)
				.addMethod(3, new DOFType[] { AlarmTime }, new DOFType[] { Active })
				.addEvent(4, new DOFType[] {})
				.addException(5, new DOFType[] {}).build();
		

		PROPERTY_ACTIVE = DEF.getProperty(PROPERTY_ACTIVE_ID);
		PROPERTY_ALARM_TIME = DEF.getProperty(PROPERTY_ALARM_TIME_ID);
		METHOD_SET_ALARM = DEF.getMethod(METHOD_SET_ALARM_ID);
		EVENT_ALARM = DEF.getEvent(EVENT_ALARM_ID);
		EXCEPTION_BAD_TIME_VALUE = DEF.getException(EXCEPTION_BAD_TIME_VALUE_ID);
	}
}
