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

/**
 * An AsyncTask used to run the parsing of an XML flux from the OpenWeatherMap api.
 */
public class OpenWeatherMapParserAsyncTask extends AsyncTask<String, Integer, OpenWeatherMapParserResult> {

	//If there is an Exception, contains a displayable explanation.
	protected int mErrorMessage;
	//the listener that is notified
	protected Listener mListener;

	public OpenWeatherMapParserAsyncTask(Listener listener) {
		super();
		mListener = listener;
	}

	/*
		AsyncTask Overrides
	 */

	@Override
	protected OpenWeatherMapParserResult doInBackground(String... params) {
		OpenWeatherMapParserResult result = null;
		try {
			//Get the target url
			final URL url = new URL(params[0]);
			publishProgress(10);

			//Get a URLConnection
			final URLConnection urlConnection = url.openConnection();
			publishProgress(20);
			urlConnection.setConnectTimeout(10000);
			publishProgress(50);
			urlConnection.setUseCaches(true);

			//Get an InputStream
			final InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			publishProgress(80);

			//Parse the InputStream
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
		//Notify the listener of a loading progress
		mListener.onWeatherLoadingProgress(values[0]);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Notify the listener
		mListener.onWeatherLoadingProgress(0);
	}

	@Override
	protected void onPostExecute(OpenWeatherMapParserResult result) {
		super.onPostExecute(result);
		if (result == null) {
			//An exception has occurred
			//Notify the listener with the explanation
			mListener.onWeatherLoadingFail(mErrorMessage);
		} else {
			//Notify the listener with the parsed result
			mListener.onWeatherLoadingSuccess(result);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		//Notify the listener that the task has been cancelled
		mListener.onWeatherLoadingCancelled();
	}

	/**
	 * Set the listener
	 *
	 * @param listener {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserAsyncTask.Listener}
	 */
	public void setListener(Listener listener) {
		if (listener == null) {
			mListener = new DummyListener();
		} else {
			mListener = listener;
		}
	}

	/**
	 * An interface for Listeners
	 */
	public interface Listener {
		//Notify parsing success
		public void onWeatherLoadingSuccess(OpenWeatherMapParserResult result);

		//Notify parsing failure
		public void onWeatherLoadingFail(int stringResourceId);

		//Notify parsing progress
		public void onWeatherLoadingProgress(int progress);

		//Notify parsing cancel
		public void onWeatherLoadingCancelled();
	}

	/**
	 * A dummy implementation of {@link fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserAsyncTask.Listener}
	 */
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
