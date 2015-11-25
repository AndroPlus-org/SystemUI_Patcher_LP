package org.androplus.systemuimod;

import android.content.res.XModuleResources;
import android.content.res.XResources.DimensionReplacement;
import android.util.TypedValue;
import android.view.View;

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
		// For change the number of tiles in a columns
		String scol = preference.getString("list_key_numcol", "4");
		int icol = Integer.parseInt(scol);
		// For change the number of tiles in a row
		String srow = preference.getString("list_key", "4");
		int irow = Integer.parseInt(srow);
		// For change battery charged alert level
		String sbl = preference.getString("key_fullalert_et", "100");
		int ibl = Integer.parseInt(sbl);
		// For change notification drawer width
		float width_drawer = Float.parseFloat(preference.getString("key_wide_notification_drawer_i", "620.0"));
		// For change quick Settings tile text size
		float qs_txtsize = Float.parseFloat(preference.getString("key_quick_settings_tile_text_size_i", "12.0"));
		
		if (!(resparam.packageName.equals("com.android.systemui")||resparam.packageName.equals("com.sonymobile.keyboardlauncher")||resparam.packageName.equals("com.android.settings")))
			return;

		if (resparam.packageName.equals("com.android.systemui")){
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
		
		// Wide notification drawer
		boolean isWidedrawer = preference.getBoolean("key_wide_notification_drawer", false);
		if(isWidedrawer){
			resparam.res.setReplacement("com.android.systemui", "dimen", "notification_panel_width", new DimensionReplacement(width_drawer,TypedValue.COMPLEX_UNIT_DIP));

		}
		
		// Quick Settings tile text size
		boolean isQstxt = preference.getBoolean("key_quick_settings_tile_text_size", false);
		if(isQstxt){
			resparam.res.setReplacement("com.android.systemui", "dimen", "qs_tile_text_size", new DimensionReplacement(qs_txtsize,TypedValue.COMPLEX_UNIT_SP));

		}
		
		// Disable dismiss all button
		boolean isNodismiss = preference.getBoolean("key_nodismiss", false);

		if(isNodismiss){
			/*resparam.res.setReplacement("com.android.systemui", "drawable", "ic_dismiss_all", new XResources.DrawableLoader()
			        {
			            @Override
			            public Drawable newDrawable(XResources res, int id) throws Throwable
			            {
			                return new ColorDrawable(Color.TRANSPARENT);
			            };
			        });*/
			resparam.res.hookLayout("com.android.systemui", "layout", "status_bar_notification_dismiss_all", new XC_LayoutInflated() {
		                @Override
		                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
		                    liparam.view.findViewById(liparam.res.getIdentifier("dismiss_text", "id", "com.android.systemui")).setVisibility(View.GONE);
		                    liparam.view.findViewById(liparam.res.getIdentifier("dismiss_text", "id", "com.android.systemui")).getLayoutParams().height = 0;
		                }
		            });
		            
		}
		
		}//End
		
		// KeyboardLauncher
		if (resparam.packageName.equals("com.sonymobile.keyboardlauncher")){
		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);

		// Hide unneeded thumbnail
		boolean isHthumb = preference.getBoolean("key_hide_thumb_keyboard_launcher", false);

		if(isHthumb){
			try {
			resparam.res.setReplacement("com.sonymobile.keyboardlauncher", "dimen", "lifestyle_back_icn_height", modRes.fwd(R.dimen.lifestyle_back_icn_height));
			resparam.res.setReplacement("com.sonymobile.keyboardlauncher", "dimen", "thumbnail_view_height", modRes.fwd(R.dimen.thumbnail_view_height));
			resparam.res.setReplacement("com.sonymobile.keyboardlauncher", "dimen", "thumbnail_capture_height", modRes.fwd(R.dimen.thumbnail_capture_height));
			resparam.res.setReplacement("com.sonymobile.keyboardlauncher", "drawable", "kasumi_lifestyle_back_icn", 0x00000000);
			} catch (Throwable t) {
			XposedBridge.log(t.getMessage());
			}
		}

		}//End
		
		// Settings
		if (resparam.packageName.equals("com.android.settings")){
		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);

		// Use locale specific sorting style
		boolean isLsss = preference.getBoolean("key_locale_specific_sorting_style", false);

		if(isLsss){
			try {
			resparam.res.setReplacement("com.android.settings", "bool", "config_localeUseSingleSortOrder", true);
			} catch (Throwable t) {
			XposedBridge.log(t.getMessage());
			}
		}

		}//End
		
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
