package obj;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import core.Canlib;


public class Handle {
	private static final long DEFAULT_TIMEOUT = 100;
	private final int handle;
	private final Canlib.ICanlib canInstance = Canlib.ICanlib.INSTANCE;
	
	
	/**
	 * Wraps the Canlib handles.
	 * Instead of having static methods which act on a handle, 
	 * we use instance methods which are defined in this class.
	 * @param channel	The CAN channel number
	 * @throws CanlibException
	 */
	public Handle(int channel) throws CanlibException{
		canInstance.canInitializeLibrary();
		int hnd = canInstance.canOpenChannel(channel, Canlib.canOPEN_ACCEPT_VIRTUAL);
		if(hnd >= 0){
			this.handle = hnd;
		}
		else{
			throw new CanlibException(Canlib.getErrorText(hnd));
		}
	}
	
	/**
	 * Wraps the Canlib handles.
	 * Instead of having static methods which act on a handle, 
	 * we use instance methods which are defined in this class.
	 * @param channel	The CAN channel number
	 * @param flags		A combination of canOPEN_xxx flags
	 * @throws CanlibException
	 */
	public Handle(int channel, int flags) throws CanlibException{
		canInstance.canInitializeLibrary();
		int hnd = canInstance.canOpenChannel(channel, flags);
		if(hnd >= 0){
			this.handle = hnd;
		}
		else{
			throw new CanlibException(Canlib.getErrorText(hnd));
		}
	}	
	
	/**
	 * Creates a handle to the first available channel (if any) on a specified device
	 * @param ean	The EAN of the device
	 * @param serialNo The device's serial number
	 */
	public Handle(String ean, String serialNo) throws CanlibException{
		canInstance.canInitializeLibrary();
		IntByReference number = new IntByReference();
		canInstance.canGetNumberOfChannels(number);
		int noOfChannels = number.getValue();
		int hnd = -1;
		ean = ean.replace("-", "").replace(" ", "");
		serialNo = serialNo.trim();
		
		for(int i = 0; i < noOfChannels; i++){
			LongByReference p = new LongByReference();
			String e, s;
			canInstance.canGetChannelData(i, Canlib.canCHANNELDATA_CARD_UPC_NO, p, 8);
			e = Long.toHexString(p.getValue());
			canInstance.canGetChannelData(i, Canlib.canCHANNELDATA_CARD_SERIAL_NO, p, 8);
			s = Long.toString(p.getValue());
			
			if(e.equals(ean) && s.equals(serialNo)){
				hnd = canInstance.canOpenChannel(i, 0);
				if(hnd >= 0){
					break;
				}
			}
		}
		if(hnd >= 0){
			this.handle = hnd;
		}
		else{
			throw new CanlibException("No such device found");
		}
	}
	
	/**
	 * Creates a handle to a specific channel on a specified device
	 * @param ean	The EAN of the device
	 * @param serialNo The device's serial number
	 * @param chanNumber The local channel number on the device 
	 */
	public Handle(String ean, String serialNo, int chanNumber) throws CanlibException{
		canInstance.canInitializeLibrary();
		IntByReference number = new IntByReference();
		canInstance.canGetNumberOfChannels(number);
		int noOfChannels = number.getValue();
		int hnd = -1;
		ean = ean.replace("-", "").replace(" ", "");
		serialNo = serialNo.trim();
		
		for(int i = 0; i < noOfChannels; i++){
			LongByReference p = new LongByReference();
			IntByReference chanRef = new IntByReference();
			String e, s;
			canInstance.canGetChannelData(i, Canlib.canCHANNELDATA_CARD_UPC_NO, p, 8);
			e = Long.toHexString(p.getValue());
			canInstance.canGetChannelData(i, Canlib.canCHANNELDATA_CARD_SERIAL_NO, p, 8);
			s = Long.toString(p.getValue());
			canInstance.canGetChannelData(i, Canlib.canCHANNELDATA_CHAN_NO_ON_CARD, chanRef, 4);
			int localNo = chanRef.getValue();
			
			if(e.equals(ean) && s.equals(serialNo) && localNo == chanNumber){
				hnd = canInstance.canOpenChannel(i, 0);
				if(hnd >= 0){
					break;
				}
			}
		}
		if(hnd >= 0){
			this.handle = hnd;
		}
		else{
			throw new CanlibException("No such device found");
		}
	}
	
	
	/*
	 * Channel setup and teardown
	 */
	/**
	 * 
	 * @param bitrate The desired bitrate (in bits per second)
	 * @throws CanlibException
	 */
	public void setBitrate(int bitrate) throws CanlibException{
		if( bitrate < 0){
			throw new IllegalArgumentException("Illegal bitrate");
		}
		else{
			checkStatus(canInstance.canSetBitrate(handle, bitrate));
		}
	}
	/**
	 * 
	 * @param freq Any of the Canlib.canBITRATE_xxx constants, or the bitrate in bits per second
	 * @param tseg1 Time segment 1, that is, the number of quanta from (but not including) the Sync Segment to the sampling point. 
	 * @param tseg2 Time segment 2, that is, the number of quanta from the sampling point to the end of the bit. 
	 * @param sjw The Synchronization Jump Width; can be 1,2,3, or 4. 
	 * @param noSamp The number of sampling points; can be 1 or 3.
	 * @param syncmode Ignored
	 * @throws CanlibException
	 */
	public void setBusParams(int freq, int tseg1, int tseg2, int sjw, int noSamp, int syncmode) throws CanlibException{
		checkStatus(canInstance.canSetBusParams(handle, freq, tseg1, tseg2, sjw, noSamp, syncmode));
	}
	
