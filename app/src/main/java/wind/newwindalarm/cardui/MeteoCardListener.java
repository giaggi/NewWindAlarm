package wind.newwindalarm.cardui;

import wind.newwindalarm.MeteoStationData;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface MeteoCardListener {
    // you can define any parameter as per your requirement
    public void meteocardselected(long index, final MeteoStationData meteoStationData);
}
