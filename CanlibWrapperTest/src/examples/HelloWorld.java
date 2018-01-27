package examples;
import core.Canlib;
import obj.CanlibException;
import obj.Handle;
import obj.Message;

public class HelloWorld {

	public static void main(String[] args) throws CanlibException {
		
		//Message info
		int msgId = 123;
		byte[] msgData = {0,1,2,3,4,5,6,7};
		int msgDlc = 8;
		int msgFlags = 0;
				
		System.out.println("Opening channel 0");
		Handle handle = new Handle(0);
		
		System.out.println("Setting channel bitrate");
		handle.setBusParams(Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		
		System.out.println("Going on bus");
		handle.busOn();


		handle.read();
		
		System.out.println("Writing a message to the channel");
		handle.write(new Message(msgId, msgData, msgDlc, msgFlags));
		
		System.out.println("Waiting until the message has been sent...");
		handle.writeSync(50);
		
		System.out.println("Going off bus");
		handle.busOff();
		
		System.out.println("Closing the channel");
		handle.close();
	}

}
