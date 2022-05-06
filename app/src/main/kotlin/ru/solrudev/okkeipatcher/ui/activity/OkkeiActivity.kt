package ru.solrudev.okkeipatcher.ui.activity

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
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding

@AndroidEntryPoint
class OkkeiActivity : AppCompatActivity(R.layout.okkei_nav_host) {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.okkei_nav_host_container)
	private lateinit var appBarConfiguration: AppBarConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		val navController = binding.okkeiNavHostContent.getFragment<NavHostFragment>().navController
		appBarConfiguration = AppBarConfiguration(setOf(R.id.home_fragment, R.id.work_fragment))
		setupActionBarWithNavController(navController, appBarConfiguration)
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