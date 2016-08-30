package com.myappintabsswipe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bishal on 7/26/2015.
 */
public class CustomFrontTableAdapter extends ArrayAdapter<String>  {

    OrderedDBAdapter odb;
    String tableNum;



    Cursor cur;

    public CustomFrontTableAdapter(Context context, String[] tableNumber) {
        super(context, R.layout.front_order_layout,tableNumber);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater bishalsInflater= LayoutInflater.from(getContext());
View customView=bishalsInflater.inflate(R.layout.front_order_layout,parent,false);


         tableNum = getItem(position);

        TextView tableNumText=(TextView) customView.findViewById(R.id.testTableNum);
        tableNumText.setText("                  Table no "+tableNum);


        printDatabase(customView);
        printDatabase(customView);
        return customView;


    }
    public void printDatabase(View customView){

        odb=new OrderedDBAdapter(getContext());
        OrderTable tableNo= new OrderTable(tableNum);

        odb.setName(tableNo);
        final Cursor[] cursor = {odb.getAllRows()};
        final String[] fromFieldNames = new String[]{
                OrderedDBAdapter.COLUMN_AMOUNT,
                OrderedDBAdapter.COLUMN_PRODUCTNAME,
                OrderedDBAdapter.COLUMN_STATUS,
                OrderedDBAdapter.COLUMN_ID
        };

        int[] toViewIDs = new int[]{
               // R.id.testId,
                R.id.testAmt,
                R.id.testItem,
                R.id.testStatus,


                // R.id.testAmt,
                //R.id.testItem
        };

        final SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getContext(), R.layout.custom_row_front_order_layout, cursor[0], fromFieldNames, toViewIDs, 0);

        final ListView myList = (ListView) customView.findViewById(R.id.testListView);
        myList.setAdapter(myCursorAdapter);

        myList.setTag(tableNum);
       // myList.setTag(String.valueOf(position));
        setListViewHeightBasedOnChildren(myList);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // String rowPosition = (String)view.getTag();
                view.setSelected(true);
                String num= myList.getTag().toString();
                cur = (Cursor) myList.getItemAtPosition(position);
                String SelectedItem = cur.getString(cur.getColumnIndexOrThrow(fromFieldNames[1]));
                String SelectedId = cur.getString(cur.getColumnIndexOrThrow(fromFieldNames[3]));

                Toast.makeText(getContext(),num,Toast.LENGTH_LONG).show();
                    odb=new OrderedDBAdapter(getContext());
                    OrderTable tableNum=new OrderTable(num);
                odb.setName(tableNum);
                odb.updateStatus(SelectedId);
              /* FrontOrders frontOrders=new FrontOrders();

                frontOrders.printTables();*/





            }
        });
    }





    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        setListViewHeight(listView, totalHeight);
    }

    public static void setListViewHeight(ListView listView, int height) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height + (listView.getDividerHeight() * (listView.getAdapter().getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}

