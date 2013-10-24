import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_objdetect;

public class Smoother {
    public static void main(String[] args) { 
	String s = Loader.load(opencv_objdetect.class);
	System.out.println(s);
    }
}
