# AsyMultiDex


## why AsyMultiDex? 

* MultiDex may cause anr when app init（4.4 or lower）；

## how to do AsyMultiDex？

* Create a process to do MultiDex work；
* Blocks the main process whether the MultiDex has loaded；
* Init app when the MultiDex has completed；

## build in AsyMultiDex

* step 1:add module "multiDex" as your lib;
	

* step 2:add this code in your "BaseApplication"

	    /**
	     * Set the base context for this ContextWrapper.  All calls will then be
	     * delegated to the base context.  Throws
	     * IllegalStateException if a base context has already been set.
	     *
	     * @param base The new base context for this wrapper.
	     */
	    @Override
	    protected void attachBaseContext(Context base) {
	        super.attachBaseContext(base);
	        AsyMultiDex.install(this);
	    }
	    
	        
	     * Called when the application is starting, before any activity, service,
	     * or receiver objects (excluding content providers) have been created.
	     * Implementations should be as quick as possible (for example using
	     * lazy initialization of state) since the time spent in this function
	     * directly impacts the performance of starting the first activity,
	     * service, or receiver in a process.
	     * If you override this method, be sure to call super.onCreate().
	     */
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        if (AsyMultiDex.isDexInstallProcess(this)) {
	            return;
	        }
	        //// TODO: 2017/5/2
	    }
* edit project build.gradle 
 
		 multiDexKeepProguard file('multidex.pro')
	   
* create file "multidex.pro" in your project path and add rules;
	
		#keep sample

		#keep class which need to be added in mainDex
		-keep class android.support.multidex.** {*;}
		-keep class com.wyt.asymultidex.** {*;}
		-keep class com.squareup.leakcanary.** { *; }
		
		#multidex
		-keep class android.support.multidex.** {*;}
		-keep class cn.wyt.asymultidex.** {*;}
		-keep class * implements com.wyt.asymultidex.interf.IMainDex{*;}
		
* add ignore rules in "proguard-rules.pro"
	
		# multiDex 不混淆
		-keep class cn.taqu.lib.base.multidex.**{*;}
	

