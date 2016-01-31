package pl.edu.pw.sgalazka.inz.devicesList;

import android.content.Context;

import pl.edu.pw.sgalazka.inz.R;

/**
 * Created by ga��zka on 2015-09-06.
 */
public class BTDeviceRow {

    private Context context;

    public BTDeviceRow(Context context, String name, deviceBonded bonded){
        this.context = context;
        this.name = name;
        this.bonded = bonded;
    }

    private String name = "";
    private deviceBonded bonded = deviceBonded.not_bonded;

    public deviceBonded getBonded() {
        return bonded;
    }

    public String getName() {
        return name;
    }

    enum deviceBonded{

        bonded(R.string.bondedStatus),
        not_bonded(R.string.not_bondedStatus);

        private int id;

        deviceBonded(int stringId){
            id = stringId;
        }

        public int getId(){
            return id;
        }
    }
}

