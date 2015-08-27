package com.tabhost.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class NetUtil {
	//TelephonyManager的常量
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B*/
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0*/
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A*/
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT*/
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	public static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B*/
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	public static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	public static final int NETWORK_TYPE_HSPAP = 15;
	//自定义的常量
	public static final int NETWORK_CLASS_2_G = 22;
	public static final int NETWORK_CLASS_3_G = 33;
	public static final int NETWORK_CLASS_4_G = 44;
	public static final int NETWORK_CLASS_UNKNOWN = -1;
	
	/*//TelephonyManager原版
	public static int getNetworkClass(int networkType) {
		
	    switch (networkType) {
	        case NETWORK_TYPE_GPRS:
	        case NETWORK_TYPE_EDGE:
	        case NETWORK_TYPE_CDMA:
	        case NETWORK_TYPE_1xRTT:
	        case NETWORK_TYPE_IDEN:
	    return NETWORK_CLASS_2_G;
	        case NETWORK_TYPE_UMTS:
	        case NETWORK_TYPE_EVDO_0:
	        case NETWORK_TYPE_EVDO_A:
	        case NETWORK_TYPE_HSDPA:
	        case NETWORK_TYPE_HSUPA:
	        case NETWORK_TYPE_HSPA:
	        case NETWORK_TYPE_EVDO_B:
	        case NETWORK_TYPE_EHRPD:
	        case NETWORK_TYPE_HSPAP:
	    return NETWORK_CLASS_3_G;
	        case NETWORK_TYPE_LTE:
	    return NETWORK_CLASS_4_G;
	        default:
	    return NETWORK_CLASS_UNKNOWN;
	    }
	}*/
	
	public static Boolean isNetAvailable(Context context) {
		/*NetworkInfo info = ((ConnectivityManager) context  
	               .getSystemService("connectivity")).getActiveNetworkInfo();  
	        if ((info != null) && (info.isAvailable()))  
	           return true;
	       return false;*/
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (cm == null || info == null || !info.isAvailable())
			return false;
		return true;
	}
	
	public static boolean is34G(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = cm.getActiveNetworkInfo();
	    switch (info.getSubtype()) {
	        case NETWORK_TYPE_GPRS:
	        case NETWORK_TYPE_EDGE:
	        case NETWORK_TYPE_CDMA:
	        case NETWORK_TYPE_1xRTT:
	        case NETWORK_TYPE_IDEN:
	    return false;
	        case NETWORK_TYPE_UMTS:
	        case NETWORK_TYPE_EVDO_0:
	        case NETWORK_TYPE_EVDO_A:
	        case NETWORK_TYPE_HSDPA:
	        case NETWORK_TYPE_HSUPA:
	        case NETWORK_TYPE_HSPA:
	        case NETWORK_TYPE_EVDO_B:
	        case NETWORK_TYPE_EHRPD:
	        case NETWORK_TYPE_HSPAP:
	    return true;
	        case NETWORK_TYPE_LTE:
	    return true;
	        default:
	    return false;
	    }
	}

	//wifi优先
	public static Boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info.getType()==1){
			return true;
		}else{
			//Toast.makeText(context,"网络连接异常，请检查", 0).show();
			return false;
		}
	}
	
	public static Boolean isMobile(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(wifiInfo.isAvailable()){
			return true;
		}else{
			//Toast.makeText(context,"网络连接异常，请检查", 0).show();
			return false;
		}
	}
	
    /** 
     * 将ip的整数形式转换成ip形式 
    *  
     * @param ipInt 
     * @return 
     */
    public static String int2ip(int ipInt) {  
        StringBuilder sb = new StringBuilder();  
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();
    }  
  
    /** 
     * 获取当前ip地址 
     *  
    * @param context 
    * @return 
    */  
    public static String getLocalIpAddress(Context context) {  
        try {  
           WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
           WifiInfo wifiInfo = wifiManager.getConnectionInfo();
           int i = wifiInfo.getIpAddress();  
           return int2ip(i);  
       } catch (Exception ex) {  
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();  
       }
   }  
}
