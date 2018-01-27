package examples;


import core.Canlib;
import core.Canlib.ICanlib.canUserIoPortData;
import obj.CanlibException;
import obj.Handle;
import obj.Message;

public class CanIoCtlLibTest {
	
	public static void main(String[] args) throws CanlibException{
		System.out.println("This is a demo of canIoCtl.");
		
		//Setting up handles
		Handle h0 = new Handle(0);
		Handle h1 = new Handle(1);
		Handle h2 = new Handle(2);
		Handle h02 = new Handle(0);
		
		Message m = new Message(10000, new byte[]{1,2,3,4,5,6,7,8}, 8, 0);
		
		h0.setBusParams(Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		h1.setBusParams(Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		h2.setBusParams(Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		h02.setBusParams(Canlib.canBITRATE_250K, 0, 0, 0, 0, 0);
		
		h0.busOn();
		h1.busOn();
		h2.busOn();
		
		//Demonstrating PreferExt and PreferStd 
		System.out.println("\nSending a message with id 10000, EXT flag off");
		System.out.println(messageInfo);
		sendAndReceive(h0, h1, m);
		
		h0.preferExt();
		sendAndReceive(h0, h1, m);
		
		h0.preferStd();
		sendAndReceive(h0, h1, m);
		
		
		//Creating an error
		System.out.println("\nCreating transmit error");
		h1.busOff();
		try{
		h0.writeWait(new Message(2, new byte[]{1},1,0));
		} 
		catch(Exception e){
			//Nothing
		}
		//Reading number of errors
		
		int txerrors = h0.readErrorCounters()[0];
		System.out.println("Number of transmit errors: " + txerrors);

		//Clearing error counter
		System.out.println("Clearing error counter");
		h0.clearErrorCounter();
		txerrors = h0.readErrorCounters()[0];
		System.out.println("Number of transmit errors: " + txerrors);		
		h1.busOn();
		//Read the message to clear the RX buffer before the next test
		h1.readWait(50); 
		
		
		//Changing timer scale
		System.out.println("Current timer scale: " + h0.getTimerScale());
		System.out.println("Changing timer scale to 100 ms");
		h1.setTimerScale(100000);
		System.out.println("Sending 5 messages 50ms apart, notice the timestamp.");
        for (int i = 0; i < 5; i++){
            sendAndReceive(h0, h1, m);
           try {
			Thread.sleep(50);
           } catch (InterruptedException e) {
			e.printStackTrace();
           }
        }
        //Setting the timer scale back to 1 ms
        h1.setTimerScale(1000);
        
        
        /*
         * Disabling timer reset on bus on
         */
        System.out.println("\nCurrent time on channel 0: " + h0.readkvTimer());
        System.out.println("Disabling timer reset, going bus off and on");
        h0.setClockResetAtBusOn(false);
        h0.busOff();
        h0.busOn();
        System.out.println("Current time on channel 0: " + h0.readkvTimer());
        System.out.println("Enabling timer reset, going bus off and on");
        h0.setClockResetAtBusOn(true);
        h0.busOff();
        h0.busOn();
        System.out.println("Current time on channel 0: " + h0.readkvTimer());
        
        
        /*
         * Testing Transmit ACKs
         */
        System.out.println("\nCurrent TXACK level for channel 0 (0: off, 1: on): " + h0.getTXACK());
        System.out.println("Turning on TXACK for channel 0");
        h0.setTXACK(1);
        System.out.println("Current TXACK level for channel 0 (0: off, 1: on): " + h0.getTXACK());
        System.out.println("Sending message from 0 to 1");
        sendAndReceive(h0, h1, m);
        //This next message is a transmit acknowledgment
        System.out.println("Reading the TXACK");
        dumpMessage(h0.readWait(50));
        //Disabling transmit ACKs again
        h0.setTXACK(0);
        
        //Showing the read queue size and how to flush it
        System.out.println("\nSending a message from channel 0 to channel 1");
        h0.writeWait(m);
        System.out.println("RX Queue level on channel 1: " + h1.getRXBufferLevel());
        System.out.println("Flushing the RX buffer");
        h1.flushRXBuffer();
        System.out.println("RX Queue level on channel 1: " + h1.getRXBufferLevel());
        //Setting a limit on the RX buffer
        System.out.println("Setting a limit at 5 messages on the receive buffer on channel 1");
        h1.setRXQueueSize(5);
        //Trying to send more messages to channel than the buffer allows
        System.out.println("Sending 10 messages to channel 1");
        for(int i = 0; i < 10; i++){
            h0.writeWait(m);
        }
        System.out.println("RX Queue level on channel 1: " + h1.getRXBufferLevel());
        System.out.println("Flushing the RX buffer");
        h1.flushRXBuffer();
        System.out.println("RX Queue level on channel 1: " + h1.getRXBufferLevel());
        h1.setRXQueueSize(10);
        
        
		//Testing transmit requests
        System.out.println("\nTurning on transmit requests for channel 0");
        h0.setTransmitRequest(true);
        System.out.println("Sending message from channel 0 to 1, notice the second message");
        sendAndReceive(h0, h1, m);
        dumpMessage(h0.readWait(50));
        h0.setTransmitRequest(false);
        
        //Testing txEcho
        System.out.println("\nTurning on transmit echo");
        h02.busOn();
        h0.setLocalTXEcho(true);
        System.out.println("Sending a message from handle 0, notice that the second handle receives it");
        h0.writeWait(m);
        dumpMessage(h02.read());
        h0.setLocalTXEcho(false);
        h1.flushRXBuffer();
        h02.busOff();
        
        
        //Testing transmit interval
        System.out.println("\nCurrent transmit interval:" + h0.getTXInterval());
        System.out.println("Setting transmit interval to 10 ms");
        h0.setTXInterval(10000);
        System.out.println("Sending five messages and receiving them, notice the timestamps.");
        for(int i = 0; i < 5; i++){
        	h0.write(new Message(i, new byte[]{0}, 1, 0));
        }
        h0.writeSync(1000);
		System.out.println(messageInfo);
        while(h1.hasMessage()){
        	dumpMessage(h1.read());
        }
        
        
        //Disabling error frame reporting
        System.out.println("\nDisabling error frame reporting for channel 1");
        h1.setErrorFramesReporting(false);
        System.out.println("Sending an error frame to channel 1");
        h0.writeWait(new Message(123, new byte[]{1,2,3},0,Canlib.canMSG_ERROR_FRAME));
        System.out.println("Checking for messages on channel 1 RX queue: " + h1.getRXBufferLevel() +
        	" messages found");
        h1.setErrorFramesReporting(true);
        
        //Testing channel quality	
        System.out.println("\nChannel 2 quality: " + h2.getChannelQuality());

        //Reads the round trip time
        System.out.println("RTT for device on channel 2: " + h2.getRoundTripTime());

        //Gets the devname for a remote device
        System.out.println("\nChannel 2 quality: " + h2.getDevNameASCII());

        //Outputs the bus type
        System.out.println("Bus type for channel 0: " + h2.getBusTypeAsString());

        //Checking when remote device was last seen
        System.out.println("Time in ms since device on channel was last seen: " + h2.getTimeSinceLastSeen());
        
        
        //Getting user IO port data (does not work on all devices, uncomment if your device supports this)
        //canUserIoPortData user = h2.getIoPortData();
        //Console.WriteLine("Port nr: {0}, Port value: {1}", user.portNo, user.portValue);
        //SetUserIoPortData(hnd0, user);
        
        //Display throttle value
        System.out.println("\nThrottle value for device on channel 0: " + h0.getThrottleScaled());
        
        System.out.println("Trying to set throttle value to 40");
        h0.setThrottleScaled(40);
        
        System.out.println("New throttle value for device on channel 0: " + h0.getThrottleScaled());
        
        
		//Going bus off and closing
		h0.busOff();
		h1.busOff();
		h2.busOff();
		h02.busOff();
		h0.close();
		h1.close();
		h2.close();
		h02.close();
	}
	
	
	
	/*
	 * Utility methods
	 */
	private static void sendAndReceive(Handle h0, Handle h1, Message m) throws CanlibException{
		h0.writeWait(m);
		if(h1.hasMessage()){
			dumpMessage(h1.read());
		}
		else{
			System.out.println("Transmission failed");
		}
	}
	
	private static final String messageInfo = "      ID DLC  DATA                    FLAGS  TIME";
	
	private static void dumpMessage(Message m){
		if(m.isErrorFrame()){
			System.out.println("***Error frame received***");
		}
		else{
	    	System.out.printf("ID: %8d  dlc: %d  data: %2d %2d %2d %2d %2d %2d %2d %2d   flags: %d  time:  %d\n",
	                m.id, m.length, m.data[0], m.data[1], m.data[2], m.data[3], m.data[4],
	                m.data[5], m.data[6], m.data[7], m.flags, m.time);
		}
    }

}
