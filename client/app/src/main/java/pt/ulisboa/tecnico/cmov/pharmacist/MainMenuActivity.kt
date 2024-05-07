package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.MapFragment
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.ProfileFragment
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.SearchMedicineFragment

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_profile -> {
                    // Handle click on Home tab
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.pharmacy_map -> {
                    // Handle click on Dashboard tab
                    replaceFragment(MapFragment())
                    true
                }
                R.id.search_medicine -> {
                    // Handle click on Notifications tab
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}