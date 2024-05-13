package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}