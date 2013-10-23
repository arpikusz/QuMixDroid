package org.wieggers.qu_apps.qumixdroid_communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Connected_Device {

	private StartThread mStartThread;
	@SuppressWarnings("unused")
	private SendThread mSendThread;
	private ReceiveThread mReceiveThread;

	private Socket mSocket;	
	private String mRemoteIp;
	private int mPort;	
	
	private LinkedBlockingQueue<byte[]> mQueue;
			
	private boolean mRunning;
	
	private LinkedList<IDeviceListener> mListeners;
	private Object mListenerLock;
	
	public Connected_Device(String remoteIp, int port) {
		mRemoteIp = remoteIp;
		mPort = port;
		mQueue = new LinkedBlockingQueue<byte[]>();
		
		mListeners = new LinkedList<IDeviceListener>();
		mListenerLock = new Object();
		
		mStartThread = new StartThread();		
		mSendThread = new SendThread();
		mReceiveThread = new ReceiveThread();
		
		mStartThread.start();
	}
	
	public void Send(byte[] message) {
		try {
			mQueue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Stop() {
		mRunning = false;
		synchronized (mListenerLock) {
			mListeners.clear();
		}		
	}
	
	public void AddListener(IDeviceListener listener) {
		synchronized (mListenerLock) {
			mListeners.add(listener);
		}
	}
	
	public void RemoveListener(IDeviceListener listener) {
		synchronized (mListenerLock) {
			mListeners.remove(listener);
		}
	}
	
	class StartThread extends Thread {
		public void run() {
			try {
				mSocket = new Socket(mRemoteIp, mPort);
				mRunning = true;
				
				mStartThread.start();
				mReceiveThread.start();
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class ReceiveThread extends Thread {
		public void run() {
			byte[] buffer = new byte[2048];
			try {
				InputStream socketInputStream = mSocket.getInputStream();
				while (mRunning) {
					int bytesRead = socketInputStream.read(buffer, 0, 2048);
					if (bytesRead != -1) {
						byte[] msg = Arrays.copyOfRange(buffer, 0,  bytesRead);
						synchronized (mListenerLock) {
							for (IDeviceListener listener : mListeners) {
								listener.receivedMessage(msg);
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	
	class SendThread extends Thread {
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}
