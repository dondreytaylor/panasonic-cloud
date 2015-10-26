/**
 * EMIT / NJIT Capstone
 *
 * @author   Dondrey Taylor <ddt7@njit.edu>
 */

// Java Library Includes
import java.util.Date;
import java.util.List;
import java.util.Random;

// Emit Library Includes
import org.emitdo.oal.DOF;
import org.emitdo.oal.DOFObject;
import org.emitdo.oal.DOFObjectID;
import org.emitdo.oal.DOFSystem;
import org.emitdo.oal.DOFValue;
import org.emitdo.oal.DOFInterface.Method;
import org.emitdo.oal.DOFInterface.Property;
import org.emitdo.oal.DOFOperation.Provide;
import org.emitdo.oal.value.DOFBoolean;
import org.emitdo.oal.value.DOFDateTime;
import org.emitdo.oal.DOFRequest;
import org.emitdo.oal.value.DOFUInt8;
import org.emitdo.oal.value.DOFUInt16;
import org.emitdo.oal.value.DOFString;

// Redis
import redis.clients.jedis.Jedis;

public class Provider {

	/*
	|--------------------------------------------------------------------------
	| Redis (Jedis is a redis client)
	|--------------------------------------------------------------------------
	| This will be used as a temporary datastore for 
	|  caching purposes. Data sent from EMIT to the 
	|  Remote healthcare server will be cached in Redis
	|  and read by the Node.js instance.
	|
	*/
	//Jedis redis;

	/*
	|--------------------------------------------------------------------------
	| DOF Setup
	|--------------------------------------------------------------------------
	|
	|  This is where we will declare our DOF class variables
	|  which we will later use to communicate with the EMIT 
	|  network.
	|
	*/
	DOFSystem mySystem 		= null;
	DOFObject oximeterProvider 	= null;
	DOFObjectID myOID 			= DOFObjectID.create("[3:provider@emit-networking.org]");
	


	/*
	|--------------------------------------------------------------------------
	| Oximeter Properties
	|--------------------------------------------------------------------------
	|  All data properties that pertain to the Oximeter. As the
	|  state of the oximeter changes, these properties will update
	|  accordingly.
	|
	*/
	String device_name;
	String device_manufacturer;
	int device_battery_percent;
	Boolean device_connected;
	short device_spo2_percent;
	int device_pulserate;



	/*
	|--------------------------------------------------------------------------
	| Constructor
	|--------------------------------------------------------------------------
	|
	|  Used to setup Provider Object. We'll initialize all of
	|  our class variables and establish a connection with EMIT
	|  here.
	|
	*/
	public Provider(DOFSystem _system)
	{
		mySystem = _system;
		init();
	}


	/*
	|--------------------------------------------------------------------------
	| Initialization
	|--------------------------------------------------------------------------
	| 
	| Establishes connection with EMIT. Begins providing 
	| various Interfaces.
	|
	*/	
	private void init()
	{
		initDeviceState();
		oximeterProvider = mySystem.createObject(myOID);
		oximeterProvider.beginProvide(PulseRateInterface.DEF, DOF.TIMEOUT_NEVER, new ProviderListener(), null);
		oximeterProvider.beginProvide(SPO2Interface.DEF, DOF.TIMEOUT_NEVER, new ProviderListener(), null);
		oximeterProvider.beginProvide(DeviceInterface.DEF, DOF.TIMEOUT_NEVER, new ProviderListener(), null);
	}


	/*
	|--------------------------------------------------------------------------
	| Initialization
	|--------------------------------------------------------------------------
	| 
	| Sets default values for device state
	|
	*/
	private void initDeviceState() 
	{
		device_name 			= "";
		device_manufacturer 		= "";
		device_battery_percent 	= 0;
		device_connected 		= false;
		device_spo2_percent 	= 0;
		device_pulserate 		= 0;
	}

	/*
	|--------------------------------------------------------------------------
	| Update Pulse Rate
	|--------------------------------------------------------------------------
	| 
	| Reads current value of pulse rate and updates provider
	| pulse rate value. 
	|
	*/	
	public void syncPulseRate(int value) 
	{ 
		device_pulserate = value;
		oximeterProvider.changed(PulseRateInterface.PULSE_RATE_PROPERTY);
	}
	

	/*
	|--------------------------------------------------------------------------
	| Update SPO2 Percent
	|--------------------------------------------------------------------------
	| 
	| Reads current value of SPO2 and updates provider
	| SPO2 value.
	|
	*/	
	public void syncSPO2(short value) 
	{ 
		device_spo2_percent = value;
		oximeterProvider.changed(SPO2Interface.SPO2_PERCENT_PROPERTY);
	}


	/*
	|--------------------------------------------------------------------------
	| Update Device Name
	|--------------------------------------------------------------------------
	| 
	| Reads current value of device name and updates provider
	| device name value.
	|
	*/	
	public void syncDeviceName(String name) 
	{ 
		device_name = name;
		oximeterProvider.changed(DeviceInterface.DEVICE_NAME_PROPERTY);
	}

	/*
	|--------------------------------------------------------------------------
	| Update Device Manufacturer Name
	|--------------------------------------------------------------------------
	| 
	| Reads current value of device manufacturer and updates provider
	| device manufacturer value.
	|
	*/	
	public void syncDeviceManufacturer(String manufacturer) 
	{ 
		device_manufacturer = manufacturer;
		oximeterProvider.changed(DeviceInterface.DEVICE_MANUFACTURER_PROPERTY);
	}


