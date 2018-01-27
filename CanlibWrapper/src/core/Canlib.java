package core;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.ptr.*;


public class Canlib {

	
	/*
	 * Interface contains mappings of most of the functions in Canlib
	 */
	public interface ICanlib extends Library{
		String libraryName = Platform.isWindows() ? "canlib32" : "canlib";
		ICanlib INSTANCE = (ICanlib) Native.loadLibrary(
	            (libraryName), ICanlib.class);
		
	
		public short canGetVersion();
		public void canInitializeLibrary();
		
		public int canOpenChannel(int channel, int flags);
		public int canClose(int handle);

		public int canGetNumberOfChannels (IntByReference no);
		public void canGetChannelData(int i, int canchanneldataCardUpcNo, ByReference p, int buflen);
		public int canGetChannelData(int handle, int item, Pointer buffer, int buflen);
		
		public int canSetBusParams(int handle, int freq, int tseg1, int tseg2, int sjw, int noSamp, int syncmode);
		public int canSetBitrate(int handle, int bitrate);
		public int canBusOn(int handle);
		public int canBusOff(int handle);
		
		public int canWrite(int handle, int id, byte[] msg, int dlc, int flag);
		public int canWriteWait(int handle, int id, byte[] msg, int dlc, int flag, long timeout);
		public int canWriteSync(int handle, long timeout);
		public int canRead(int handle, IntByReference pId, byte[] pMsg, IntByReference pDlc, 
				IntByReference pFlag, LongByReference pTime);
		public int canReadWait(int handle, IntByReference pId, byte[] pMsg, IntByReference pDlc, 
				IntByReference pFlag, LongByReference pTime, LongByReference pTimeout);
		public int canReadSync(int handle, long time);
		
		public int canReadErrorCounters(int handle, IntByReference txerrors, 
				IntByReference rxerrors, IntByReference overrors);
		public long canReadTimer(int handle);
		public int kvReadTimer(int handle, LongByReference time);
		
		public int canIoCtl(int handle, int func, int buf, int buflen);
		public int canIoCtl(int handle, int func, ByReference buf, int buflen);
		public int canIoCtl(int handle, int func, char[] chars, int length);
		public int canIoCtl(int handle, int func, byte[] chars, int length);
		public int canIoCtl(int handle, int func, Pointer p, int length);
		

		public int canGetErrorText(int status, byte[] text, int length);
		
		
		/*
		 * Mapping of the canUserIoPortData structure
		 * Untested.
		 */
		public static class canUserIoPortData extends Structure{

			public int portNo;
			public int portValue;
			
			public canUserIoPortData(){
				super();
			}
			
			public canUserIoPortData(int alignType) {
				super(alignType);
			}
			public canUserIoPortData(Pointer p, int alignType) {
				super(p, alignType);
			}

			public canUserIoPortData(Pointer p) {
				super(p);
			}

			@Override
			protected List<String> getFieldOrder() {
				List<String> order = new ArrayList<>();
				order.add("portNo");
				order.add("portValue");
				return order;
			}
		}
	}
	

	/*
	 * Wraps the canGetErrorText function
	 */
	public static String getErrorText(int status){
		byte[] chars = new byte[50];
		ICanlib.INSTANCE.canGetErrorText(status, chars, 50);
		return new String(chars).trim();
	}
	
	
	//Constants
	public final static int canBITRATE_100K = -5;
    public final static int canBITRATE_10K = -9;
    public final static int canBITRATE_125K = -4;
    public final static int canBITRATE_1M = -1;
    public final static int canBITRATE_250K = -3;
    public final static int canBITRATE_500K = -2;
    public final static int canBITRATE_50K = -7;
    public final static int canBITRATE_62K = -6;
    public final static int canBITRATE_83K = -8;
	public final static int canOPEN_ACCEPT_VIRTUAL = 32;
	public final static int canMSG_ERROR_FRAME = 32;
	
    // Bus types
    public final static int kvBUSTYPE_GROUP_INTERNAL = 4;
    public final static  int kvBUSTYPE_GROUP_LOCAL = 2;
    public final static  int kvBUSTYPE_GROUP_REMOTE = 3;
    public final static  int kvBUSTYPE_GROUP_VIRTUAL = 1;
	
