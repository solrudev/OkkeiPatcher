package solru.okkeipatcher.pm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class InstallationEventsReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent?) {
		val manager = LocalBroadcastManager.getInstance(context)
		val modifiedIntent = Intent(intent)
		modifiedIntent.component = null
		manager.sendBroadcast(modifiedIntent)
	}
}