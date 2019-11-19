package ie.tcd.mantiqul.node;

import ie.tcd.mantiqul.Terminal;
import ie.tcd.mantiqul.packet.FeatureResultPacketContent;
import ie.tcd.mantiqul.packet.HelloPacketContent;
import ie.tcd.mantiqul.packet.PacketContent;
import ie.tcd.mantiqul.packet.PacketInPacketContent;
import ie.tcd.mantiqul.packet.PayloadPacketContent;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Switch extends Node {

  private final String CONTROLLER = "controller";

  private Terminal terminal;
  private double versionNumber;
  private Map<String, String> flowtable;
  private List<String> connections;

  Switch(int listeningPort, List<String> connections) throws SocketException {
    super(listeningPort);
    terminal = new Terminal(getClass().getSimpleName());
    versionNumber = 1.0;
    flowtable = new Hashtable<>();
    this.connections = connections;
  }

  /**
   * Override method which handles the each type of packet received.
   *
   * @param packet The packet received
   */
  @Override
  public void onReceipt(DatagramPacket packet) {
    PacketContent packetContent = PacketContent.fromDatagramPacket(packet);
    switch (packetContent.type) {
      case PacketContent.HELLO_PACKET:
        HelloPacketContent helloPacketContent = (HelloPacketContent) packetContent;
        double receivedVersionNumber = helloPacketContent.getVersionNumber();
        if (receivedVersionNumber < versionNumber) versionNumber = receivedVersionNumber;
        break;
      case PacketContent.PAYLOAD_PACKET:
        PayloadPacketContent payloadPacketContent = (PayloadPacketContent) packetContent;
        String payloadDestination = payloadPacketContent.getDestination();
        boolean tableMiss = !flowtable.containsKey(payloadDestination);
        if (tableMiss) {
          InetSocketAddress destination = new InetSocketAddress(CONTROLLER, DEFAULT_PORT);
          send(new PacketInPacketContent(payloadPacketContent), destination);
        } else {
          String nextNodeHop = flowtable.get(payloadDestination);
          InetSocketAddress nextHopAddress = new InetSocketAddress(nextNodeHop, DEFAULT_PORT);
          send(payloadPacketContent, nextHopAddress);
        }
        break;
      case PacketContent.FEATURE_REQUEST:
        int num_buffers = 11;
        int num_tables = 1;
        FeatureResultPacketContent specifications =
            new FeatureResultPacketContent(num_buffers, num_tables, connections);
        send(specifications, packet.getAddress(), packet.getPort());
        break;
      default:
        terminal.println("Unknown packet received");
    }
    terminal.println("---------------------------------------------------------------------------");
    terminal.println(packetContent.toString());
    terminal.println("---------------------------------------------------------------------------");
  }

  /** Initialises the router */
  public void start() {
    terminal.println(this.toString());
    send(new HelloPacketContent(versionNumber), new InetSocketAddress(CONTROLLER, DEFAULT_PORT));
  }

  public static void main(String[] args) throws SocketException {
    if (args.length < 1) {
      System.out.println("Usage: java ie.tcd.mantiqul.Switch <connection 1> ... <connection N>");
      return;
    }
    (new Switch(DEFAULT_PORT, Arrays.asList(args))).start();
  }
}
