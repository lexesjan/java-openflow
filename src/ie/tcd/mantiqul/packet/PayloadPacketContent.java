package ie.tcd.mantiqul.packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PayloadPacketContent extends PacketContent {

  private String payload;

  /** @param payload the payload to be sent */
  public PayloadPacketContent(String payload) {
    type = PAYLOAD_PACKET;
    this.payload = payload;
  }

  /**
   * Constructs an ack packet out of a datagram packet.
   *
   * @param oin The received packet as an object input stream
   */
  protected PayloadPacketContent(ObjectInputStream oin) {
    try {
      type = PAYLOAD_PACKET;
      payload = oin.readUTF();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes the content into an ObjectOutputStream
   *
   * @param oout The object output stream to write to
   */
  protected void toObjectOutputStream(ObjectOutputStream oout) {
    try {
      oout.writeUTF(payload);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the content of the packet as String.
   *
   * @return Returns the content of the packet as String.
   */
  public String toString() {
    return "PAYLOAD_PACKET payload: " + payload;
  }

  /**
   * Returns the payload string
   *
   * @return the payload string
   */
  public String getPayload() {
    return payload;
  }
}
