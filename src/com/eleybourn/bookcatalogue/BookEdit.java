/*
 * @copyright 2010 Evan Leybourn
 * @license GNU General Public License
 * 
 * This file is part of Book Catalogue.
 *
 * Book Catalogue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Book Catalogue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Book Catalogue.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.eleybourn.bookcatalogue;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

public class BookEdit extends Activity {

	private AutoCompleteTextView mAuthorText;
	private EditText mTitleText;
	private EditText mIsbnText;
	private AutoCompleteTextView mPublisherText;
	private DatePicker mDate_publishedText;
	private RatingBar mRatingText;
	private Spinner mBookshelfText;
	private ArrayAdapter<String> spinnerAdapter;
	private CheckBox mReadText;
	private AutoCompleteTextView mSeriesText;
	private EditText mSeriesNumText;
	private EditText mPagesText;
	private Button mConfirmButton;
	private Button mCancelButton;
    private Long mRowId;
    private CatalogueDBAdapter mDbHelper;
    private ImageView mImageView;
   
    protected void getRowId() {
        /* Get any information from the extras bundle */
       	Bundle extras = getIntent().getExtras();
       	mRowId = extras != null ? extras.getLong(CatalogueDBAdapter.KEY_ROWID) : null;
    }
    
    protected ArrayList<String> getAuthors() {
    	ArrayList<String> author_list = new ArrayList<String>();
    	Cursor author_cur = mDbHelper.fetchAllAuthors("All Books");
    	startManagingCursor(author_cur);
    	while (author_cur.moveToNext()) {
    		String family = author_cur.getString(author_cur.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_FAMILY_NAME));
    		String given = author_cur.getString(author_cur.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_GIVEN_NAMES));
    		author_list.add(family + ", " + given);
    	}
    	return author_list;
    }
    
    protected ArrayList<String> getSeries() {
    	ArrayList<String> series_list = new ArrayList<String>();
    	Cursor series_cur = mDbHelper.fetchAllSeries();
    	startManagingCursor(series_cur);
    	while (series_cur.moveToNext()) {
    		String series = series_cur.getString(series_cur.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_SERIES));
    		series_list.add(series);
    	}
    	return series_list;
    }
    
    protected ArrayList<String> getPublishers() {
    	ArrayList<String> publisher_list = new ArrayList<String>();
    	Cursor publisher_cur = mDbHelper.fetchAllPublishers();
    	startManagingCursor(publisher_cur);
    	while (publisher_cur.moveToNext()) {
    		String publisher = publisher_cur.getString(publisher_cur.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_PUBLISHER));
    		publisher_list.add(publisher);
    	}
    	return publisher_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        mDbHelper = new CatalogueDBAdapter(this);
	        mDbHelper.open();
	        
	        setContentView(R.layout.edit_book);
	       
	        ArrayAdapter<String> author_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getAuthors());
	        mAuthorText = (AutoCompleteTextView) findViewById(R.id.author);
	        mAuthorText.setAdapter(author_adapter);

	        mTitleText = (EditText) findViewById(R.id.title);
	        mIsbnText = (EditText) findViewById(R.id.isbn);

	        ArrayAdapter<String> publisher_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getPublishers());
	        mPublisherText = (AutoCompleteTextView) findViewById(R.id.publisher);
	        mPublisherText.setAdapter(publisher_adapter);
	        
	        mDate_publishedText = (DatePicker) findViewById(R.id.date_published);
	        mRatingText = (RatingBar) findViewById(R.id.rating);
	        mReadText = (CheckBox) findViewById(R.id.read);
	        
	        ArrayAdapter<String> series_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getSeries());
	        mSeriesText = (AutoCompleteTextView) findViewById(R.id.series);
	        mSeriesText.setAdapter(series_adapter);

	        mSeriesNumText = (EditText) findViewById(R.id.series_num);
	        mPagesText = (EditText) findViewById(R.id.pages);
            mConfirmButton = (Button) findViewById(R.id.confirm);
	        mCancelButton = (Button) findViewById(R.id.cancel);
	        mImageView = (ImageView) findViewById(R.id.row_img);
	        
	        /* Setup the Bookshelf Spinner */
	        mBookshelfText = (Spinner) findViewById(R.id.bookshelf);
	        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        mBookshelfText.setAdapter(spinnerAdapter);
	        
	        Cursor bookshelves = mDbHelper.fetchAllBookshelves();
	        if (bookshelves.moveToFirst()) { 
                do { 
                	spinnerAdapter.add(bookshelves.getString(1)); 
                } 
                while (bookshelves.moveToNext()); 
            } 

	        mRowId = savedInstanceState != null ? savedInstanceState.getLong(CatalogueDBAdapter.KEY_ROWID) : null;
	        if (mRowId == null) {
	        	getRowId();
	        }
	        populateFields();
	        
	        mConfirmButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                saveState();
	                setResult(RESULT_OK);
	                finish();
	            }
	        });
	        
	        mCancelButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                setResult(RESULT_OK);
	                finish();
	            }
	        });
	        
    	} catch (Exception e) {
    		//Log.e("Book Catalogue", "Unknown error " + e.toString());
    	}
    }
    
    private void populateFields() {
    	Bundle extras = getIntent().getExtras();
        if (mRowId == null) {
        	getRowId();
        }
        
        if (mRowId != null && mRowId > 0) {
        	// From the database (edit)
            Cursor book = mDbHelper.fetchBook(mRowId);
            startManagingCursor(book);
            
            mAuthorText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_AUTHOR)));
            mTitleText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_TITLE)));
            mIsbnText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_ISBN)));
            mPublisherText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_PUBLISHER)));
            String[] date = book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_DATE_PUBLISHED)).split("-");
            int yyyy = Integer.parseInt(date[0]);
            int mm = Integer.parseInt(date[1]);
            int dd = Integer.parseInt(date[2]);
            mDate_publishedText.updateDate(yyyy, mm, dd);
            mRatingText.setRating(book.getFloat(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_RATING)));
            
            String bs = book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_BOOKSHELF));
            mBookshelfText.setSelection(spinnerAdapter.getPosition(bs));
            //mBookshelfText.setSelection(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_BOOKSHELF)));
            
            mReadText.setChecked((book.getInt(book.getColumnIndex(CatalogueDBAdapter.KEY_READ))==0? false:true) );
            mSeriesText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_SERIES)));
            mSeriesNumText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_SERIES_NUM)));
            mPagesText.setText(book.getString(book.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_PAGES)));
            mConfirmButton.setText(R.string.confirm_save);
            String thumbFilename = Environment.getExternalStorageDirectory() + "/" + CatalogueDBAdapter.LOCATION + "/" + mRowId + ".jpg";
        	mImageView.setImageBitmap(BitmapFactory.decodeFile(thumbFilename));

        } else if (extras != null) {
        	// From the ISBN Search (add)
        	String[] book = extras.getStringArray("book");

            mAuthorText.setText(book[0]);
            mTitleText.setText(book[1]);
            mIsbnText.setText(book[2]);
            mPublisherText.setText(book[3]);
            try {
	            String[] date = book[4].split("-");
	            int yyyy = Integer.parseInt(date[0]);
	            int mm = Integer.parseInt(date[1]);
	            int dd = Integer.parseInt(date[2]);
	            mDate_publishedText.updateDate(yyyy, mm, dd);
            } catch (ArrayIndexOutOfBoundsException e) {
            	//do nothing
            } catch (NumberFormatException e) {
            	//do nothing
            }
            mRatingText.setRating(Float.valueOf(book[5]));

            mBookshelfText.setSelection(spinnerAdapter.getPosition(book[6]));
            String read = book[7];
            if (read == "true") {
                mReadText.setChecked(true);
            } else {
                mReadText.setChecked(false);
            }
            mSeriesText.setText(book[8]);
            mSeriesNumText.setText(book[10]);
            mPagesText.setText(book[9]);
            mConfirmButton.setText(R.string.confirm_add);
            String tmpThumbFilename = Environment.getExternalStorageDirectory() + "/" + CatalogueDBAdapter.LOCATION + "/tmp.jpg";
            mImageView.setImageBitmap(BitmapFactory.decodeFile(tmpThumbFilename));
        } else {
        	// Manual Add
            mConfirmButton.setText(R.string.confirm_add);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CatalogueDBAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String author = mAuthorText.getText().toString();
        
        /* Move "The, A, An" to the end of the string */
        String title = mTitleText.getText().toString();
        String[] title_words = title.split(" ");
        try {
        	if (title_words[0].matches("a|A|an|An|the|The")) {
        		title = "";
        		for (int i = 1; i < title_words.length; i++) {
        			if (i != 1) {
        				title += " ";
        			}
        			title += title_words[i];
        		}
        		title += ", " + title_words[0];
        	}
        } catch (Exception e) {
        	//do nothing. Title stays the same
        }
        
        String isbn = mIsbnText.getText().toString();
        String publisher = mPublisherText.getText().toString();
        int yyyy =  mDate_publishedText.getYear();
        int mm =  mDate_publishedText.getMonth();
        int dd =  mDate_publishedText.getDayOfMonth();
        String date_published = yyyy + "-" + mm + "-" + dd;
        float rating = mRatingText.getRating();
        String bookshelf = mBookshelfText.getAdapter().getItem(mBookshelfText.getSelectedItemPosition()).toString(); 
        //.getText().toString();
        Boolean read = mReadText.isChecked();
        String series = mSeriesText.getText().toString();
        String series_num = mSeriesNumText.getText().toString();
    	int pages = 0;
        try {
        	pages = Integer.parseInt(mPagesText.getText().toString());
        } catch (NumberFormatException e) {
        	pages = 0;
        }

        if (mRowId == null || mRowId == 0) {
        	/* Check if the book currently exists */
        	if (!isbn.equals("")) {
        		Cursor book = mDbHelper.fetchBookByISBN(isbn);
        		int rows = book.getCount();
        		if (rows != 0) {
        			Toast.makeText(this, R.string.book_exists, Toast.LENGTH_LONG).show();
        			return;
        		}
        	}
        	
            long id = mDbHelper.createBook(author, title, isbn, publisher, date_published, rating, bookshelf, read, series, pages, series_num);
            if (id > 0) {
                mRowId = id;
                String tmpThumbFilename = Environment.getExternalStorageDirectory() + "/" + CatalogueDBAdapter.LOCATION + "/tmp.jpg";
                String realThumbFilename = Environment.getExternalStorageDirectory() + "/" + CatalogueDBAdapter.LOCATION + "/" + id + ".jpg";
                File thumb = new File(tmpThumbFilename);
                File real = new File(realThumbFilename);
                thumb.renameTo(real);
            }
        } else {
            mDbHelper.updateBook(mRowId, author, title, isbn, publisher, date_published, rating, bookshelf, read, series, pages, series_num);
        }
        return;
    }

}