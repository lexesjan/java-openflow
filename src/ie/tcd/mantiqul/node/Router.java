package ie.tcd.mantiqul.node;

import java.net.DatagramPacket;
import java.net.SocketException;

public class Router extends Node {

  Router(int recPort) throws SocketException {
    super(recPort);
  }

  /**
   * Override method which handles the each type of packet received.
   *
   * @param packet The packet received
   */
  @Override
  public void onReceipt(DatagramPacket packet) {

  }
}
