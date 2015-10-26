import org.emitdo.oal.*;

public class ProxyNode {

	EMITTrainingDOFAbstraction dofAbstraction;
	
	public ProxyNode(){
		dofAbstraction = new EMITTrainingDOFAbstraction();
		dofAbstraction.createRouterDOF();
		DOFConnection con = dofAbstraction.createASConnection("localhost", 3567);
		DOFServer ser = dofAbstraction.createSecureServer("localhost", 3567);
		
		try { 
			ser.start(6000);
		}
		catch(Exception e ) {}

		System.out.println("server started: " + ser.getState().isStarted());
	}

	public static void main(String args[]) { 
		new ProxyNode();
	}
}