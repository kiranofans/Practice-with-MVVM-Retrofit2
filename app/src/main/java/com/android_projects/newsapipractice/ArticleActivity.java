package com.android_projects.newsapipractice;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android_projects.newsapipractice.data.Models.Article;
import com.android_projects.newsapipractice.data.Models.NewsArticleMod;
import com.android_projects.newsapipractice.databinding.ActivityArticleBinding;
import com.android_projects.newsapipractice.network.HttpHelper;
import com.android_projects.newsapipractice.network.HttpRequestClient;
import com.bumptech.glide.Glide;

import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_IMG_URL;
import static com.android_projects.newsapipractice.data.AppConstants.EXTRA_KEY_TITLE;
import static com.android_projects.newsapipractice.network.APIConstants.API_KEY;
import static com.android_projects.newsapipractice.network.APIConstants.BASE_URL;
import static com.android_projects.newsapipractice.network.APIConstants.ENDPOINT_EVERYTHING;

public class ArticleActivity extends AppCompatActivity {

    private ActivityArticleBinding mBinding;

    private NewsArticleMod newsArticleMod;
    private Article articleMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article);

        getStringExtra();

        newsArticleMod = new NewsArticleMod();
        articleMod = new Article();

    }

    private void getStringExtra(){
        String title = getIntent().getExtras().getString(EXTRA_KEY_TITLE);
        String imgURL = getIntent().getExtras().getString(EXTRA_KEY_IMG_URL);

        mBinding.articleTvContentTitle.setText(title);
        Glide.with(this).load(imgURL).into(mBinding.articleImgViewContent);
    }

    class AsyncTaskLoadArticle extends AsyncTask<Void, Void, String> {
        private HttpRequestClient client;
        @Override
        protected String doInBackground(Void... voids) {
            client = new HttpRequestClient();
            return client.requestGETString(BASE_URL + ENDPOINT_EVERYTHING +
                    "?bitcoin&apiKey="+API_KEY);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            HttpHelper.fromJson(result, Article.class);

            mBinding.articleAuthorTv.setText(articleMod.getAuthor());
            mBinding.articleTvDate.setText(articleMod.getPublishedAt());
            mBinding.articleTvContent.setText(articleMod.getContent());
        }
    }

}