	/*
	|--------------------------------------------------------------------------
	| Update Device Connectivity
	|--------------------------------------------------------------------------
	| 
	| Reads current value of device connectivity and updates provider
	| device connectivity value.
	|
	*/	
	public void syncDeviceConnectivity(Boolean isConnected) 
	{ 
		device_connected = isConnected;
		oximeterProvider.changed(DeviceInterface.DEVICE_CONNECTED_PROPERTY);
	}


	/*
	|--------------------------------------------------------------------------
	| Update Device Battery
	|--------------------------------------------------------------------------
	| 
	| Reads current value of device battery percent and updates provider
	| device battery percent value.
	|
	*/	
	public void syncDeviceBatteryPercent(int batteryPercent) 
	{ 
		device_battery_percent = batteryPercent;
		oximeterProvider.changed(DeviceInterface.DEVICE_BATTERY_PERCENT_PROPERTY);
	}



	/*
	|--------------------------------------------------------------------------
	| Generates Random Numner (Used Testing Purposes Only)
	|--------------------------------------------------------------------------
	| 
	| Generates a random number between two integers.
	|
	*/	
	public int generateRandomNumber(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt( (max - min) + 1) + min;
		return randomNum; 
	}


	/*
	|--------------------------------------------------------------------------
	| Device Pulse Rate
	|--------------------------------------------------------------------------
	| 
	| Retrieves pulse rate value from oximeter provider
	|
	*/
	public int getOximeterDevicePulseRate() {
		return device_pulserate;
	}


	/*
	|--------------------------------------------------------------------------
	| Device SPO2 Level
	|--------------------------------------------------------------------------
	| 
	| Retrieves sp02 value from oximeter provider
	|
	*/
	public int getOximeterDeviceSPO2() {
		return device_spo2_percent;
	}

	/*
	|--------------------------------------------------------------------------
	| Device Name
	|--------------------------------------------------------------------------
	| 
	| Retrieves device name  from oximeter provider
	|
	*/
	public String getOximeterDeviceName() {
		return device_name;
	}
	
	/*
	|--------------------------------------------------------------------------
	| Device Manufacturer Name
	|--------------------------------------------------------------------------
	| 
	| Retrieves device manufacturer name  from oximeter provider
	|
	*/
	public String getOximeterDeviceManufacturerName() {
		return device_manufacturer;
	}

	/*
	|--------------------------------------------------------------------------
	| Device Connectivity Status
	|--------------------------------------------------------------------------
	| 
	| Retrieves device connectivity status. 
	| True - Connected
	| False - Disconnected
	|
	*/
	public boolean getOximeterDeviceConnectivity() {
		return device_connected;
	}


	/*
	|--------------------------------------------------------------------------
	| Device Battery Life
	|--------------------------------------------------------------------------
	| 
	| Retrieves device battery life as a percentage from oximeter provider
	|
	*/
	public int getOximeterDeviceBatteryPercent() {
		return device_battery_percent;
	}

	/*
	|--------------------------------------------------------------------------
	| Provider Listener
	|--------------------------------------------------------------------------
	| 
	| Handles all requests made to provide
	|
	*/
	public class ProviderListener extends DOFObject.DefaultProvider {

		/*
		|--------------------------------------------------------------------------
		| Interface Method: GET
		|--------------------------------------------------------------------------
		| 
		| Handles GET requests based on the interface property
		| being requested. Each property if checked againsts its ID
		|
		*/
		@Override
		public void get(Provide operation, DOFRequest.Get request, Property property) 
		{
			if (property.equals(PulseRateInterface.PULSE_RATE_PROPERTY)) 
			{
				DOFUInt16 value = new DOFUInt16(device_pulserate);
				request.respond(value);
			}
			else if (property.equals(SPO2Interface.SPO2_PERCENT_PROPERTY)) 
			{
				DOFUInt8 value = new DOFUInt8(device_spo2_percent);
				request.respond(value);
			}
			else if (property.equals(DeviceInterface.DEVICE_NAME_PROPERTY)) 
			{
				DOFString value = new DOFString(device_name);
				request.respond(value);
			}
			else if (property.equals(DeviceInterface.DEVICE_MANUFACTURER_PROPERTY)) 
			{
				DOFString value = new DOFString(device_manufacturer);
				request.respond(value);
			}
			else if (property.equals(DeviceInterface.DEVICE_CONNECTED_PROPERTY)) 
			{
				DOFBoolean value = new DOFBoolean(device_connected);
				request.respond(value);
			}
			else if (property.equals(DeviceInterface.DEVICE_BATTERY_PERCENT_PROPERTY)) 
			{
				DOFUInt16 value = new DOFUInt16(device_battery_percent);
				request.respond(value);
			}
		}
		

		/*
		|--------------------------------------------------------------------------
		| Interface Method: SET
		|--------------------------------------------------------------------------
		| 
		| Handles SET requests
		|
		*/
		@Override
		public void set(Provide operation, DOFRequest.Set request, Property property, DOFValue value) 
		{
			request.respond();
		}


		/*
		|--------------------------------------------------------------------------
		| Interface Method: INVOKE
		|--------------------------------------------------------------------------
		| 
		| Handles INVOKE requests
		|
		*/		
		@Override
		public void invoke(Provide operation, DOFRequest.Invoke request, Method method, List<DOFValue> parameters) 
		{
			request.respond();
		}
	}
}
