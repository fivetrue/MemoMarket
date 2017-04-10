package com.fivetrue.market.memo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.model.vo.Product;
import com.opencsv.CSVWriter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 7..
 */

public class ExportUtil {

    private static final String TAG = "ExportUtil";

    public static final String CSV_FILE_DIRECTORY = "/memo/csv/";
    public static final String EXCEL_FILE_DIRECTORY = "/memo/excel/";
    public static final String FILE_CSV = ".csv";
    public static final String FILE_EXCEL = ".xlsx";

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");

    public static String writeProductsToCVSInExternalStorage(Context context, String fileName, List<Product> objects) throws ExportException {
        new File(Environment.getExternalStorageDirectory(), CSV_FILE_DIRECTORY).mkdirs();
        if(!fileName.endsWith(FILE_CSV)){
            fileName += FILE_CSV;
        }
        File file = new File(Environment.getExternalStorageDirectory(), CSV_FILE_DIRECTORY + fileName);
        return writeProductsToCVS(context, file, objects);
    }

    public static String writeProductsToCVS(Context context, File file, List<Product> objects) throws ExportException {
        if (file != null && objects != null) {
            if (!file.exists()) {
                try {
                    if (file.createNewFile()){
                        Log.i(TAG, "writeProductsToCVS: create file " + file.getAbsolutePath());
                    }else{
                        throw new IOException("Can not create file " + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    throw new ExportException("FAIL : create file", e.getCause());
                }
            }

            List<String[]> list = Observable.fromIterable(objects)
                    .map(object -> {
                        String[] data = new String[3];
                        data[0] = getDate(object);
                        data[1] = getName(object);
                        data[2] = getPrice(object);
                        return data;
                    }).toList().blockingGet();

            list.add(0, makeHeaderFields(context));
            CSVWriter cw = null;
            try {
                cw = new CSVWriter(new FileWriter(file), ',', '"');
            } catch (IOException e) {
                Log.e(TAG, "writeProductsToCVS: ", e);
                throw new ExportException("FAIL : write data", e.getCause());
            }
            Iterator<String[]> it = list.iterator();
            while (it.hasNext()) {
                String[] s = it.next();
                cw.writeNext(s);
            }
            Log.i(TAG, "writeProductsToCVS: finish write, try to close");
            try {
                cw.close();
            } catch (IOException e) {
                Log.e(TAG, "writeProductsToCVS: ", e);
                throw new ExportException("FAIL : close ", e.getCause());

            }
        }
        Log.d(TAG, "writeProductsToCVS() returned: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String writeProductToExcelInExternalStorage(Context context, String fileName, List<Product> objects) throws ExportException {
        new File(Environment.getExternalStorageDirectory(), EXCEL_FILE_DIRECTORY).mkdirs();
        if(!fileName.endsWith(FILE_EXCEL)){
            fileName += FILE_EXCEL;
        }
        File file = new File(Environment.getExternalStorageDirectory(), EXCEL_FILE_DIRECTORY + fileName);
        return writeProductToExcel(context, file, objects);
    }


    public static String writeProductToExcel(Context context, File file, List<Product> objects) throws ExportException {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(file.getName());
        int rowCount = 0;
        Log.i(TAG, "writeProductToExcel: make header row : " + rowCount);
        Row row = sheet.createRow(rowCount);
        String[] header = makeHeaderFields(context);
        for(int i = 0 ; i < header.length ; i++){
            row.createCell(i).setCellValue(header[i]);
        }

        List<String[]> list = Observable.fromIterable(objects)
                .map(object -> {
                    String[] data = new String[3];
                    data[0] = getDate(object);
                    data[1] = getName(object);
                    data[2] = getPrice(object);
                    return data;
                }).toList().blockingGet();

        for(String[] data : list){
            rowCount ++;
            row = sheet.createRow(rowCount);
            for(int i = 0 ; i < data.length ; i++){
                row.createCell(i).setCellValue(data[i]);
            }

        }

        // 출력 파일 위치및 파일명 설정
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            workbook.write(outFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "writeProductToExcel: FILE NOT FOUND", e);
            throw new ExportException("FILE NOT FOUND", e.getCause());
        } catch (IOException e) {
            Log.e(TAG, "writeProductToExcel: IO Error", e);
            throw new ExportException("IO ERROR", e.getCause());
        }finally {
            if(outFile != null){
                try {
                    outFile.close();
                } catch (IOException e) {
                    Log.e(TAG, "writeProductToExcel: file close error", e);
                }
            }
        }
        return file.getAbsolutePath();
    }

    private static String[] makeHeaderFields(Context context){
        return new String[]{context.getString(R.string.date)
                , context.getString(R.string.used)
                , context.getString(R.string.price)};
    }

    private static String getName(Product product){
        String name = null;
        if(!TextUtils.isEmpty(product.getStoreName())){
            name = product.getStoreName();
        }
        return !TextUtils.isEmpty(name) ? name + "-" + product.getName() : product.getName();
    }

    private static String getDate(Product product){
        return SDF.format(new Date(product.getCheckOutDate()));
    }

    private static String getPrice(Product product){
        return CommonUtils.convertToCurrency(product.getPrice());
    }

    public static class ExportException extends Exception{
        public ExportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
