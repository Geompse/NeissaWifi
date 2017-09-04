package org.neissa.wifi;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import android.view.*;

public class MapListView extends ListView
{
	int[] boutons = {0xFF00FF00,0xFF0088FF,0xFFFFFF00};
	String filter = "";
	public MapListView(Context c)
	{
		super(c);
		init(c, null);
	}
	public MapListView(Context c, AttributeSet attrSet)
	{
		super(c, attrSet);
		init(c, attrSet);
	}
	public MapListView(Context c, AttributeSet attrSet, int def1)
	{
		super(c, attrSet, def1);
		init(c, attrSet);
	}
	public MapListView(Context c, AttributeSet attrSet, int def1, int def2)
	{
		super(c, attrSet, def1, def2);
		init(c, attrSet);
	}
	Paint paint;
	public void init(Context context, AttributeSet attrs)
	{
		setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View view, int position, long p4)
				{
					String id = MainActivity.arraylist.get(position).split(" ")[0];
					filter = (filter.equals(id) ?"": id);
				}

			});
		paint = new Paint();
        paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		/*if (MainActivity.current != -1)
		 return;*/

		float mw = getWidth();
		float mh = getHeight();

		float pl = getPaddingLeft();
		float pr = getPaddingRight();
		float pt = getPaddingTop();
		float pb = getPaddingBottom();

		float w = mw - pl + pr;
		float h = mh - pt + pb;

		float r = Math.min(w, h) / 2;
		float x = pl + w / 2;
		float y = pt + h - r * 4 / 5;
		paint.setStrokeWidth(1);
		paint.setColor(0x88008800);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawCircle(x, y, r, paint);
		for (int i=0; i < 3; i++)
		{
			float r1 = 10;
			float x1 = x - r / 2 * (float)Math.sin(i * 2 * Math.PI / 3);
			float y1 = y - r / 2 * (float)Math.cos(i * 2 * Math.PI / 3);
			paint.setColor(boutons[i] - 0x88000000);
			canvas.drawCircle(x1, y1, r1, paint);
			paint.setColor(boutons[i]);
			paint.setStyle(Paint.Style.STROKE);
			for (String id : MainActivity.networks.keySet())
				if (filter.equals(id) || filter.equals("") || filter.equals("--"))
					canvas.drawCircle(x1, y1, r / 2 * ((float)MainActivity.networks.get(id).getLevel(i)) / -100, paint);
		}
		paint.setStrokeWidth(5);
		paint.setColor(0xFFFF00FF);
		for (String id : MainActivity.networks.keySet())
			if (filter.equals(id) || filter.equals("") || filter.equals("--"))
			{
				float level1 = MainActivity.networks.get(id).getLevel(0);
				float level2 = MainActivity.networks.get(id).getLevel(1);
				float level3 = MainActivity.networks.get(id).getLevel(2);
				float vr = 5;
				float vx = x;
				float vy = y;
				vx += vr*(level1-level2)*Math.cos(Math.PI/3);
				vy -= vr*(level1-level2)*Math.sin(Math.PI/3);
				vx += vr*(level2-level3)*Math.cos(Math.PI);
				vy -= vr*(level2-level3)*Math.sin(Math.PI);
				vx += vr*(level3-level1)*Math.cos(-Math.PI/3);
				vy -= vr*(level3-level1)*Math.sin(-Math.PI/3);
				canvas.drawLine(x, y, vx, vy, paint);
			}
	}

}
