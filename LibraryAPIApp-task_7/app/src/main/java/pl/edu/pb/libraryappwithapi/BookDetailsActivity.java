package pl.edu.pb.libraryappwithapi;

import static pl.edu.pb.libraryappwithapi.MainActivity.IMAGE_URL_BASE;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

public class BookDetailsActivity extends AppCompatActivity {
    public final static String EXTRA_BOOK_OBJ = "EXTRA_BOOK_OBJ";

    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView numberOfPagesTextView;
    private ImageView bookCover;

    //ADDITIONAL INFO
    private TextView bookFirstPublishYearTextView;
    private TextView bookLanguagesTextView;
    private TextView bookPublishersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details);
        bookTitleTextView = findViewById(R.id.book_title);
        bookAuthorTextView = findViewById(R.id.book_author);
        numberOfPagesTextView = findViewById(R.id.number_of_pages);
        bookCover = findViewById(R.id.img_cover);

        bookFirstPublishYearTextView = findViewById(R.id.book_first_publish_year);
        bookLanguagesTextView = findViewById(R.id.book_languages);
        bookPublishersTextView = findViewById(R.id.book_publishers);

        bookCover = findViewById(R.id.img_cover);

        Book book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK_OBJ);

        bookTitleTextView.setText(getString(R.string.title) + book.getTitle());
        bookAuthorTextView.setText(getString(R.string.author) + TextUtils.join(", ", book.getAuthors()));
        numberOfPagesTextView.setText(getString(R.string.no_pages) + book.getNumberOfPages());
        bookFirstPublishYearTextView.setText(getString(R.string.year_published) + String.valueOf(book.getFirstPublishYear()));
        bookLanguagesTextView.setText(getString(R.string.languages) + TextUtils.join(", ", book.getLanguages()));
        bookPublishersTextView.setText(getString(R.string.publishers) + TextUtils.join(", ", book.getPublishers()));

        if (book.getCover() != null) {
            Picasso.with(getApplicationContext())
                    .load(IMAGE_URL_BASE + book.getCover() + "-L.jpg")
                    .placeholder(R.drawable.ic_baseline_book_24).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.ic_baseline_book_24);
        }
    }


}
