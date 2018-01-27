package examples_no_obj;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import core.Canlib;

public class CanDump {
	
	private static Canlib.ICanlib canInstance = Canlib.ICanlib.INSTANCE;
	
	public static void main(String[] args){
		int status;
		int hnd;
		
		//Setting up the channel and going on bus
		canInstance.canInitializeLibrary();
		hnd = canInstance.canOpenChannel(0, Canlib.canOPEN_ACCEPT_VIRTUAL);
		status = canInstance.canSetBusParams(hnd, Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		displayError(status);
		status = canInstance.canBusOn(hnd);
		displayError(status);
		
		//Start dumping messages
		dumpMessageLoop(hnd);
		
		//Going off bus and closing
		status = canInstance.canBusOff(hnd);
		displayError(status);
		status = canInstance.canClose(hnd);
		displayError(status);
	}
	
	/*
	 * Waits for messages and prints them to the screen
	 */
	private static void dumpMessageLoop(int handle){
		int status;
		boolean finished = false;
		
		IntByReference id = new IntByReference();
		byte[] data = new byte[8];
		IntByReference dlc = new IntByReference();
		IntByReference flags = new IntByReference();
		LongByReference time = new LongByReference();
		LongByReference timeout = new LongByReference(100);
		System.out.println("Channel 0 opened.");				
        System.out.println("   ID    DLC DATA                      Timestamp");
        
        while(!finished){
        	status = canInstance.canReadWait(handle, id, data, dlc, flags, time, timeout);
        	
        	if(status == Canlib.canOK){
        		dumpMessage(id.getValue(),data, dlc.getValue(), flags.getValue(), time.getValue());
        	}
        	
        	else if(status != Canlib.canERR_NOMSG){
        		displayError(status);
        		finished = true;
        	}
        }
	}
	
	 /*
     * Prints a received message
     */
	private static void dumpMessage(int id, byte[] data, int dlc, int flags, long time){
    	if((flags & Canlib.canMSG_ERROR_FRAME) == Canlib.canMSG_ERROR_FRAME){
    		System.out.println("***Error frame received***");
    	}
    	else{
	    	String idString = String.format("%8s", Integer.toBinaryString(id)).replace(' ', '0');
	    	System.out.printf("%s  %d  %2d %2d %2d %2d %2d %2d %2d %2d   %d\n",
	                idString, dlc, data[0], data[1], data[2], data[3], data[4],
	                data[5], data[6], data[7], time);
    	}
    }
	
	private static void displayError(int status){
		if(status < 0){
			System.out.println("Error: " + Canlib.getErrorText(status));
		}
	}
}