	/**
	 * Closes the channel. 
	 * @throws CanlibException
	 */
	public void close() throws CanlibException{
		checkStatus(canInstance.canClose(handle));
	}
	
	/**
	 * Puts the channel on bus
	 * @throws CanlibException
	 */
	public void busOn() throws CanlibException{
		checkStatus(canInstance.canBusOn(handle));
	}
	
	/**
	 * Puts the channel on bus
	 * @throws CanlibException
	 */
	public void busOff() throws CanlibException{
		checkStatus(canInstance.canBusOff(handle));
	}
	
	/*
	 * Sending and receiving messages
	 */
	/**
	 * Writes a message to the channel
	 * @param message The Message to write to the channel
	 * @throws CanlibException
	 */
	public void write(Message message) throws CanlibException{
		checkStatus(canInstance.canWrite(handle, message.id, message.data, message.length, message.flags));
	}
	
	/**
	 * Writes a message to the channel, waiting until the message has been sent or until a timeout occurs.
	 * @param message The message to write to the channel
	 * @param timeout Timeout length in milliseconds.
	 * @throws CanlibException
	 */
	public void writeWait(Message message, long timeout) throws CanlibException{
		int status = canInstance.canWriteWait(handle,  message.id, message.data, message.length, message.flags, timeout);
		checkStatus(status);
	}
	
	/**
	 * Writes a message to the channel, waiting until the message has been sent or until 100 ms have passed.
	 * @param message The message to write to the channel
	 * @throws CanlibException
	 */
	public void writeWait(Message message) throws CanlibException{
		writeWait(message, DEFAULT_TIMEOUT);
	}
	
	/**
	 * Waits until all queued messages have been written to the channel or until a timeout occurs. 
	 * @param timeout Timeout length in milliseconds
	 * @throws CanlibException
	 */
	public void writeSync(long timeout) throws CanlibException{
		checkStatus(canInstance.canWriteSync(handle, timeout));
	}
	
	/**
	 * Reads a message from the channel
	 * @return The first message in the receiving buffer
	 * @throws CanlibException If there are no messages on the channel, or if an error occurs
	 */
	public Message read() throws CanlibException{
		IntByReference idRef = new IntByReference();
		byte[] msg = new byte[8];
		IntByReference dlcRef = new IntByReference();
		IntByReference flagRef = new IntByReference();
		LongByReference timeRef = new LongByReference();
		
		checkStatus(canInstance.canRead(handle, idRef, msg, dlcRef, flagRef, timeRef));
		
		return new Message(idRef.getValue(), msg, dlcRef.getValue(), flagRef.getValue(), timeRef.getValue());
	}
	
