/**
 * EMIT / NJIT Capstone
 *
 * @author   Dondrey Taylor <ddt7@njit.edu>
 */


// Java Library Includes
import java.util.Date;
import java.util.List;

// EMIT Library ncludes
import org.emitdo.oal.*;
import org.emitdo.oal.DOF;
import org.emitdo.oal.DOFException;
import org.emitdo.oal.DOFObject;
import org.emitdo.oal.DOFObjectID;
import org.emitdo.oal.DOFResult;
import org.emitdo.oal.DOFSystem;
import org.emitdo.oal.DOFValue;
import org.emitdo.oal.value.DOFBoolean;
import org.emitdo.oal.value.DOFDateTime;
import org.emitdo.oal.value.DOFUInt8;
import org.emitdo.oal.value.DOFUInt16;
import org.emitdo.oal.value.DOFString;

// Redis
import redis.clients.jedis.Jedis;

public class Requestor {

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
	Jedis redis;
	String redisBeagleBoneDataPrefix 	= "emit:beaglebone:";
	String redisDeviceDataPrefix           	= "emit:device:";

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
	DOFSystem mySystem 	    	= null;
	DOFObject providerObject         	= null;
	DOFObjectID providerObjectID 	= DOFObjectID.create("[3:provider@emit-networking.org]");
	


	/*
	|--------------------------------------------------------------------------
	| Constructor
	|--------------------------------------------------------------------------
	|
	|  Used to setup Requestor Object. We'll initialize all of
	|  our class variables and establish a connection with EMIT
	|  here.
	|
	*/
	public Requestor(DOFSystem _system){
		mySystem = _system;
		init();
	}


	/*
	|--------------------------------------------------------------------------
	| Initialization
	|--------------------------------------------------------------------------
	| 
	| Establishes connection with EMIT and Redis. Begins providing 
	| various Interfaces.
	|
	*/	
	private void init() {
		providerObject = mySystem.createObject(providerObjectID);
		providerObject.beginSubscribe(PulseRateInterface.PULSE_RATE_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		providerObject.beginSubscribe(SPO2Interface.SPO2_PERCENT_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		providerObject.beginSubscribe(DeviceInterface.DEVICE_NAME_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		providerObject.beginSubscribe(DeviceInterface.DEVICE_MANUFACTURER_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		providerObject.beginSubscribe(DeviceInterface.DEVICE_CONNECTED_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		providerObject.beginSubscribe(DeviceInterface.DEVICE_BATTERY_PERCENT_PROPERTY, 200, DOF.TIMEOUT_NEVER, new SubscribeListener(), null);
		redis = new Jedis("emit.elasticbeanstalk.com",6379);
	}
	


	/*
	|--------------------------------------------------------------------------
	| Device Pulse Rate
	|--------------------------------------------------------------------------
	| 
	| Retrieves pulse rate value from oximeter provider
	|
	*/
	public int getOximeterDevicePulseRate() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(PulseRateInterface.PULSE_RATE_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFUInt16 value = (DOFUInt16)_value;
		return (int)value.get(); 
	}


	/*
	|--------------------------------------------------------------------------
	| Device SPO2 Level
	|--------------------------------------------------------------------------
	| 
	| Retrieves sp02 value from oximeter provider
	|
	*/
	public int getOximeterDeviceSPO2() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(SPO2Interface.SPO2_PERCENT_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFUInt8 value = (DOFUInt8)_value;
		return (int)value.get(); 
	}

	/*
	|--------------------------------------------------------------------------
	| Device Name
	|--------------------------------------------------------------------------
	| 
	| Retrieves device name  from oximeter provider
	|
	*/
	public String getOximeterDeviceName() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(DeviceInterface.DEVICE_NAME_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFString value = (DOFString)_value;
		return value.get().toString(); 
	}
	
	/*
	|--------------------------------------------------------------------------
	| Device Manufacturer Name
	|--------------------------------------------------------------------------
	| 
	| Retrieves device manufacturer name  from oximeter provider
	|
	*/
	public String getOximeterDeviceManufacturerName() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(DeviceInterface.DEVICE_MANUFACTURER_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFString value = (DOFString)_value;
		return (String)value.get(); 
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
	public boolean getOximeterDeviceConnectivity() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(DeviceInterface.DEVICE_CONNECTED_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFBoolean value = (DOFBoolean)_value;
		return (boolean)value.get(); 
	}


	/*
	|--------------------------------------------------------------------------
	| Device Battery Life
	|--------------------------------------------------------------------------
	| 
	| Retrieves device battery life as a percentage from oximeter provider
	|
	*/
	public int getOximeterDeviceBatteryPercent() throws DOFException{
		DOFResult<DOFValue> myResult;
		myResult = providerObject.get(DeviceInterface.DEVICE_BATTERY_PERCENT_PROPERTY, DOF.TIMEOUT_NEVER);
		DOFValue _value = myResult.get();
		DOFUInt16 value = (DOFUInt16)_value;
		return (int)value.get(); 
	}



	/*
	|--------------------------------------------------------------------------
	| Subscription Listener
	|--------------------------------------------------------------------------
	| 
	| Used to handle subscription operations that get triggered on
	| various property changes.
	|
	*/
	private class SubscribeListener implements DOFObject.SubscribeOperationListener {
		
		/*
		|--------------------------------------------------------------------------
		| Interface Method: Property Changed
		|--------------------------------------------------------------------------
		| 
		| Gets called each time the subscribed property changes value
		|
		*/
		@Override
		public void propertyChanged(DOFOperation.Subscribe operation, DOFProviderInfo providerInfo, DOFValue value, DOFException exception) {
			
			DOFInterface.Property property = operation.getProperty(); 
			
			if (property.equals(PulseRateInterface.PULSE_RATE_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_pulserate", value.toString() );
			}
			else if (property.equals(SPO2Interface.SPO2_PERCENT_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_spo2_percent", value.toString() );
			}
			else if (property.equals(DeviceInterface.DEVICE_NAME_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_name", value.toString() );
			}
			else if (property.equals(DeviceInterface.DEVICE_MANUFACTURER_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_manufacturer", value.toString() );
			}
			else if (property.equals(DeviceInterface.DEVICE_CONNECTED_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_connected", value.toString() );
			}
			else if (property.equals(DeviceInterface.DEVICE_BATTERY_PERCENT_PROPERTY)) 
			{
				redis.set(redisBeagleBoneDataPrefix + "device_battery_percent", value.toString() );
			}
		}


		/*
		|--------------------------------------------------------------------------
		| Interface Method: Complete
		|--------------------------------------------------------------------------
		| 
		| Gets called when the operation is complete
		|
		*/
		@Override
		public void complete(DOFOperation operation, DOFException exception) { 
		}
	}
}
