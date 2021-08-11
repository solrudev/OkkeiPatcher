package solru.okkeipatcher.ui.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.R
import solru.okkeipatcher.databinding.ActivityMainBinding
import solru.okkeipatcher.pm.PackageManager
import solru.okkeipatcher.viewmodels.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val _viewModel: MainViewModel by viewModels()
	private var _backPressed = false
	private var _lastBackPressedTimestamp: Long = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.lifecycleOwner = this
		binding.viewModel = _viewModel
		binding.activity = this

		setSupportActionBar(binding.toolbar)

		lifecycle.addObserver(_viewModel)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
			val perms =
				arrayOf(
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
				)
			requestPermissions(perms, 0)
		}
	}

	override fun onBackPressed() {
		val lastBackPressedTimestampTemp = _lastBackPressedTimestamp
		_lastBackPressedTimestamp = SystemClock.elapsedRealtime()
		val sinceLastBackPressed = _lastBackPressedTimestamp - lastBackPressedTimestampTemp

		if (_backPressed && sinceLastBackPressed <= 2000) {
			_backPressed = false
			super.onBackPressed()
			return
		}

		_backPressed = true
		Toast.makeText(this, R.string.back_button_pressed, Toast.LENGTH_SHORT).show()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> {
				Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun onPatchClick() {
		if (!_viewModel.isRunning) {
			_viewModel.patch()
			return
		}
		_viewModel.cancel()
	}

	fun onRestoreClick() {
		if (!_viewModel.isRunning) {
			_viewModel.restore()
			return
		}
		_viewModel.cancel()
	}

	fun onClearDataClick() {
	}

	fun onProcessSaveDataClick(view: View) {
		if (view !is CheckBox) return
		if (!view.isChecked) {
			return
		}
		//TODO: check for permissions and request them if not granted
	}

	fun onInfoButtonClick(view: View) {
		Snackbar.make(
			view,
			getString(R.string.info_version, PackageManager.versionString),
			Snackbar.LENGTH_LONG
		).setAction("Action", null).show()
	}
}