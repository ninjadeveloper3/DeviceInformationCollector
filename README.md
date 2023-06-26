# DeviceInformationCollector
device data collector
..............
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

 ...............


 Step 2. Add the dependency


implementation 'com.github.ninjadeveloper3:DeviceInformationCollector:Tag'

..............
import module by adding this line in dependencies

implementation project(path: ':deviceinformationlibrary')



[![](https://jitpack.io/v/ninjadeveloper3/DeviceInformationCollector.svg)](https://jitpack.io/#ninjadeveloper3/DeviceInformationCollector)
