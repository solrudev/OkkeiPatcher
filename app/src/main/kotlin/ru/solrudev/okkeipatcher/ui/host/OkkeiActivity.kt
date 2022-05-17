package ru.solrudev.okkeipatcher.ui.host

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.OkkeiContainerBinding

@AndroidEntryPoint
class OkkeiActivity : AppCompatActivity(R.layout.okkei_container) {

	private val binding by viewBinding(OkkeiContainerBinding::bind, R.id.okkei_container)
	private val appBarConfiguration = AppBarConfiguration(setOf(R.id.home_fragment, R.id.work_fragment))

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		with(binding.okkeiContent) {
			setSupportActionBar(toolbar)
			val navController = okkeiNavHostContent.getFragment<NavHostFragment>().navController
			setupActionBarWithNavController(navController, appBarConfiguration)
		}
		setupOptionsMenu()
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.okkei_nav_host_content)
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}

	private fun setupOptionsMenu() {
		addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.okkei_menu, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				val navController = findNavController(R.id.okkei_nav_host_content)
				return menuItem.onNavDestinationSelected(navController)
			}
		})
	}
}