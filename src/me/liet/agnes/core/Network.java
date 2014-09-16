package me.liet.agnes.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.util.SubnetUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class Network
{
  public enum Protocol{
    TCP,
    UDP,
    ICMP,
    IGMP,
    UNKNOWN;

    public static Protocol fromString(String proto){

      if(proto != null){
        proto = proto.toLowerCase();

        if(proto.equals("tcp"))
          return TCP;

        else if(proto.equals("udp"))
          return UDP;

        else if(proto.equals("icmp"))
          return ICMP;

        else if(proto.equals("igmp"))
          return IGMP;
      }

      return UNKNOWN;
    }

    public String toString()
    {
      switch(this)
      {
        case ICMP:
          return "icmp";
        case IGMP:
          return "igmp";
        case TCP:
          return "tcp";
        case UDP:
          return "udp";
        default:
          return "unknown";
      }
    }
  }

  private ConnectivityManager mConnectivityManager = null;
  private WifiManager mWifiManager = null;
  private DhcpInfo mInfo = null;
  private WifiInfo mWifiInfo = null;
  private NetworkInterface mInterface = null;
  private IP4Address mGateway = null;
  private IP4Address mNetmask = null;
  private IP4Address mLocal = null;
  private IP4Address mBase = null;

  /** see http://en.wikipedia.org/wiki/Reserved_IP_addresses
   */
  private static final String[] PRIVATE_NETWORKS = {
          "10.0.0.0/8",
          "100.64.0.0/10",
          "172.16.0.0/12",
          "192.168.0.0/16"
  };

  public Network(Context context) throws NoRouteToHostException, SocketException, UnknownHostException{
    mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    mInfo = mWifiManager.getDhcpInfo();
    mWifiInfo = mWifiManager.getConnectionInfo();
    mLocal = new IP4Address(mInfo.ipAddress);
    mGateway = new IP4Address(mInfo.gateway);
    mNetmask = getNetmask();
    mBase = new IP4Address(mInfo.netmask & mInfo.gateway);

    if(isConnected() == false)
      throw new NoRouteToHostException("Not connected to any WiFi access point.");

    else{
      try{
        mInterface = NetworkInterface.getByInetAddress(getLocalAddress());
        if(mInterface == null)
          throw new IllegalStateException("Error retrieving network interface.");
      }
      catch(SocketException e){
    	Log.v("AGNES",e.toString());
        mInterface = NetworkInterface.getByName(java.lang.System.getProperty("wifi.interface", "wlan0"));

        if(mInterface == null)
          throw e;
      }
    }
  }

  private IP4Address getNetmask() throws UnknownHostException {
    IP4Address result = new IP4Address(mInfo.netmask);
    return result;
  }

  public boolean equals(Network network){
    return mInfo.equals(network.getInfo());
  }

  public boolean isInternal(int ip){
    return isInternal((ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF));
  }

  public boolean isInternal(String ip){
    try{
      byte[] gateway = mGateway.toByteArray();
      byte[] address = InetAddress.getByName(ip).getAddress();
      byte[] mask = mNetmask.toByteArray();

      for(int i = 0; i < gateway.length; i++)
        if((gateway[i] & mask[i]) != (address[i] & mask[i]))
          return false;

      return true;
    }
    catch(UnknownHostException e){
      System.errorLogging(e);
    }

    return false;
  }

  public static boolean isWifiConnected(Context context){
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    return info != null && info.isConnected() && info.isAvailable();
  }

  public static boolean isConnectivityAvailable(Context context){
    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = manager.getActiveNetworkInfo();

    return info != null && info.isConnected();
  }

  public boolean isConnected(){
    return mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
  }

  public String getSSID(){
    return mWifiInfo.getSSID();
  }

  public int getNumberOfAddresses(){
    return IP4Address.ntohl(~mNetmask.toInteger());
  }

  public IP4Address getStartAddress(){
    return mBase;
  }

  public String getNetworkMasked(){
    int network = mBase.toInteger();

    return (network & 0xFF) + "." + ((network >> 8) & 0xFF) + "." + ((network >> 16) & 0xFF) + "." + ((network >> 24) & 0xFF);
  }

  public String getNetworkRepresentation(){
    return getNetworkMasked() + "/" + mNetmask.getPrefixLength();
  }

  public DhcpInfo getInfo(){
    return mInfo;
  }

  public InetAddress getNetmaskAddress(){
    return mNetmask.toInetAddress();
  }

  public InetAddress getGatewayAddress(){
    return mGateway.toInetAddress();
  }

  public byte[] getLocalHardware(){
    try{
      return mInterface.getHardwareAddress();
    }
    catch(SocketException e){
      System.errorLogging(e);
    }

    return null;
  }

  public String getLocalAddressAsString(){
    return mLocal.toString();
  }

  public InetAddress getLocalAddress(){
    return mLocal.toInetAddress();
  }

  public NetworkInterface getInterface(){
    return mInterface;
  }
}
