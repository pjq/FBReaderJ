/*
 * Copyright (C) 2009-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.preferences;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.preference.ListPreference;

import android.net.Uri;

import java.util.*;

import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.fbreader.formats.Formats;
import org.geometerplus.fbreader.formats.BigMimeTypeMap;
import org.geometerplus.android.fbreader.preferences.ZLPreferenceActivity.Screen;

import android.util.Log;

class FormatPreference extends ListPreference {
	private final ZLStringOption myOption;
	private final HashSet<String> myPaths = new HashSet<String>();
	private final Screen myScreen;

	FormatPreference(Context context, ZLStringOption option, String formatName, boolean isNative, Screen scr) {
		super(context);

		myOption = option;
		setTitle(formatName);
		myScreen = scr;

		final PackageManager pm = context.getPackageManager();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("file:///sdcard/fgsfds." + formatName));
		final String mimeType = BigMimeTypeMap.getType(formatName);
		if (mimeType != null) {
			intent.setDataAndType(Uri.parse("file:///sdcard/fgsfds." + formatName), mimeType);
		}
		for (ResolveInfo packageInfo : pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)) {
			if (!myPaths.contains(packageInfo.activityInfo.applicationInfo.packageName)) {
				values.add(packageInfo.activityInfo.applicationInfo.packageName);
				names.add(packageInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
				myPaths.add(packageInfo.activityInfo.applicationInfo.packageName);
			}
		}
		if (!isNative) {
			values.add("DELETE");
			names.add("Delete this format");
		}
		setEntries(names.toArray(new String[names.size()]));
		setEntryValues(values.toArray(new String[values.size()]));
		if (myOption.getValue() != "") {
			setValue(myOption.getValue());
		}
		if (getEntry() != null) {
			setSummary(getEntry());
		}
	}

	@Override
	protected void onDialogClosed(boolean result) {
		super.onDialogClosed(result);
		if (result) {
			if (getValue().equals("DELETE")) {
				myScreen.removePreference(this);
				Formats.removeFormat(getTitle().toString());
				myOption.setValue("");
			} else {
				setSummary(getEntry());
				myOption.setValue(getValue());
			}
		}
	}
}
