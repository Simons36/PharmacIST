package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.MapFragment
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.ProfileFragment
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.SearchMedicineFragment

class MainMenuActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // Initialize map fragment only once
        mapFragment = MapFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                R.id.pharmacy_map -> {
                    replaceFragment(mapFragment)
                    true
                }

                R.id.search_medicine -> {
                    replaceFragment(SearchMedicineFragment())
                    true
                }

                else -> false
            }
        }

        // Set the default selected item
        bottomNavigationView.selectedItemId = R.id.navigation_profile
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // Check if there's already a fragment added to the container
        val currentFragment = getVisibleFragment()

        // Hide the current fragment if it exists
        currentFragment?.let {
            transaction.hide(it)
            Log.i("DEBUG", "Hiding fragment: ${it.javaClass.simpleName}")
        }

        // Check if the fragment to be replaced is already added
        val existingFragment =
            supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)

        if (existingFragment == null) {
            // Add the new fragment if it's not already added
            transaction.add(R.id.fragment_container, fragment, fragment.javaClass.simpleName)
        } else {
            // Show the existing fragment if it was already added
            transaction.show(existingFragment)
        }

        // Commit the transaction
        transaction.commit()
    }

    private fun getVisibleFragment(): Fragment? {
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

}