	//canStatus
  	public final static int canERR__RESERVED = -41;
  	public final static int canERR_SCRIPT_WRONG_VERSION = -40;
  	public final static int canERR_SCRIPT_FAIL = -39;
  	public final static int canERR_MEMO_FAIL = -38;
  	public final static int canERR_CONFIG = -37;
  	public final static int canERR_CRC = -36;
  	public final static int canERR_DISK = -35;
  	public final static int canERR_HOST_FILE = -34;
  	public final static int canERR_DEVICE_FILE = -33;
  	public final static int canERR_NOT_IMPLEMENTED = -32;
  	public final static int canERR_NO_ACCESS = -31;
  	public final static int canERR_INTERNAL = -30;
  	public final static int canERR_LICENSE = -29;
  	public final static int canERR_REGISTRY = -28;
  	public final static int canERR_RESERVED_7 = -27;
  	public final static int canERR_NOCARD = -26;
  	public final static int canERR_NOCONFIGMGR = -25;
  	public final static int canERR_DRIVERFAILED = -24;
  	public final static int canERR_DRIVERLOAD = -23;
  	public final static int canERR_RESERVED_2 = -22;
  	public final static int canERR_RESERVED_6 = -21;
  	public final static int canERR_RESERVED_5 = -20;
  	public final static int canERR_RESERVED_4 = -19;
  	public final static int canERR_DYNAINIT = -18;
  	public final static int canERR_DYNALIB = -17;
  	public final static int canERR_DYNALOAD = -16;
  	public final static int canERR_HARDWARE = -15;
  	public final static int canERR_RESERVED_1 = -14;
  	public final static int canERR_TXBUFOFL = -13;
  	public final static int canERR_DRIVER = -12;
  	public final static int canERR_INIFILE = -11;
  	public final static int canERR_INVHANDLE = -10;
  	public final static int canERR_NOHANDLES = -9;
  	public final static int canERR_NOTINITIALIZED = -8;
  	public final static int canERR_TIMEOUT = -7;
  	public final static int canERR_RESERVED_3 = -6;
  	public final static int canERR_NOCHANNELS = -5;
  	public final static int canERR_NOMEM = -4;
  	public final static int canERR_NOTFOUND = -3;
	public final static int canERR_NOMSG = -2;
	public final static int canERR_PARAM = -1;
	public final static int canOK = 0;
	
	
	//canIoCtl function constants
    //
    // Summary:
    //      This define is used in canIoCtl(), buf and buflen refers to this functions
    //     arguments.
    //     Tells CANLIB to clear the CAN error counters. The contents of buf and buflen
    //     are ignored.
    //
    // Remarks:
    //     Not all CAN controllers support this operation (and if they don't, nothing
    //     will happen.)
    public static final int canIOCTL_CLEAR_ERROR_COUNTERS = 5;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     Connects the handle to the virtual bus number (0..31) which the buf points
    //     to.
    public static final int canIOCTL_CONNECT_TO_VIRTUAL_BUS = 22;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     Disconnects the handle from the virtual bus number (0..31) which the buf points
    //     to.
    public static final int canIOCTL_DISCONNECT_FROM_VIRTUAL_BUS = 23;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf and buflen refers to this functions
    //     arguments.
    //     Discard the current contents of the RX queue. The values of buf and buflen
    //     are ignored.
    //
    // Remarks:
    //     This is the same thing as calling canFlushReceiveQueue()
    public static final int canIOCTL_FLUSH_RX_BUFFER = 10;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf and buflen refers to this functions
    //     arguments.
    //     Discard the current contents of the TX queue. The values of buf and buflen
    //     are ignored.
    //
    // Remarks:
    //     This is the same thing as calling canFlushTransmitQueue().
    public static final int canIOCTL_FLUSH_TX_BUFFER = 11;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD that contains the kvBUSTYPE_GROUP_xxx bus type.
    public static final int canIOCTL_GET_BUS_TYPE = 36;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a 32-bit unsigned integer that receives the quality of the
    //     channel, where the quality is measured in percent of optimal quality.
    //     For a WLAN, -90 dBm and -35 dBm are considered 0% and 100%, respectively.
    //     The quality is 100% for any directly connected channel (USB, PCI etc.).
    public static final int canIOCTL_GET_CHANNEL_QUALITY = 34;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a CHAR array of at least 32 characters which receives the current
    //     device name as a NULL terminated ASCII string.
    //     If device name is not set or the device does not support this functionality,
    //     an error will be returned.
    public static final int canIOCTL_GET_DEVNAME_ASCII = 37;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a HANDLE which receives the Windows handle related to the CANLIB
    //     handle.
    public static final int canIOCTL_GET_DRIVERHANDLE = 17;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points at a DWORD which receives a Windows Event handle which can be
    //     passed to the Win32 API WaitForSingleObject. The event is signaled when "something"
    //     (typically that a CAN message has been received or transmitted) happens in
    //     the driver.
    //
    // Remarks:
    //     There is no more information available as to what happened when this call
    //     returns. The call may return on an "internal" event in CANLIB and your application
    //     must be prepared to handle this (i.e. go to sleep again.)
    //     If canWaitForEvent() returns with success status (canOK), you must call canRead()
    //     repeatedly until it returns canERR_NOMSG, before calling canWaitForEvent()
    //     again. This will flush the driver's internal event queues.  Failure to call
    //     canRead() can cause canWaitForEvent() to get stuck in a state where it always
    //     sleeps for the specified timeout and then returns with canERR_TIMEOUT.
    public static final int canIOCTL_GET_EVENTHANDLE = 14;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a BYTE which receives the current setting of the access error
    //     reporting (0 or 1.)
    public static final int canIOCTL_GET_REPORT_ACCESS_ERRORS = 21;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD that contains the roundtrip time measured in milliseconds.
    public static final int canIOCTL_GET_ROUNDTRIP_TIME = 35;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points at a DWORD which receives the current RX queue level. The returned
    //     value is approximative (this is because not all hardware supports retrieving
    //     the queue levels. In that case a best-effort guess is returned. Also note
    //     that a device with embedded CPU will report its queue levels to the host
    //     computer after a short delay that depends on the bus traffic intensity, and
    //     consequently the value returned by the call to canIoCtl() might be a few
    //     milliseconds old.)
    public static final int canIOCTL_GET_RX_BUFFER_LEVEL = 8;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD that contains the time in milliseconds since the last
    //     communication occured.
    //     For WLAN devices, this is the time since the last keep-alive message.
    public static final int canIOCTL_GET_TIME_SINCE_LAST_SEEN = 38;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD which contains the desired time-stamp clock resolution
    //     in microseconds. Note that the accuracy of the clock isn't affected. The
    //     default value is 1000 microseconds, i.e. one millisecond.
    public static final int canIOCTL_GET_TIMER_SCALE = 12;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points at a DWORD which receives the current TX queue level. The returned
    //     value is approximative (this is because not all hardware supports retrieving
    //     the queue levels. In that case a best-effort guess is returned. Also note
    //     that a device with embedded CPU will report its queue levels to the host
    //     computer after a short delay that depends on the bus traffic intensity, and
    //     consequently the value returned by the call to canIoCtl() might be a few
    //     milliseconds old.)
    public static final int canIOCTL_GET_TX_BUFFER_LEVEL = 9;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     Returns the state of the Transmit Acknowledge as a DWORD in buf:
    //     0: Transmit Acknowledges is turned off.  1: Transmit Acknowledges is turned
    //     on.  2: Transmit Acknowledges is turned off, even for the driver's internal
    //     usage.
    public static final int canIOCTL_GET_TXACK = 31;
    //
    // Summary:
    //      This ioctl can be used to set the responsitivity of some devices. 
    // 		buf points to a DWORD that should contain a value between 0 and 100. 
    //		A value of 0 means that the device should be very responsive and a value of
    //		100 means that the device generates less cpu load or requires more bandwidth. 
    //		Note that not all devices support this. Some hardware will accept this command 
    // 		but neglect it. This can be found out by reading the scaled throttle. 
    public static final int canIOCTL_SET_THROTTLE_SCALED = 41;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a canUserIoPortData struct that contains a port number. After
    //     the call, the struct will contain the current value of the I/O port. This
    //     is used by special hardware only.
    public static final int canIOCTL_GET_USER_IOPORT = 25;
    //
    // Summary:
    //		This define is used in canIoCtl().
    //		Returns the current throttle value
    public static final int canIOCTL_GET_THROTTLE_SCALED = 42;
    //
    // Summary:
    //      This define is used in canIoCtl().
    //     This is only intended for internal use.
    public static final int canIOCTL_GET_WAKEUP = 19;
    //
    // Summary:
    //      This define is used in canIoCtl().
    //     This is only intended for internal use.
    public static final int canIOCTL_MAP_RXQUEUE = 18;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf and buflen refers to this functions
    //     arguments.
    //     Tells CANLIB to "prefer" extended identifiers; that is, if you send a message
    //     with canWrite() and don't specify canMSG_EXT nor canMSG_STD, canMSG_EXT will
    //     be assumed. The contents of buf and buflen are ignored. canRead() et al will
    //     set canMSG_EXT and/or canMSG_STD as usual and are not affected by this call.
    public static final int canIOCTL_PREFER_EXT = 1;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf and buflen refers to this functions
    //     arguments.
    //     Tells CANLIB to "prefer" standard identifiers; that is, if you send a message
    //     with canWrite() and don't specify canMSG_EXT nor canMSG_STD, canMSG_STD will
    //     be assumed. The contents of buf and buflen are ignored. canRead() et al will
    //     set canMSG_EXT and/or canMSG_STD as usual and are not affected by this call.
    public static final int canIOCTL_PREFER_STD = 2;
    //
    // Summary:
    //      This define is used in canIoCtl().
    //     This is only intended for internal use.
    public static final int canIOCTL_SET_BUFFER_WRAPAROUND_MODE = 26;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD. If the value is zero, the CAN clock will not be reset
    //     at buson for the handle. Otherwise, the CAN clock will be reset at buson.
    //     Default value is 1, the CAN clock will be reset at buson.
    public static final int canIOCTL_SET_BUSON_TIME_AUTO_RESET = 30;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //
    // Remarks:
    //     Not yet implemented.
    public static final int canIOCTL_SET_BYPASS_MODE = 15;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to an unsigned byte. If the value is zero, the reporting of error
    //     frames is turned off for the handle. Otherwise, error frame reporting is
    //     turned on.
    //     Default value is 1, error frame reporting is turned on.
    public static final int canIOCTL_SET_ERROR_FRAMES_REPORTING = 33;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to an unsigned byte. If the value is zero, the local transmit
    //     echo is turned off for the handle. Otherwise, local transmit echo is turned
    //     on.
    //     Local transmit echo is turned on by default on all handles. This means that
    //     if two handles are open on the same channel, and a message is transmitted
    //     on the first handle, it will be received as a normal message on the second
    //     handle. Use the canIOCTL_SET_LOCAL_TXECHO function code to turn this function
    //     off, if it is not desired on a certain handle.
    public static final int canIOCTL_SET_LOCAL_TXECHO = 32;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a BYTE which contains
    //     0 to turn access error reporting off, and 1 to turn access error reporting
    //     on.  Default value is 0, access error reporting off.
    public static final int canIOCTL_SET_REPORT_ACCESS_ERRORS = 20;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     Use this function code to set the size of the receive buffer for a specific
    //     handle. buf points to an unsigned integer which contains the new size (number
    //     of messages) of the receive buffer.
    //
    // Remarks:
    //     The receive buffer consumes system nonpaged pool memory, which is a limited
    //     resource. Do not increase the receive buffer size unless you have good reasons
    //     to do so.
    //     You can't use this function code when the channel is on bus.
    public static final int canIOCTL_SET_RX_QUEUE_SIZE = 27;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD which contains the desired time-stamp clock resolution
    //     in microseconds. The default value is 1000 microseconds, i.e.  one millisecond.
    //
    // Remarks:
    //     The accuracy of the clock isn't affected.
    public static final int canIOCTL_SET_TIMER_SCALE = 6;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD which contains
    //     0: to turn Transmit Acknowledges off.  1: to turn Transmit Acknowledges on.
    //      2: to turn Transmit Acknowledges off, even for the driver's internal usage.
    //     This might enhance performance but will cause some other APIs to stop working
    //     (for example, the current size of the transmit queue can not be read when
    //     this mode is active.)
    //     The default value is 0, Transmit Acknowledge is off.
    public static final int canIOCTL_SET_TXACK = 7;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD which contains
    //     0 to turn Transmit Requests off.  1 to turn Transmit Requests on.  Default
    //     value is 0, Transmit Requests off.
    public static final int canIOCTL_SET_TXRQ = 13;
    //
    //
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a canUserIoPortData struct that contains a port number and
    //     a port value to set. This is used by special hardware only.
    public static final int canIOCTL_SET_USER_IOPORT = 24;
    //
    // Summary:
    //      This define is used in canIoCtl().
    //     This is only intended for internal use.
    public static final int canIOCTL_SET_WAKEUP = 16;
    //
    // Summary:
    //      This define is used in canIoCtl(), buf mentioned below refers to this functions
    //     argument.
    //     buf points to a DWORD that contains the number of microseconds the minimum
    //     CAN message transmit interval should be set to, or 0xffffffff to fetch the
    //     current setting.  The minimum interval can not be set to more than one second.
    //     When a CAN channel goes bus on, the minimum interval is set to zero.  I.e.
    //     CAN transmissions happen at the maximum speed the device is capable of.
    //     If the device does not support this functionality, or if an invalid minimum
    //     interval is requested, an error will be returned.
    //
    // Remarks:
    //     The minimum CAN messages transmit interval applies to the physical CAN channel.
    //     It will thus affect all messages transmitted, even those sent using other
    //     CANlib handles to the same physical channel. The interval is defined as the
    //     time from the successful completion of one transmit to the beginning of the
    //     next one.
    public static final int canIOCTL_TX_INTERVAL = 40;
	

