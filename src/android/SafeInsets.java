package com.fasal.safeinsets;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.apache.cordova.CordovaPlugin;

/**
 * Android 16 (API 36) ignores windowOptOutEdgeToEdgeEnforcement for apps targeting SDK 36,
 * so the web view draws under the status bar, navigation bar and keyboard.
 * cordova-android 14 (pinned by Meteor) does not handle this, so pad the web view's parent by those insets.
 * Remove once Meteor ships cordova-android 15+, which handles insets itself.
 */
public class SafeInsets extends CordovaPlugin {
  private static final int ANDROID_16 = 36;
  private static final int BAR_BACKGROUND_COLOR = Color.WHITE;

  @Override
  protected void pluginInitialize() {
    if (Build.VERSION.SDK_INT < ANDROID_16) return;

    cordova.getActivity().runOnUiThread(() -> {
      View parent = (View) webView.getView().getParent();
      if (parent == null) return;

      parent.setBackgroundColor(BAR_BACKGROUND_COLOR);
      ViewCompat.setOnApplyWindowInsetsListener(parent, (v, insets) -> {
        Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
        v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
        return WindowInsetsCompat.CONSUMED;
      });
      ViewCompat.requestApplyInsets(parent);
    });
  }
}