	/**
	 * Reads a message from the channel, waiting until a message is received or a timeout occurs
	 * @param timeout Timeout length in milliseconds
	 * @return The first message in the receiving buffer
	 * @throws CanlibException If there are no messages on the channel, or if an error occurs
	 */
	public Message readWait(long timeout) throws CanlibException{
		IntByReference idRef = new IntByReference();
		byte[] msg = new byte[8];
		IntByReference dlcRef = new IntByReference();
		IntByReference flagRef = new IntByReference();
		LongByReference timeRef = new LongByReference();
		LongByReference timeoutRef = new LongByReference();
		
		checkStatus(canInstance.canReadWait(handle, idRef, msg, dlcRef, flagRef, timeRef, timeoutRef));
		
		return new Message(idRef.getValue(), msg, dlcRef.getValue(), flagRef.getValue(), timeRef.getValue());
	}
	
	/**
	 * Checks if there are any messages in the receiving buffer, waiting for a set period of time
	 * @param timeout The timeout length in milliseconds
	 * @return true if a message exists on the buffer, false otherwise
	 * @throws CanlibException
	 */
	public boolean hasMessage(long timeout) throws CanlibException{
		int status = canInstance.canReadSync(handle, timeout);
		
		switch (status){
		case Canlib.canOK :
			return true;
		case Canlib.canERR_TIMEOUT :
			return false;
		default : 
			throw new CanlibException("Error " + status + " " + Canlib.getErrorText(status));
		}
	}

	/**
	 * Checks if there are any messages in the receiving buffer, waiting for 100 ms
	 * @return true if a message exists on the buffer, false otherwise
	 * @throws CanlibException
	 */
	public boolean hasMessage() throws CanlibException{
		return hasMessage(DEFAULT_TIMEOUT);
	}
	
	/**
	 * Reads the error counters
	 * @return An array of three elements, consisting of the number of transmit, receiving and overflow errors.
	 */
	public int[] readErrorCounters(){
		IntByReference txerrors = new IntByReference();
		IntByReference rxerrors = new IntByReference();
		IntByReference overrors = new IntByReference();
		canInstance.canReadErrorCounters(handle, txerrors, rxerrors, overrors);
		return new int[]{txerrors.getValue(), rxerrors.getValue(), overrors.getValue()};
	}
	
	
	/**
	 * Returns the current time of the device
	 * @return The time in ms
	 * @throws CanlibException
	 */
	public long readkvTimer() throws CanlibException{
		LongByReference ref = new LongByReference();
		checkStatus(canInstance.kvReadTimer(handle, ref));
		return ref.getValue();
	}

