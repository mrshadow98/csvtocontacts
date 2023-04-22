package com.geniobits.csvtocontacts;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.siegmar.fastcsv.reader.CloseableIterator;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;

public class CSV {
    private String csv_path;
    private String csv_string;

    public CSV(String csv_path) {
        this.csv_path=csv_path;
        readCSV();
    }

    public List<String> getCSVHeaders(){
        List<String> headers = new ArrayList<>();
        try {

            Iterator<CsvRow> csv_data = CsvReader.builder().build(csv_string).iterator();
            if(csv_data.hasNext()){
                headers.addAll(csv_data.next().getFields());
                Log.e("headers", headers.get(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return headers;
    }


    public String readCSV(){
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(csv_path)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        csv_string = text.toString();
        return csv_string;
    }

    public ArrayList<Contacts> getContactList(String name_column, String email_column, String number_column, String init){
        ArrayList<Contacts> contactsArrayList = new ArrayList<>();
        for (final Iterator<NamedCsvRow> iterator = NamedCsvReader.builder()
                .build(csv_string).iterator(); iterator.hasNext();) {
            final NamedCsvRow csvRow = iterator.next();
            if(init==null){
                init="";
            }
            String name=null;
            String number=null;
            String email = null;
            if(name_column!=null){
                name = init+csvRow.getField(name_column);
            }
            if(number_column!=null){
                number = csvRow.getField(number_column);
            }
            if(email_column!=null){
                email = csvRow.getField(email_column);
            }
            contactsArrayList.add(new Contacts(name,number,email));
        }
        return contactsArrayList;
    }



}
