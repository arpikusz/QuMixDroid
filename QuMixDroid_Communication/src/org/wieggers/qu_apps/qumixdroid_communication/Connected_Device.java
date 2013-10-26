package org.wieggers.qu_apps.qumixdroid_communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connected_Device {

	@SuppressWarnings("unused")
	private static final String mTag = "Connected_Device";
	
	private StartThread mStartThread;
	private SendThread mSendThread;
	private ReceiveThread mReceiveThread;
	private KeepAliveThread mKeepAliveThread;

	private Socket mSocket;	
	private String mRemoteIp;
	private int mPort;	
	
	private LinkedBlockingQueue<byte[]> mQueue;
			
	private boolean mRunning;
	
	private ConcurrentLinkedQueue<IDeviceListener> mListeners;
	
	public Connected_Device(String remoteIp, int port) {
		mRemoteIp = remoteIp;
		mPort = port;
		mQueue = new LinkedBlockingQueue<byte[]>();
		
		mListeners = new ConcurrentLinkedQueue<IDeviceListener>();
	}
	
	public void send(byte[] message) {
		try {
			mQueue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		mListeners.clear();
	}
	
	public void addListener(IDeviceListener listener) {
		mListeners.add(listener);
	}
	
	public void removeListener(IDeviceListener listener) {
		mListeners.remove(listener);
	}
	
	private class StartThread extends Thread {
		public void run() {
			try {
				SocketAddress address = new InetSocketAddress(mRemoteIp, mPort);
				mSocket = new Socket();
				mSocket.connect(address, 1000);
				mRunning = true;
				
				mSendThread.start();
				mReceiveThread.start();
				mKeepAliveThread.start();
				
			} catch (Exception ex) {
				for (IDeviceListener listener : mListeners) {
					listener.errorOccurred(ex);
				}
			}
		}
	}
	
	private class ReceiveThread extends Thread {
		public void run() {
			byte[] buffer = new byte[2048];
			try {
				InputStream socketInputStream = mSocket.getInputStream();
				while (mRunning) {
					int bytesRead = socketInputStream.read(buffer, 0, 2048);
					if (bytesRead != -1) {
						
						byte[] msg = Arrays.copyOfRange(buffer, 0, bytesRead);
							
						//Log.d(mTag, Arrays.toString(msg));
						for (IDeviceListener listener : mListeners) {
							listener.receivedMessage(msg);
						}
					}
				}
			} catch (Exception e) {
				for (IDeviceListener listener : mListeners) {
					listener.errorOccurred(e);
				}
			}			
		}
	}

	
	private class SendThread extends Thread {
		public void run() {
			try {
				OutputStream socketOutputStream = mSocket.getOutputStream();
				
				while (mRunning) {
					LinkedList<byte[]> sendQueue = new LinkedList<byte[]>(); 
					sendQueue.add(mQueue.take()); // block while waiting for data to be sent;
					mQueue.drainTo(sendQueue); // when data available, empty entire send queue
					 
					for (byte[] sendData : sendQueue) {
						socketOutputStream.write(sendData); // and repeatedly send all messages
					}
				}
			} catch (Exception e) {
				for (IDeviceListener listener : mListeners) {
					listener.errorOccurred(e);
				}
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
				for (IDeviceListener listener : mListeners) {
					listener.errorOccurred(e);
				}				
			}
		}
	}
}