    public static final int canCHANNELDATA_CARD_FIRMWARE_REV = 9;
    public static final int canCHANNELDATA_CARD_HARDWARE_REV = 10;
    public static final int canCHANNELDATA_CARD_NUMBER = 5;
    public static final int canCHANNELDATA_CARD_SERIAL_NO = 7;
    public static final int canCHANNELDATA_CARD_TYPE = 4;
    public static final int canCHANNELDATA_CARD_UPC_NO = 11;
    public static final int canCHANNELDATA_CHAN_NO_ON_CARD = 6;
    public static final int canCHANNELDATA_CHANNEL_CAP = 1;
    public static final int canCHANNELDATA_CHANNEL_FLAGS = 3;
    public static final int canCHANNELDATA_CHANNEL_NAME = 13;
    public static final int canCHANNELDATA_CHANNEL_QUALITY = 28;
    public static final int canCHANNELDATA_DEVDESCR_ASCII = 26;
    public static final int canCHANNELDATA_DEVDESCR_UNICODE = 25;
    public static final int canCHANNELDATA_DEVICE_PHYSICAL_POSITION = 18;
    public static final int canCHANNELDATA_DEVNAME_ASCII = 31;
    public static final int canCHANNELDATA_DLL_FILE_VERSION = 14;
    public static final int canCHANNELDATA_DLL_FILETYPE = 16;
    public static final int canCHANNELDATA_DLL_PRODUCT_VERSION = 15;
    public static final int canCHANNELDATA_DRIVER_FILE_VERSION = 21;
    public static final int canCHANNELDATA_DRIVER_NAME = 27;
    public static final int canCHANNELDATA_DRIVER_PRODUCT_VERSION = 22;
    public static final int canCHANNELDATA_MFGNAME_ASCII = 24;
    public static final int canCHANNELDATA_MFGNAME_UNICODE = 23;
    public static final int canCHANNELDATA_REMOTE_HOST_NAME = 35;
    public static final int canCHANNELDATA_REMOTE_OPERATIONAL_MODE = 33;
    public static final int canCHANNELDATA_REMOTE_PROFILE_NAME = 34;
    public static final int canCHANNELDATA_ROUNDTRIP_TIME = 29;
    public static final int canCHANNELDATA_TIME_SINCE_LAST_SEEN = 32;
    public static final int canCHANNELDATA_TIMESYNC_ENABLED = 20;
    public static final int canCHANNELDATA_TRANS_CAP = 2;
    public static final int canCHANNELDATA_TRANS_SERIAL_NO = 8;
    public static final int canCHANNELDATA_TRANS_TYPE = 17;
    public static final int canCHANNELDATA_TRANS_UPC_NO = 12;
    public static final int canCHANNELDATA_UI_NUMBER = 19;
	
}
