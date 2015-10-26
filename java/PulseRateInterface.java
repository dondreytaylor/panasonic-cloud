import org.emitdo.oal.DOFInterface;
import org.emitdo.oal.DOFInterfaceID;
import org.emitdo.oal.DOFType;
import org.emitdo.oal.value.DOFUInt16;

public class PulseRateInterface {
	public static final DOFType PULSE_RATE_TYPE = DOFUInt16.TYPE;
	public static final DOFInterface.Property PULSE_RATE_PROPERTY;
	public static final int PULSE_RATE_PROPERTY_ID = 1;
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID;
	
	static {
		IID = DOFInterfaceID.create("[63:{BA34}]");
		DEF = new DOFInterface.Builder(IID)
			.addProperty(PULSE_RATE_PROPERTY_ID, false, true, PULSE_RATE_TYPE)
			.build();
		
		PULSE_RATE_PROPERTY = DEF.getProperty(PULSE_RATE_PROPERTY_ID);
	}
}
