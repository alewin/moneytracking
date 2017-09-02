package com.unibo.koci.moneytracking.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReportActivity extends AppCompatActivity {

    Toolbar report_toolbar;
    Spinner spinner;
    Button report_button;
    ListView pdf_report_list;
    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    LocalDate start, end;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_reports:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Remove all reports?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHelper.clearReport()) {
                                    Toast.makeText(ReportActivity.this, "Reports deleted", Toast.LENGTH_LONG).show();
                                    update_listreport();
                                }

                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dbHelper = new DBHelper(this);
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();


        init_listview();
        init_toolbar();
        init_spinner();

        loadReportData(); // check if there are some item
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

        update_listreport();
    }

    private void update_listreport() {
        arrayAdapter.clear();
        listItems.clear();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";

        File fileList = new File(path);
        if (fileList.exists() && fileList != null) {
            File[] files = fileList.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.getName().contains("Report-") && f.getName().contains("pdf")) {
                        arrayAdapter.add(f.getName());
                    }
                }
            }
        }
        arrayAdapter.notifyDataSetChanged();

    }

    private void loadReportData() {
        LocalDate s = new LocalDate(new Date(0)), e = new LocalDate();

        TextView textView_report = (TextView) findViewById(R.id.textView_report);
        String reportText = "Total: " + String.valueOf(dbHelper.getTotal(s, e)) +
                "€\nTotal expense: " + String.valueOf(dbHelper.getTotalExpense(s, e)) +
                "€\nTotal profit: " + String.valueOf(dbHelper.getTotalProfit(s, e)) +
                "€\nAverange expense: " + String.valueOf(dbHelper.getAVGExpense(s, e)) +
                "€\nAverange profit: " + String.valueOf(dbHelper.getAVGProfit(s, e)) +
                "€\nMin expense: " + String.valueOf(dbHelper.getMINExpense(s, e)) +
                "€\nMax profit: " + String.valueOf(dbHelper.getMAXProfit(s, e)) + "€";

        textView_report.setText(reportText);
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


    private boolean createPdf() {

        Document doc = new Document();
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.ITALIC);

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


            Font bold_red = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.RED);
            Chunk TitleText = new Chunk("Report MoneyTrack ", bold_red);
            Paragraph p1 = new Paragraph(TitleText);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(p1);

            Font bold = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC, BaseColor.BLACK);
            Chunk SubTitleText = new Chunk(start.toString() + " - " + end.toString(), bold);
            Paragraph p2 = new Paragraph(SubTitleText);
            p2.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(p2);

            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            ArrayList<String> arrayStringReport = new ArrayList<>();
            arrayStringReport.add("Total: " + String.valueOf(dbHelper.getTotal(start, end)) + "€");
            arrayStringReport.add("Total expense " + String.valueOf(dbHelper.getTotalExpense(start, end)) + "€");
            arrayStringReport.add("Total profit: " + String.valueOf(dbHelper.getTotalProfit(start, end)) + "€");
            arrayStringReport.add("Averange expense: " + String.valueOf(dbHelper.getAVGExpense(start, end)) + "€");
            arrayStringReport.add("Averange profit: " + String.valueOf(dbHelper.getAVGProfit(start, end)) + "€");
            arrayStringReport.add("Min expense: " + String.valueOf(dbHelper.getMINExpense(start, end)) + "€");
            arrayStringReport.add("Max profit: " + String.valueOf(dbHelper.getMAXProfit(start, end)) + "€");

            for (int i = 0; i < arrayStringReport.size(); i++) {
                Paragraph p = new Paragraph(arrayStringReport.get(i));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                p.setFont(normalFont);
                doc.add(p);
            }
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            List<MoneyItem> moneyItemList = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();


            PdfPTable table = new PdfPTable(new float[]{1, 3, 4, 3, 3, 4, 2});
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setLockedWidth(true);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Description");
            table.addCell("Category");
            table.addCell("Date");
            table.addCell("Location");
            table.addCell("Amount");
            table.setHeaderRows(1);

            PdfPCell[] cells = table.getRow(0).getCells();
            for (int j = 0; j < cells.length; j++) {
                cells[j].setBackgroundColor(BaseColor.GRAY);
            }


            for (int i = 0; i < moneyItemList.size(); i++) {
                MoneyItem mi = moneyItemList.get(i);

                table.addCell(String.valueOf(i));
                table.addCell(mi.getName());
                table.addCell(mi.getDescription());
                table.addCell(mi.getCategory().getName());
                table.addCell(new LocalDate(mi.getDate()).toString());
                table.addCell(mi.getLocation().getName());
                table.addCell(mi.getAmount() + "€");


            }
            doc.add(table);


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

                long item_count = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).count();

                if (item_count == 0) {
                    Toast.makeText(ReportActivity.this, "There aren't sufficient item for a report ", Toast.LENGTH_LONG).show();
                } else {
                    LocalDate dt = new LocalDate(LocalDate.now());
                    if (start.toString().isEmpty() || end.toString().isEmpty()) {
                        Toast.makeText(ReportActivity.this, "Please fill all input", Toast.LENGTH_LONG).show();
                    } else {
                        if (!isStoragePermissionGranted()) {
                            finish();
                        } else {
                            createPdf();
                            Toast.makeText(ReportActivity.this, "Pdf created", Toast.LENGTH_LONG).show();
                            update_listreport();

                        }
                    }
                }


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
                        start = dt;
                        end = dt;
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