	/**
	 * Returns the current time of the device
	 * @return The current time, or an error code
	 */
	public long readTimer() {
		return canInstance.canReadTimer(handle);
	}
	
	
	/**
	 * When this method is called, all messages will have the EXT flag set unless otherwise specified
	 * @throws CanlibException
	 */
	public void preferExt() throws CanlibException{
        checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_PREFER_EXT, 0, 0));
    }
	
	/**
	 * When this method is called, all messages will have the STD flag set unless otherwise specified
	 * @throws CanlibException
	 */
	public void preferStd() throws CanlibException{
        checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_PREFER_STD, 0, 0));
    }
	
	/**
	 * Clears the error counters
	 * @throws CanlibException
	 */
    public void clearErrorCounter() throws CanlibException{
        checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_CLEAR_ERROR_COUNTERS, 0, 0));
    }

    /**
     * Sets the timer scale
     * @param scale The new timer scale in milliseconds
     * @throws CanlibException
     */
    public void setTimerScale(int scale) throws CanlibException{
        if (scale < 0)
        {
            throw new IllegalArgumentException("Cannot set timer scale to negative number");
        }
        int status =  canInstance.canIoCtl(handle,  Canlib.canIOCTL_SET_TIMER_SCALE, new IntByReference(scale), 4);
        checkStatus(status);
    }
    
    /**
     * Get the timer scale
     * @return The timer scale in milliseconds
     * @throws CanlibException
     */
    public int getTimerScale() throws CanlibException{
    	return getIoCtlInt(Canlib.canIOCTL_GET_TIMER_SCALE);
    }
    
    /**
     * Turns transmit ACKs on or off
     * @param level 0: Off, 1: On, 2: Off even for internal use
     * @throws CanlibException
     */
	public void setTXACK(int level) throws CanlibException{
		if (level < 0 || level > 2){
            throw new IllegalArgumentException("TXACK level must be 0, 1 or 2");
        }
        checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_TXACK, new IntByReference(level), 4));
	}
	
	/**
	 * Gets the device's transmit ACK setting
	 * @return 0: Off, 1: On, 2: Off even for internal use
	 * @throws CanlibException
	 */
	public int getTXACK() throws CanlibException{
    	return getIoCtlInt(Canlib.canIOCTL_GET_TXACK);
	}
	
	/**
	 * Reads the rx buffer level
	 * @return The number of messages in the receiving queue
	 * @throws CanlibException
	 */
	public int getRXBufferLevel() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_RX_BUFFER_LEVEL);
	}

	/**
	 * Reads the tx buffer level
	 * @return The number of messages in the transmit queue
	 * @throws CanlibException
	 */
	public int getTXBufferLevel() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_TX_BUFFER_LEVEL);
	}
	
	/**
	 * Empties the receiving buffer
	 * @throws CanlibException
	 */
	public void flushRXBuffer() throws CanlibException{
		checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_FLUSH_RX_BUFFER, 0, 0));
	}

	/**
	 * Empties the transmitting buffer
	 * @throws CanlibException
	 */
	public void flushTXBuffer() throws CanlibException{
		checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_FLUSH_TX_BUFFER, 0, 0));
	}
	
	/**
	 * Turn transmit requests on or off
	 * @param on True to turn transmit requests on, false to turn them off
	 * @throws CanlibException
	 */
	public void setTransmitRequest(boolean on) throws CanlibException{
		int buf = on ? 1 : 0;
		checkStatus(canInstance.canIoCtl(handle,  Canlib.canIOCTL_SET_TXRQ, new IntByReference(buf), 4));
	}
	
	//Turns clock reset at bus on on or off
	//On (default): the timer will reset when the channel goes on bus
	//Off: The timer will keep its value
	/**
	 * Turns clock reset at bus on on or off
	 * @param on True: the timer will reset when the channel goes on bus, 
	 * 				False: The timer will keep its value
	 * @throws CanlibException
	 */
	public void setClockResetAtBusOn(boolean on) throws CanlibException{
		int buf = on ? 1 : 0;
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_BUSON_TIME_AUTO_RESET, new IntByReference(buf), 4));
	}
	
	/**
	 * Gets the User IO port data
	 * @return The IO Port data
	 * @throws CanlibException
	 */
	public Canlib.ICanlib.canUserIoPortData getIoPortData() throws CanlibException{
		PointerByReference pRef = new PointerByReference();
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_GET_USER_IOPORT, pRef, 8));
		return new Canlib.ICanlib.canUserIoPortData(pRef.getValue());
	}
	
	/**
	 * Sets the User IO Port data
	 * @param data The new User IO Port data object
	 * @throws CanlibException
	 */
	public void setUserIoPortData(Canlib.ICanlib.canUserIoPortData data) throws CanlibException{
		Pointer p = data.getPointer();
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_USER_IOPORT, p, data.size()));
	}
	
	/**
	 * Sets the limit of the receiving queue size
	 * @param size The new limit of the RX buffer
	 * @throws CanlibException
	 */
	public void setRXQueueSize(int size) throws CanlibException{
        if (size < 0)
        {
            throw new IllegalArgumentException("Cannot set RX Queue size to a negative number");
        }
        checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_RX_QUEUE_SIZE, new IntByReference(size), 4));
    }
	
	/**
	 * Turns Local transmit echo on or off
	 * @param on True: Turn TX echo on, False, turn TX echo off
	 * @throws CanlibException
	 */
	public void setLocalTXEcho(boolean on) throws CanlibException{
		int buf = on ? 1 : 0;
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_LOCAL_TXECHO, new IntByReference(buf), 4));
	}
	/**
	 * Turns error frames reporting on or off.  
	 * @param on True: Turn error frames reporting on, False, turn error frames reporting off
	 * @throws CanlibException
	 */
	public void setErrorFramesReporting(boolean on) throws CanlibException{
		int buf = on ? 1 : 0;
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_ERROR_FRAMES_REPORTING, new IntByReference(buf), 4));
	}
	
	/**
	 * Returns the channel quality, scaled from 0 to 100. 
	 * @return The channel quality
	 * @throws CanlibException
	 */
	public int getChannelQuality() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_CHANNEL_QUALITY);
	}
	/**
	 * Returns the Round trip time to the device
	 * @return The round trip time in milliseconds
	 * @throws CanlibException
	 */
	public int getRoundTripTime() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_ROUNDTRIP_TIME);
	}
	
	/**
	 * Gets the bus type of the device
	 * @return One of the Canlib.kvBUSTYPE_XXX constants
	 * @throws CanlibException
	 */
	public int getBusType() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_BUS_TYPE);
	}
	
	/**
	 * Returns the bus type of the device
	 * @return The device's bus type as a string
	 * @throws CanlibException
	 */
	public String getBusTypeAsString() throws CanlibException{
		String s;
		switch (getBusType())
        {
            case Canlib.kvBUSTYPE_GROUP_INTERNAL: 
                s = "Internal";
                break;
            case Canlib.kvBUSTYPE_GROUP_REMOTE:
                s = "Remote";
                break;
            case Canlib.kvBUSTYPE_GROUP_LOCAL:
                s = "Local";
                break;
            case Canlib.kvBUSTYPE_GROUP_VIRTUAL:
                s = "Virtual";
                break;
            default:
                s = "Unknown";
                break;
        }
        return s;
	}
	
	/**
	 * Returns the device name
	 * @return The current device name
	 * @throws CanlibException
	 */
	public String getDevNameASCII() throws CanlibException{
		byte[] chars = new byte[50];
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_GET_DEVNAME_ASCII, chars, chars.length));
		return new String(chars);
	}
	
	/**
	 * Returns the time since last communication occured
	 * @return The time since last seen in milliseconds
	 * @throws CanlibException
	 */
	public int getTimeSinceLastSeen() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_TIME_SINCE_LAST_SEEN);
	}
	
	/**
	 * Returns the transmit interval
	 * @return The transmit interval of the device, in microseconds
	 * @throws CanlibException
	 */
	public int getTXInterval() throws CanlibException{
		IntByReference intRef = new IntByReference(-1);
    	int status = canInstance.canIoCtl(handle,  Canlib.canIOCTL_TX_INTERVAL, intRef, 4);
    	checkStatus(status);
    	return intRef.getValue();
	}
	
	/**
	 * Sets the transmit interval
	 * @param interval The new transmit interval, in microseconds
	 * @throws CanlibException
	 * @throws IllegalArgumentException If the interval is negative or more than one second
	 */
	public void setTXInterval(int interval) throws CanlibException, IllegalArgumentException{
		if(interval < 0 || interval > 1000000){
			throw new IllegalArgumentException("Illegal transmission interval");
		}
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_TX_INTERVAL, new IntByReference(interval), 4));
	}
	
	/**
	 * Sets the responsitivity of the device
	 * @param throttle The new throttle value
	 * @throws CanlibException
	 * @throws IllegalArgumentException If the new throttle value is not in the interval [0, 100]
	 */
	public void setThrottleScaled(int throttle) throws CanlibException, IllegalArgumentException{
		if(throttle < 0 || throttle > 100){
			throw new IllegalArgumentException("Illegal throttle value");
		}
		checkStatus(canInstance.canIoCtl(handle, Canlib.canIOCTL_SET_THROTTLE_SCALED, new IntByReference(throttle), 4));
	}
	
	/**
	 * Gets the throttle value of the device
	 * @return The throttle value, between 0 and 100.
	 * @throws CanlibException
	 */
	public int getThrottleScaled() throws CanlibException{
		return getIoCtlInt(Canlib.canIOCTL_GET_THROTTLE_SCALED);
	}
	
	//Helper methods
	
	/*
	 * Throws an exception if the status indicates an error
	 */
	private void checkStatus(int status) throws CanlibException{
		if(status != Canlib.canOK){
			throw new CanlibException(Canlib.getErrorText(status));
		}
	}
	
	/*
	 * Wraps the canIoCtl function, used for the cases where it 
	 * returns an integer value
	 */
	private int getIoCtlInt(int func) throws CanlibException{
		IntByReference intRef = new IntByReference();
    	int status = canInstance.canIoCtl(handle,  func, intRef, 4);
    	checkStatus(status);
    	return intRef.getValue();
	}
}
