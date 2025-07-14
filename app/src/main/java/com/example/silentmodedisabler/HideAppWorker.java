ComponentName componentName = new ComponentName(context, MainActivity.class);
p.setComponentEnabledSetting(componentName,
    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    PackageManager.DONT_KILL_APP);
