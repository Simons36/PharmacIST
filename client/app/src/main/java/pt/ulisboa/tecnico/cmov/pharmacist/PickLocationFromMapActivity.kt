package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.MapFragment
import pt.ulisboa.tecnico.cmov.pharmacist.util.MapOpeningMode

class PickLocationFromMapActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location_from_map_activity)

        // Initialize a new instance of MapFragment
        mapFragment = MapFragment.newInstance(MapOpeningMode.MAP_PICK_LOCATION_MODE)

        // Add the fragment to the activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mapFragment, MapFragment::class.java.simpleName)
            .commit()



        // Add any event listeners or additional setup here
    }

}
