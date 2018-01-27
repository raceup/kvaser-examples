package obj;

import core.Canlib;

public class Message {
	public final int id;
	public final byte[] data;
	public final int length;
	public final int flags;
	public final long time;
	
	/**
	 * Creates a Message object, representing a received CAN message
	 * @param id	The identifier of the message
	 * @param data	The message body
	 * @param length The length of the message body
	 * @param flags	Contains information about the message
	 * @param time	The time when the message was received
	 */
	public Message(int id, byte[] data, int length, int flags, long time) {
		super();
		this.id = id;
		this.data = data;
		this.flags = flags;
		this.length = length;
		this.time = time;
	}

	/**
	 * Creates a Message object, representing a CAN message to transmit. 
	 * The time value will be set to zero as it is not useful when sending messages
	 * @param id	The identifier of the message
	 * @param data	The message body
	 * @param length The length of the message body
	 * @param flags	Contains information about the message
	 */
	public Message(int id, byte[] data, int length, int flags) {
		super();
		this.id = id;
		this.data = data;
		this.flags = flags;
		this.length = length;
		this.time = 0;
	}

	/**
	 * Checks if the message is an error frame
	 * @return True if the canMSG_ERROR_FRAME flag is set.
	 */
	public boolean isErrorFrame(){
		return (flags & Canlib.canMSG_ERROR_FRAME) == Canlib.canMSG_ERROR_FRAME;
	}
	

}
