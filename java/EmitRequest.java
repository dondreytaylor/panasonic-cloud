/**
 * EMIT / NJIT Capstone
 *
 * @author   Dondrey Taylor <ddt7@njit.edu>
 */

// Java Library Includes
import java.lang.Thread;

// EMIT Library Includes
import org.emitdo.oal.*;


public class EmitRequest  {
		
	public static void main(String args[]) 
	{
		// // EMIT DOF Helper
		EMITDOFAbstraction dofAbstraction = new EMITDOFAbstraction();
		
		// // Initialize requestor
		Requestor requestor = new Requestor(dofAbstraction.createDOFSystem("requestor"));     
	}
}
