package org.wieggers.qu_apps.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.wieggers.qu_apps.qu16.Qu16_Mixer;

import android.util.Log;

public class Connected_Device {

	private static final String mTag = "Connected_Device";
	private static final boolean mLogReceive = false;
	
	private StartThread mStartThread;
	private SendThread mSendThread;
	private ReceiveThread mReceiveThread;
	private KeepAliveThread mKeepAliveThread;

	private Socket mSocket;	
	private String mRemoteIp;
	private int mPort;	
	
	private LinkedBlockingQueue<byte[]> mQueue;
			
	private boolean mRunning;
	
	private IDeviceListener mParent;
	
	public Connected_Device(String remoteIp, int port, IDeviceListener parent) {
		mRemoteIp = remoteIp;
		mPort = port;
		mQueue = new LinkedBlockingQueue<byte[]>();
		
		mParent = parent;
	}
	
	public void send(byte[] message) {
		try {
			mQueue.put(message);
		} catch (InterruptedException e) {
		}
	}
	
	public void start() {
		mStartThread = new StartThread();
		mStartThread.setName("startThread: " + mRemoteIp);
		mSendThread = new SendThread();
		mSendThread.setName("sendThread: " + mRemoteIp);
		mReceiveThread = new ReceiveThread();
		mReceiveThread.setName("receiveThread: " + mRemoteIp);
		mKeepAliveThread = new KeepAliveThread();
		mKeepAliveThread.setName("keepAliveThread: " + mRemoteIp);
		
		mStartThread.start();		
	}
	
	public void stop() {
		mRunning = false;
		try {
			mSocket.close();
		} catch (IOException e) {
		}
	}
	
	private class StartThread extends Thread {
		public void run() {
			try {
				SocketAddress address = new InetSocketAddress(mRemoteIp, mPort);
				mSocket = new Socket();
				mSocket.connect(address, 1000);
				mSocket.setTcpNoDelay(true);
				mRunning = true;
				
				mSendThread.start();
				mReceiveThread.start();
				mKeepAliveThread.start();
				
			} catch (Exception ex) {
				
				mParent.errorOccurred(ex);
				
			}
		}
	}
	
	private class ReceiveThread extends Thread {
		@SuppressWarnings("unused")
		public void run() {
			byte[] buffer = new byte[2048];
			try {
				InputStream socketInputStream = mSocket.getInputStream();
				while (mRunning) {
					int bytesRead = socketInputStream.read(buffer, 0, 2048);
					if (bytesRead != -1) {
						
						byte[] msg = Arrays.copyOfRange(buffer, 0, bytesRead);
						
						if (mLogReceive && (bytesRead > 1)) {
							Log.d(mTag, Qu16_Mixer.bytesToHex(msg));
						}
						mParent.receivedMessage(msg);
					}
				}
			} catch (Exception e) {
				mParent.errorOccurred(e);
			}			
		}
	}

	
	private class SendThread extends Thread {
		public void run() {
			try {
				OutputStream socketOutputStream = mSocket.getOutputStream();
				mSocket.setTcpNoDelay(true);
				
				while (mRunning) {
					LinkedList<byte[]> sendQueue = new LinkedList<byte[]>(); 
					sendQueue.add(mQueue.take()); // block while waiting for data to be sent;
					mQueue.drainTo(sendQueue); // when data available, empty entire send queue
					 
					for (byte[] sendData : sendQueue) {
						socketOutputStream.write(sendData); // and repeatedly send all messages
					}
					socketOutputStream.flush();
				}
			} catch (Exception e) {
				mParent.errorOccurred(e);
			}			
		}
	}
	
	private class KeepAliveThread extends Thread {
		public void run() {
			try {
				while (mRunning) {
					Thread.sleep(300);				
					send(new byte[] {(byte) 0xFE});	
				}				
			}
			catch (Exception e) {
				mParent.errorOccurred(e);
			}
		}
	}
}
