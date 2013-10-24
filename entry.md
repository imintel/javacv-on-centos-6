Using JavaCV on CentOS 6.4
==========================

[JavaCV](https://code.google.com/p/javacv/) is a "Java interface to
[OpenCV](http://opencv.org/) and more". OpenCV is the de facto standard
of computer vision software enabling facial recognition, motion
detection, general object detection to run over images. JavaCV
incorporates with other technologies like [ffmpeg](http://www.ffmpeg.org/) allowing
the same computer vision algorithms to intake videos.

If you are using Java, or the JVM, Java Bindings are used
to interact with c/c++ libraries like OpenCV and ffmpeg. JavaCV provides all the jars
pre-built including a jar that contains .so files, shared object files,
for the various platforms, Mac, Linux and Windows. Their intention is to
not require a system level install of the OpenCV libraries.

We ran into issues on CentOS 6.4 unable to find the
shared object files. JavaCV loaded the .so files from the
provided jar and dynamically made the jnilib\*.so files. Unfortunately
the [libc](https://www.gnu.org/software/libc/) version provided with
CentOS 6.4 is below the version the provided .so files were built for and the linking operation failed.
To see what version of libc is installed, `ldd --version`. In our case
it is 2.12 and we are getting stack traces that looked like this:

```
Exception in thread "main" java.lang.UnsatisfiedLinkError: no jniopencv_objdetect in java.library.path
        at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1681)
        at java.lang.Runtime.loadLibrary0(Runtime.java:840)
        at java.lang.System.loadLibrary(System.java:1047)
        at com.googlecode.javacpp.Loader.loadLibrary(Loader.java:701)
        at com.googlecode.javacpp.Loader.load(Loader.java:578)
        at com.googlecode.javacpp.Loader.load(Loader.java:532)
        at com.googlecode.javacv.cpp.opencv_objdetect.<clinit>(opencv_objdetect.java:91)
        at java.lang.Class.forName0(Native Method)
        at java.lang.Class.forName(Class.java:266)
        at com.googlecode.javacpp.Loader.load(Loader.java:553)
        at Smoother.main(Smoother.java:6)
Caused by: java.lang.UnsatisfiedLinkError: /tmp/javacpp8883169523366/libjniopencv_objdetect.so: /lib64/libc.so.6: version `GLIBC_2.14' not found (required by /tmp/javacpp8883169523366/libjniopencv_objdetect.so)
        at java.lang.ClassLoader$NativeLibrary.load(Native Method)
        at java.lang.ClassLoader.loadLibrary0(ClassLoader.java:1750)
        at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1646)
        at java.lang.Runtime.load0(Runtime.java:787)
        at java.lang.System.load(System.java:1022)
        at com.googlecode.javacpp.Loader.loadLibrary(Loader.java:690)
```

To try out the provided demo on CentOS 6.4

```
git clone https://github.com/imintel/javacv-on-centos-6.git && cd javacv-on-centos-6/demo
javac -cp javacv-linux-x86_64.jar:javacpp.jar:javacv.jar Smoother.java
java -cp javacv-linux-x86_64.jar:javacpp.jar:javacv.jar Smoother
```

You should see a similar stack trace from above.

To fix this we will compile our own javacv-linux-x86_64.jar and OpenCV libraries.

###Building OpenCV

**Make sure to have JAVA_HOME defined.** Should be something like `/usr/lib/jvm/java`

1. Install system packages
	
			sudo yum groupinstall "Development Tools"
			sudo yum install cmake
			sudo yum install ant
			
2. Clone the OpenCV repository
			
			 git clone https://github.com/Itseez/opencv.git && cd opencv

3. Checkout desired release
			
			 git checkout 2.4.6.2
			 
4. Make release directory

			 mkdir release && cd release
			 
5. Generate make file, make and install

	This step will take some time depending on the machine and what 3rd party libraries are built.


			 cmake -DBUILD_SHARED_LIBS=ON -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/ ..
			 make
			 sudo make install
			 
			 
6. Ensure library files exist
			 
			 ls /usr/lib/*opencv*
			
	

###Building JavaCV

1. Install [maven](http://maven.apache.org/download.cgi) if needed.

2. Clone the JavaCV repository

   			git clone https://code.google.com/p/javacv/ && cd javacv
   			
3. Checkout the latest release tag, 0.6 at this time.
	
			git checkout 0.6

4. Build the project
   
   			mvn install

	In step 3 you should see c++ compiler commands executing picking up on the libraries built in the "Building 	OpenCV" step.

5. Copy the built jar

			cp target/javacv-linux-x86_64.jar PATH_TO_DEMO_REPOSITORY/
			
###Using the jar built from source
			
Run the same test commands from above

```
javac -cp javacv-linux-x86_64.jar:javacpp.jar:javacv.jar Smoother.java
java -cp javacv-linux-x86_64.jar:javacpp.jar:javacv.jar Smoother
```

And you should see the correctly linked libjni file path.

```
/tmp/javacpp14551117426304/libjniopencv_objdetect.so
```

The provided Smoother.java file attempts to load the shared library and print the path.
To actually get started with JavaCV take a look at their [cookbook](https://code.google.com/p/javacv/wiki/OpenCV2_Cookbook_Examples).

