package solru.okkeipatcher.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.R
import solru.okkeipatcher.databinding.OkkeiNavHostBinding

@AndroidEntryPoint
class OkkeiActivity : AppCompatActivity(R.layout.okkei_nav_host) {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.okkei_nav_host_container)
	private lateinit var appBarConfiguration: AppBarConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		val navController = binding.okkeiNavHostContent.getFragment<NavHostFragment>().navController
		appBarConfiguration = AppBarConfiguration(navController.graph)
		setupActionBarWithNavController(navController, appBarConfiguration)

		val perms = arrayOf(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE
		)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
			&& checkSelfPermission(perms[0]) != PackageManager.PERMISSION_GRANTED
		) {
			requestPermissions(perms, 0)
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.okkei_nav_host_content)
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val navController = findNavController(R.id.okkei_nav_host_content)
		return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
	}
}