import org.emitdo.oal.DOFInterface;
import org.emitdo.oal.DOFInterfaceID;
import org.emitdo.oal.DOFType;
import org.emitdo.oal.value.DOFBoolean;
import org.emitdo.oal.value.DOFString;
import org.emitdo.oal.value.DOFString.Type;
import org.emitdo.oal.value.DOFUInt16;

public class DeviceInterface {
	
	public static final DOFType DEVICE_BATTERY_PERCENT_TYPE = DOFUInt16.TYPE;
	public static final DOFInterface.Property DEVICE_BATTERY_PERCENT_PROPERTY;
	public static final int DEVICE_BATTERY_PERCENT_PROPERTY_ID = 3;

	public static final DOFType DEVICE_NAME_TYPE = new DOFString.Type(DOFString.UTF_8, 255);
	public static final DOFInterface.Property DEVICE_NAME_PROPERTY;
	public static final int DEVICE_NAME_PROPERTY_ID = 4;

	public static final DOFType DEVICE_MANUFACTURER_TYPE =  new DOFString.Type(DOFString.UTF_8, 255);
	public static final DOFInterface.Property DEVICE_MANUFACTURER_PROPERTY;
	public static final int DEVICE_MANUFACTURER_PROPERTY_ID = 5;

	public static final DOFType DEVICE_CONNECTED_TYPE = DOFBoolean.TYPE;
	public static final DOFInterface.Property DEVICE_CONNECTED_PROPERTY;
	public static final int DEVICE_CONNECTED_PROPERTY_ID = 6;

	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID;
	
	static {
		IID = DOFInterfaceID.create("[63:{BA36}]");
		DEF = new DOFInterface.Builder(IID)
			.addProperty(DEVICE_BATTERY_PERCENT_PROPERTY_ID, false, true, DEVICE_BATTERY_PERCENT_TYPE)
			.addProperty(DEVICE_NAME_PROPERTY_ID, false, true, DEVICE_NAME_TYPE)
			.addProperty(DEVICE_MANUFACTURER_PROPERTY_ID, false, true, DEVICE_MANUFACTURER_TYPE)
			.addProperty(DEVICE_CONNECTED_PROPERTY_ID, false, true, DEVICE_CONNECTED_TYPE)
			.build();
		
		DEVICE_BATTERY_PERCENT_PROPERTY = DEF.getProperty(DEVICE_BATTERY_PERCENT_PROPERTY_ID);
		DEVICE_NAME_PROPERTY = DEF.getProperty(DEVICE_NAME_PROPERTY_ID);
		DEVICE_MANUFACTURER_PROPERTY = DEF.getProperty(DEVICE_MANUFACTURER_PROPERTY_ID);
		DEVICE_CONNECTED_PROPERTY = DEF.getProperty(DEVICE_CONNECTED_PROPERTY_ID);
	}
}
