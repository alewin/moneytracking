package com.unibo.koci.moneytracking.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ReportActivity extends AppCompatActivity {

    Toolbar report_toolbar;
    Spinner spinner;
    Button report_button;
    ListView pdf_report_list;
    DBHelper dbHelper;

    MoneyItemDao moneyItemDao;
    List<MoneyItem> money_list;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    LocalDate start, end;
    String pdfText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DBHelper(this);
        init_listview();
        init_toolbar();
        init_spinner();
        init_report_button();
    }


    private void init_listview() {


        pdf_report_list = (ListView) findViewById(R.id.pdf_report_list);
        pdf_report_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = ((TextView) view).getText().toString();
                Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_SHORT).show();

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
                File dir = new File(path);
                File file = new File(dir, filename);
                viewPdf(file);
            }
        });


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        pdf_report_list.setAdapter(arrayAdapter);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";

        File fileList = new File(path); // path which you want to read
        if (fileList != null) {
            File[] files = fileList.listFiles();

            for (File f : files) {
                arrayAdapter.add(f.getName());
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void loadReportData(LocalDate start, LocalDate end) {
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();
        money_list = new ArrayList<>();
        money_list = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        TextView textView_report = (TextView) findViewById(R.id.textView_report);
        pdfText = "Totale: " + String.valueOf(dbHelper.getTotal(start, end)) +
                "€\n|Totale speso: " + String.valueOf(dbHelper.getTotalExpense(start, end)) +
                "€\n|Totale guadagnato: " + String.valueOf(dbHelper.getTotalProfit(start, end));
                /*
                +
                "€\n|Media spesa: " + String.valueOf(dbh.avgLoss()) +
                "€\n|Media entrata: " + String.valueOf(dbh.avgEarn()) +
                "€\n|Massima spesa: " + String.valueOf(dbh.maxLoss()) +
                "€\n|Massimo guadagno: " + String.valueOf(dbh.maxEarn()) + "€";
                */
        textView_report.setText(pdfText);
    }

    private void viewPdf(File pdf_file) {
        Intent intent = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".my.package.name.provider", pdf_file));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean createPdf2(String text) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/moneytrack";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);


            File file = new File(dir, "sample.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(document, fOut);

            //open the document
            document.open();


            Paragraph p1 = new Paragraph("Sample PDF CREATION USING IText");
            Font paraFont = new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            document.add(p1);

            Paragraph p2 = new Paragraph("This is an example of a simple paragraph");
            Font paraFont2 = new Font(Font.FontFamily.COURIER, 14.0f, 0, CMYKColor.GREEN);
            p2.setAlignment(Paragraph.ALIGN_CENTER);
            p2.setFont(paraFont2);

            document.add(p2);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_action_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            document.add(myImg);


        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            return false;

        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            return false;

        } finally {
            document.close();
            return true;
        }

    }

    private boolean createPdf(String text) {
        Document doc = new Document();
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
        // Font font = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            File file = new File(dir, "Report-" + start.toString() + "-" + end.toString() + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter.getInstance(doc, fOut);


            doc.open();

            Paragraph p1 = new Paragraph("Report MoneyTrack");
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(boldFont);
            doc.add(p1);

            Paragraph p2 = new Paragraph(start.toString() + " - " + end.toString());
            p2.setAlignment(Paragraph.ALIGN_CENTER);
            p2.setFont(normalFont);
            doc.add(p2);

            String[] arrayS = pdfText.split("\\|");

            for (int i = 0; i < 10; i++) {

                Paragraph p = new Paragraph(arrayS[i]);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                p.setFont(normalFont);
                doc.add(p);
            }


        } catch (DocumentException de) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            doc.close();
            return true;
        }

    }


    private void init_report_button() {
        report_button = (Button) findViewById(R.id.report_button);
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalDate dt = new LocalDate(LocalDate.now());
                if (start.toString().isEmpty() || end.toString().isEmpty()) {
                    Toast.makeText(ReportActivity.this, "Please fill all input", Toast.LENGTH_LONG).show();
                } else {
                    loadReportData(start, end);
                    if (!isStoragePermissionGranted()) {
                        finish();
                    } else {
                        createPdf(pdfText);
                        Toast.makeText(ReportActivity.this, "Pdf created", Toast.LENGTH_LONG).show();
                    }
                }
                arrayAdapter.notifyDataSetChanged();


            }
        });

    }

    private void init_spinner() {
        spinner = (Spinner) findViewById(R.id.report_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {

                LocalDate dt = new LocalDate(LocalDate.now());


                switch (position) {
                    case 0: // Day
                        start = dt.minusDays(1);
                        end = dt.plusDays(1);
                        break;
                    case 1: // week
                        start = dt.dayOfWeek().withMinimumValue();
                        end = dt.dayOfWeek().withMaximumValue();
                        break;
                    case 2: // month
                        start = dt.dayOfMonth().withMinimumValue();
                        end = dt.dayOfMonth().withMaximumValue();
                        break;
                    case 3: // year
                        start = dt.dayOfYear().withMinimumValue();
                        end = dt.dayOfYear().withMaximumValue();
                        break;
                    case 4: // all
                        start = new LocalDate(0);
                        end = dt;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }


    private void init_toolbar() {
        report_toolbar = (Toolbar) findViewById(R.id.toolbar_report);
        setSupportActionBar(report_toolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


}
