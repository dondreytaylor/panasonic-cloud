/**
 * EMIT / NJIT Capstone
 *
 * @author   Dondrey Taylor <ddt7@njit.edu>
 */

// Java Library Includes
import java.lang.Thread;

// EMIT Library Includes
import org.emitdo.oal.DOFException;

// Redis
import redis.clients.jedis.Jedis;
import org.emitdo.oal.*;

public class EmitProvide  {
		
	public static void main(String args[]) 
	{
		// EMIT DOF Helper
		EMITDOFAbstraction dofAbstraction = new EMITDOFAbstraction();

		// Initialize provider
		Provider provider = new Provider(dofAbstraction.createDOFSystem("provider"));
		Requestor requestor = new Requestor(dofAbstraction.createDOFSystem("requestor"));
		
		// Amount of time we'll wait before reading device information
		int sleepTime = 1000; 

		// Redis
		Jedis redis = new Jedis("emit.elasticbeanstalk.com", 6379); 

		// Prefix
		String prefix = "emit:device:"; 

		// Device Information
		String devicePulseRate;
		String deviceSPO2;
		String deviceName;
		String deviceManufacturer;
		String deviceBattery;
		String deviceConnect;

		
		// Start device read loop
		while(true) { 

			deviceConnect 	= redis.get(prefix + "device_connected");
			provider.syncDeviceConnectivity(Boolean.parseBoolean(deviceConnect));

			if (Boolean.parseBoolean(deviceConnect)) 
			{ 
				devicePulseRate 	= redis.get(prefix + "device_pulserate");
				deviceSPO2 		= redis.get(prefix + "device_spo2_percent");
				deviceName 		= redis.get(prefix + "device_name"); 
				deviceManufacturer 	= redis.get(prefix + "device_manufacturer");
				deviceBattery 		= redis.get(prefix + "device_battery_percent");
			

				if (Integer.parseInt(devicePulseRate) != provider.getOximeterDevicePulseRate())  {
					provider.syncPulseRate(Integer.parseInt(devicePulseRate));
				}

				if (Short.parseShort(deviceSPO2) != provider.getOximeterDeviceSPO2()) { 
					provider.syncSPO2(Short.parseShort(deviceSPO2));
				}

				if (!deviceName.equals(provider.getOximeterDeviceName())) { 
					provider.syncDeviceName(deviceName);
				}

				if (!deviceManufacturer.equals(provider.getOximeterDeviceManufacturerName())) { 
					provider.syncDeviceManufacturer(deviceManufacturer);
				}

				if (Integer.parseInt(deviceBattery) != provider.getOximeterDeviceBatteryPercent()) { 
					provider.syncDeviceBatteryPercent(Integer.parseInt(deviceBattery));
				}
			}

			// Sleep for a while
			//System.out.println("Sleeping for " + sleepTime + "ms");

			try { 
				Thread.sleep(sleepTime);
			}
			catch(Exception e) { 
			}
			
		}
	}
}
