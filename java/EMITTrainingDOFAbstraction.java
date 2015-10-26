import java.util.Collection;
import java.util.HashMap;

import org.emitdo.oal.DOF;
import org.emitdo.oal.DOFAddress;
import org.emitdo.oal.DOFConnection;
import org.emitdo.oal.DOFCredentials;
import org.emitdo.oal.DOFDomain;
import org.emitdo.oal.DOFDomain.State;
import org.emitdo.oal.DOFException;
import org.emitdo.oal.DOFObjectID;
import org.emitdo.oal.DOFProtocolNegotiator;
import org.emitdo.oal.DOFServer;
import org.emitdo.oal.DOFSystem;
import org.emitdo.oal.DOF.SecurityDesire;
import org.emitdo.sdk.ReconnectingStateListener;
import org.emitdo.sdk.RestartingStateListener;
import org.emitdo.transport.inet.InetTransport;

public class EMITTrainingDOFAbstraction implements DOFDomain.StateListener{
	DOF dof;
	DOF.Config myDofConfig;
	HashMap<String, DOFSystem> systemMap = new HashMap<String, DOFSystem>(3);
	public static final int TIMEOUT = 60000; //This sample uses a very long timeout to ensure the DOFSystem creation does not fail.
	private RestartingStateListener restartingListener = null;
	private ReconnectingStateListener reconnectingListener = null;
	
	
	public EMITTrainingDOFAbstraction(){

	}
	
	public DOF createDOF(){
		myDofConfig = new DOF.Config.Builder()
			.build();
	
		dof = new DOF(myDofConfig);
		return dof;
	}
	
	public DOF createRouterDOF(){
		myDofConfig = new DOF.Config.Builder()
			.setRouter(true)
			.build();
	
		dof = new DOF(myDofConfig);
		return dof;
	}

	public DOFConnection createSecureConnection(String address, int port){
		DOFAddress myAddress = InetTransport.createAddress(address, port);

		/* STM1: 12. Add a DOFCredentials to a DOFConnection.Config. */
		DOFConnection.Config myConnectionConfig = new DOFConnection.Config.Builder(DOFConnection.Type.STREAM, myAddress)
		   // .setCredentials(credentials) //Add the credentials to the connection config
		  //  .setSecurityDesire(SecurityDesire.SECURE) //Specify this connection should only connect to a secure server
			.build();
		
		DOFConnection myConnection = dof.createConnection(myConnectionConfig);

		//Add the reconnecting listener
		if(reconnectingListener == null)
			reconnectingListener = new ReconnectingStateListener();
		myConnection.addStateListener(reconnectingListener);
		
		return myConnection;
	}
	
	public DOFConnection createASConnection(String address, int port){
		DOFAddress myAddress = InetTransport.createAddress(address, port);
		
		DOFConnection.Config connectionConfig = new DOFConnection.Config.Builder(DOFConnection.Type.STREAM, myAddress)
		 //   .addTrustedDomains(DOFObjectID.DOMAIN_BROADCAST) //This tells the connection that it can resolve credentials on any domain
		 //   .setProtocolNegotiator(DOFProtocolNegotiator.createDefaultASOnly()) //This make it so the connection can only send secure related traffic.
			.build();
		
		DOFConnection connectionToAS = dof.createConnection(connectionConfig);
		
		if(reconnectingListener == null)
			reconnectingListener = new ReconnectingStateListener();
		connectionToAS.addStateListener(reconnectingListener);
		
		return connectionToAS;
	}
	
	public DOFServer createSecureServer(String address, int port){
		DOFServer.Config myServerConfig;
		DOFAddress myAddress;
		myAddress = InetTransport.createAddress(address, port);
		
		/* STM1: 13. Add a DOFCredentials to a DOFServer.Config. */
		myServerConfig = new DOFServer.Config.Builder(DOFServer.Type.STREAM, myAddress)
		   // .setSecurityDesire(SecurityDesire.SECURE_ANY)
		   // .addCredentials(myCredentials)
			.build();
		DOFServer myServer = dof.createServer(myServerConfig);
		
		if(restartingListener == null)
			restartingListener = new RestartingStateListener();
		myServer.addStateListener(restartingListener);
		
		return myServer;
	}
	
	public DOFSystem createDOFSystem(String _name){
		DOFSystem returnSystem;
		DOFSystem.Config systemConfig;
		if(dof != null){
			systemConfig = new DOFSystem.Config.Builder()
				.setName(_name)
				.build();
			
			try {
				returnSystem = dof.createSystem(systemConfig, 0);
				systemMap.put(_name, returnSystem);
				return returnSystem;
			} catch (DOFException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public DOFSystem createSecureDOFSystem(String _name,  int timeout){
		DOFSystem returnSystem;
		DOFSystem.Config systemConfig;
				
		if(dof != null){
			systemConfig = new DOFSystem.Config.Builder()
			  //  .setCredentials(systemCredentials) //add the credentials to the system config
				.setName(_name)
				.build();

			try {
				//Creates the system. The credentials must be resolved so the timeout must be greater than 0.
				//A connection to the AS is required or credentials cannot be resolved and the system creation will fail.
				returnSystem = dof.createSystem(systemConfig, TIMEOUT);
				systemMap.put(_name, returnSystem);
				return returnSystem;
			} catch (DOFException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public DOFSystem getDOFSystem(String _name){
		return systemMap.get(_name);
	}
	
	public void destroy(){
		Collection<DOFSystem> systemList = systemMap.values(); 
		
		for(DOFSystem system: systemList){
			system.destroy();
		}
		dof.destroy();
	}

	public DOFDomain setDomainCredential(DOFCredentials credentials){
		DOFDomain.Config c = new DOFDomain.Config.Builder(credentials)
			.build(); 
		
		DOFDomain domain = dof.createDomain(c);
		return domain;
	}
	
	public boolean isDomainConnected(DOFDomain domain){
		if(domain != null)
			return domain.getState().isConnected();
		else
			return false;
	}

	@Override
	public void stateChanged(DOFDomain domain, State state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removed(DOFDomain domain, DOFException exception) {
		// TODO Auto-generated method stub
		
	}
}


