package examples_no_obj;

import core.Canlib;

public class HelloWorld {
	
	public static void main(String[] args)  {
		//Message info
		int msgId = 123;
		byte[] msgData = {0,1,2,3,4,5,6,7};
		int msgDlc = 8;
		int status, hnd;
		Canlib.ICanlib canInstance = Canlib.ICanlib.INSTANCE;
		
		System.out.println("Initializing library");
		canInstance.canInitializeLibrary();
		
		System.out.println("Opening channel 0");
		hnd = canInstance.canOpenChannel(0, Canlib.canOPEN_ACCEPT_VIRTUAL);
		displayError(hnd);
		
		System.out.println("Setting channel bitrate");
		status = canInstance.canSetBusParams(hnd, Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		displayError(status);
		
		System.out.println("Going on bus");
		status = canInstance.canBusOn(hnd);
		displayError(status);

		System.out.println("Writing a message to the channel");
		status= canInstance.canWrite(hnd, msgId, msgData, msgDlc, 0);	
		displayError(status);

		System.out.println("Waiting until the message has been sent...");
		status = canInstance.canWriteSync(hnd, 50);
		
		System.out.println("Going off bus");
		status = canInstance.canBusOff(hnd);

		System.out.println("Closing the channel");
		status= canInstance.canClose(hnd);
		
		System.out.println("finished");
	}
	
	private static void displayError(int status){
		if(status < 0){
			System.out.println("Error: " + Canlib.getErrorText(status));
		}
	}
	
	
}
