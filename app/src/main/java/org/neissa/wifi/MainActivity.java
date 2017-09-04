package org.neissa.wifi;

import android.app.*;
import android.content.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.util.*;
import android.view.*;
import java.security.*;

public class MainActivity extends Activity
{
	public static int current = 0;

    WifiManager wifi;
	HashMap<String,Integer> hashmap = new HashMap<>();
	public static HashMap<String,NeissaNetwork> networks = new HashMap<>();
    public static ArrayList<String> arraylist = new ArrayList<>();
    ArrayAdapter adapter;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        adapter = new ArrayAdapter<>(this, R.layout.network, arraylist);
		((ListView)findViewById(R.id.wifilist)).setAdapter(this.adapter);

		wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		new Timer().scheduleAtFixedRate(new TimerTask(){
				@Override
				public void run()
				{
					if (current != -1)
						wifi.startScan();
				}
			}, 0, 5000);
	}
	public void map(View view)
	{
		current = -1;
		displayMap();
	}
	public void at0(View view)
	{
		current = 0;
		displayList();
	}
	public void at1(View view)
	{
		current = 1;
		displayList();
	}
	public void at2(View view)
	{
		current = 2;
		displayList();
	}
	public void displayMap()
	{
		//arraylist.clear();
		//adapter.notifyDataSetChanged();
	}
	public void displayList()
	{
		ArrayList<NeissaNetwork> tmp = new ArrayList<NeissaNetwork>();
		for (String id : networks.keySet())
			tmp.add(networks.get(id));
		Collections.sort(tmp, new Comparator<NeissaNetwork>() {
				public int compare(NeissaNetwork n1, NeissaNetwork n2)
				{
					return n2.getLevel(current) - n1.getLevel(current);
				}
			});
		arraylist.clear();
		int nbgroup = 0;
		for (NeissaNetwork network : tmp)
		{
			int level = network.getLevel(current);
			String group = null;
			if (nbgroup == 0)
			{
				group = "excellente";
				nbgroup++;
			}
			if (nbgroup == 1 && level <= -50)
			{
				group = "bonne";
				nbgroup++;
			}
			if (nbgroup == 2 && level <= -60)
			{
				group = "moyenne";
				nbgroup++;
			}
			if (nbgroup == 3 && level <= -70)
			{
				group = "faible";
				nbgroup++;
			}
			if (group != null)
			{
				if (arraylist.size() > 0)
					arraylist.add("");
				arraylist.add("-- Réception " + group + " --");
			}
			arraylist.add(network.getLabel(current));
		}
		adapter.notifyDataSetChanged();
	}
    BroadcastReceiver wifiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context c, Intent intent)
        {
			if (current == -1)
				return;

			List<ScanResult> results = wifi.getScanResults();

			for (int i=0; i < results.size(); i++)
			{
				ScanResult result = results.get(i);
				String id = result.BSSID.replace(":", "").toUpperCase();
				if (!networks.containsKey(id))
					networks.put(id, new NeissaNetwork());
				networks.get(id).add(result);
			}
			displayList();
        }
    };
	public class NeissaNetwork
	{
		public String id = "";
		public String BSSID = "";
		public String SSID = "";
		public int frequency = 0;
		public String capabilities = "";
		HashMap<Integer,ArrayList<ScanResult>> hashmap = new HashMap<>();
		NeissaNetwork()
		{
			for (int i=0; i <= 3; i++)
				hashmap.put(i, new ArrayList<ScanResult>());
		}
		public boolean add(ScanResult result)
		{
			hashmap.get(current).add(result);
			id = BSSID.replace(":", "").toUpperCase();
			BSSID = result.BSSID;
			SSID = result.SSID;
			frequency = result.frequency;
			capabilities = result.capabilities;
			return true;
		}
		public int getLevel(int current)
		{
			for (int j=hashmap.get(current).size()-1; j>=0; j--)
				if(hashmap.get(current).get(j).timestamp*1000 < System.currentTimeMillis()-60*1000)
					hashmap.get(current).remove(j);
			if (hashmap.get(current).size() == 0)
				return -100;
			int level = 0;
			for (int j=0; j < hashmap.get(current).size(); j++)
				level += hashmap.get(current).get(j).level;
			return (int)Math.round((double)level / hashmap.get(current).size());
		}
		public String getLabel(int current)
		{
			return id + " \"" + SSID + "\" " + getLevel(current) + "dB@" + frequency/*+":"+centerFreq0+":"+centerFreq1+";"+channelWidth*//*+" "+capabilities*/;
		}
	}

}
