package biz.appvisor.push.android.sdk;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

public abstract class BaseHandlerActivity extends FragmentActivity {

    public ConcreteTestHandler handler = new ConcreteTestHandler();

    public void sendMessage(int what) {
        this.sendMessage(what, null);
    }

    public void sendMessage(int what, Bundle bundle) {
        Message message = handler.obtainMessage(what);
        if (bundle != null) {
            message.setData(bundle);
        }
        handler.sendMessage(message);
    }

    public abstract void processMessage(Message message);

    @Override
    public void onPause() {
        super.onPause();
        handler.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.setActivity(this);
        handler.resume();
    }

    /**
     * Message Handler class that supports buffering up of messages when the
     * activity is paused i.e. in the background.
     */
    static class ConcreteTestHandler extends PauseHandler {

        /**
         * Activity instance
         */
        protected BaseHandlerActivity activity;

        final void setActivity(BaseHandlerActivity activity) {
            this.activity = activity;
        }

        @Override
        final protected boolean storeMessage(Message message) {
            // All messages are stored by default
            return true;
        };

        @Override
        final protected void processMessage(Message msg) {
            if (activity != null) {
            	activity.processMessage(msg);
            }
        }
    }
}