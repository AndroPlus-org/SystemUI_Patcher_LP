package org.androplus.systemuimod;

import android.content.res.XModuleResources;
import android.content.res.XResources.DimensionReplacement;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class NotificationiconPatcher implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage {
	public XSharedPreferences preference;
	private static String MODULE_PATH = null;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
	preference = new XSharedPreferences(NotificationiconPatcher.class.getPackage().getName());
		MODULE_PATH = startupParam.modulePath;
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		// For Change the number of tiles in a columns
		String scol = preference.getString("list_key_numcol", "4");
		int icol = Integer.parseInt(scol);
		// For Change the number of tiles in a row
		String srow = preference.getString("list_key", "4");
		int irow = Integer.parseInt(srow);
		// Change battery charged alert level
		String sbl = preference.getString("key_fullalert_et", "100");
		int ibl = Integer.parseInt(sbl);
		
		if (!resparam.packageName.equals("com.android.systemui"))
			return;

		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);

		// Brighten status bar icon
		boolean isBrig = preference.getBoolean("key_brigi", false);
		if(isBrig){
			resparam.res.setReplacement("com.android.systemui", "dimen", "status_bar_icon_drawing_alpha", modRes.fwd(R.dimen.status_bar_icon_drawing_alpha));

		}
		
		// Enlarge status bar icon
		boolean isBigi = preference.getBoolean("key_bigi", false);
		if(isBigi){
			resparam.res.setReplacement("com.android.systemui", "dimen", "status_bar_icon_drawing_size", modRes.fwd(R.dimen.status_bar_icon_drawing_size));

		}
		
		// Change the number of tiles in a columns and unlimited items in quick settings
		boolean isNumcolumns = preference.getBoolean("key_numcol", false);

		if(isNumcolumns){
			resparam.res.setReplacement("com.android.systemui", "integer", "quick_settings_num_columns", icol);
		}
		
		// Change the number of tiles in a row and unlimited items in quick settings
		boolean isNumrow = preference.getBoolean("key_numrow", false);

		if(isNumrow){
			resparam.res.setReplacement("com.android.systemui", "integer", "quick_settings_max_rows", irow);
			resparam.res.setReplacement("com.android.systemui", "integer", "config_maxToolItems", 99);
		}
		
		// Change battery charged alert level
		boolean iskey_fullalert = preference.getBoolean("key_fullalert", false);

		if(iskey_fullalert){
			resparam.res.setReplacement("com.android.systemui", "integer", "config_batteryChargedAlertLevel", ibl);
		}
		
		// Enable Close All button
		boolean isCloseall = preference.getBoolean("key_closeall", false);
		if(isCloseall){
			resparam.res.setReplacement("com.android.systemui", "bool", "config_enableCloseAllButton", true);

		}
		
		// Move Close All button
		boolean isMvcloseall = preference.getBoolean("key_mvcloseall", false);
		if(isMvcloseall){
			resparam.res.setReplacement("com.android.systemui", "drawable", "somc_close_all_background", modRes.fwd(R.drawable.ic_close_all));
			resparam.res.setReplacement("com.android.systemui", "dimen", "close_all_button_layout_margin_top", modRes.fwd(R.dimen.close_all_button_layout_margin_top));
			resparam.res.setReplacement("com.android.systemui", "string", "close_all_apps_button_text", "");

		}
		
	}


	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

		if (!lpparam.packageName.equals("com.android.systemui"))
			return;
	
	// Hide search bar in recent apps
		boolean isNoarc = preference.getBoolean("key_nosearchbar", false);

		if(isNoarc){
		try {
			XposedHelpers.findAndHookMethod(
					"com.android.systemui.recents.RecentsActivity",
					lpparam.classLoader,
					"addSearchBarAppWidgetView",
					XC_MethodReplacement.DO_NOTHING
			);
		} catch (Throwable t) {
			XposedBridge.log(t.getMessage());
		}
		}

	// Hide No SIM icon
	boolean isNosim = preference.getBoolean("key_nosim", false);
	if(isNosim){
		try {
			XposedHelpers.findAndHookMethod(
					"com.android.systemui.statusbar.policy.NetworkController",
					lpparam.classLoader,
					"updateSimIcon",
					XC_MethodReplacement.DO_NOTHING
			);
		} catch (Throwable t) {
			XposedBridge.log(t.getMessage());
		}
		}
		
		
	}; //handleLoadPackage



}
