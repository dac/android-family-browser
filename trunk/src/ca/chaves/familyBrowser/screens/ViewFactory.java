package ca.chaves.familyBrowser.screens;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The class ViewFactory contains functions that make it easy to inflate views
 * from layouts. We use this factory to build screens.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class ViewFactory {

    /** The application context */
    private final Context m_context;
    /** The layout inflater */
    private final LayoutInflater m_inflater;

    /**
     * Constructor.
     *
     * @param activity
     */
    public ViewFactory(final Activity activity) {
        this.m_context = activity.getApplicationContext();
        this.m_inflater = activity.getLayoutInflater();
    }

    /**
     * Get the application context.
     *
     * @return Application.Context
     */
    public Context getContext() {
        return this.m_context;
    }

    /**
     * Create a view from the given layout resource_id.
     *
     * @param layoutResource
     * @param parentView
     * @return View
     */
    public View createView(final int layoutResource, final ViewGroup parentView) {
        return this.m_inflater.inflate(layoutResource, parentView, false);
    }

    /**
     * Create a text view from the given layout resource_id.
     *
     * @param text
     * @param textResource
     *            resource_id, embedded inside the layoutResource
     * @param layoutResource
     *            resource_id
     * @param parentView
     * @return View
     */
    public View createTextView(final String text, final int textResource, final int layoutResource, final ViewGroup parentView) {
        final View layout = this.createView(layoutResource, parentView);
        if (text != null) {
            final TextView textView = (TextView) layout.findViewById(textResource);
            textView.setText(text);
        }
        return layout;
    }

    /**
     * Create two text views from the given layout resource_id.
     *
     * @param text
     * @param textResource
     *            resource_id, embedded inside the layoutResource
     * @param text2
     * @param text2Resource
     * @param layoutResource
     *            resource_id
     * @param parentView
     * @return View
     */
    public View createTextViews(final String text, final int textResource, final String text2, final int text2Resource,
                    final int layoutResource, final ViewGroup parentView) {
        final View layout = this.createView(layoutResource, parentView);
        if (text != null) {
            final TextView textView = (TextView) layout.findViewById(textResource);
            textView.setText(text);
        }
        if (text2 != null) {
            final TextView textView = (TextView) layout.findViewById(text2Resource);
            textView.setText(text2);
        }
        return layout;
    }

}
