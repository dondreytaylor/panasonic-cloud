import org.emitdo.oal.DOFInterface;
import org.emitdo.oal.DOFInterfaceID;
import org.emitdo.oal.DOFType;
import org.emitdo.oal.value.DOFUInt8;

public class SPO2Interface {
	public static final DOFType SPO2_PERCENT_TYPE = DOFUInt8.TYPE;
	public static final DOFInterface.Property SPO2_PERCENT_PROPERTY;
	public static final int SPO2_PERCENT_PROPERTY_ID = 2;
	public static final DOFInterface DEF;
	public static final DOFInterfaceID IID;
	
	static {
		IID = DOFInterfaceID.create("[63:{BA35}]");
		DEF = new DOFInterface.Builder(IID)
			.addProperty(SPO2_PERCENT_PROPERTY_ID, false, true, SPO2_PERCENT_TYPE)
			.build();
		
		SPO2_PERCENT_PROPERTY = DEF.getProperty(SPO2_PERCENT_PROPERTY_ID);
	}
}
