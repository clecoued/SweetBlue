package com.idevicesinc.sweetblue;

import com.idevicesinc.sweetblue.utils.BitwiseEnum;

public class P_ServerStateTracker extends PA_StateTracker {

	private BleServer.StateListener m_stateListener;
	private final BleServer m_server;
	
	P_ServerStateTracker(BleServer server)
	{
		super(server.getManager().getLogger());
		
		m_server = server;
		
		set(BleDeviceState.UNDISCOVERED, true, BleDeviceState.DISCONNECTED, true);
	}
	
	public void setListener(BleServer.StateListener listener)
	{
		if( listener != null )
		{
			m_stateListener = new P_WrappingServerStateListener(listener, m_server.getManager().m_mainThreadHandler, m_server.getManager().m_config.postCallbacksToMainThread);
		}
		else
		{
			m_stateListener = null;
		}
	}

	@Override protected void onStateChange(int oldStateBits, int newStateBits)
	{
		if( m_stateListener != null )
		{
			m_stateListener.onStateChange(m_server, oldStateBits, newStateBits);
		}
		
		if( m_server.getManager().m_defaultServerStateListener != null )
		{
			m_server.getManager().m_defaultServerStateListener.onStateChange(m_server, oldStateBits, newStateBits);
		}
		
//		m_device.getManager().getLogger().e(this.toString());
	}

	@Override protected void append_assert(BitwiseEnum newState)
	{
		if( newState.ordinal() > BleDeviceState.CONNECTING.ordinal() )
		{
			//--- DRK > No longer valid...during the connection flow a rogue disconnect can come in.
			//---		This immediately changes the native state of the device but the actual callback
			//---		for the disconnect is sent to the update thread so for a brief time we can be
			//---		abstractly connected/connecting but actually not natively connected. 
//			m_device.getManager().ASSERT(m_device.m_nativeWrapper.isNativelyConnected());
		}
	}
	
	@Override public String toString()
	{
		return super.toString(BleDeviceState.values());
	}
}
