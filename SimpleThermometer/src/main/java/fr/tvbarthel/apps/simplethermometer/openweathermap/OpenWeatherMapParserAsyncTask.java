package fr.tvbarthel.apps.simplethermometer.openweathermap;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import fr.tvbarthel.apps.simplethermometer.R;

public class OpenWeatherMapParserAsyncTask extends AsyncTask<String, Integer, OpenWeatherMapParserResult> {

	private int mErrorMessage;
	private Listener mListener;

	public OpenWeatherMapParserAsyncTask(Listener listener) {
		super();
		mListener = listener;
	}


	@Override
	protected OpenWeatherMapParserResult doInBackground(String... params) {
		OpenWeatherMapParserResult result = null;
		try {
			final URL url = new URL(params[0]);
			publishProgress(10);
			final URLConnection urlConnection = url.openConnection();
			publishProgress(20);
			urlConnection.setConnectTimeout(10000);
			publishProgress(50);
			urlConnection.setUseCaches(true);
			final InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			publishProgress(80);
			final OpenWeatherMapParser parser = new OpenWeatherMapParser();
			result = parser.parse(inputStream);
			publishProgress(90);
		} catch (SocketTimeoutException e) {
			mErrorMessage = R.string.error_message_server_not_available;
		} catch (MalformedURLException e) {
			mErrorMessage = R.string.error_message_malformed_url;
		} catch (IOException e) {
			mErrorMessage = R.string.error_message_io_exception;
		} catch (XmlPullParserException e) {
			mErrorMessage = R.string.error_message_xml_pull_parser_exception;
		}
		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		mListener.onWeatherLoadingProgress(values[0]);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mListener.onWeatherLoadingProgress(0);
	}

	@Override
	protected void onPostExecute(OpenWeatherMapParserResult result) {
		super.onPostExecute(result);
		if (result == null) {
			mListener.onWeatherLoadingFail(mErrorMessage);
		} else {
			mListener.onWeatherLoadingSuccess(result);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		mListener.onWeatherLoadingCancelled();
	}

	public void setListener(Listener listener) {
		if (listener == null) {
			mListener = new DummyListener();
		} else {
			mListener = listener;
		}
	}

	public interface Listener {
		public void onWeatherLoadingSuccess(OpenWeatherMapParserResult result);

		public void onWeatherLoadingFail(int stringResourceId);

		public void onWeatherLoadingProgress(int progress);

		public void onWeatherLoadingCancelled();
	}

	private static class DummyListener implements Listener {
		@Override
		public void onWeatherLoadingSuccess(OpenWeatherMapParserResult result) {
		}

		@Override
		public void onWeatherLoadingFail(int stringResourceId) {
		}

		@Override
		public void onWeatherLoadingProgress(int progress) {
		}

		@Override
		public void onWeatherLoadingCancelled() {
		}
	}
}
