package eu.doniec.piotr.naspotkanie.mobile;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MeetingMapActivity extends MapActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_map);
		
		MapView mapView = (MapView)findViewById(R.id.map_view);
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);	
		GeoPoint point = new GeoPoint(19240000,-99120000);
		OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
		
		FriendOverlay friend = new FriendOverlay(drawable);
		friend.addOverlay(overlayitem);
		
		mapOverlays.add(friend);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	class FriendOverlay extends ItemizedOverlay {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		public FriendOverlay(Drawable marker) {
			super(boundCenterBottom(marker));
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}
		
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
		
	}
	
}
